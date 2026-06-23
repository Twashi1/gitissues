import type { Issue, IssuePatchRequest, IssuePatchVariables } from '../../types/issue'
import TextArea from '../../ui/TextArea.tsx'
import Input from '../../ui/Input.tsx'
import { useState, useEffect } from 'react'
import { useQueryClient, useMutation } from '@tanstack/react-query'
import { patchIssue } from '../../services/issues.ts'

type Props = {
  issue: Issue
  onClick: () => void
}

export default function IssueDetail({ issue, onClick }: Props) {
  const [title, setTitle] = useState(issue.title);
  const [description, setDescription] = useState(issue.description);
  const queryClient = useQueryClient()

  useEffect(() => {
    setTitle(issue.title)
  }, [issue.title])
  useEffect(() => {
    setDescription(issue.description)
  }, [issue.description])

  const mutation = useMutation({
    mutationFn: patchIssue,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['issues'] })
    },
  })

  const handlePatch = async (title: string | null, description: string | null, id: number) => {
    const data: IssuePatchRequest = {}
    if (title !== null) { data.title = title }
    if (description !== null) { data.description = description }

    const variables: IssuePatchVariables = {
      id: id,
      request: data
    }

    mutation.mutate(variables)
  }

  return (
    <div onClick={onClick} className="rounded-md bg-slate-800 p-3 text-sm text-slate-200 opacity-90 shadow">
      <div className="space-y-2">
        <div>
          <p className="p-2 text-sm text-slate-400">Title</p>
          <Input
            variant="secondary"
            placeholder="Title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            onBlur={() => handlePatch(title, null, issue.id)}
            className="p-2 text-slate-100 text-xs"
            onClick={(e) => e.stopPropagation()}
          />
        </div>

        <div>
          <p className="p-2 text-sm text-slate-400">Description</p>
          <TextArea
            variant="secondary"
            placeholder="Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            onBlur={() => handlePatch(null, description, issue.id)}
            className="p-2 text-slate-100 text-xs whitespace-pre-wrap"
            onClick={(e) => e.stopPropagation()}
          />
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
