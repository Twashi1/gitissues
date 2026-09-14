import { useState } from 'react'
import Header from './Header'
import Sidebar from './Sidebar'
import WorleyBackground from '../background/WorleyNoise'
import ExportDirectory from '../../components/ExportDirectory'
import Button from '../../ui/Button'

export default function AppLayout({
  children,
}: {
  children: React.ReactNode
}) {
  const [showExportDir, setShowExportDir] = useState(false)

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col">
      <Header />

      <div className="flex flex-1 overflow-hidden">
        <Sidebar />

        <main className="flex-1 overflow-y-auto">
          <div className="relative w-full min-h-screen">
            <WorleyBackground />

            <div className="relative z-10">
              {children}
            </div>

            {/* Export Directory Button (bottom-left) */}
            <Button
              variant="secondary"
              onClick={() => setShowExportDir(true)}
              className="fixed bottom-4 left-4 z-20 flex items-center gap-1 px-3 py-1 bg-slate-600 text-slate-100 hover:bg-slate-500 rounded text-md"
            >
              📁 Export Directory
            </Button>
          </div>
        </main>
      </div>

      {/* Export Directory Modal */}
      <ExportDirectory open={showExportDir} onClose={() => setShowExportDir(false)} />
    </div>
  )
}