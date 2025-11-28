import { useState, useMemo } from 'react';
import { useLocations } from '../hooks/useLocations';
import { LocationsTable } from '../features/locations/LocationsTable';
import type { components } from '../types/api';

type Location = components['schemas']['Location'];

export function Locations() {
  const { data: locations, isLoading, error } = useLocations();
  const [searchTerm, setSearchTerm] = useState('');

  const filteredLocations = useMemo(() => {
    if (!locations) return [];
    if (!searchTerm.trim()) return locations;

    const searchLower = searchTerm.toLowerCase();

    return locations.filter((location: Location) => {
      const name = location.name?.toLowerCase() || '';

      const addressParts = [
        location.address1,
        location.address2,
        location.city,
        location.region,
        location.zip,
        location.country,
      ].filter(Boolean);
      const address = addressParts.join(', ').toLowerCase();

      return name.includes(searchLower) || address.includes(searchLower);
    });
  }, [locations, searchTerm]);

  return (
    <div className="max-w-full mx-auto px-4 py-6">
      <div className="bg-white rounded-lg shadow-md p-8">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-3xl font-bold text-gray-800">
            Locations
          </h1>

          <div className="relative w-96">
            <input
              type="text"
              placeholder="Search by name or address..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            {searchTerm && (
              <button
                onClick={() => setSearchTerm('')}
                className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                ✕
              </button>
            )}
          </div>
        </div>

        {isLoading && (
          <div className="flex justify-center items-center py-12">
            <div className="text-gray-600">Loading locations...</div>
          </div>
        )}

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
            Error loading locations: {error instanceof Error ? error.message : 'Unknown error'}
          </div>
        )}

        {locations && (
          <>
            <div className="mb-4 text-sm text-gray-600">
              Showing {filteredLocations.length} of {locations.length} locations
            </div>
            <LocationsTable data={filteredLocations} />
          </>
        )}
      </div>
    </div>
  );
}
