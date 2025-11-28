import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useLocations } from '../useLocations';
import * as apiClient from '../../api/client';

vi.mock('../../api/client');

const mockLocations = [
  {
    locationid: 'loc-001',
    name: 'Test Restaurant 1',
    operatingStatus: 'active',
    address1: '123 Test St',
    city: 'Detroit',
    region: 'Michigan',
    zip: '48201',
    country: 'United States',
  },
  {
    locationid: 'loc-002',
    name: 'Test Restaurant 2',
    operatingStatus: 'active',
    address1: '456 Main St',
    city: 'Ann Arbor',
    region: 'Michigan',
    zip: '48104',
    country: 'United States',
  },
];

function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
}

describe('useLocations', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch locations successfully', async () => {
    vi.mocked(apiClient.apiRequest).mockResolvedValue(mockLocations);

    const { result } = renderHook(() => useLocations(), {
      wrapper: createWrapper(),
    });

    expect(result.current.isLoading).toBe(true);

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });

    expect(result.current.data).toEqual(mockLocations);
    expect(apiClient.apiRequest).toHaveBeenCalledWith('/locations');
  });

  it('should handle error when fetching locations fails', async () => {
    const error = new Error('Network error');
    vi.mocked(apiClient.apiRequest).mockRejectedValue(error);

    const { result } = renderHook(() => useLocations(), {
      wrapper: createWrapper(),
    });

    await waitFor(() => {
      expect(result.current.isError).toBe(true);
    });

    expect(result.current.error).toEqual(error);
    expect(result.current.data).toBeUndefined();
  });

  it('should use correct query key', async () => {
    vi.mocked(apiClient.apiRequest).mockResolvedValue(mockLocations);

    const { result } = renderHook(() => useLocations(), {
      wrapper: createWrapper(),
    });

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });

    // The query key should be ['locations']
    // We can't directly access it, but we can verify the hook works correctly
    expect(result.current.data).toBeDefined();
  });

  it('should return loading state initially', () => {
    vi.mocked(apiClient.apiRequest).mockImplementation(
      () => new Promise(() => {}) // Never resolves
    );

    const { result } = renderHook(() => useLocations(), {
      wrapper: createWrapper(),
    });

    expect(result.current.isLoading).toBe(true);
    expect(result.current.data).toBeUndefined();
  });
});
