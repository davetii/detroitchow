import { useQuery } from '@tanstack/react-query';
import { apiRequest } from '../api/client';
import type { components } from '../types/api';

type Location = components['schemas']['Location'];

export function useLocations() {
  return useQuery<Location[]>({
    queryKey: ['locations'],
    queryFn: () => apiRequest<Location[]>('/locations'),
  });
}
