# Testing Guide

**⚠️ MANDATORY READING:** Comprehensive testing patterns and examples for DetroitChow Admin UI.

**Claude Code:** Read this file before writing ANY tests. The patterns here prevent common testing mistakes and ensure 80% coverage compliance.

**Key Rule:** Test files with JSX must use `.test.tsx` extension and import React.

## Table of Contents
- [Testing Philosophy](#testing-philosophy)
- [Test File Organization](#test-file-organization)
- [Testing Custom Hooks](#testing-custom-hooks)
- [Testing React Components](#testing-react-components)
- [Testing Pages](#testing-pages)
- [Mocking Patterns](#mocking-patterns)
- [Coverage Requirements](#coverage-requirements)
- [Common Testing Mistakes](#common-testing-mistakes)

---

## Testing Philosophy

### Keep Tests Simple

**The #1 Rule: Tests should be as simple as possible while still being effective.**

- Avoid complex test setup unless absolutely necessary
- Use helpers and utilities to reduce duplication
- Mock only what you need to mock
- Test behavior, not implementation details

### What to Test

✅ **DO test:**
- Component renders correctly with different props
- User interactions (clicks, form inputs, etc.)
- API data fetching and error handling
- Loading and error states
- Navigation and routing
- Form validation and submission

❌ **DON'T test:**
- Implementation details (internal state, private methods)
- Third-party library internals
- Styling/CSS classes (unless critical to functionality)
- Trivial code (getters, setters)

---

## Test File Organization

### Directory Structure

```
src/
├── hooks/
│   ├── useLocation.ts
│   ├── useGooglePlace.ts
│   └── __tests__/
│       ├── useLocation.test.tsx      # Note: .tsx for JSX in wrappers
│       └── useGooglePlace.test.tsx
├── pages/
│   ├── Location.tsx
│   ├── Locations.tsx
│   └── __tests__/
│       ├── Location.test.tsx
│       └── Locations.test.tsx
└── components/
    ├── MyComponent.tsx
    └── __tests__/
        └── MyComponent.test.tsx
```

### File Extensions

- **`.test.tsx`** - When test contains JSX (component tests, hook tests with wrappers)
- **`.test.ts`** - When test contains NO JSX (pure utility functions)

**Rule of Thumb:** If you need `<QueryClientProvider>` or any JSX, use `.tsx`

---

## Testing Custom Hooks

### Basic Hook Test Pattern

```typescript
// src/hooks/__tests__/useLocation.test.tsx
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import React from 'react';  // Required for JSX
import { useLocation } from '../useLocation';
import * as apiClient from '../../api/client';

// Mock the API client
vi.mock('../../api/client');

// Test data
const mockLocation = {
  locationid: 'loc-001',
  name: 'Test Restaurant',
  address1: '123 Test St',
  city: 'Detroit',
};

// Create a wrapper helper (MUST be in .tsx file)
function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },  // Disable retries in tests
    },
  });

  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
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
    const error = new Error('Failed to fetch');
    vi.mocked(apiClient.apiRequest).mockRejectedValue(error);

    const { result } = renderHook(() => useLocation('loc-001'), {
      wrapper: createWrapper(),
    });

    await waitFor(() => expect(result.current.isError).toBe(true));

    expect(result.current.error).toEqual(error);
  });
});
```

### Hook Testing Key Points

1. **ALWAYS clear mocks in `beforeEach`**
2. **ALWAYS use `waitFor` for async operations**
3. **Test the hook's return values, not internal implementation**
4. **Mock backend responses to match actual format** (camelCase if needed)

---

## Testing React Components

### Page Component Test Pattern

```typescript
// src/pages/__tests__/Locations.test.tsx
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Locations } from '../Locations';
import * as useLocationsModule from '../../hooks/useLocations';

// Mock the hook (not the component)
vi.mock('../../hooks/useLocations');

const mockLocations = [
  {
    locationid: 'loc-001',
    name: 'Test Restaurant 1',
    city: 'Detroit',
  },
  {
    locationid: 'loc-002',
    name: 'Test Restaurant 2',
    city: 'Ann Arbor',
  },
];

// Helper to render with providers
function renderWithProviders(component: React.ReactElement) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  });

  return render(
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        {component}
      </BrowserRouter>
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
    } as any);

    renderWithProviders(<Locations />);

    expect(screen.getByText('Loading locations...')).toBeInTheDocument();
  });

  it('should render error state', () => {
    const error = new Error('Failed to fetch');
    vi.mocked(useLocationsModule.useLocations).mockReturnValue({
      data: undefined,
      isLoading: false,
      error,
    } as any);

    renderWithProviders(<Locations />);

    expect(screen.getByText(/Error loading locations/)).toBeInTheDocument();
    expect(screen.getByText(/Failed to fetch/)).toBeInTheDocument();
  });

  it('should render locations table', () => {
    vi.mocked(useLocationsModule.useLocations).mockReturnValue({
      data: mockLocations,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders(<Locations />);

    expect(screen.getByText('Test Restaurant 1')).toBeInTheDocument();
    expect(screen.getByText('Test Restaurant 2')).toBeInTheDocument();
  });

  it('should filter locations by search', async () => {
    const user = userEvent.setup();

    vi.mocked(useLocationsModule.useLocations).mockReturnValue({
      data: mockLocations,
      isLoading: false,
      error: null,
    } as any);

    renderWithProviders(<Locations />);

    const searchInput = screen.getByPlaceholderText('Search...');
    await user.type(searchInput, 'Detroit');

    // Verify filtered results
    expect(screen.getByText('Test Restaurant 1')).toBeInTheDocument();
    expect(screen.queryByText('Test Restaurant 2')).not.toBeInTheDocument();
  });
});
```

### Form Component Test Pattern

```typescript
// Testing React Hook Form components
it('should enable save button when form is dirty', async () => {
  const user = userEvent.setup();

  renderWithProviders(<MyFormComponent />);

  const saveButton = screen.getByRole('button', { name: /Save/i });

  // Initially disabled
  expect(saveButton).toBeDisabled();

  // Type in form field
  const nameInput = screen.getByDisplayValue('Original Name');
  await user.clear(nameInput);
  await user.type(nameInput, 'New Name');

  // Should be enabled after change
  await waitFor(() => {
    expect(saveButton).toBeEnabled();
  });
});

it('should reset form when undo is clicked', async () => {
  const user = userEvent.setup();

  renderWithProviders(<MyFormComponent />);

  const nameInput = screen.getByDisplayValue('Original Name');
  await user.clear(nameInput);
  await user.type(nameInput, 'Changed Name');

  expect(nameInput).toHaveValue('Changed Name');

  const undoButton = screen.getByRole('button', { name: /Undo/i });
  await user.click(undoButton);

  await waitFor(() => {
    expect(nameInput).toHaveValue('Original Name');
  });
});
```

---

## Mocking Patterns

### Mock Custom Hooks

```typescript
// ✅ Preferred: Mock the hook module
vi.mock('../../hooks/useLocations');

// In test:
vi.mocked(useLocationsModule.useLocations).mockReturnValue({
  data: mockData,
  isLoading: false,
  error: null,
} as any);
```

### Mock API Client

```typescript
// Mock entire module
vi.mock('../../api/client');

// In test:
vi.mocked(apiClient.apiRequest).mockResolvedValue({
  data: mockData,
});

// For errors:
vi.mocked(apiClient.apiRequest).mockRejectedValue(
  new Error('API Error')
);

// For specific status codes:
import { ApiError } from '../../api/client';
vi.mocked(apiClient.apiRequest).mockRejectedValue(
  new ApiError('Not Found', 404)
);
```

### Mock Backend Responses (camelCase handling)

```typescript
// Backend returns camelCase, but hook maps to snake_case
it('should map backend response correctly', async () => {
  // Mock backend's actual camelCase response
  vi.mocked(apiClient.apiRequest).mockResolvedValue({
    data: {
      locationid: 'loc-001',
      placeId: 'ChIJ123',  // Backend: camelCase
      formattedAddress: '123 Main St',
      businessStatus: 'OPERATIONAL',
    },
  });

  const { result } = renderHook(() => useGooglePlace('loc-001'), {
    wrapper: createWrapper(),
  });

  await waitFor(() => expect(result.current.isSuccess).toBe(true));

  // Verify hook returns snake_case
  expect(result.current.data).toMatchObject({
    place_id: 'ChIJ123',  // Hook returns: snake_case
    formatted_address: '123 Main St',
    business_status: 'OPERATIONAL',
  });
});
```

---

## Coverage Requirements

### Required Thresholds

All code must meet these minimum coverage levels:

- **Lines:** 80%
- **Functions:** 80%
- **Branches:** 80%
- **Statements:** 80%

Configured in `vitest.config.ts`:

```typescript
coverage: {
  thresholds: {
    lines: 80,
    functions: 80,
    branches: 80,
    statements: 80,
  },
}
```

### Running Coverage

```bash
# Run tests with coverage
npm run test -- --coverage --run

# View coverage report
open coverage/index.html
```

### Coverage Strategies

**To meet 80% coverage:**

1. **Test all render paths**
   - Loading state
   - Error state
   - Success state (with data)
   - Success state (empty data)

2. **Test all user interactions**
   - Button clicks
   - Form inputs
   - Navigation

3. **Test conditional logic**
   - If/else branches
   - Ternary operators
   - Optional chaining

**Example:**
```typescript
// This code has 3 branches to test:
const displayValue = googlePlace
  ? googlePlace.name     // Branch 1: has data
  : undefined;           // Branch 2: no data

// Tests needed:
it('shows google place name when data exists', () => { ... });
it('handles missing google place data', () => { ... });
```

---

## Common Testing Mistakes

### ❌ MISTAKE 1: Using wrong file extension

```typescript
// ❌ Wrong - useMyHook.test.ts (can't use JSX)
const wrapper = ({ children }) => (
  <QueryClientProvider>  // ERROR: JSX not allowed in .ts
    {children}
  </QueryClientProvider>
);

// ✅ Correct - useMyHook.test.tsx
import React from 'react';
const wrapper = ({ children }: { children: React.ReactNode }) => (
  <QueryClientProvider>
    {children}
  </QueryClientProvider>
);
```

### ❌ MISTAKE 2: Testing implementation details

```typescript
// ❌ Wrong - Testing internal state
expect(component.state.count).toBe(5);

// ✅ Correct - Testing output/behavior
expect(screen.getByText('Count: 5')).toBeInTheDocument();
```

### ❌ MISTAKE 3: Not waiting for async updates

```typescript
// ❌ Wrong - Not waiting
const { result } = renderHook(() => useMyHook());
expect(result.current.data).toBeDefined();  // May fail!

// ✅ Correct - Using waitFor
const { result } = renderHook(() => useMyHook());
await waitFor(() => {
  expect(result.current.data).toBeDefined();
});
```

### ❌ MISTAKE 4: Using getByLabelText with React Hook Form

```typescript
// ❌ May fail - React Hook Form doesn't use htmlFor
expect(screen.getByLabelText('Name')).toBeInTheDocument();

// ✅ Correct - Use getByText for label, getByDisplayValue for input
expect(screen.getByText('Name')).toBeInTheDocument();
expect(screen.getByDisplayValue('John Doe')).toBeInTheDocument();

// ✅ Or use getByRole
expect(screen.getByRole('textbox', { name: /name/i }));
```

### ❌ MISTAKE 5: Not clearing mocks

```typescript
// ❌ Wrong - Mocks persist between tests
describe('My Tests', () => {
  it('test 1', () => {
    vi.mocked(myFunc).mockReturnValue('A');
    // test...
  });

  it('test 2', () => {
    // Still mocked from test 1! ❌
  });
});

// ✅ Correct - Clear in beforeEach
describe('My Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('test 1', () => { ... });
  it('test 2', () => { ... });
});
```

### ❌ MISTAKE 6: Mocking backend wrong format

```typescript
// ❌ Wrong - Mocking snake_case when backend returns camelCase
vi.mocked(apiClient.apiRequest).mockResolvedValue({
  data: {
    place_id: 'ChIJ123',  // Backend actually returns placeId!
  },
});

// ✅ Correct - Mock backend's actual format
vi.mocked(apiClient.apiRequest).mockResolvedValue({
  data: {
    placeId: 'ChIJ123',  // Backend returns camelCase
    formattedAddress: '123 Main St',
    businessStatus: 'OPERATIONAL',
  },
});
// Hook should map this to snake_case internally
```

---

## Quick Testing Checklist

Before submitting tests:

- [ ] File extension is `.test.tsx` if JSX is used
- [ ] All tests import `React` if they contain JSX
- [ ] `vi.clearAllMocks()` in `beforeEach`
- [ ] Using `waitFor` for async operations
- [ ] Mocking backend responses in actual format (camelCase)
- [ ] Testing behavior, not implementation
- [ ] All user interactions tested
- [ ] Loading, error, and success states covered
- [ ] Coverage meets 80% threshold
- [ ] Tests are simple and easy to understand

