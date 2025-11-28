import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { LocationsTable } from '../LocationsTable';
import type { components } from '../../../types/api';

type Location = components['schemas']['Location'];

const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
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

function renderWithRouter(component: React.ReactElement) {
  return render(<BrowserRouter>{component}</BrowserRouter>);
}

describe('LocationsTable', () => {
  it('should render table with location data', () => {
    renderWithRouter(<LocationsTable data={mockLocations} />);

    expect(screen.getByText('Test Restaurant 1')).toBeInTheDocument();
    expect(screen.getByText('Test Restaurant 2')).toBeInTheDocument();
    expect(screen.getByText('loc-001')).toBeInTheDocument();
    expect(screen.getByText('loc-002')).toBeInTheDocument();
  });

  it('should display empty state when no data', () => {
    renderWithRouter(<LocationsTable data={[]} />);

    expect(screen.getByText('No locations found.')).toBeInTheDocument();
  });

  it('should render status badges with correct colors', () => {
    renderWithRouter(<LocationsTable data={mockLocations} />);

    const activeStatus = screen.getByText('active');
    const closedStatus = screen.getByText('temporarily_closed');

    expect(activeStatus).toHaveClass('bg-green-100', 'text-green-800');
    expect(closedStatus).toHaveClass('bg-yellow-100', 'text-yellow-800');
  });

  it('should render consolidated address', () => {
    renderWithRouter(<LocationsTable data={mockLocations} />);

    expect(screen.getByText(/123 Test St, Detroit, Michigan, 48201, United States/)).toBeInTheDocument();
    expect(screen.getByText(/456 Main St, Ann Arbor, Michigan, 48104, United States/)).toBeInTheDocument();
  });

  it('should render website links', () => {
    renderWithRouter(<LocationsTable data={mockLocations} />);

    const links = screen.getAllByRole('link', { name: 'Link' });
    expect(links.length).toBeGreaterThan(0);
    expect(links[0]).toHaveAttribute('href', 'https://test1.com');
    expect(links[0]).toHaveAttribute('target', '_blank');
  });

  it('should navigate to detail page when row clicked', async () => {
    const user = userEvent.setup();

    renderWithRouter(<LocationsTable data={mockLocations} />);

    const firstRow = screen.getByText('Test Restaurant 1').closest('tr');
    expect(firstRow).toBeInTheDocument();

    await user.click(firstRow!);

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

    renderWithRouter(<LocationsTable data={locationWithMissingFields} />);

    const emDashes = screen.getAllByText('—');
    expect(emDashes.length).toBeGreaterThan(0);
  });

  it('should render all table headers', () => {
    renderWithRouter(<LocationsTable data={mockLocations} />);

    expect(screen.getByText('Location ID')).toBeInTheDocument();
    expect(screen.getByText('Name')).toBeInTheDocument();
    expect(screen.getByText('Description')).toBeInTheDocument();
    expect(screen.getByText('Status')).toBeInTheDocument();
    expect(screen.getByText('Address')).toBeInTheDocument();
    expect(screen.getByText('Phone')).toBeInTheDocument();
    expect(screen.getByText('Website')).toBeInTheDocument();
    expect(screen.getByText('Facebook')).toBeInTheDocument();
    expect(screen.getByText('Hours')).toBeInTheDocument();
  });

  it('should apply hover styles to rows', () => {
    renderWithRouter(<LocationsTable data={mockLocations} />);

    const firstRow = screen.getByText('Test Restaurant 1').closest('tr');
    expect(firstRow).toHaveClass('hover:bg-gray-50', 'cursor-pointer');
  });
});
