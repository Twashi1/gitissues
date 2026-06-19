import Button from '../../ui/Button.tsx'
import type { Issue } from '../../types/issue.ts'

type Props = {
  issue: Issue
  onClick: () => void
}

export default function IssueButton({ issue, onClick }: Props) {
  return (
    <div className="relative">
      <Button variant='secondary' onClick={onClick} className="w-full text-left">
        { issue.title }
      </Button>
    </div>
  )
}
