import IssueButton from './IssueButton.tsx'
import IssueDetail from './IssueDetail.tsx'
import type { Issue } from '../../types/issue'
import { useState } from 'react'
import clsx from 'clsx'

type Props = {
  issue: Issue
  onDelete: (id: number) => void
}

export default function IssueListItem({ issue, onDelete }: Props) {
  const [open, setOpen] = useState(false)

  return (
    <div className="w-full">
      <IssueButton
        issue={issue}
        onClick={() => setOpen(v => !v)}
        onDelete={onDelete}
      />

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
