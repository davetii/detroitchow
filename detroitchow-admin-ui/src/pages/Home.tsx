import { Link } from 'react-router-dom';

export function Home() {
  return (
    <div className="max-w-4xl mx-auto">
      <div className="bg-white rounded-lg shadow-md p-8">
        <h1 className="text-4xl font-bold text-gray-800 mb-4">
          Welcome to DetroitChow Admin
        </h1>
        <p className="text-lg text-gray-600 mb-8">
          Manage restaurant locations and menus for Metro Detroit's premier dining guide.
        </p>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="border border-gray-200 rounded-lg p-6 hover:shadow-lg transition-shadow">
            <h2 className="text-2xl font-semibold text-gray-800 mb-3">
              Locations
            </h2>
            <p className="text-gray-600 mb-4">
              View, create, and manage restaurant locations across Metro Detroit.
            </p>
            <Link
              to="/locations"
              className="inline-block bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 transition-colors"
            >
              Manage Locations
            </Link>
          </div>

          <div className="border border-gray-200 rounded-lg p-6 hover:shadow-lg transition-shadow">
            <h2 className="text-2xl font-semibold text-gray-800 mb-3">
              System Info
            </h2>
            <div className="text-gray-600 space-y-2">
              <p><strong>API Base URL:</strong> http://localhost:8080/api/v1</p>
              <p><strong>Database:</strong> PostgreSQL (detroitchow)</p>
              <p><strong>Environment:</strong> Development</p>
            </div>
          </div>
        </div>

        <div className="mt-8 p-6 bg-blue-50 rounded-lg">
          <h3 className="text-xl font-semibold text-gray-800 mb-2">
            Quick Start
          </h3>
          <ul className="list-disc list-inside text-gray-600 space-y-1">
            <li>Navigate to Locations to view all restaurant entries</li>
            <li>Add new locations using the create form</li>
            <li>Manage menus for each location</li>
            <li>Update social media links and contact information</li>
          </ul>
        </div>
      </div>
    </div>
  );
}
