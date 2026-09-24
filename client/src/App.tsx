import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'

import AppLayout from './components/Layout/AppLayout'
import DashboardPage from './pages/DashboardPage'
import ProjectPage from './pages/ProjectPage'

function App() {
  return (
    <BrowserRouter>
      <AppLayout>
        <div className="text-slate-100">
          <div className="flex-1 px-6 py-10 space-y-10 min-h-0">
            <Routes>
              <Route path="/" element={<DashboardPage />} />
              <Route path="/project/:projectId" element={<ProjectPage />} />
              {import.meta.env.DEV && (
                <>
                  <Route path="/dev/swagger" element={
                    <iframe
                      title="Swagger UI"
                      style={{ width: '100%', height: '100vh', border: 'none' }}
                      src="http://localhost:8080/swagger-ui.html"
                    />
                  } />
                </>
              )}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>

            <section id="spacer" className="h-10"></section>
          </div>
        </div>
      </AppLayout>
    </BrowserRouter>
  )
}

export default App