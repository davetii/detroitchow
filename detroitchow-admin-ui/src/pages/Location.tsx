import { useParams, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useEffect } from 'react';
import { useLocation } from '../hooks/useLocation';
import { useGooglePlace } from '../hooks/useGooglePlace';
import { apiRequest } from '../api/client';
import type { components } from '../types/api';

type Location = components['schemas']['Location'];

interface LocationFormData {
  locationid: string;
  name: string;
  operatingStatus: string;
  address1: string;
  address2: string;
  city: string;
  region: string;
  zip: string;
  country: string;
  phone1: string;
  website: string;
  facebook: string;
  twitter: string;
  instagram: string;
  opentable: string;
  tripadvisor: string;
  yelp: string;
  hours: string;
  contact_text: string;
  lat?: string;
  lng?: string;
  description?: string;
  locality?: string;
  phone2?: string;
}

export function Location() {
  const { locationId } = useParams<{ locationId: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const { data: location, isLoading: locationLoading, error: locationError } = useLocation(locationId);
  const { data: googlePlace, isLoading: googlePlaceLoading } = useGooglePlace(locationId);

  const { register, handleSubmit, reset, formState: { isDirty } } = useForm<LocationFormData>();

  // Initialize form with location data
  useEffect(() => {
    if (location) {
      reset({
        locationid: location.locationid || '',
        name: location.name || '',
        operatingStatus: location.operatingStatus || 'active',
        address1: location.address1 || '',
        address2: location.address2 || '',
        city: location.city || '',
        region: location.region || '',
        zip: location.zip || '',
        country: location.country || '',
        phone1: location.phone1 || '',
        website: location.website || '',
        facebook: location.facebook || '',
        twitter: location.twitter || '',
        instagram: location.instagram || '',
        opentable: location.opentable || '',
        tripadvisor: location.tripadvisor || '',
        yelp: location.yelp || '',
        hours: location.hours || '',
        contact_text: location.contact_text || '',
        lat: location.lat || '',
        lng: location.lng || '',
        description: location.description || '',
        locality: location.locality || '',
        phone2: location.phone2 || '',
      });
    }
  }, [location, reset]);

  const updateLocationMutation = useMutation({
    mutationFn: (data: LocationFormData) =>
      apiRequest<Location>('/location', {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['location', locationId] });
      queryClient.invalidateQueries({ queryKey: ['locations'] });
      alert('Location updated successfully!');
    },
    onError: (error) => {
      alert(`Failed to update location: ${error instanceof Error ? error.message : 'Unknown error'}`);
    },
  });

  const onSubmit = (data: LocationFormData) => {
    updateLocationMutation.mutate(data);
  };

  const handleUndo = () => {
    if (location) {
      reset({
        locationid: location.locationid || '',
        name: location.name || '',
        operatingStatus: location.operatingStatus || 'active',
        address1: location.address1 || '',
        address2: location.address2 || '',
        city: location.city || '',
        region: location.region || '',
        zip: location.zip || '',
        country: location.country || '',
        phone1: location.phone1 || '',
        website: location.website || '',
        facebook: location.facebook || '',
        twitter: location.twitter || '',
        instagram: location.instagram || '',
        opentable: location.opentable || '',
        tripadvisor: location.tripadvisor || '',
        yelp: location.yelp || '',
        hours: location.hours || '',
        contact_text: location.contact_text || '',
        lat: location.lat || '',
        lng: location.lng || '',
        description: location.description || '',
        locality: location.locality || '',
        phone2: location.phone2 || '',
      });
    }
  };

  const isLoading = locationLoading || googlePlaceLoading;

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="bg-white rounded-lg shadow-md p-8">
          <div className="flex justify-center items-center py-12">
            <div className="text-gray-600">Loading location details...</div>
          </div>
        </div>
      </div>
    );
  }

  if (locationError) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="bg-white rounded-lg shadow-md p-8">
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
            Error loading location: {locationError instanceof Error ? locationError.message : 'Unknown error'}
          </div>
          <button
            onClick={() => navigate('/locations')}
            className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700"
          >
            Back to Locations
          </button>
        </div>
      </div>
    );
  }

  if (!location) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="bg-white rounded-lg shadow-md p-8">
          <div className="text-gray-600">Location not found.</div>
          <button
            onClick={() => navigate('/locations')}
            className="mt-4 px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700"
          >
            Back to Locations
          </button>
        </div>
      </div>
    );
  }

  const showGooglePlaceColumn = !!googlePlace;

  return (
    <div className="max-w-7xl mx-auto px-4 py-6">
      <div className="bg-white rounded-lg shadow-md p-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-3xl font-bold text-gray-800 mb-2">
              {location.name}
            </h1>
            <p className="text-gray-500 text-sm">
              Location ID: {location.locationid}
            </p>
          </div>
          <button
            onClick={() => navigate('/locations')}
            className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700"
          >
            Back to Locations
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          {/* Row 1: Name */}
          <div className={`grid gap-4 ${showGooglePlaceColumn ? 'grid-cols-1 lg:grid-cols-2' : 'grid-cols-1'}`}>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Name
              </label>
              <input
                type="text"
                {...register('name', { required: true })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            {showGooglePlaceColumn && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Google Places Name (Reference)
                </label>
                <input
                  type="text"
                  value={googlePlace.name || ''}
                  readOnly
                  className="w-full px-3 py-2 border border-gray-200 rounded-md bg-gray-50 text-gray-600"
                />
              </div>
            )}
          </div>

          {/* Row 2: Operating Status */}
          <div className={`grid gap-4 ${showGooglePlaceColumn ? 'grid-cols-1 lg:grid-cols-2' : 'grid-cols-1'}`}>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Operating Status
              </label>
              <select
                {...register('operatingStatus', { required: true })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="active">active</option>
                <option value="temporarily_closed">temporarily_closed</option>
                <option value="permanently_closed">permanently_closed</option>
              </select>
            </div>
            {showGooglePlaceColumn && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Google Business Status (Reference)
                </label>
                <input
                  type="text"
                  value={googlePlace.business_status || ''}
                  readOnly
                  className="w-full px-3 py-2 border border-gray-200 rounded-md bg-gray-50 text-gray-600"
                />
              </div>
            )}
          </div>

          {/* Row 3: Address */}
          <div className={`grid gap-4 ${showGooglePlaceColumn ? 'grid-cols-1 lg:grid-cols-2' : 'grid-cols-1'}`}>
            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Address 1
                </label>
                <input
                  type="text"
                  {...register('address1', { required: true })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Address 2
                </label>
                <input
                  type="text"
                  {...register('address2')}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    City
                  </label>
                  <input
                    type="text"
                    {...register('city', { required: true })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Region
                  </label>
                  <input
                    type="text"
                    {...register('region', { required: true })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    ZIP
                  </label>
                  <input
                    type="text"
                    {...register('zip', { required: true })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Country
                  </label>
                  <input
                    type="text"
                    {...register('country', { required: true })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>
            </div>
            {showGooglePlaceColumn && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Google Formatted Address (Reference)
                </label>
                <textarea
                  value={googlePlace.formatted_address || ''}
                  readOnly
                  rows={8}
                  className="w-full px-3 py-2 border border-gray-200 rounded-md bg-gray-50 text-gray-600 resize-none"
                />
              </div>
            )}
          </div>

          {/* Row 4: Phone */}
          <div className={`grid gap-4 ${showGooglePlaceColumn ? 'grid-cols-1 lg:grid-cols-2' : 'grid-cols-1'}`}>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Phone 1
              </label>
              <input
                type="text"
                {...register('phone1')}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            {showGooglePlaceColumn && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Google Phone (Reference)
                </label>
                <input
                  type="text"
                  value={googlePlace.phone1 || ''}
                  readOnly
                  className="w-full px-3 py-2 border border-gray-200 rounded-md bg-gray-50 text-gray-600"
                />
              </div>
            )}
          </div>

          {/* Row 5: Website */}
          <div className={`grid gap-4 ${showGooglePlaceColumn ? 'grid-cols-1 lg:grid-cols-2' : 'grid-cols-1'}`}>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Website
              </label>
              <input
                type="url"
                {...register('website')}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            {showGooglePlaceColumn && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Google Website (Reference)
                </label>
                <input
                  type="text"
                  value={googlePlace.website || ''}
                  readOnly
                  className="w-full px-3 py-2 border border-gray-200 rounded-md bg-gray-50 text-gray-600"
                />
              </div>
            )}
          </div>

          {/* Row 6: Facebook */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Facebook
            </label>
            <input
              type="url"
              {...register('facebook')}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Row 7: Twitter */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Twitter
            </label>
            <input
              type="url"
              {...register('twitter')}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Row 8: Instagram */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Instagram
            </label>
            <input
              type="url"
              {...register('instagram')}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Row 9: OpenTable */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              OpenTable
            </label>
            <input
              type="url"
              {...register('opentable')}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Row 10: TripAdvisor */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              TripAdvisor
            </label>
            <input
              type="url"
              {...register('tripadvisor')}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Row 11: Yelp */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Yelp
            </label>
            <input
              type="url"
              {...register('yelp')}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Row 12: Hours */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Hours
            </label>
            <textarea
              {...register('hours')}
              rows={3}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Row 13: Contact Text */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Contact Text
            </label>
            <textarea
              {...register('contact_text')}
              rows={3}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {/* Action Buttons */}
          <div className="flex gap-4 pt-4 border-t border-gray-200">
            <button
              type="submit"
              disabled={!isDirty || updateLocationMutation.isPending}
              className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed font-medium"
            >
              {updateLocationMutation.isPending ? 'Saving...' : 'Save'}
            </button>
            <button
              type="button"
              onClick={handleUndo}
              disabled={!isDirty || updateLocationMutation.isPending}
              className="px-6 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed font-medium"
            >
              Undo
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
