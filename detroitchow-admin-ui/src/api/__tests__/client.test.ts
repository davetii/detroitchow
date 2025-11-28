import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { apiRequest, ApiError } from '../client';

// Mock fetch globally
const mockFetch = vi.fn();
globalThis.fetch = mockFetch as unknown as typeof fetch;

describe('ApiError', () => {
  it('should create error with message, status, and response', () => {
    const error = new ApiError('Test error', 404, { detail: 'Not found' });

    expect(error.name).toBe('ApiError');
    expect(error.message).toBe('Test error');
    expect(error.status).toBe(404);
    expect(error.response).toEqual({ detail: 'Not found' });
  });

  it('should create error without response data', () => {
    const error = new ApiError('Test error', 500);

    expect(error.name).toBe('ApiError');
    expect(error.message).toBe('Test error');
    expect(error.status).toBe(500);
    expect(error.response).toBeUndefined();
  });

  it('should be instance of Error', () => {
    const error = new ApiError('Test error', 400);

    expect(error instanceof Error).toBe(true);
    expect(error instanceof ApiError).toBe(true);
  });
});

describe('apiRequest', () => {
  const baseUrl = 'http://localhost:8080/api/v1';

  beforeEach(() => {
    vi.clearAllMocks();
    // Set environment variable for tests
    import.meta.env.VITE_API_BASE_URL = baseUrl;
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  describe('successful requests', () => {
    it('should make GET request with correct URL', async () => {
      const mockData = { id: 1, name: 'Test' };
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => mockData,
      });

      const result = await apiRequest('/test');

      expect(mockFetch).toHaveBeenCalledWith(
        `${baseUrl}/test`,
        expect.objectContaining({
          headers: {
            'Content-Type': 'application/json',
          },
        })
      );
      expect(result).toEqual(mockData);
    });

    it('should make POST request with body', async () => {
      const requestBody = { name: 'New Item' };
      const responseData = { id: 1, name: 'New Item' };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 201,
        json: async () => responseData,
      });

      const result = await apiRequest('/items', {
        method: 'POST',
        body: JSON.stringify(requestBody),
      });

      expect(mockFetch).toHaveBeenCalledWith(
        `${baseUrl}/items`,
        expect.objectContaining({
          method: 'POST',
          body: JSON.stringify(requestBody),
          headers: {
            'Content-Type': 'application/json',
          },
        })
      );
      expect(result).toEqual(responseData);
    });

    it('should handle 204 No Content response', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 204,
      });

      const result = await apiRequest('/delete', {
        method: 'DELETE',
      });

      expect(result).toBeUndefined();
    });

    it('should merge custom headers with default headers', async () => {
      const mockData = { success: true };
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => mockData,
      });

      await apiRequest('/test', {
        headers: {
          'Authorization': 'Bearer token123',
          'X-Custom-Header': 'custom-value',
        },
      });

      expect(mockFetch).toHaveBeenCalledWith(
        `${baseUrl}/test`,
        expect.objectContaining({
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer token123',
            'X-Custom-Header': 'custom-value',
          },
        })
      );
    });

    it('should pass through request options', async () => {
      const mockData = { data: 'test' };
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => mockData,
      });

      await apiRequest('/test', {
        method: 'PUT',
        credentials: 'include',
      });

      expect(mockFetch).toHaveBeenCalledWith(
        `${baseUrl}/test`,
        expect.objectContaining({
          method: 'PUT',
          credentials: 'include',
        })
      );
    });
  });

  describe('error handling', () => {
    it('should throw ApiError on HTTP 404', async () => {
      const errorResponse = { message: 'Resource not found' };
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 404,
        statusText: 'Not Found',
        json: async () => errorResponse,
      });

      try {
        await apiRequest('/missing');
        expect.fail('Should have thrown an error');
      } catch (error) {
        expect(error).toBeInstanceOf(ApiError);
        expect((error as ApiError).status).toBe(404);
        expect((error as ApiError).message).toBe('Resource not found');
        expect((error as ApiError).response).toEqual(errorResponse);
      }
    });

    it('should throw ApiError on HTTP 500', async () => {
      const errorResponse = { message: 'Internal server error' };
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 500,
        statusText: 'Internal Server Error',
        json: async () => errorResponse,
      });

      try {
        await apiRequest('/error');
        expect.fail('Should have thrown an error');
      } catch (error) {
        expect(error).toBeInstanceOf(ApiError);
        expect((error as ApiError).status).toBe(500);
        expect((error as ApiError).message).toBe('Internal server error');
      }
    });

    it('should handle error response without JSON body', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 400,
        statusText: 'Bad Request',
        json: async () => {
          throw new Error('Invalid JSON');
        },
      });

      try {
        await apiRequest('/bad');
      } catch (error) {
        expect(error).toBeInstanceOf(ApiError);
        expect((error as ApiError).status).toBe(400);
        expect((error as ApiError).message).toBe('HTTP 400: Bad Request');
        expect((error as ApiError).response).toBeNull();
      }
    });

    it('should handle network errors', async () => {
      const networkError = new Error('Network connection failed');
      mockFetch.mockRejectedValueOnce(networkError);

      try {
        await apiRequest('/test');
      } catch (error) {
        expect(error).toBeInstanceOf(ApiError);
        expect((error as ApiError).status).toBe(0);
        expect((error as ApiError).message).toBe('Network connection failed');
      }
    });

    it('should handle unknown errors', async () => {
      mockFetch.mockRejectedValueOnce('Unknown error string');

      try {
        await apiRequest('/test');
      } catch (error) {
        expect(error).toBeInstanceOf(ApiError);
        expect((error as ApiError).status).toBe(0);
        expect((error as ApiError).message).toBe('An unknown error occurred');
      }
    });

    it('should rethrow ApiError instances', async () => {
      const apiError = new ApiError('Custom API error', 403, { forbidden: true });
      mockFetch.mockRejectedValueOnce(apiError);

      try {
        await apiRequest('/test');
      } catch (error) {
        expect(error).toBe(apiError);
        expect((error as ApiError).status).toBe(403);
        expect((error as ApiError).message).toBe('Custom API error');
        expect((error as ApiError).response).toEqual({ forbidden: true });
      }
    });
  });

  describe('response parsing', () => {
    it('should parse JSON response correctly', async () => {
      const complexData = {
        id: 1,
        name: 'Test',
        nested: {
          array: [1, 2, 3],
          object: { key: 'value' },
        },
      };

      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => complexData,
      });

      const result = await apiRequest('/complex');
      expect(result).toEqual(complexData);
    });

    it('should handle array responses', async () => {
      const arrayData = [
        { id: 1, name: 'Item 1' },
        { id: 2, name: 'Item 2' },
      ];

      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => arrayData,
      });

      const result = await apiRequest('/items');
      expect(result).toEqual(arrayData);
    });

    it('should handle empty object response', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => ({}),
      });

      const result = await apiRequest('/empty');
      expect(result).toEqual({});
    });

    it('should handle null response', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => null,
      });

      const result = await apiRequest('/null');
      expect(result).toBeNull();
    });
  });

  describe('URL construction', () => {
    it('should use default base URL when env variable not set', async () => {
      // Clear environment variable
      delete import.meta.env.VITE_API_BASE_URL;

      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => ({ success: true }),
      });

      await apiRequest('/test');

      expect(mockFetch).toHaveBeenCalledWith(
        'http://localhost:8080/api/v1/test',
        expect.any(Object)
      );
    });

    it('should handle endpoint with leading slash', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => ({ success: true }),
      });

      await apiRequest('/test');

      expect(mockFetch).toHaveBeenCalledWith(
        `${baseUrl}/test`,
        expect.any(Object)
      );
    });

    it('should handle endpoint without leading slash', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => ({ success: true }),
      });

      await apiRequest('test');

      expect(mockFetch).toHaveBeenCalledWith(
        `${baseUrl}test`,
        expect.any(Object)
      );
    });
  });

  describe('HTTP methods', () => {
    it('should support GET requests', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => ({ method: 'GET' }),
      });

      await apiRequest('/test', { method: 'GET' });

      expect(mockFetch).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({ method: 'GET' })
      );
    });

    it('should support POST requests', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 201,
        json: async () => ({ method: 'POST' }),
      });

      await apiRequest('/test', { method: 'POST' });

      expect(mockFetch).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({ method: 'POST' })
      );
    });

    it('should support PUT requests', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => ({ method: 'PUT' }),
      });

      await apiRequest('/test', { method: 'PUT' });

      expect(mockFetch).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({ method: 'PUT' })
      );
    });

    it('should support DELETE requests', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 204,
      });

      await apiRequest('/test', { method: 'DELETE' });

      expect(mockFetch).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({ method: 'DELETE' })
      );
    });
  });
});
