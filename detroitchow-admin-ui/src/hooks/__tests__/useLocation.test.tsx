import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import React from 'react';
import { useLocation } from '../useLocation';
import * as apiClient from '../../api/client';

vi.mock('../../api/client');

const mockLocation = {
  locationid: 'loc-001',
  name: 'Test Restaurant',
  operatingStatus: 'active',
  address1: '123 Test St',
  city: 'Detroit',
  region: 'Michigan',
  zip: '48201',
  country: 'United States',
};

function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  });

  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
}

describe('useLocation', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch location data successfully', async () => {
    vi.mocked(apiClient.apiRequest).mockResolvedValue({
      data: mockLocation,
    });

    const { result } = renderHook(() => useLocation('loc-001'), {
      wrapper: createWrapper(),
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    expect(result.current.data).toEqual(mockLocation);
    expect(apiClient.apiRequest).toHaveBeenCalledWith('/location/loc-001');
  });

  it('should not fetch when locationId is undefined', () => {
    const { result } = renderHook(() => useLocation(undefined), {
      wrapper: createWrapper(),
    });

    expect(result.current.data).toBeUndefined();
    expect(apiClient.apiRequest).not.toHaveBeenCalled();
  });

  it('should handle fetch error', async () => {
    const error = new Error('Failed to fetch location');
    vi.mocked(apiClient.apiRequest).mockRejectedValue(error);

    const { result } = renderHook(() => useLocation('loc-001'), {
      wrapper: createWrapper(),
    });

    await waitFor(() => expect(result.current.isError).toBe(true));

    expect(result.current.error).toEqual(error);
  });

  it('should handle response with empty data object', async () => {
    vi.mocked(apiClient.apiRequest).mockResolvedValue({ data: null });

    const { result } = renderHook(() => useLocation('loc-001'), {
      wrapper: createWrapper(),
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    expect(result.current.data).toBeNull();
  });
});
