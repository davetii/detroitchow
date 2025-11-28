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
    operatingStatus: 'active',
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
    operatingStatus: 'temporarily_closed',
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
        operatingStatus: 'active',
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

  it('should trigger onChange handlers for social media and hours fields', async () => {
    const locationWithAllFields: Location[] = [
      {
        locationid: 'loc-004',
        name: 'Full Restaurant',
        operatingStatus: 'active',
        address1: '100 Complete St',
        city: 'Detroit',
        region: 'Michigan',
        zip: '48201',
        country: 'United States',
        facebook: 'https://facebook.com/full',
        twitter: 'https://twitter.com/full',
        instagram: 'https://instagram.com/full',
        hours: 'Mon-Fri: 9am-5pm',
      },
    ];

    const user = userEvent.setup();
    renderWithProviders(<LocationsTable data={locationWithAllFields} />);

    // Click edit
    const editButton = screen.getByRole('button', { name: 'Edit' });
    await user.click(editButton);

    // Type into each field to trigger onChange handlers - this exercises the uncovered lines
    await user.type(screen.getByDisplayValue('https://facebook.com/full'), 'x');
    await user.type(screen.getByDisplayValue('https://twitter.com/full'), 'y');
    await user.type(screen.getByDisplayValue('https://instagram.com/full'), 'z');
    await user.type(screen.getByDisplayValue('Mon-Fri: 9am-5pm'), '!');

    // If the onChange handlers executed without errors, the test passes
    // Save and Cancel buttons should still be visible
    expect(screen.getByRole('button', { name: 'Save' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Cancel' })).toBeInTheDocument();
  });

  it('should allow editing textarea fields like description', async () => {
    const user = userEvent.setup();
    renderWithProviders(<LocationsTable data={mockLocations} />);

    const editButtons = screen.getAllByRole('button', { name: 'Edit' });
    await user.click(editButtons[0]);

    // Find description textarea
    const descriptionTextarea = screen.getByDisplayValue('A test restaurant');
    expect(descriptionTextarea.tagName).toBe('TEXTAREA');
  });

  it('should disable other Edit buttons when one row is being edited', async () => {
    const user = userEvent.setup();
    renderWithProviders(<LocationsTable data={mockLocations} />);

    const editButtons = screen.getAllByRole('button', { name: 'Edit' });
    await user.click(editButtons[0]);

    // Re-query for all edit buttons to get updated disabled state
    const allButtons = screen.getAllByRole('button');
    const editButtonsAfter = allButtons.filter(btn => btn.textContent === 'Edit');

    // The second edit button should be disabled
    expect(editButtonsAfter[0]).toBeDisabled();
  });

  it('should cancel editing and revert changes when Cancel is clicked', async () => {
    const user = userEvent.setup();
    renderWithProviders(<LocationsTable data={mockLocations} />);

    const editButtons = screen.getAllByRole('button', { name: 'Edit' });
    await user.click(editButtons[0]);

    // Make a change to the name field
    const nameInput = screen.getByDisplayValue('Test Restaurant 1');
    await user.clear(nameInput);
    await user.type(nameInput, 'Changed Name');

    // Click cancel
    const cancelButton = screen.getByRole('button', { name: 'Cancel' });
    await user.click(cancelButton);

    // Should no longer be in edit mode
    expect(screen.queryByRole('button', { name: 'Save' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Cancel' })).not.toBeInTheDocument();

    // Name should be back to original
    expect(screen.getByText('Test Restaurant 1')).toBeInTheDocument();
  });

  it('should render editable inputs for all address fields when editing', async () => {
    const user = userEvent.setup();
    renderWithProviders(<LocationsTable data={mockLocations} />);

    const editButtons = screen.getAllByRole('button', { name: 'Edit' });
    await user.click(editButtons[0]);

    // Verify all address fields become editable inputs
    const address1Input = screen.getByDisplayValue('123 Test St');
    expect(address1Input.tagName).toBe('INPUT');

    const cityInput = screen.getByDisplayValue('Detroit');
    expect(cityInput.tagName).toBe('INPUT');

    const regionInput = screen.getByDisplayValue('Michigan');
    expect(regionInput.tagName).toBe('INPUT');

    const zipInput = screen.getByDisplayValue('48201');
    expect(zipInput.tagName).toBe('INPUT');

    const countryInput = screen.getByDisplayValue('United States');
    expect(countryInput.tagName).toBe('INPUT');
  });

  it('should render editable inputs for phone and website fields when editing', async () => {
    const user = userEvent.setup();
    renderWithProviders(<LocationsTable data={mockLocations} />);

    const editButtons = screen.getAllByRole('button', { name: 'Edit' });
    await user.click(editButtons[0]);

    // Type into phone1, website, and country to trigger onChange handlers
    await user.type(screen.getByDisplayValue('313-555-0100'), '1');
    await user.type(screen.getByDisplayValue('https://test1.com'), 'x');
    await user.type(screen.getByDisplayValue('United States'), 'x');

    // Verify inputs still exist (handlers executed without errors)
    expect(screen.getByRole('button', { name: 'Save' })).toBeInTheDocument();
  });

  it('should handle editing operating status dropdown', async () => {
    const user = userEvent.setup();
    renderWithProviders(<LocationsTable data={mockLocations} />);

    const editButtons = screen.getAllByRole('button', { name: 'Edit' });
    await user.click(editButtons[0]);

    // Find the status select dropdown
    const statusSelect = screen.getByDisplayValue('active');
    expect(statusSelect.tagName).toBe('SELECT');

    // Change to temporarily_closed
    await user.selectOptions(statusSelect, 'temporarily_closed');

    // Verify change
    expect(statusSelect).toHaveValue('temporarily_closed');
  });

  it('should handle editing phone2 field when present', async () => {
    const locationWithPhone2: Location[] = [
      {
        locationid: 'loc-005',
        name: 'Restaurant with Two Phones',
        operatingStatus: 'active',
        address1: '200 Main St',
        city: 'Detroit',
        region: 'Michigan',
        zip: '48201',
        country: 'United States',
        phone1: '313-555-0001',
        phone2: '313-555-0002',
      },
    ];

    const user = userEvent.setup();
    renderWithProviders(<LocationsTable data={locationWithPhone2} />);

    const editButton = screen.getByRole('button', { name: 'Edit' });
    await user.click(editButton);

    // Type into phone2 to trigger onChange handler
    await user.type(screen.getByDisplayValue('313-555-0002'), '9');

    // Verify handler executed without errors
    expect(screen.getByRole('button', { name: 'Save' })).toBeInTheDocument();
  });
});
