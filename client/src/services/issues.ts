import type { Issue, IssueCreateRequest, IssuePatchVariables } from '../types/issue'

export async function createIssue(data: IssueCreateRequest): Promise<Issue> {
  const response = await fetch('/api/issue', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })

  if (!response.ok) {
    throw new Error('Failed to create issue')
  }

  return response.json() as Promise<Issue>
}

export async function getIssues(): Promise<Issue[]> {
  const response = await fetch('/api/issue')

  if (!response.ok) {
    throw new Error('Failed to fetch issues')
  }
  
  return response.json() as Promise<Issue[]>
}

export async function deleteIssue(id: number): Promise<void> {
  const response = await fetch(`/api/issue/${id}`, {
    method: 'DELETE',
  })

  if (!response.ok) {
    throw new Error('Failed to delete issue')
  }
}

export async function patchIssue(data: IssuePatchVariables): Promise<Issue> {
  const response = await fetch(`/api/issue/${data.id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data.request),
  })

  if (!response.ok) {
    throw new Error('Failed to patch issue')
  }

  return response.json() as Promise<Issue>
}
