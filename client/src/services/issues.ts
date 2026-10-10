import type { Issue, IssueCreateRequest, IssuePatchVariables } from '../types/issue'

export async function createIssue(projectId: number, data: IssueCreateRequest, listId: number): Promise<Issue> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists/${listId}/issues`, {
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

export async function getIssues(listId: number, projectId: number): Promise<Issue[]> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists/${listId}/issues`)

  if (!response.ok) {
    throw new Error('Failed to fetch issues')
  }

  return response.json() as Promise<Issue[]>
}

export async function createIssueByProject(projectId: number, data: IssueCreateRequest, listId: number): Promise<Issue> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists/${listId}/issues`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })

  if (!response.ok) {
    throw new Error('Failed to create issue for project')
  }

  return response.json() as Promise<Issue>
}

export async function getIssuesByProject(projectId: number, listId: number): Promise<Issue[]> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists/${listId}/issues`)

  if (!response.ok) {
    throw new Error('Failed to fetch issues for project')
  }

  return response.json() as Promise<Issue[]>
}

export async function deleteIssue(projectId: number, listId: number, id: number): Promise<void> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists/${listId}/issues/${id}`, {
    method: 'DELETE',
  })

  if (!response.ok) {
    throw new Error('Failed to delete issue')
  }
}

export async function deleteIssueByProject(projectId: number, listId: number, id: number): Promise<void> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists/${listId}/issues/${id}`, {
    method: 'DELETE',
  })

  if (!response.ok) {
    throw new Error('Failed to delete issue for project')
  }
}

export async function patchIssue(projectId: number, data: IssuePatchVariables, listId: number, id: number): Promise<Issue> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists/${listId}/issues/${id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })

  if (!response.ok) {
    throw new Error('Failed to patch issue')
  }

  return response.json() as Promise<Issue>
}

export async function patchIssueByProject(projectId: number, data: IssuePatchVariables, listId: number, id: number): Promise<Issue> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists/${listId}/issues/${id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data.request),
  })

  if (!response.ok) {
    throw new Error('Failed to patch issue for project')
  }

  return response.json() as Promise<Issue>
}

export async function moveIssue(projectId: number, issueId: number, listId: number): Promise<void> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists/${listId}/issues/${issueId}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ listId }),
  })

  if (!response.ok) {
    throw new Error('Failed to move issue')
  }
}

export async function moveIssueByProject(projectId: number, issueId: number, listId: number): Promise<void> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists/${listId}/issues/${issueId}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ listId }),
  })

  if (!response.ok) {
    throw new Error('Failed to move issue for project')
  }
}