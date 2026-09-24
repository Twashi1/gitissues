import Header from './Header'
import Sidebar from './Sidebar'
import WorleyBackground from '../background/WorleyNoise'

export default function AppLayout({
  children,
}: {
  children: React.ReactNode
}) {
  // const [showExportDir, setShowExportDir] = useState(false)

  return (
    <div className="min-h-screen text-slate-100 flex flex-col">
      {/* Header: solid bg-slate-900 */}
      <div className="bg-slate-900">
        <Header />
      </div>

      {/* Main container: flex-1 row (Sidebar + main content) */}
      <div className="flex-1 relative">
        {/* Background shader: full size of this container, behind content */}
        <div className="absolute inset-0 z-[-10]">
          <WorleyBackground />
        </div>

        {/* Sidebar and main content flex row */}
        <div className="flex h-full">
          {/* Sidebar - fits content and fills height */}
          <div className="border-r border-slate-800 bg-slate-900 relative z-10 flex flex-col h-full min-h-screen">
            <Sidebar />
          </div>

          {/* Main content */}
          <main className="flex-1 overflow-y-auto relative z-10 p-4">
            {children}

            {/* Export Directory Button (bottom-right of main content) */}
            {/*<Button
              variant="secondary"
              onClick={() => setShowExportDir(true)}
              className="absolute bottom-4 right-4 z-20 flex items-center gap-1 px-3 py-1 bg-slate-600/50 text-slate-100 hover:bg-slate-500/50 rounded text-md"
            >
              📁 Export Directory
            </Button>*/}
          </main>
        </div>
      </div>

      {/* Export Directory Modal */}
      {/*<ExportDirectory open={showExportDir} onClose={() => setShowExportDir(false)} />*/}
    </div>
  )
}
