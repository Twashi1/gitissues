import { useEffect, useRef, useState } from 'react'
import type { Issue } from './types/issue'

import AppLayout from './components/Layout/AppLayout'
import IssueList from './components/IssueList/IssueList'

import { getIssues, deleteIssue } from './services/issues'
import { getIssueLists, createIssueList } from './services/issueLists'

function App() {
  const [issueLists, setIssueLists] = useState<Array<{id: number; title: string; createdAt: string}>>([])
  const [issuesByListId, setIssuesByListId] = useState<Record<number, Issue[]>>({})
  const [newListTitle, setNewListTitle] = useState<string>('')
  const [showNewListForm, setShowNewListForm] = useState(false)
  const [creatingList, setCreatingList] = useState(false)
  const scrollRef = useRef<HTMLDivElement>(null)

  // Fetch issue lists on mount
  useEffect(() => {
    fetchIssueLists()
  }, [])

  // Scroll to right when the new list form appears
  useEffect(() => {
    if (showNewListForm) {
      requestAnimationFrame(() => {
        if (scrollRef.current) {
          scrollRef.current.scrollLeft = scrollRef.current.scrollWidth
        }
      })
    }
  }, [showNewListForm, scrollRef])

  const fetchIssueLists = async () => {
    try {
      const lists = await getIssueLists()
      setIssueLists(lists)
      // For each list, fetch issues
      const issuesPromises = lists.map(list =>
        getIssues(list.id)
      )
      const results = await Promise.all(issuesPromises)
      const newIssuesByListId: Record<number, Issue[]> = {}
      lists.forEach((list, index) => {
        newIssuesByListId[list.id] = results[index]
      })
      setIssuesByListId(newIssuesByListId)
    } catch (error) {
      console.error('Failed to fetch issue lists:', error)
    }
  }

  const handleCreateList = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!newListTitle.trim()) return
    setCreatingList(true)
    try {
      const newList = await createIssueList(newListTitle)
      setIssueLists(prev => [...prev, newList])
      setNewListTitle('')
      setShowNewListForm(false)
      // Fetch issues for the new list (empty array)
      setIssuesByListId(prev => ({
        ...prev,
        [newList.id]: []
      }))
    } catch (error) {
      console.error('Failed to create issue list:', error)
    } finally {
      setCreatingList(false)
    }
  }

  const handleUpdateListTitle = async (listId: number, title: string) => {
    try {
      await fetch(`/api/issue-lists/${listId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ title }),
      })
      setIssueLists(prev => prev.map(list =>
        list.id === listId ? { ...list, title } : list
      ))
    } catch (error) {
      console.error('Failed to update issue list title:', error)
    }
  }

  const handleDeleteList = async (listId: number) => {
    try {
      await fetch(`/api/issue-lists/${listId}`, {
        method: 'DELETE',
      })
      setIssueLists(prev => prev.filter(list => list.id !== listId))
      setIssuesByListId(prev => {
        const newState = { ...prev }
        delete newState[listId]
        return newState
      })
      // Also delete issues in this list? We'll let the backend handle cascade or we can delete them here.
      // For simplicity, we'll assume the backend deletes the issues when the list is deleted.
    } catch (error) {
      console.error('Failed to delete issue list:', error)
    }
  }

  const handleCreateIssue = async (listId: number, title: string, description: string) => {
    try {
      await fetch('/api/issue', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ title, description, listId, status: 'UNASSIGNED' }),
      })
      // Refetch issues for this list
      const issues = await getIssues(listId)
      setIssuesByListId(prev => ({
        ...prev,
        [listId]: issues
      }))
    } catch (error) {
      console.error('Failed to create issue:', error)
    }
  }

  const handleDeleteIssue = async (id: number) => {
    try {
      await deleteIssue(id)
      // Refetch all lists and issues
      await fetchIssueLists()
    } catch (error) {
      console.error('Failed to delete issue:', error)
    }
  }

  return (
    <>
      <AppLayout>
        <div className="text-slate-100">
          <div className="flex-1 px-6 py-10 space-y-10 min-h-0">
            <section id="issue-lists" className="mb-6">
              <div ref={scrollRef} className="flex gap-4 items-start overflow-x-auto pb-4 w-full">
                {/* Map over issue lists */}
                {issueLists.map(list => (
                  <IssueList
                    key={list.id}
                    listId={list.id}
                    title={list.title}
                    issues={issuesByListId[list.id] || []}
                    onUpdateTitle={handleUpdateListTitle}
                    onDeleteList={handleDeleteList}
                    onDeleteIssue={handleDeleteIssue}
                    onCreateIssue={handleCreateIssue}
                  />
                ))}

                {/* Button to create new list */}
                <div className="flex-shrink-0">
                  <button
                    onClick={() => setShowNewListForm(true)}
                    className="mb-2 px-4 py-2 bg-slate-600 text-slate-100 hover:bg-slate-500 rounded whitespace-nowrap"
                  >
                    New List
                  </button>
                  {showNewListForm && (
                    <form onSubmit={handleCreateList} className="flex gap-2">
                      <input
                        type="text"
                        value={newListTitle}
                        onChange={(e) => setNewListTitle(e.target.value)}
                        placeholder="List title"
                        className="px-3 py-2 border border-slate-600 rounded bg-slate-800 text-slate-100"
                        autoFocus
                      />
                      <button
                        type="submit"
                        disabled={creatingList}
                        className="px-4 py-2 bg-slate-600 text-slate-100 hover:bg-slate-500 rounded whitespace-nowrap"
                      >
                        {creatingList ? 'Creating...' : 'Create'}
                      </button>
                      <button
                        type="button"
                        onClick={() => {
                          setNewListTitle('');
                          setShowNewListForm(false);
                        }}
                        className="ml-2 px-4 py-2 bg-slate-600 text-slate-100 hover:bg-slate-500 rounded whitespace-nowrap"
                      >
                        Cancel
                      </button>
                    </form>
                  )}
                </div>
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