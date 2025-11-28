import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Locations } from '../Locations';
import * as useLocationsModule from '../../hooks/useLocations';

// Mock the useLocations hook
vi.mock('../../hooks/useLocations');

// Mock the LocationsTable component to simplify testing
vi.mock('../../features/locations/LocationsTable', () => ({
  LocationsTable: ({ data }: { data: any[] }) => (
    <div data-testid="locations-table">
      Table with {data.length} locations
    </div>
  ),
}));

const mockLocations = [
  {
    locationid: 'loc-001',
    name: 'Test Restaurant 1',
    description: 'A test restaurant',
    status: 'active',
    address1: '123 Test St',
    city: 'Detroit',
    region: 'Michigan',
    zip: '48201',
    country: 'United States',
    phone1: '313-555-0100',
  },
  {
    locationid: 'loc-002',
    name: 'Test Restaurant 2',
    description: 'Another test restaurant',
    status: 'active',
    address1: '456 Main St',
    city: 'Ann Arbor',
    region: 'Michigan',
    zip: '48104',
    country: 'United States',
    phone1: '734-555-0200',
  },
];

function renderWithProviders(component: React.ReactElement) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  });

  return render(
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>{component}</BrowserRouter>
    </QueryClientProvider>
  );
}

describe('Locations Page', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render loading state', () => {
    vi.mocked(useLocationsModule.useLocations).mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
      isError: false,
      isSuccess: false,
      status: 'pending',
    } as any);

    renderWithProviders(<Locations />);

    expect(screen.getByText('Loading locations...')).toBeInTheDocument();
  });

  it('should render error state', () => {
    const error = new Error('Failed to fetch locations');
    vi.mocked(useLocationsModule.useLocations).mockReturnValue({
      data: undefined,
      isLoading: false,
      error,
      isError: true,
      isSuccess: false,
      status: 'error',
    } as any);

    renderWithProviders(<Locations />);

    expect(screen.getByText(/Error loading locations/)).toBeInTheDocument();
    expect(screen.getByText(/Failed to fetch locations/)).toBeInTheDocument();
  });

  it('should render locations table with data', () => {
    vi.mocked(useLocationsModule.useLocations).mockReturnValue({
      data: mockLocations,
      isLoading: false,
      error: null,
      isError: false,
      isSuccess: true,
      status: 'success',
    } as any);

    renderWithProviders(<Locations />);

    expect(screen.getByText('Locations')).toBeInTheDocument();
    expect(screen.getByTestId('locations-table')).toBeInTheDocument();
    expect(screen.getByText(/Showing 2 of 2 locations/)).toBeInTheDocument();
  });

  it('should filter locations by search term', async () => {
    const user = userEvent.setup();

    vi.mocked(useLocationsModule.useLocations).mockReturnValue({
      data: mockLocations,
      isLoading: false,
      error: null,
      isError: false,
      isSuccess: true,
      status: 'success',
    } as any);

    renderWithProviders(<Locations />);

    const searchInput = screen.getByPlaceholderText('Search by name or address...');

    await user.type(searchInput, 'Detroit');

    await waitFor(() => {
      expect(screen.getByText(/Showing 1 of 2 locations/)).toBeInTheDocument();
    });
  });

  it('should show clear button when search has text', async () => {
    const user = userEvent.setup();

    vi.mocked(useLocationsModule.useLocations).mockReturnValue({
      data: mockLocations,
      isLoading: false,
      error: null,
      isError: false,
      isSuccess: true,
      status: 'success',
    } as any);

    renderWithProviders(<Locations />);

    const searchInput = screen.getByPlaceholderText('Search by name or address...');

    expect(screen.queryByRole('button', { name: /✕/ })).not.toBeInTheDocument();

    await user.type(searchInput, 'Detroit');

    expect(screen.getByRole('button', { name: /✕/ })).toBeInTheDocument();
  });

  it('should clear search when clear button clicked', async () => {
    const user = userEvent.setup();

    vi.mocked(useLocationsModule.useLocations).mockReturnValue({
      data: mockLocations,
      isLoading: false,
      error: null,
      isError: false,
      isSuccess: true,
      status: 'success',
    } as any);

    renderWithProviders(<Locations />);

    const searchInput = screen.getByPlaceholderText('Search by name or address...') as HTMLInputElement;

    await user.type(searchInput, 'Detroit');
    expect(searchInput.value).toBe('Detroit');

    const clearButton = screen.getByRole('button', { name: /✕/ });
    await user.click(clearButton);

    expect(searchInput.value).toBe('');
    expect(screen.getByText(/Showing 2 of 2 locations/)).toBeInTheDocument();
  });

  it('should filter by address fields', async () => {
    const user = userEvent.setup();

    vi.mocked(useLocationsModule.useLocations).mockReturnValue({
      data: mockLocations,
      isLoading: false,
      error: null,
      isError: false,
      isSuccess: true,
      status: 'success',
    } as any);

    renderWithProviders(<Locations />);

    const searchInput = screen.getByPlaceholderText('Search by name or address...');

    await user.type(searchInput, 'Ann Arbor');

    await waitFor(() => {
      expect(screen.getByText(/Showing 1 of 2 locations/)).toBeInTheDocument();
    });
  });

  it('should be case insensitive search', async () => {
    const user = userEvent.setup();

    vi.mocked(useLocationsModule.useLocations).mockReturnValue({
      data: mockLocations,
      isLoading: false,
      error: null,
      isError: false,
      isSuccess: true,
      status: 'success',
    } as any);

    renderWithProviders(<Locations />);

    const searchInput = screen.getByPlaceholderText('Search by name or address...');

    await user.type(searchInput, 'DETROIT');

    await waitFor(() => {
      expect(screen.getByText(/Showing 1 of 2 locations/)).toBeInTheDocument();
    });
  });
});
