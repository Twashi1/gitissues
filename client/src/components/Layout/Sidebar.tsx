import { Link } from 'react-router-dom'

export default function Sidebar() {
  return (
    <aside className="flex-1 flex flex-col">
      <nav className="flex-1 flex flex-col justify-start space-y-1">
        <Link to="/" className="items-center px-4 py-1.5 text-sm font-medium hover:bg-slate-800">
          Dashboard
        </Link>

        <a href="#" className="items-center px-4 py-1.5 text-sm font-medium hover:bg-slate-800">
          Issues
        </a>

        <a href="#" className="items-center px-4 py-1.5 text-sm font-medium hover:bg-slate-800">
          Settings
        </a>
      </nav>
    </aside>
  )
}
