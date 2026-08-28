import IssueButton from './IssueButton'
import IssueDetail from './IssueDetail'
import type { Issue } from '../../types/issue'
import { useState } from 'react'
import clsx from 'clsx'

type Props = {
  issue: Issue
  onDelete: (id: number) => void
}

export default function IssueListItem({ issue, onDelete }: Props) {
  const [open, setOpen] = useState(false)
  const [isDragging, setIsDragging] = useState(false)

  const handleDragStart = (e: React.DragEvent) => {
    e.dataTransfer.effectAllowed = 'move'
    e.dataTransfer.setData('application/json', JSON.stringify({
      id: issue.id,
      listId: issue.listId
    }))
    setIsDragging(true)
  }

  const handleDragEnd = () => {
    setIsDragging(false)
  }

  return (
    <div className="w-full">
      <div
        draggable="true"
        onDragStart={handleDragStart}
        onDragEnd={handleDragEnd}
        className={isDragging ? 'opacity-50' : ''}
      >
        <IssueButton
          issue={issue}
          onClick={() => setOpen(v => !v)}
          onDelete={onDelete}
        />
      </div>

      {/* always mounted */}
      <div
        className={clsx(
          'overflow-hidden transition-all duration-200 ease-out',
          open ? 'h-fit opacity-100 mt-2' : 'max-h-0 opacity-0 mt-0'
        )}
      >
        <div className="pt-2">
          <IssueDetail onClick={() => setOpen(false)} issue={issue} />
        </div>
      </div>
    </div>
  )
}
