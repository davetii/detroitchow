import { useQuery } from '@tanstack/react-query';
import { apiRequest } from '../api/client';
import type { components } from '../types/api';

type Location = components['schemas']['Location'];

interface LocationResponse {
  data?: Location;
}

export function useLocation(locationId: string | undefined) {
  return useQuery<Location | null>({
    queryKey: ['location', locationId],
    queryFn: async () => {
      const response = await apiRequest<LocationResponse>(`/location/${locationId}`);
      return response.data || null;
    },
    enabled: !!locationId,
  });
}
