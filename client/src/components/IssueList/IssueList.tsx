import IssueListItem from './IssueListItem.tsx'
import type { Issue } from '../../types/issue.ts'

type Props = {
  issues: Issue[]
}

export default function IssueList({ issues }: Props) {
  return (
    <div className="flex flex-col gap-2 w-full max-w-lg">
      {issues.map((issue) => (
        <IssueListItem issue={issue} />
      ))}
    </div>
  )
}
