import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Location } from '../Location';
import * as useLocationModule from '../../hooks/useLocation';
import * as useGooglePlaceModule from '../../hooks/useGooglePlace';
import * as apiClient from '../../api/client';

vi.mock('../../hooks/useLocation');
vi.mock('../../hooks/useGooglePlace');
vi.mock('../../api/client');

const mockLocation = {
  locationid: 'loc-001',
  name: 'Test Restaurant',
  operatingStatus: 'active',
  address1: '123 Test St',
  address2: 'Suite 100',
  city: 'Detroit',
  region: 'Michigan',
  zip: '48201',
  country: 'United States',
  phone1: '313-555-0100',
  phone2: '313-555-0101',
  website: 'https://example.com',
  facebook: 'https://facebook.com/test',
  twitter: 'https://twitter.com/test',
  instagram: 'https://instagram.com/test',
  opentable: 'https://opentable.com/test',
  tripadvisor: 'https://tripadvisor.com/test',
  yelp: 'https://yelp.com/test',
  hours: 'Mon-Fri 9am-5pm',
  contact_text: 'Contact us for reservations',
  lat: '42.3314',
  lng: '-83.0458',
  description: 'A great restaurant',
  locality: 'Downtown',
};

const mockGooglePlace = {
  locationid: 'loc-001',
  place_id: 'ChIJN1blFLsB6IkRv5ZSZsJVHmM',
  name: 'Test Restaurant Google',
  formatted_address: '123 Test St, Detroit, MI 48201, USA',
  business_status: 'OPERATIONAL' as const,
  phone1: '313-555-9999',
  website: 'https://google-example.com',
};

function renderWithProviders(initialRoute = '/location/loc-001') {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  return render(
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/location/:locationId" element={<Location />} />
          <Route path="/locations" element={<div>Locations Page</div>} />
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>,
    {
      wrapper: ({ children }) => (
        <div>
          {children}
          <div id="route-tracker">{initialRoute}</div>
        </div>
      ),
    }
  );
}

describe('Location Page', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    window.history.pushState({}, '', '/location/loc-001');
  });

  it('should render loading state', () => {
    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders();

    expect(screen.getByText('Loading location details...')).toBeInTheDocument();
  });

  it('should render error state when location fetch fails', () => {
    const error = new Error('Failed to fetch location');
    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: undefined,
      isLoading: false,
      error,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders();

    expect(screen.getByText(/Error loading location/)).toBeInTheDocument();
    expect(screen.getByText(/Failed to fetch location/)).toBeInTheDocument();
  });

  it('should render not found state when location is not found', () => {
    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders();

    expect(screen.getByText('Location not found.')).toBeInTheDocument();
  });

  it('should render location form with data (without Google Places)', () => {
    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: mockLocation,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders();

    expect(screen.getByText('Test Restaurant')).toBeInTheDocument();
    expect(screen.getByText('Location ID: loc-001')).toBeInTheDocument();
    expect(screen.getByDisplayValue('Test Restaurant')).toBeInTheDocument();
    expect(screen.getByDisplayValue('123 Test St')).toBeInTheDocument();
    expect(screen.getByDisplayValue('Detroit')).toBeInTheDocument();

    // Should not show Google Places reference fields
    expect(screen.queryByText('Google Places Name (Reference)')).not.toBeInTheDocument();
  });

  it('should render location form with Google Places data', () => {
    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: mockLocation,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: mockGooglePlace,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders();

    // Should show Google Places reference fields
    expect(screen.getByText('Google Places Name (Reference)')).toBeInTheDocument();
    expect(screen.getByDisplayValue('Test Restaurant Google')).toBeInTheDocument();
    expect(screen.getByText('Google Business Status (Reference)')).toBeInTheDocument();
    expect(screen.getByDisplayValue('OPERATIONAL')).toBeInTheDocument();
    expect(screen.getByDisplayValue('123 Test St, Detroit, MI 48201, USA')).toBeInTheDocument();
  });

  it('should enable save and undo buttons when form is dirty', async () => {
    const user = userEvent.setup();

    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: mockLocation,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders();

    const saveButton = screen.getByRole('button', { name: /Save/i });
    const undoButton = screen.getByRole('button', { name: /Undo/i });

    // Initially disabled
    expect(saveButton).toBeDisabled();
    expect(undoButton).toBeDisabled();

    // Type in name field
    const nameInput = screen.getByDisplayValue('Test Restaurant');
    await user.clear(nameInput);
    await user.type(nameInput, 'Updated Restaurant');

    // Should be enabled after change
    await waitFor(() => {
      expect(saveButton).toBeEnabled();
      expect(undoButton).toBeEnabled();
    });
  });

  it('should reset form when undo button is clicked', async () => {
    const user = userEvent.setup();

    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: mockLocation,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders();

    const nameInput = screen.getByDisplayValue('Test Restaurant');
    await user.clear(nameInput);
    await user.type(nameInput, 'Updated Restaurant');

    expect(nameInput).toHaveValue('Updated Restaurant');

    const undoButton = screen.getByRole('button', { name: /Undo/i });
    await user.click(undoButton);

    await waitFor(() => {
      expect(nameInput).toHaveValue('Test Restaurant');
    });
  });

  it('should call update mutation when form is submitted', async () => {
    const user = userEvent.setup();

    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: mockLocation,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(apiClient.apiRequest).mockResolvedValue({
      data: { ...mockLocation, name: 'Updated Restaurant' },
    });

    renderWithProviders();

    const nameInput = screen.getByDisplayValue('Test Restaurant');
    await user.clear(nameInput);
    await user.type(nameInput, 'Updated Restaurant');

    const saveButton = screen.getByRole('button', { name: /Save/i });
    await user.click(saveButton);

    await waitFor(() => {
      expect(apiClient.apiRequest).toHaveBeenCalledWith('/location', {
        method: 'PUT',
        body: expect.stringContaining('Updated Restaurant'),
      });
    });
  });

  it('should display all form fields correctly', () => {
    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: mockLocation,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders();

    // Check all fields are present by their text labels
    expect(screen.getByText('Name')).toBeInTheDocument();
    expect(screen.getByText('Operating Status')).toBeInTheDocument();
    expect(screen.getByText('Address 1')).toBeInTheDocument();
    expect(screen.getByText('Address 2')).toBeInTheDocument();
    expect(screen.getByText('City')).toBeInTheDocument();
    expect(screen.getByText('Region')).toBeInTheDocument();
    expect(screen.getByText('ZIP')).toBeInTheDocument();
    expect(screen.getByText('Country')).toBeInTheDocument();
    expect(screen.getByText('Phone 1')).toBeInTheDocument();
    expect(screen.getByText('Website')).toBeInTheDocument();
    expect(screen.getByText('Facebook')).toBeInTheDocument();
    expect(screen.getByText('Twitter')).toBeInTheDocument();
    expect(screen.getByText('Instagram')).toBeInTheDocument();
    expect(screen.getByText('OpenTable')).toBeInTheDocument();
    expect(screen.getByText('TripAdvisor')).toBeInTheDocument();
    expect(screen.getByText('Yelp')).toBeInTheDocument();
    expect(screen.getByText('Hours')).toBeInTheDocument();
    expect(screen.getByText('Contact Text')).toBeInTheDocument();
  });

  it('should default operating status to active when null', async () => {
    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: { ...mockLocation, operatingStatus: null as any },
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders();

    const statusSelect = screen.getByDisplayValue('active') as HTMLSelectElement;

    await waitFor(() => {
      expect(statusSelect.value).toBe('active');
    });
  });

  it('should have operating status dropdown with correct options', () => {
    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: mockLocation,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders();

    const statusSelect = screen.getByDisplayValue('active');
    const options = statusSelect.querySelectorAll('option');

    expect(options).toHaveLength(3);
    expect(options[0]).toHaveValue('active');
    expect(options[1]).toHaveValue('temporarily_closed');
    expect(options[2]).toHaveValue('permanently_closed');
  });

  it('should show back to locations button', () => {
    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: mockLocation,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders();

    expect(screen.getByRole('button', { name: /Back to Locations/i })).toBeInTheDocument();
  });

  it('should handle form submission with all fields', async () => {
    const user = userEvent.setup();

    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: mockLocation,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: null,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(apiClient.apiRequest).mockResolvedValue({
      data: mockLocation,
    });

    renderWithProviders();

    // Modify multiple fields
    const nameInput = screen.getByDisplayValue('Test Restaurant');
    await user.clear(nameInput);
    await user.type(nameInput, 'New Name');

    const cityInput = screen.getByDisplayValue('Detroit');
    await user.clear(cityInput);
    await user.type(cityInput, 'Ann Arbor');

    const saveButton = screen.getByRole('button', { name: /Save/i });
    await user.click(saveButton);

    await waitFor(() => {
      expect(apiClient.apiRequest).toHaveBeenCalled();
      const callArgs = vi.mocked(apiClient.apiRequest).mock.calls[0];
      const body = JSON.parse(callArgs[1]?.body as string);
      expect(body.name).toBe('New Name');
      expect(body.city).toBe('Ann Arbor');
    });
  });

  it('should handle location with minimal fields', () => {
    const minimalLocation = {
      locationid: 'loc-002',
      name: 'Minimal Restaurant',
      operatingStatus: 'active',
      address1: '456 Test Ave',
      city: 'Ann Arbor',
      region: 'Michigan',
      zip: '48104',
      country: 'United States',
    };

    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: minimalLocation as any,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: null,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders();

    expect(screen.getByText('Minimal Restaurant')).toBeInTheDocument();
    expect(screen.getByDisplayValue('456 Test Ave')).toBeInTheDocument();
  });

  it('should hide google column when loading', () => {
    vi.mocked(useLocationModule.useLocation).mockReturnValue({
      data: mockLocation,
      isLoading: false,
      error: null,
    } as any);

    vi.mocked(useGooglePlaceModule.useGooglePlace).mockReturnValue({
      data: null,
      isLoading: true,
      error: null,
    } as any);

    renderWithProviders();

    expect(screen.queryByText('Google Places Name (Reference)')).not.toBeInTheDocument();
  });
});
