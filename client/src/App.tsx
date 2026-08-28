import { useEffect, useRef, useState } from 'react'
import type { Issue } from './types/issue'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'

import AppLayout from './components/Layout/AppLayout'
import IssueList from './components/IssueList/IssueList'
import ShaderDevPage from './pages/ShaderDevPage'

import { getIssues, deleteIssue, moveIssue, createIssue } from './services/issues'
import { getIssueLists, createIssueList, patchIssueList, deleteIssueList } from './services/issueLists'

function App() {
  const [issueLists, setIssueLists] = useState<Array<{id: number; title: string; createdAt: string}>>([])
  const [issuesByListId, setIssuesByListId] = useState<Record<number, Issue[]>>({})
  const [newListTitle, setNewListTitle] = useState<string>('')
  const [showNewListForm, setShowNewListForm] = useState(false)
  const [creatingList, setCreatingList] = useState(false)
  const [isMoving, setIsMoving] = useState(false)
  const scrollRef = useRef<HTMLDivElement>(null)

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

  // Fetch issue lists on mount
  useEffect(() => {
    fetchIssueLists()
  }, [])

  
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
      await patchIssueList(listId, title)
      setIssueLists(prev => prev.map(list =>
        list.id === listId ? { ...list, title } : list
      ))
    } catch (error) {
      console.error('Failed to update issue list title:', error)
    }
  }

  const handleDeleteList = async (listId: number) => {
    try {
      await deleteIssueList(listId)
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
      const createdIssue: Issue = await createIssue({ title, description, listId, status: 'UNASSIGNED' })
      // Optimistically add the issue to the list with the correct listId
      const issueWithListId = { ...createdIssue, listId }
      setIssuesByListId(prev => ({
        ...prev,
        [listId]: [...(prev[listId] || []), issueWithListId]
      }))
    } catch (error) {
      console.error('Failed to create issue:', error)
      // TODO: handle error, maybe show a notification
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

  const handleMoveIssue = async (issueId: number, targetListId: number) => {
    // Prevent concurrent moves
    if (isMoving) {
      return
    }
    setIsMoving(true)
    let sourceListId: number | undefined
    let issueIndex: number | undefined
    let movedIssue: Issue | undefined

    try {
      // We'll optimistically update the state: remove the issue from its current list and add it to the target list
      setIssuesByListId(prev => {
        // We need to find the issue in the current state
        for (const [listIdStr, issues] of Object.entries(prev)) {
          const listIdNum = Number(listIdStr)
          const idx = issues.findIndex(issue => issue.id === issueId)
          if (idx !== -1) {
            sourceListId = listIdNum
            issueIndex = idx
            movedIssue = issues[idx]
            // Remove the issue from the source list
            const newSourceList = [...issues]
            newSourceList.splice(idx, 1)
            // Create a new state object
            const newState = { ...prev }
            newState[sourceListId] = newSourceList
            // Add the issue to the target list (at the end)
            const targetList = newState[targetListId] || []
            newState[targetListId] = [...targetList, movedIssue]
            return newState
          }
        }
        // If we didn't find the issue, return the state unchanged
        return prev
      })

      // Now, call the API to move the issue
      await moveIssue(issueId, targetListId)
      // If we get here, the move was successful
      setIsMoving(false)
    } catch (error) {
      // If there was an error, rollback the state update
      setIssuesByListId(prev => {
        // We need to revert: remove the issue from the target list and put it back in the source list at the original index
        if (sourceListId !== undefined && issueIndex !== undefined && movedIssue) {
          // Remove the issue from the target list (we don't know the index, so we find it by id)
          const targetList = [...(prev[targetListId] || [])]
          const targetIndex = targetList.findIndex(issue => issue.id === issueId)
          if (targetIndex !== -1) {
            targetList.splice(targetIndex, 1)
          }

          // Insert the issue back into the source list at the original index
          const sourceList = [...(prev[sourceListId] || [])]
          sourceList.splice(issueIndex, 0, movedIssue)

          return {
            ...prev,
            [targetListId]: targetList,
            [sourceListId]: sourceList
          }
        }
        return prev
      })

      setIsMoving(false)
      throw error // Re-throw so that the caller can handle it if needed
    }
  }

  return (
    <BrowserRouter>
      <AppLayout>
        <div className="text-slate-100">
          <div className="flex-1 px-6 py-10 space-y-10 min-h-0">
              <Routes>
                <Route path="/" element={
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
                          onMoveIssue={handleMoveIssue}
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
                }/>
                {import.meta.env.DEV && (
                  <Route path="/dev/shader" element={<ShaderDevPage />} />
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
