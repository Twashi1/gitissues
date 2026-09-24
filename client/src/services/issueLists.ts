import type { IssueList } from '../types/issueList'

export async function getIssueLists(): Promise<IssueList[]> {
  const response = await fetch('/api/issue-lists')

  if (!response.ok) {
    throw new Error('Failed to fetch issue lists')
  }

  return response.json() as Promise<IssueList[]>
}

export async function getIssueListsByProject(projectId: number): Promise<IssueList[]> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists`)

  if (!response.ok) {
    throw new Error('Failed to fetch issue lists for project')
  }

  return response.json() as Promise<IssueList[]>
}

export async function createIssueList(title: string): Promise<IssueList> {
  const response = await fetch('/api/issue-lists', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ title }),
  })

  if (!response.ok) {
    throw new Error('Failed to create issue list')
  }

  return response.json() as Promise<IssueList>
}

export async function createIssueListForProject(projectId: number, title: string): Promise<IssueList> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ title }),
  })

  if (!response.ok) {
    throw new Error('Failed to create issue list for project')
  }

  return response.json() as Promise<IssueList>
}

export async function patchIssueList(id: number, title: string): Promise<IssueList> {
  const response = await fetch(`/api/issue-lists/${id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ title }),
  })

  if (!response.ok) {
    throw new Error('Failed to update issue list')
  }

  return response.json() as Promise<IssueList>
}

export async function patchIssueListForProject(projectId: number, id: number, title: string): Promise<IssueList> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists/${id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ title }),
  })

  if (!response.ok) {
    throw new Error('Failed to update issue list for project')
  }

  return response.json() as Promise<IssueList>
}

export async function deleteIssueList(id: number): Promise<void> {
  const response = await fetch(`/api/issue-lists/${id}`, {
    method: 'DELETE',
  })

  if (!response.ok) {
    throw new Error('Failed to delete issue list')
  }
}

export async function deleteIssueListForProject(projectId: number, id: number): Promise<void> {
  const response = await fetch(`/api/projects/${projectId}/issue-lists/${id}`, {
    method: 'DELETE',
  })

  if (!response.ok) {
    throw new Error('Failed to delete issue list for project')
  }
}
