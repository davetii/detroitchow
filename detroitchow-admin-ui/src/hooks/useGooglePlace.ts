import { useQuery } from '@tanstack/react-query';
import { apiRequest, ApiError } from '../api/client';
import type { components } from '../types/api';

type GooglePlaces = components['schemas']['GooglePlaces'];

// Backend returns camelCase but spec defines snake_case
interface GooglePlaceBackendResponse {
  data?: {
    locationid: string;
    placeId: string;
    name?: string;
    lat?: string;
    lng?: string;
    phone1?: string | null;
    phone2?: string | null;
    formattedAddress?: string;
    website?: string | null;
    googleUrl?: string | null;
    businessStatus?: 'OPERATIONAL' | 'CLOSED_TEMPORARILY' | 'CLOSED_PERMANENTLY';
    txtsearchJson?: Record<string, unknown> | null;
    detailJson?: Record<string, unknown> | null;
    storeJson?: Record<string, unknown> | null;
  };
}

// Map backend camelCase response to snake_case expected by frontend
function mapGooglePlaceResponse(backendData: GooglePlaceBackendResponse['data']): GooglePlaces | null {
  if (!backendData) return null;

  return {
    locationid: backendData.locationid,
    place_id: backendData.placeId,
    name: backendData.name,
    lat: backendData.lat,
    lng: backendData.lng,
    phone1: backendData.phone1,
    phone2: backendData.phone2,
    formatted_address: backendData.formattedAddress,
    website: backendData.website,
    google_url: backendData.googleUrl,
    business_status: backendData.businessStatus,
    txtsearch_json: backendData.txtsearchJson,
    detail_json: backendData.detailJson,
    store_json: backendData.storeJson,
  };
}

export function useGooglePlace(locationId: string | undefined) {
  return useQuery<GooglePlaces | null>({
    queryKey: ['googleplace', locationId],
    queryFn: async () => {
      try {
        const response = await apiRequest<GooglePlaceBackendResponse>(`/googleplace/${locationId}`);
        return mapGooglePlaceResponse(response.data);
      } catch (error) {
        // If 404, return null (no Google Place data exists)
        if (error instanceof ApiError && error.status === 404) {
          return null;
        }
        throw error;
      }
    },
    enabled: !!locationId,
    retry: false, // Don't retry on 404
  });
}
