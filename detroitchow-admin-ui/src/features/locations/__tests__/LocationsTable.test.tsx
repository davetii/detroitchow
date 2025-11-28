import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { LocationsTable } from '../LocationsTable';
import type { components } from '../../../types/api';

type Location = components['schemas']['Location'];

const mockNavigate = vi.fn();
const mockBlocker = { state: 'unblocked', proceed: vi.fn(), reset: vi.fn() };

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useBlocker: () => mockBlocker,
  };
});

const mockLocations: Location[] = [
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
    website: 'https://test1.com',
    facebook: 'https://facebook.com/test1',
  },
  {
    locationid: 'loc-002',
    name: 'Test Restaurant 2',
    description: 'Another test restaurant',
    status: 'temporarily_closed',
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
      mutations: { retry: false },
    },
  });

  return render(
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>{component}</BrowserRouter>
    </QueryClientProvider>
  );
}

describe('LocationsTable', () => {
  it('should render table with location data', () => {
    renderWithProviders(<LocationsTable data={mockLocations} />);

    expect(screen.getByText('Test Restaurant 1')).toBeInTheDocument();
    expect(screen.getByText('Test Restaurant 2')).toBeInTheDocument();
    expect(screen.getByText('loc-001')).toBeInTheDocument();
    expect(screen.getByText('loc-002')).toBeInTheDocument();
  });

  it('should display empty state when no data', () => {
    renderWithProviders(<LocationsTable data={[]} />);

    expect(screen.getByText('No locations found.')).toBeInTheDocument();
  });

  it('should render status badges with correct colors', () => {
    renderWithProviders(<LocationsTable data={mockLocations} />);

    const activeStatus = screen.getByText('active');
    const closedStatus = screen.getByText('temporarily_closed');

    expect(activeStatus).toHaveClass('bg-green-100', 'text-green-800');
    expect(closedStatus).toHaveClass('bg-yellow-100', 'text-yellow-800');
  });

  it('should render Edit and View buttons', () => {
    renderWithProviders(<LocationsTable data={mockLocations} />);

    const editButtons = screen.getAllByRole('button', { name: 'Edit' });
    const viewButtons = screen.getAllByRole('button', { name: 'View' });

    expect(editButtons).toHaveLength(2);
    expect(viewButtons).toHaveLength(2);
  });

  it('should navigate to detail page when View button clicked', async () => {
    const user = userEvent.setup();

    renderWithProviders(<LocationsTable data={mockLocations} />);

    const viewButtons = screen.getAllByRole('button', { name: 'View' });
    await user.click(viewButtons[0]);

    expect(mockNavigate).toHaveBeenCalledWith('/location/loc-001');
  });

  it('should display em dash for missing fields', () => {
    const locationWithMissingFields: Location[] = [
      {
        locationid: 'loc-003',
        name: 'Minimal Location',
        status: 'active',
        address1: '789 Oak St',
        city: 'Detroit',
        region: 'Michigan',
        zip: '48201',
        country: 'United States',
      },
    ];

    renderWithProviders(<LocationsTable data={locationWithMissingFields} />);

    const emDashes = screen.getAllByText('—');
    expect(emDashes.length).toBeGreaterThan(0);
  });

  it('should render all table headers', () => {
    renderWithProviders(<LocationsTable data={mockLocations} />);

    expect(screen.getByText('Location ID')).toBeInTheDocument();
    expect(screen.getByText('Name')).toBeInTheDocument();
    expect(screen.getByText('Description')).toBeInTheDocument();
    expect(screen.getByText('Status')).toBeInTheDocument();
    expect(screen.getByText('Actions')).toBeInTheDocument();
  });

  it('should show Save and Cancel buttons when Edit is clicked', async () => {
    const user = userEvent.setup();

    renderWithProviders(<LocationsTable data={mockLocations} />);

    const editButtons = screen.getAllByRole('button', { name: 'Edit' });
    await user.click(editButtons[0]);

    expect(screen.getByRole('button', { name: 'Save' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Cancel' })).toBeInTheDocument();
  });

  it('should highlight row when editing', async () => {
    const user = userEvent.setup();

    renderWithProviders(<LocationsTable data={mockLocations} />);

    const editButtons = screen.getAllByRole('button', { name: 'Edit' });
    await user.click(editButtons[0]);

    // When editing, the name becomes an input field, so find the row by the locationid which doesn't change
    const firstRow = screen.getByText('loc-001').closest('tr');
    expect(firstRow).toHaveClass('bg-blue-50');
  });
});
