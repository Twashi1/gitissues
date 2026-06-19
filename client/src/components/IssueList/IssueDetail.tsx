import type { Issue } from '../../types/issue'

type Props = {
  issue: Issue
  onClick: () => void
}

export default function IssueDetail({ issue, onClick }: Props) {
  return (
    <div onClick={onClick} className="rounded-md bg-slate-800 p-3 text-sm text-slate-200 opacity-90 shadow">
      <div className="space-y-2">
        <div>
          <p className="text-xs text-slate-400">Title</p>
          <p className="font-medium text-slate-100">{issue.title}</p>
        </div>

        <div>
          <p className="text-xs text-slate-400">Description</p>
          <p className="text-slate-300 whitespace-pre-wrap">
            {issue.description}
          </p>
        </div>

        <div className="flex items-center gap-2 pt-1">
          <p className="text-xs text-slate-400">Status</p>
          <span className="rounded bg-slate-700 px-2 py-0.5 text-xs text-slate-200">
            {issue.status}
          </span>
        </div>
      </div>
    </div>
  )
}
