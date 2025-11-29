# Frontend Development Patterns & Standards

**⚠️ MANDATORY READING:** This document defines non-negotiable patterns for React/TypeScript development in the DetroitChow Admin UI.

**Claude Code:** You MUST read this file before creating any React components, hooks, or tests. The patterns here prevent common mistakes that cause build failures and test errors.

**Key Rule:** NEVER create `.jsx` or `.js` files. ALWAYS use `.tsx` or `.ts`.

## Table of Contents
- [File Extensions & TypeScript](#file-extensions--typescript)
- [Component Patterns](#component-patterns)
- [Custom Hooks](#custom-hooks)
- [API Integration](#api-integration)
- [Testing Patterns](#testing-patterns)
- [Common Pitfalls](#common-pitfalls)

---

## File Extensions & TypeScript

### ✅ ALWAYS Use TypeScript

**NEVER create `.jsx` or `.js` files in this project. ALWAYS use `.tsx` or `.ts`.**

```
✅ Correct:
src/components/MyComponent.tsx
src/hooks/useMyHook.ts
src/utils/helpers.ts

❌ Wrong:
src/components/MyComponent.jsx   # Never use .jsx
src/hooks/useMyHook.js            # Never use .js
```

### File Extension Rules

- **`.tsx`** - For files containing JSX (React components, test files with JSX)
- **`.ts`** - For files without JSX (utilities, types, plain hooks)

**Test files MUST use `.tsx` if they contain JSX:**
```typescript
// ✅ Correct: src/hooks/__tests__/useMyHook.test.tsx
import React from 'react';  // Required for JSX
import { renderHook } from '@testing-library/react';

// ❌ Wrong: src/hooks/__tests__/useMyHook.test.ts
// This will fail if you use <QueryClientProvider> etc.
```

---

## Component Patterns

### Component Structure

Always export function components (not arrow functions for top-level components):

```typescript
// ✅ Preferred
export function MyComponent() {
  return <div>Hello</div>;
}

// ⚠️ Acceptable but less preferred
export const MyComponent = () => {
  return <div>Hello</div>;
};
```

### Props Type Definition

Always define props as an interface:

```typescript
interface MyComponentProps {
  title: string;
  count: number;
  onSave?: () => void;  // Optional props use ?
}

export function MyComponent({ title, count, onSave }: MyComponentProps) {
  // Component logic
}
```

### Component Organization

```typescript
// 1. Imports
import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import type { components } from '../types/api';

// 2. Type definitions
type MyData = components['schemas']['MyData'];

interface MyComponentProps {
  id: string;
}

// 3. Component
export function MyComponent({ id }: MyComponentProps) {
  // 3a. Hooks (always at top, never conditional)
  const [state, setState] = useState('');
  const { data, isLoading } = useQuery({ /* ... */ });

  // 3b. Event handlers
  const handleClick = () => {
    // ...
  };

  // 3c. Computed values
  const displayValue = data ? data.name : 'N/A';

  // 3d. Render
  return <div>{displayValue}</div>;
}
```

---

## Custom Hooks

### Hook Naming

- **MUST** start with `use`
- **MUST** be defined in `src/hooks/`
- **MUST** have corresponding test file

```
✅ src/hooks/useLocation.ts
✅ src/hooks/__tests__/useLocation.test.tsx
```

### Hook Pattern for Data Fetching

```typescript
import { useQuery } from '@tanstack/react-query';
import { apiRequest } from '../api/client';
import type { components } from '../types/api';

type Location = components['schemas']['Location'];

interface LocationResponse {
  data?: Location;
}

export function useLocation(locationId: string | undefined) {
  return useQuery<Location | null>({
    queryKey: ['location', locationId],
    queryFn: async () => {
      const response = await apiRequest<LocationResponse>(`/location/${locationId}`);
      return response.data || null;  // NEVER return undefined
    },
    enabled: !!locationId,  // Only run if locationId exists
  });
}
```

### Key Hook Rules

1. **NEVER return `undefined` from queryFn** - Use `null` instead
2. **ALWAYS use `enabled` for conditional queries**
3. **ALWAYS type the query result** - `useQuery<MyType | null>`
4. **ALWAYS handle API response mapping** if backend doesn't match spec

---

## API Integration

### Backend Response Mapping

**IMPORTANT:** The backend returns camelCase but OpenAPI spec defines snake_case. Always map responses:

```typescript
// Backend returns: { placeId, formattedAddress, businessStatus }
// Spec expects: { place_id, formatted_address, business_status }

interface BackendResponse {
  data?: {
    placeId: string;
    formattedAddress: string;
    businessStatus: string;
  };
}

function mapResponse(backendData: BackendResponse['data']): SpecType | null {
  if (!backendData) return null;

  return {
    place_id: backendData.placeId,
    formatted_address: backendData.formattedAddress,
    business_status: backendData.businessStatus,
  };
}

export function useMyData(id: string | undefined) {
  return useQuery({
    queryKey: ['mydata', id],
    queryFn: async () => {
      const response = await apiRequest<BackendResponse>(`/mydata/${id}`);
      return mapResponse(response.data);
    },
    enabled: !!id,
  });
}
```

### API Error Handling

```typescript
import { ApiError } from '../api/client';

// Handle specific error codes
try {
  const response = await apiRequest('/endpoint');
  return response.data || null;
} catch (error) {
  if (error instanceof ApiError && error.status === 404) {
    return null;  // Not found is acceptable
  }
  throw error;  // Re-throw other errors
}
```

---

## Testing Patterns

See [TESTING.md](./TESTING.md) for comprehensive testing guide.

### Quick Reference

```typescript
// ✅ Hook tests need .tsx extension for JSX
// src/hooks/__tests__/useMyHook.test.tsx
import React from 'react';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

// Create wrapper helper
function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });

  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
}

// Use in tests
const { result } = renderHook(() => useMyHook('id'), {
  wrapper: createWrapper(),
});
```

---

## Common Pitfalls

### ❌ AVOID: Using .js/.jsx extensions
```javascript
// ❌ NEVER DO THIS
// MyComponent.jsx
export const MyComponent = () => <div>Hello</div>;
```

```typescript
// ✅ ALWAYS DO THIS
// MyComponent.tsx
export function MyComponent() {
  return <div>Hello</div>;
}
```

### ❌ AVOID: Returning undefined from queries
```typescript
// ❌ Wrong - TanStack Query rejects undefined
queryFn: async () => {
  const response = await apiRequest('/endpoint');
  return response.data;  // Could be undefined!
}

// ✅ Correct - Always return a value
queryFn: async () => {
  const response = await apiRequest('/endpoint');
  return response.data || null;
}
```

### ❌ AVOID: Test files without React import when using JSX
```typescript
// ❌ Wrong - useGooglePlace.test.ts (note .ts not .tsx)
import { renderHook } from '@testing-library/react';
// Missing: import React from 'react';

return ({ children }) => (
  <QueryClientProvider client={queryClient}>  // ERROR!
    {children}
  </QueryClientProvider>
);

// ✅ Correct - useGooglePlace.test.tsx (note .tsx)
import React from 'react';
import { renderHook } from '@testing-library/react';

function createWrapper() {
  const queryClient = new QueryClient({ /* ... */ });
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
}
```

### ❌ AVOID: Conditional hooks
```typescript
// ❌ Wrong
function MyComponent({ showData }: Props) {
  if (showData) {
    const data = useQuery({ /* ... */ });  // ERROR: Conditional hook
  }
}

// ✅ Correct - Use enabled option
function MyComponent({ showData }: Props) {
  const { data } = useQuery({
    queryKey: ['data'],
    queryFn: fetchData,
    enabled: showData,  // Conditionally enable
  });
}
```

### ❌ AVOID: Directly using getByLabelText with React Hook Form
```typescript
// ❌ May fail - React Hook Form doesn't use htmlFor
expect(screen.getByLabelText('Name')).toBeInTheDocument();

// ✅ Use getByText for labels, getByDisplayValue for inputs
expect(screen.getByText('Name')).toBeInTheDocument();
expect(screen.getByDisplayValue('John')).toBeInTheDocument();
```

---

## Quick Checklist

Before creating any new React component or hook:

- [ ] File extension is `.tsx` (if JSX) or `.ts` (if no JSX)
- [ ] TypeScript interfaces defined for all props
- [ ] Using `type` from `components['schemas']['X']` for API types
- [ ] Custom hooks start with `use` and return non-undefined values
- [ ] Test file extension matches source (`.test.tsx` if JSX needed)
- [ ] All tests import `React` if they contain JSX
- [ ] Backend response mapped to match OpenAPI spec naming
- [ ] Coverage target of 80% will be met

