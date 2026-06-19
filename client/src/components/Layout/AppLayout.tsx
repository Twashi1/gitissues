import Header from './Header'
import Sidebar from './Sidebar'
import WorleyBackground from '../background/WorleyNoise.tsx'

export default function AppLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col">
      <Header />

      <div className="flex flex-1 overflow-hidden">
        <Sidebar />

        <main className="flex-1 overflow-y-auto">
          <div className="relative w-full min-h-screen overflow-hidden">
            <WorleyBackground />

            <div className="relative z-10">
              {children}
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}
