import Button from '../../ui/Button.tsx'
import type { Issue } from '../../types/issue.ts'

type Props = {
  issue: Issue
  onClick: () => void
  onDelete: (id: number) => void
}

export default function IssueButton({ issue, onClick, onDelete }: Props) {
  // TODO: add focus ring
  return (
    <div className="relative">
      <div onClick={onClick} className="flex items-center gap-2 rounded-md bg-slate-700 p-2 opacity-90 transition-colors hover:bg-slate-600">
        <span className="flex-1 text-base">{issue.title}</span>

        <Button variant='secondary' onClick={(e) => {
            e.stopPropagation()
            onDelete(issue.id)
          }}
          className="px-2 py-1 text-red-700 text-2xl"
          aria-label={`Delete ${issue.title}`}
        >
          ×
        </Button>
      </div>
    </div>
  )
}
