import IssueListItem from './IssueListItem.tsx'
import type { Issue } from '../../types/issue.ts'

type Props = {
  issues: Issue[]
  onDelete: (id: number) => void
}

export default function IssueList({ issues, onDelete }: Props) {
  return (
    <div className="flex flex-col gap-2 w-full max-w-lg">
      {issues.map((issue) => (
        <IssueListItem issue={issue} onDelete={onDelete} />
      ))}
    </div>
  )
}
