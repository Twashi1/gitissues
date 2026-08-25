import IssueListItem from './IssueListItem'
import type { Issue } from '../../types/issue'
import { useState } from 'react'
import Input from '../../ui/Input'
import Button from '../../ui/Button'
import TextArea from '../../ui/TextArea'

type Props = {
  listId: number
  title: string
  issues: Issue[]
  onUpdateTitle: (listId: number, title: string) => void
  onDeleteList: (listId: number) => void
  onDeleteIssue: (issueId: number) => void
  onCreateIssue: (listId: number, title: string, description: string) => void
}

export default function IssueList({ listId, title, issues, onUpdateTitle, onDeleteList, onDeleteIssue, onCreateIssue }: Props) {
  const [isEditingTitle, setIsEditingTitle] = useState(false)
  const [editTitle, setEditTitle] = useState(title)
  const [showNewIssueForm, setShowNewIssueForm] = useState(false)
  const [newIssueTitle, setNewIssueTitle] = useState('')
  const [newIssueDescription, setNewIssueDescription] = useState('')

  const handleTitleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setEditTitle(e.target.value)
  }

  const handleTitleSave = () => {
    onUpdateTitle(listId, editTitle)
    setIsEditingTitle(false)
  }

  const handleTitleCancel = () => {
    setEditTitle(title)
    setIsEditingTitle(false)
  }

  const handleTitleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      handleTitleSave()
    } else if (e.key === 'Escape') {
      e.preventDefault()
      handleTitleCancel()
    }
  }

  const handleNewIssueSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (newIssueTitle.trim()) {
      onCreateIssue(listId, newIssueTitle, newIssueDescription)
      setNewIssueTitle('')
      setNewIssueDescription('')
      setShowNewIssueForm(false)
    }
  }

  const handleNewIssueCancel = () => {
    setNewIssueTitle('')
    setNewIssueDescription('')
    setShowNewIssueForm(false)
  }

  return (
    <div className="flex flex-col gap-2 w-full min-w-md bg-slate-800/40 rounded-md p-4 border border-slate-700">
      <div className="mb-4 flex items-center justify-between">
        <div className="flex-1">
          {isEditingTitle ? (
            <Input
              variant="secondary"
              value={editTitle}
              onChange={handleTitleChange}
              onBlur={handleTitleSave}
              onKeyDown={handleTitleKeyDown}
              autoFocus
              className="border border-slate-600 bg-slate-800 text-slate-100 rounded mb-2"
            />
          ) : (
            <Button
              variant="list-title"
              onClick={() => setIsEditingTitle(true)}
            >
              {title}
            </Button>
          )}
        </div>
        <Button variant="secondary" onClick={() => onDeleteList(listId)} className="border border-slate-600">
          Delete List
        </Button>
      </div>

      {/* Horizontal spacer line */}
      <div className="h-0.5 bg-slate-600 my-2"></div>

      {issues.length > 0 ? (
        <>
          {issues.map((issue) => (
            <IssueListItem key={issue.id} issue={issue} onDelete={(id) => onDeleteIssue(id)} />
          ))}
        </>
      ) : (
        <p className="text-slate-500 text-center py-12">No issues yet</p>
      )}

      {/* New Issue button moved to bottom */}
      {showNewIssueForm ? (
        <form onSubmit={handleNewIssueSubmit} className="flex flex-col gap-3 mb-4">
          <Input
            variant="primary"
            placeholder="Title"
            value={newIssueTitle}
            onChange={(e) => setNewIssueTitle(e.target.value)}
            className="border border-slate-600 bg-slate-800 text-slate-100 rounded"
          />
          <TextArea
            placeholder="Description"
            rows={3}
            value={newIssueDescription}
            onChange={(e) => setNewIssueDescription(e.target.value)}
            className="border border-slate-600 bg-slate-800 text-slate-100 rounded"
          />
          <div className="flex gap-2 justify-end">
            <Button variant="secondary" onClick={handleNewIssueCancel} className="border border-slate-600">
              Cancel
            </Button>
            <Button variant="secondary" onClick={handleNewIssueSubmit} className="border border-slate-600">
              Add Issue
            </Button>
          </div>
        </form>
      ) : (
        <Button variant="secondary" onClick={() => setShowNewIssueForm(true)} className="border border-slate-600">
          New Issue
        </Button>
      )}
    </div>
  )
}
