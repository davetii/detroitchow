import { Link } from 'react-router-dom';

export function Header() {
  return (
    <header className="bg-gray-800 text-white shadow-md">
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <Link to="/" className="text-2xl font-bold hover:text-gray-300 transition-colors">
            DetroitChow Admin
          </Link>
          <nav>
            <ul className="flex space-x-6">
              <li>
                <Link to="/" className="hover:text-gray-300 transition-colors">
                  Home
                </Link>
              </li>
              <li>
                <Link to="/locations" className="hover:text-gray-300 transition-colors">
                  Locations
                </Link>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </header>
  );
}
