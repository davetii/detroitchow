import {
  createColumnHelper,
  flexRender,
  getCoreRowModel,
  useReactTable,
  getSortedRowModel,
} from '@tanstack/react-table';
import type { SortingState } from '@tanstack/react-table';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { components } from '../../types/api';

type Location = components['schemas']['Location'];

const columnHelper = createColumnHelper<Location>();

const columns = [
  columnHelper.accessor('locationid', {
    header: 'Location ID',
    cell: (info) => info.getValue(),
  }),
  columnHelper.accessor('name', {
    header: 'Name',
    cell: (info) => info.getValue(),
  }),
  columnHelper.accessor('description', {
    header: 'Description',
    cell: (info) => info.getValue() || '—',
  }),
  columnHelper.accessor('status', {
    header: 'Status',
    cell: (info) => {
      const status = info.getValue();
      const statusColors = {
        active: 'bg-green-100 text-green-800',
        temporarily_closed: 'bg-yellow-100 text-yellow-800',
        permanently_closed: 'bg-red-100 text-red-800',
      };
      return (
        <span
          className={`px-2 py-1 rounded-full text-xs font-medium ${
            statusColors[status as keyof typeof statusColors] || 'bg-gray-100 text-gray-800'
          }`}
        >
          {status}
        </span>
      );
    },
  }),
  columnHelper.display({
    id: 'address',
    header: 'Address',
    cell: (props) => {
      const location = props.row.original;
      const parts = [
        location.address1,
        location.address2,
        location.city,
        location.region,
        location.zip,
        location.country,
      ].filter(Boolean);
      return <span className="text-sm">{parts.join(', ') || '—'}</span>;
    },
  }),
  columnHelper.accessor('phone1', {
    header: 'Phone',
    cell: (info) => info.getValue() || '—',
  }),
  columnHelper.accessor('website', {
    header: 'Website',
    cell: (info) => {
      const website = info.getValue();
      if (!website) return '—';
      return (
        <a
          href={website}
          target="_blank"
          rel="noopener noreferrer"
          className="text-blue-600 hover:text-blue-800 underline"
          onClick={(e) => e.stopPropagation()}
        >
          Link
        </a>
      );
    },
  }),
  columnHelper.accessor('facebook', {
    header: 'Facebook',
    cell: (info) => {
      const facebook = info.getValue();
      if (!facebook) return '—';
      return (
        <a
          href={facebook}
          target="_blank"
          rel="noopener noreferrer"
          className="text-blue-600 hover:text-blue-800 underline"
          onClick={(e) => e.stopPropagation()}
        >
          Link
        </a>
      );
    },
  }),
  columnHelper.accessor('hours', {
    header: 'Hours',
    cell: (info) => info.getValue() || '—',
  }),
];

interface LocationsTableProps {
  data: Location[];
}

export function LocationsTable({ data }: LocationsTableProps) {
  const navigate = useNavigate();
  const [sorting, setSorting] = useState<SortingState>([]);

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

  const handleRowClick = (locationId: string) => {
    navigate(`/location/${locationId}`);
  };

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full bg-white border border-gray-200 rounded-lg">
        <thead className="bg-gray-50">
          {table.getHeaderGroups().map((headerGroup) => (
            <tr key={headerGroup.id}>
              {headerGroup.headers.map((header) => (
                <th
                  key={header.id}
                  className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider border-b border-gray-200"
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
              ))}
            </tr>
          ))}
        </thead>
        <tbody className="divide-y divide-gray-200">
          {table.getRowModel().rows.map((row) => (
            <tr
              key={row.id}
              onClick={() => handleRowClick(row.original.locationid)}
              className="hover:bg-gray-50 cursor-pointer transition-colors"
            >
              {row.getVisibleCells().map((cell) => (
                <td
                  key={cell.id}
                  className="px-6 py-4 whitespace-nowrap text-sm text-gray-900"
                >
                  {flexRender(cell.column.columnDef.cell, cell.getContext())}
                </td>
              ))}
            </tr>
          ))}
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
