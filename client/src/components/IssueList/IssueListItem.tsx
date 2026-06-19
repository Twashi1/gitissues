import IssueButton from './IssueButton.tsx'
import IssueDetail from './IssueDetail.tsx'
import type { Issue } from '../../types/issue'
import { useState } from 'react'
import clsx from 'clsx'

type Props = {
  issue: Issue
}

export default function IssueListItem({ issue }: Props) {
  const [open, setOpen] = useState(false)

  return (
    <div className="w-full">
      <IssueButton
        issue={issue}
        onClick={() => setOpen(v => !v)}
      />

      {/* always mounted */}
      <div
        className={clsx(
          'overflow-hidden transition-all duration-200 ease-out',
          open ? 'max-h-40 opacity-100 mt-2' : 'max-h-0 opacity-0 mt-0'
        )}
      >
        <div className="pt-2">
          <IssueDetail onClick={() => setOpen(false)} issue={issue} />
        </div>
      </div>
    </div>
  )
}
