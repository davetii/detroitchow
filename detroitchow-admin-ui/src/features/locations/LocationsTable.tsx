import {
  createColumnHelper,
  flexRender,
  getCoreRowModel,
  useReactTable,
  getSortedRowModel,
} from '@tanstack/react-table';
import type { SortingState } from '@tanstack/react-table';
import { useState, useEffect } from 'react';
import { useNavigate, useBlocker } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { apiRequest } from '../../api/client';
import type { components } from '../../types/api';

type Location = components['schemas']['Location'];

const columnHelper = createColumnHelper<Location>();

interface EditableCellProps {
  value: string | undefined;
  onChange: (value: string) => void;
  isEditing: boolean;
  type?: 'text' | 'textarea';
}

function EditableCell({ value, onChange, isEditing, type = 'text' }: EditableCellProps) {
  if (!isEditing) {
    return <span>{value || '—'}</span>;
  }

  if (type === 'textarea') {
    return (
      <textarea
        value={value || ''}
        onChange={(e) => onChange(e.target.value)}
        className="w-full px-2 py-1 border border-blue-500 rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
        rows={2}
      />
    );
  }

  return (
    <input
      type="text"
      value={value || ''}
      onChange={(e) => onChange(e.target.value)}
      className="w-full px-2 py-1 border border-blue-500 rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
    />
  );
}

interface StatusCellProps {
  value: string | undefined;
  onChange: (value: string) => void;
  isEditing: boolean;
}

function StatusCell({ value, onChange, isEditing }: StatusCellProps) {
  const statusColors = {
    active: 'bg-green-100 text-green-800',
    temporarily_closed: 'bg-yellow-100 text-yellow-800',
    permanently_closed: 'bg-red-100 text-red-800',
  };

  if (!isEditing) {
    return (
      <span
        className={`px-2 py-1 rounded-full text-xs font-medium ${
          statusColors[value as keyof typeof statusColors] || 'bg-gray-100 text-gray-800'
        }`}
      >
        {value}
      </span>
    );
  }

  return (
    <select
      value={value || 'active'}
      onChange={(e) => onChange(e.target.value)}
      className="w-full px-2 py-1 border border-blue-500 rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
    >
      <option value="active">active</option>
      <option value="temporarily_closed">temporarily_closed</option>
      <option value="permanently_closed">permanently_closed</option>
    </select>
  );
}

interface LocationsTableProps {
  data: Location[];
}

export function LocationsTable({ data }: LocationsTableProps) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [sorting, setSorting] = useState<SortingState>([]);
  const [editingRowId, setEditingRowId] = useState<string | null>(null);
  const [editedData, setEditedData] = useState<Partial<Location>>({});
  const [originalData, setOriginalData] = useState<Location | null>(null);

  const hasUnsavedChanges = editingRowId !== null;

  // Block navigation when there are unsaved changes
  const blocker = useBlocker(
    ({ currentLocation, nextLocation }) =>
      hasUnsavedChanges && currentLocation.pathname !== nextLocation.pathname
  );

  // Show confirmation dialog when navigation is blocked
  useEffect(() => {
    if (blocker.state === 'blocked') {
      const confirmed = window.confirm(
        'You have unsaved changes. Are you sure you want to leave?'
      );
      if (confirmed) {
        blocker.proceed();
      } else {
        blocker.reset();
      }
    }
  }, [blocker]);

  // Prevent browser navigation (refresh/close)
  useEffect(() => {
    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
      if (hasUnsavedChanges) {
        e.preventDefault();
        e.returnValue = '';
      }
    };

    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => window.removeEventListener('beforeunload', handleBeforeUnload);
  }, [hasUnsavedChanges]);

  // Update location mutation
  const updateLocationMutation = useMutation({
    mutationFn: (location: Location) =>
      apiRequest<Location>('/location', {
        method: 'PUT',
        body: JSON.stringify(location),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['locations'] });
      setEditingRowId(null);
      setEditedData({});
      setOriginalData(null);
    },
    onError: (error) => {
      alert(`Failed to update location: ${error instanceof Error ? error.message : 'Unknown error'}`);
    },
  });

  const handleEditRow = (location: Location) => {
    setEditingRowId(location.locationid);
    setOriginalData(location);
    setEditedData({});
  };

  const handleCancelEdit = () => {
    setEditingRowId(null);
    setEditedData({});
    setOriginalData(null);
  };

  const handleSaveEdit = () => {
    if (!originalData) return;

    const updatedLocation = {
      ...originalData,
      ...editedData,
    };

    updateLocationMutation.mutate(updatedLocation);
  };

  const handleFieldChange = (field: keyof Location, value: string) => {
    setEditedData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const getCurrentValue = (location: Location, field: keyof Location): string | undefined => {
    if (editingRowId === location.locationid && field in editedData) {
      return editedData[field] as string | undefined;
    }
    return location[field] as string | undefined;
  };

  const columns = [
    // Sticky column 1: Location ID
    columnHelper.accessor('locationid', {
      header: 'Location ID',
      cell: (info) => info.getValue(),
      meta: { sticky: 'left', width: '150px' },
    }),
    // Sticky column 2: Name
    columnHelper.accessor('name', {
      header: 'Name',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'name')}
            onChange={(value) => handleFieldChange('name', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { sticky: 'left', width: '200px', leftOffset: '150px' },
    }),
    columnHelper.accessor('description', {
      header: 'Description',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'description')}
            onChange={(value) => handleFieldChange('description', value)}
            isEditing={isEditing}
            type="textarea"
          />
        );
      },
      meta: { width: '300px' },
    }),
    columnHelper.accessor('operatingStatus', {
      header: 'Status',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <StatusCell
            value={getCurrentValue(location, 'operatingStatus')}
            onChange={(value) => handleFieldChange('operatingStatus', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { width: '180px' },
    }),
    columnHelper.accessor('address1', {
      header: 'Address 1',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'address1')}
            onChange={(value) => handleFieldChange('address1', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { width: '200px' },
    }),
    columnHelper.accessor('address2', {
      header: 'Address 2',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'address2')}
            onChange={(value) => handleFieldChange('address2', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { width: '200px' },
    }),
    columnHelper.accessor('city', {
      header: 'City',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'city')}
            onChange={(value) => handleFieldChange('city', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { width: '150px' },
    }),
    columnHelper.accessor('region', {
      header: 'Region',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'region')}
            onChange={(value) => handleFieldChange('region', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { width: '100px' },
    }),
    columnHelper.accessor('zip', {
      header: 'ZIP',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'zip')}
            onChange={(value) => handleFieldChange('zip', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { width: '100px' },
    }),
    columnHelper.accessor('country', {
      header: 'Country',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'country')}
            onChange={(value) => handleFieldChange('country', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { width: '100px' },
    }),
    columnHelper.accessor('phone1', {
      header: 'Phone 1',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'phone1')}
            onChange={(value) => handleFieldChange('phone1', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { width: '150px' },
    }),
    columnHelper.accessor('phone2', {
      header: 'Phone 2',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'phone2')}
            onChange={(value) => handleFieldChange('phone2', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { width: '150px' },
    }),
    columnHelper.accessor('website', {
      header: 'Website',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'website')}
            onChange={(value) => handleFieldChange('website', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { width: '200px' },
    }),
    columnHelper.accessor('facebook', {
      header: 'Facebook',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'facebook')}
            onChange={(value) => handleFieldChange('facebook', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { width: '200px' },
    }),
    columnHelper.accessor('twitter', {
      header: 'Twitter',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'twitter')}
            onChange={(value) => handleFieldChange('twitter', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { width: '200px' },
    }),
    columnHelper.accessor('instagram', {
      header: 'Instagram',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'instagram')}
            onChange={(value) => handleFieldChange('instagram', value)}
            isEditing={isEditing}
          />
        );
      },
      meta: { width: '200px' },
    }),
    columnHelper.accessor('hours', {
      header: 'Hours',
      cell: (info) => {
        const location = info.row.original;
        const isEditing = editingRowId === location.locationid;
        return (
          <EditableCell
            value={getCurrentValue(location, 'hours')}
            onChange={(value) => handleFieldChange('hours', value)}
            isEditing={isEditing}
            type="textarea"
          />
        );
      },
      meta: { width: '200px' },
    }),
    // Sticky Actions column (right side)
    columnHelper.display({
      id: 'actions',
      header: 'Actions',
      cell: (props) => {
        const location = props.row.original;
        const isEditing = editingRowId === location.locationid;

        if (isEditing) {
          return (
            <div className="flex gap-2">
              <button
                onClick={handleSaveEdit}
                disabled={updateLocationMutation.isPending}
                className="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed text-sm font-medium"
              >
                {updateLocationMutation.isPending ? 'Saving...' : 'Save'}
              </button>
              <button
                onClick={handleCancelEdit}
                disabled={updateLocationMutation.isPending}
                className="px-3 py-1 bg-gray-600 text-white rounded hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed text-sm font-medium"
              >
                Cancel
              </button>
            </div>
          );
        }

        return (
          <div className="flex gap-2">
            <button
              onClick={() => handleEditRow(location)}
              disabled={editingRowId !== null}
              className="px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed text-sm font-medium"
            >
              Edit
            </button>
            <button
              onClick={() => navigate(`/location/${location.locationid}`)}
              className="px-3 py-1 bg-gray-600 text-white rounded hover:bg-gray-700 text-sm font-medium"
            >
              View
            </button>
          </div>
        );
      },
      meta: { sticky: 'right', width: '180px' },
    }),
  ];

  // eslint-disable-next-line react-hooks/incompatible-library
  const table = useReactTable({
    data,
    columns,
    state: {
      sorting,
    },
    onSortingChange: setSorting,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
  });

  return (
    <div className="relative overflow-x-auto">
      <table className="min-w-full bg-white border border-gray-200 rounded-lg">
        <thead className="bg-gray-50">
          {table.getHeaderGroups().map((headerGroup) => (
            <tr key={headerGroup.id}>
              {headerGroup.headers.map((header) => {
                const meta = header.column.columnDef.meta as { sticky?: string; width?: string; leftOffset?: string } | undefined;
                const isSticky = meta?.sticky;
                const stickyClass = isSticky === 'left'
                  ? 'sticky left-0 z-10 bg-gray-50'
                  : isSticky === 'right'
                  ? 'sticky right-0 z-10 bg-gray-50'
                  : '';
                const leftOffset = meta?.leftOffset || '0';
                const width = meta?.width || 'auto';

                return (
                  <th
                    key={header.id}
                    className={`px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider border-b border-gray-200 ${stickyClass}`}
                    style={{
                      left: isSticky === 'left' ? leftOffset : undefined,
                      width,
                      minWidth: width,
                    }}
                  >
                    {header.isPlaceholder ? null : (
                      <div
                        className={
                          header.column.getCanSort()
                            ? 'cursor-pointer select-none'
                            : ''
                        }
                        onClick={header.column.getToggleSortingHandler()}
                      >
                        {flexRender(
                          header.column.columnDef.header,
                          header.getContext()
                        )}
                        {{
                          asc: ' 🔼',
                          desc: ' 🔽',
                        }[header.column.getIsSorted() as string] ?? null}
                      </div>
                    )}
                  </th>
                );
              })}
            </tr>
          ))}
        </thead>
        <tbody className="divide-y divide-gray-200">
          {table.getRowModel().rows.map((row) => {
            const isEditing = editingRowId === row.original.locationid;
            return (
              <tr
                key={row.id}
                className={`transition-colors ${
                  isEditing ? 'bg-blue-50' : 'hover:bg-gray-50'
                }`}
              >
                {row.getVisibleCells().map((cell) => {
                  const meta = cell.column.columnDef.meta as { sticky?: string; width?: string; leftOffset?: string } | undefined;
                  const isSticky = meta?.sticky;
                  const stickyClass = isSticky === 'left'
                    ? `sticky left-0 z-10 ${isEditing ? 'bg-blue-50' : 'bg-white'}`
                    : isSticky === 'right'
                    ? `sticky right-0 z-10 ${isEditing ? 'bg-blue-50' : 'bg-white'}`
                    : '';
                  const leftOffset = meta?.leftOffset || '0';
                  const width = meta?.width || 'auto';

                  return (
                    <td
                      key={cell.id}
                      className={`px-6 py-4 text-sm text-gray-900 ${stickyClass}`}
                      style={{
                        left: isSticky === 'left' ? leftOffset : undefined,
                        width,
                        minWidth: width,
                      }}
                    >
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </td>
                  );
                })}
              </tr>
            );
          })}
        </tbody>
      </table>
      {data.length === 0 && (
        <div className="text-center py-8 text-gray-500">
          No locations found.
        </div>
      )}
    </div>
  );
}
