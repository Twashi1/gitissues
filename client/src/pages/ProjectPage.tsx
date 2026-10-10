import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'

import { getProject, updateProject } from '../services/projects'
import type { Project } from '../types/project'
import { getIssueListsByProject } from '../services/issueLists'
import { getIssuesByProject } from '../services/issues'
import type { Issue } from '../types/issue'
import { IssueStatus } from '../types/issue'
import type { IssueList as IssueListType } from '../types/issueList'
import IssueList from '../components/IssueList/IssueList'
import { createIssueListForProject } from '../services/issueLists'
import { createIssueByProject } from '../services/issues'
import { deleteIssueListForProject } from '../services/issueLists'
import { patchIssueListForProject } from '../services/issueLists'
import { deleteIssueByProject } from '../services/issues'
import { moveIssueByProject } from '../services/issues'
import Button from '../ui/Button'
import ExportDirectory from '../components/ExportDirectory'

export default function ProjectPage() {
  const { projectId } = useParams<{ projectId: string }>()
  const numericProjectId = Number(projectId)
  const navigate = useNavigate()

  const [project, setProject] = useState<Project | null>(null)
  const [issueLists, setIssueLists] = useState<IssueListType[]>([])
  const [issuesByListId, setIssuesByListId] = useState<Record<number, Issue[]>>({})
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  // For creating new list
  const [newListTitle, setNewListTitle] = useState<string>('')
  const [showNewListForm, setShowNewListForm] = useState(false)
  const [creatingList, setCreatingList] = useState(false)

  // For editing export directory
  const [showExportDir, setShowExportDir] = useState(false)

  useEffect(() => {
    loadProject()
  }, [numericProjectId])

  const loadProject = async () => {
    setLoading(true)
    setError(null)
    try {
      const proj = await getProject(numericProjectId)
      setProject(proj)

      // Load issue lists for this project
      const lists = await getIssueListsByProject(numericProjectId)
      setIssueLists(lists)

      // Load issues for each list
      const issuesPromises = lists.map(list =>
        getIssuesByProject(numericProjectId, list.id)
      )
      const results = await Promise.all(issuesPromises)
      const newIssuesByListId: Record<number, Issue[]> = {}
      lists.forEach((list, index) => {
        newIssuesByListId[list.id] = results[index]
      })
      setIssuesByListId(newIssuesByListId)
    } catch (err) {
      console.error('Failed to load project data:', err)
      setError('Failed to load project data')
      setProject(null)
      setIssueLists([])
      setIssuesByListId({})
    } finally {
      setLoading(false)
    }
  }

  const handleSubmitExportDirectory = async (value: string) => {
    if (!project) return

    try {
      const updatedProject = await updateProject(
        project.id,
        project.name,
        value
      )
      setProject(updatedProject)
    } catch (err) {
      console.error('Failed to update project:', err)
      setError('Failed to update project')
    }
  }

  const handleCreateList = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!newListTitle.trim()) return
    setCreatingList(true)
    try {
      const newList = await createIssueListForProject(numericProjectId, newListTitle)
      setIssueLists(prev => [...prev, newList])
      setNewListTitle('')
      setShowNewListForm(false)
      // Initialize empty issues array for the new list
      setIssuesByListId(prev => ({
        ...prev,
        [newList.id]: []
      }))
    } catch (err) {
      console.error('Failed to create issue list:', err)
      setError('Failed to create issue list')
    } finally {
      setCreatingList(false)
    }
  }

  const handleUpdateListTitle = async (listId: number, title: string) => {
    try {
      await patchIssueListForProject(numericProjectId, listId, title)
      setIssueLists(prev => prev.map(list =>
        list.id === listId ? { ...list, title } : list
      ))
    } catch (err) {
      console.error('Failed to update issue list title:', err)
      setError('Failed to update issue list title')
    }
  }

  const handleDeleteList = async (listId: number) => {
    try {
      await deleteIssueListForProject(numericProjectId, listId)
      setIssueLists(prev => prev.filter(list => list.id !== listId))
      const newIssuesByListId = { ...issuesByListId }
      delete newIssuesByListId[listId]
      setIssuesByListId(newIssuesByListId)
    } catch (err) {
      console.error('Failed to delete issue list:', err)
      setError('Failed to delete issue list')
    }
  }

  const handleCreateIssue = async (listId: number, title: string, description: string, status: string) => {
    // Create temporary issue with negative ID
    const tempId = -Date.now()
    const tempIssue: Issue = {
      id: tempId,
      title,
      description,
      listId,
      status: status as IssueStatus
    }

    // Optimistically add to state
    setIssuesByListId(prev => {
      const newList = [...(prev[listId] || []), tempIssue]
      const newState = { ...prev }
      newState[listId] = newList
      return newState
    })

    try {
      const createdIssue = await createIssueByProject(numericProjectId, {
        title: title,
        description: description,
        status: status as IssueStatus,
        listId: listId
      }, listId)
      // Replace temporary issue with real one
      setIssuesByListId(prev => {
        const listIssues = prev[listId] || []
        const index = listIssues.findIndex(issue => issue.id === tempId)
        if (index !== -1) {
          const newList = [...listIssues]
          newList[index] = createdIssue
          const newState = { ...prev }
          newState[listId] = newList
          return newState
        }
        return prev
      })
    } catch (err) {
      // Rollback: remove temporary issue
      setIssuesByListId(prev => {
        const listIssues = prev[listId] || []
        const index = listIssues.findIndex(issue => issue.id === tempId)
        if (index !== -1) {
          const newList = [...listIssues]
          newList.splice(index, 1)
          const newState = { ...prev }
          newState[listId] = newList
          return newState
        }
        return prev
      })
      console.error('Failed to create issue:', err)
      setError('Failed to create issue')
    }
  }

  const handleDeleteIssue = async (issueId: number) => {
    // We need to know which list the issue belongs to to update state correctly
    // We'll search through issuesByListId to find the list containing this issue
    let listIdContainingIssue: number | null = null
    let issueToRestore: Issue | null = null

    for (const listIdStr in issuesByListId) {
      const listIdNum = Number(listIdStr)
      const listIssues = issuesByListId[listIdStr]
      const idx = listIssues.findIndex(issue => issue.id === issueId)
      if (idx !== -1) {
        listIdContainingIssue = listIdNum
        issueToRestore = listIssues[idx]
        break
      }
    }

    if (!listIdContainingIssue || !issueToRestore) {
      console.warn(`Attempted to delete non-existent issue with id ${issueId}`)
      return
    }

    // Optimistically remove from state
    setIssuesByListId(prev => {
      const listIssues = prev[listIdContainingIssue] || []
      const newList = [...listIssues]
      const index = newList.findIndex(issue => issue.id === issueId)
      if (index !== -1) {
        newList.splice(index, 1)
        const newState = { ...prev }
        newState[listIdContainingIssue] = newList
        return newState
      }
      return prev
    })

    try {
      await deleteIssueByProject(numericProjectId, issueId)
      // On success, do nothing (issue already removed optimistically)
    } catch (err) {
      // Rollback: restore the issue to its original list
      setIssuesByListId(prev => {
        const listIssues = prev[listIdContainingIssue] || []
        const newList = [...listIssues, issueToRestore]
        const newState = { ...prev }
        newState[listIdContainingIssue] = newList
        return newState
      })
      console.error('Failed to delete issue:', err)
      setError('Failed to delete issue')
    }
  }

  const handleMoveIssue = async (issueId: number, targetListId: number) => {
    // We'll optimistically update the state: remove the issue from its current list and add it to the target list
    setIssuesByListId(prev => {
      // Find the source list
      let sourceListId: number | null = null
      let issueIndex: number | null = null
      let movedIssue: Issue | null = null

      for (const listIdStr in prev) {
        const listIdNum = Number(listIdStr)
        const listIssues = prev[listIdStr]
        const idx = listIssues.findIndex(issue => issue.id === issueId)
        if (idx !== -1) {
          sourceListId = listIdNum
          issueIndex = idx
          movedIssue = listIssues[idx]
          break
        }
      }

      if (sourceListId === null || issueIndex === null || !movedIssue) {
        // Issue not found, return state unchanged
        return prev
      }

      // Remove the issue from the source list
      const newSourceList = [...(prev[sourceListId] || [])]
      newSourceList.splice(issueIndex, 1)
      // Create a new state object
      const newState = { ...prev }
      newState[sourceListId] = newSourceList
      // Add the issue to the target list (at the end)
      const targetList = newState[targetListId] || []
      newState[targetListId] = [...targetList, movedIssue]
      return newState
    })

    try {
      await moveIssueByProject(numericProjectId, issueId, targetListId)
      // If we get here, the move was successful
    } catch (err) {
      // If there was an error, rollback the state update
      setIssuesByListId(prev => {
        // We need to revert: remove the issue from the target list and put it back in the source list at the original index
        // We need to know source list id, issue index, and moved issue
        // We'll compute again from prev (state before optimistic update)
        let sourceListId: number | null = null
        let issueIndex: number | null = null
        let movedIssue: Issue | null = null

        for (const listIdStr in prev) {
          const listIdNum = Number(listIdStr)
          const listIssues = prev[listIdStr]
          const idx = listIssues.findIndex(issue => issue.id === issueId)
          if (idx !== -1) {
            sourceListId = listIdNum
            issueIndex = idx
            movedIssue = listIssues[idx]
            break
          }
        }

        if (sourceListId === null || issueIndex === null || !movedIssue) {
          // Should not happen, but return prev
          return prev
        }

        // Remove the issue from the target list
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
      })
      setError('Failed to move issue')
      console.error('Failed to move issue:', err)
    }
  }

  const handleUpdateIssue = (issueId: number, title: string | null, description: string | null) => {
    // Find the list containing the issue
    let listIdContainingIssue: number | null = null;
    for (const listIdStr in issuesByListId) {
      const listIdNum = Number(listIdStr);
      const listIssues = issuesByListId[listIdStr];
      const idx = listIssues.findIndex(issue => issue.id === issueId);
      if (idx !== -1) {
        listIdContainingIssue = listIdNum;
        break;
      }
    }
    if (listIdContainingIssue === null) {
      // Issue not found, return early
      return;
    }
    const listIssues = issuesByListId[listIdContainingIssue];
    const newList = [...listIssues];
    const index = newList.findIndex(issue => issue.id === issueId);
    if (index !== -1) {
      const updatedIssue = { ...newList[index], title: title ?? newList[index].title, description: description ?? newList[index].description };
      newList[index] = updatedIssue;
    }
    setIssuesByListId(prev => {
      const newState = { ...prev };
      newState[listIdContainingIssue] = newList;
      return newState;
    });
  }

  if (loading && !project) {
    return (
      <div className="text-slate-100 min-h-screen px-6 py-10">
        <p className="text-center text-slate-400">Loading project...</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="text-slate-100 min-h-screen px-6 py-10">
        <p className="text-center text-red-400">{error}</p>
      </div>
    )
  }

  if (!project) {
    return (
      <div className="text-slate-100 min-h-screen px-6 py-10">
        <p className="text-center text-slate-400">Project not found</p>
        <button
          onClick={() => navigate('/')}
          className="mt-4 px-4 py-2 bg-slate-600 text-slate-100 hover:bg-slate-500 rounded"
        >
          Back to Projects
        </button>
      </div>
    )
  }

  return (
    <div className="text-slate-100 min-h-screen px-6 py-10">
      <div className="flex justify-between items-center mb-6">
        <div className="flex flex-col">
          <h1 className="text-2xl font-bold">Project: {project.name}</h1>
          {/* Export Directory Button (bottom-right of main content) */}
          <Button
            variant="secondary"
            onClick={() => setShowExportDir(true)}
            className="flex items-center gap-1 px-3 py-1"
          >
            📁 Modify Export Directory
          </Button>
        </div>
        <div className="flex gap-3">
          <button
            onClick={() => {
              setShowNewListForm(true)
            }}
            className="px-4 py-2 bg-slate-600 text-slate-100 hover:bg-slate-500 rounded whitespace-nowrap"
          >
            New List
          </button>
          <button
            onClick={() => {
              navigate('/')
            }}
            className="px-4 py-2 bg-slate-600 text-slate-100 hover:bg-slate-500 rounded"
          >
            Back to Projects List
          </button>
        </div>
      </div>

      {/* New List Form */}
      {showNewListForm && (
        <form onSubmit={handleCreateList} className="mb-6 flex gap-2">
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
              setNewListTitle('')
              setShowNewListForm(false)
            }}
            className="ml-2 px-4 py-2 bg-slate-600 text-slate-100 hover:bg-slate-500 rounded whitespace-nowrap"
          >
            Cancel
          </button>
        </form>
      )}

      {/* Error Message */}
      {error && (
        <div className="mb-4 p-4 bg-red-600/20 border border-red-500/50 rounded text-red-400">
          {error}
        </div>
      )}

      {/* Loading State for lists */}
      {loading && issueLists.length === 0 && (
        <p className="text-center text-slate-400">Loading issue lists...</p>
      )}

      {/* Issue Lists */}
      {!loading && (
        <div className="space-y-4">
          {issueLists.length === 0 ? (
            <p className="text-center text-slate-400">
              No issue lists found for this project
            </p>
          ) : (
            <div>
              <h2 className="text-xl font-semibold mb-4">Issue Lists</h2>
              <div className="flex gap-4 items-start overflow-x-auto pb-4 w-full">
                {issueLists.map(list => (
                  <IssueList
                    key={list.id}
                    listId={list.id}
                    title={list.title}
                    issues={issuesByListId[list.id] || []}
                    projectId={numericProjectId}
                    onUpdateTitle={handleUpdateListTitle}
                    onDeleteList={handleDeleteList}
                    onDeleteIssue={handleDeleteIssue}
                    onCreateIssue={handleCreateIssue}
                    onMoveIssue={(issueId: number, targetListId: number) => handleMoveIssue(issueId, targetListId)}
                    onUpdateIssue={handleUpdateIssue}
                  />
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Export Directory Modal */}
      <ExportDirectory open={showExportDir} onClose={() => setShowExportDir(false)} initial={project?.exportDirectory} onSubmit={handleSubmitExportDirectory} />
    </div>
  )
}
