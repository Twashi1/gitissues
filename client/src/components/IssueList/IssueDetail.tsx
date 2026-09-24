import type { Issue, IssuePatchRequest, IssuePatchVariables } from '../../types/issue'
import TextArea from '../../ui/TextArea'
import Input from '../../ui/Input'
import { useState, useEffect } from 'react'
import { useQueryClient, useMutation } from '@tanstack/react-query'
import { patchIssueByProject, patchIssue } from '../../services/issues'

type Props = {
  issue: Issue
  onClick: () => void
  projectId?: number
  onUpdateIssue: (issueId: number, title: string | null, description: string | null) => void
}

export default function IssueDetail({ issue, onClick, projectId, onUpdateIssue }: Props) {
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
    mutationFn: async ({ projectId, data }: { projectId?: number; data: IssuePatchVariables }) => {
      if (projectId !== undefined) {
        return patchIssueByProject(projectId, data)
      } else {
        return patchIssue(data)
      }
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['issues'] })
      onUpdateIssue(variables.data.id, variables.data.request.title ?? null, variables.data.request.description ?? null)
    },
  })

  const handlePatch = async (title: string | null, description: string | null, id: number) => {
    const data: IssuePatchRequest = {}
    if (title !== null) { data.title = title }
    if (description !== null) { data.description = description }

    mutation.mutate({
      projectId: projectId,
      data: {
        id: id,
        request: data
      }
    })
  }

  return (
    <div onClick={onClick} className="rounded-md bg-slate-800 p-3 text-sm text-slate-200 border border-slate-700 shadow">
      <div className="space-y-2">
        <div>
          <p className="p-2 text-sm text-slate-400">Title</p>
          <Input
            variant="secondary"
            placeholder="Title"
            value={title}
            onChange={(value) => setTitle(value)}
            onSave={() => handlePatch(title, null, issue.id)}
            onCancel={() => setTitle(issue.title)}
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
            onKeyDown={(e) => {
              if (e.key === 'Enter') {
                e.preventDefault()
                handlePatch(null, description, issue.id)
              } else if (e.key === 'Escape') {
                e.preventDefault()
                setDescription(issue.description)
              }
            }}
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