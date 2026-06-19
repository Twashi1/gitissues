import { useQuery } from '@tanstack/react-query'

import AppLayout from './components/Layout/AppLayout'
import RandomButton from './components/RandomButton'
import NewIssue from './components/NewIssue/NewIssue'
import IssueList from './components/IssueList/IssueList'

import { getIssues } from './services/issues'

function App() {
  const {
    data: issues = [],
    isLoading,
  } = useQuery({
    queryKey: ['issues'],
    queryFn: getIssues,
    refetchInterval: 5000,
  })

  return (
    <>
      <AppLayout>
        <div className="min-h-screen text-slate-100">
          <div className="max-w-3xl mx-auto px-6 py-10 space-y-10">
            <section id="api-endpoints" className="space-y-6">
              <h1 className="text-2xl font-semibold tracking-tight">
                API Endpoints
              </h1>

              <div className="flex flex-col gap-4">
                <RandomButton />
                <NewIssue />
              </div>
            </section>

            <section id="issue-list">
              <div className="p-6 min-h-screen text-white">
                {isLoading ? (
                      <p className="text-slate-400">Loading...</p>
                ) : (
                  <IssueList issues={issues} />
                )}
              </div>
            </section>

            <section id="spacer" className="h-10"></section>
          </div>
        </div>
      </AppLayout>
    </>
  )
}

export default App
