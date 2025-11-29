import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import React from 'react';
import { useGooglePlace } from '../useGooglePlace';
import { ApiError } from '../../api/client';
import * as apiClient from '../../api/client';

vi.mock('../../api/client', async () => {
  const actual = await vi.importActual('../../api/client');
  return {
    ...actual,
    apiRequest: vi.fn(),
  };
});

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

describe('useGooglePlace', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch Google Place data successfully', async () => {
    // Mock backend response in camelCase
    vi.mocked(apiClient.apiRequest).mockResolvedValue({
      data: {
        locationid: 'loc-001',
        placeId: 'ChIJN1blFLsB6IkRv5ZSZsJVHmM',
        name: 'Test Restaurant Google',
        formattedAddress: '123 Test St, Detroit, MI 48201, USA',
        businessStatus: 'OPERATIONAL' as const,
        phone1: '313-555-9999',
        website: 'https://google-example.com',
      },
    });

    const { result } = renderHook(() => useGooglePlace('loc-001'), {
      wrapper: createWrapper(),
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    // Verify it's mapped to snake_case
    expect(result.current.data).toMatchObject({
      locationid: 'loc-001',
      place_id: 'ChIJN1blFLsB6IkRv5ZSZsJVHmM',
      name: 'Test Restaurant Google',
      formatted_address: '123 Test St, Detroit, MI 48201, USA',
      business_status: 'OPERATIONAL',
      phone1: '313-555-9999',
      website: 'https://google-example.com',
    });
    expect(apiClient.apiRequest).toHaveBeenCalledWith('/googleplace/loc-001');
  });

  it('should return null when 404 error occurs', async () => {
    const error = new ApiError('Not Found', 404);
    vi.mocked(apiClient.apiRequest).mockRejectedValue(error);

    const { result } = renderHook(() => useGooglePlace('loc-001'), {
      wrapper: createWrapper(),
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    expect(result.current.data).toBeNull();
    expect(result.current.error).toBeNull();
  });

  it('should not fetch when locationId is undefined', () => {
    const { result } = renderHook(() => useGooglePlace(undefined), {
      wrapper: createWrapper(),
    });

    expect(result.current.data).toBeUndefined();
    expect(apiClient.apiRequest).not.toHaveBeenCalled();
  });

  it('should throw error for non-404 errors', async () => {
    const error = new Error('Server error');
    vi.mocked(apiClient.apiRequest).mockRejectedValue(error);

    const { result } = renderHook(() => useGooglePlace('loc-001'), {
      wrapper: createWrapper(),
    });

    await waitFor(() => expect(result.current.isError).toBe(true));

    expect(result.current.error).toEqual(error);
  });

  it('should handle response with empty data object', async () => {
    vi.mocked(apiClient.apiRequest).mockResolvedValue({ data: null });

    const { result } = renderHook(() => useGooglePlace('loc-001'), {
      wrapper: createWrapper(),
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    expect(result.current.data).toBeNull();
  });
});
