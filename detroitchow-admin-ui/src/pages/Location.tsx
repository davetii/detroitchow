import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { apiRequest } from '../api/client';
import type { components } from '../types/api';

type Location = components['schemas']['Location'];

export function Location() {
  const { locationId } = useParams<{ locationId: string }>();

  const { data: location, isLoading, error } = useQuery<Location>({
    queryKey: ['location', locationId],
    queryFn: () => apiRequest<Location>(`/location/${locationId}`),
    enabled: !!locationId,
  });

  return (
    <div className="max-w-4xl mx-auto px-4 py-6">
      <div className="bg-white rounded-lg shadow-md p-8">
        {isLoading && (
          <div className="flex justify-center items-center py-12">
            <div className="text-gray-600">Loading location details...</div>
          </div>
        )}

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
            Error loading location: {error instanceof Error ? error.message : 'Unknown error'}
          </div>
        )}

        {location && (
          <div>
            <h1 className="text-3xl font-bold text-gray-800 mb-2">
              {location.name}
            </h1>
            <p className="text-gray-500 text-sm mb-6">
              Location ID: {location.locationid}
            </p>

            <div className="text-gray-600">
              Location details will be implemented in a future session.
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
