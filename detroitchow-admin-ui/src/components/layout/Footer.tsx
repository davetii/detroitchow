export function Footer() {
  return (
    <footer className="bg-gray-800 text-white mt-auto">
      <div className="container mx-auto px-4 py-6">
        <div className="text-center text-gray-400">
          <p>&copy; {new Date().getFullYear()} DetroitChow. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
}
