export default function Sidebar() {
  return (
    <aside className="w-60 border-r border-slate-800 p-4 space-y-2">
      <nav className="space-y-2 text-sm">
        <a className="block px-3 py-2 rounded hover:bg-slate-800" href="#">
          Dashboard
        </a>

        <a className="block px-3 py-2 rounded hover:bg-slate-800" href="#">
          Issues
        </a>

        <a className="block px-3 py-2 rounded hover:bg-slate-800" href="#">
          Settings
        </a>
      </nav>
    </aside>
  )
}
