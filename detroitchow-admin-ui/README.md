# DetroitChow Admin UI

Admin interface for managing DetroitChow restaurant locations and menus.

## Tech Stack

- **React 18+** with TypeScript
- **Vite** - Build tool
- **React Router v6** - Routing
- **TanStack Query** - Server state management
- **TanStack Table** - Data tables
- **React Hook Form** - Form handling
- **Zod** - Schema validation
- **Tailwind CSS** - Styling
- **Vitest** - Testing
- **React Testing Library** - Component testing

## Prerequisites

- Node.js 18+
- npm or yarn
- DetroitChow Admin API running on `http://localhost:8080`

## Getting Started

### Install Dependencies

```bash
npm install
```

### Environment Configuration

Copy the example environment file:

```bash
cp .env.example .env
```

Edit `.env` to configure your API base URL (defaults to `http://localhost:8080/api/v1`).

### Generate TypeScript Types from OpenAPI

Generate TypeScript types from the backend OpenAPI specification:

```bash
npm run generate-types
```

This will create `src/types/api.ts` with type-safe definitions matching your backend API.

### Development Server

Start the development server:

```bash
npm run dev
```

The app will be available at `http://localhost:5173`.

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint
- `npm run test` - Run tests with Vitest
- `npm run test:ui` - Run tests with Vitest UI
- `npm run generate-types` - Generate TypeScript types from OpenAPI spec

## Project Structure

```
src/
├── api/                    # API client and service functions
│   └── client.ts          # Base fetch wrapper
├── components/
│   ├── layout/            # Layout components (Header, Footer, Layout)
│   └── ui/                # Reusable UI components
├── features/
│   ├── locations/         # Location-specific components and logic
│   └── menus/            # Menu-specific components and logic
├── hooks/                 # Custom React hooks
├── lib/                   # Utility functions and configurations
│   └── queryClient.ts    # TanStack Query configuration
├── pages/                 # Page components
│   ├── Home.tsx
│   └── Locations.tsx
├── test/                  # Test utilities and setup
├── types/                 # TypeScript type definitions
│   └── api.ts            # Generated from OpenAPI spec
├── App.tsx               # Main app component
├── main.tsx              # Entry point
└── index.css             # Global styles (Tailwind imports)
```

## API Integration

The app connects to the DetroitChow Admin API at `http://localhost:8080/api/v1` by default.

### Type-Safe API Calls

Use the `apiRequest` function from `src/api/client.ts` for type-safe API calls:

```typescript
import { apiRequest } from './api/client';
import type { Location } from './types/api';

// GET request
const locations = await apiRequest<Location[]>('/locations');

// POST request
const newLocation = await apiRequest<Location>('/location', {
  method: 'POST',
  body: JSON.stringify(locationData),
});
```

### TanStack Query Integration

Use TanStack Query for data fetching with caching and automatic refetching:

```typescript
import { useQuery } from '@tanstack/react-query';
import { apiRequest } from '../api/client';

function useLocations() {
  return useQuery({
    queryKey: ['locations'],
    queryFn: () => apiRequest('/locations'),
  });
}
```

## Testing

Run tests:

```bash
npm test
```

Run tests with UI:

```bash
npm run test:ui
```

## Building for Production

Build the project:

```bash
npm run build
```

Preview the production build:

```bash
npm run preview
```

## Development Workflow

1. Ensure the backend API is running
2. Generate types from OpenAPI spec: `npm run generate-types`
3. Start development server: `npm run dev`
4. Make changes and test
5. Run tests: `npm test`
6. Build for production: `npm run build`

## Features

- Modern React with TypeScript
- Type-safe API integration with OpenAPI-generated types
- Responsive layout with Tailwind CSS
- Client-side routing with React Router
- Optimized data fetching with TanStack Query
- Form handling with React Hook Form
- Schema validation with Zod
- Comprehensive testing setup

## Next Steps

- Implement location list view with TanStack Table
- Add location create/edit forms with React Hook Form
- Implement menu management
- Add search and filtering
- Implement pagination
- Add error boundaries
- Add loading states and skeletons
