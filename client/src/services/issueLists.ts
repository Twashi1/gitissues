import type { IssueList } from '../types/issueList'

export async function getIssueLists(): Promise<IssueList[]> {
  const response = await fetch('/api/issue-lists')

  if (!response.ok) {
    throw new Error('Failed to fetch issue lists')
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

export async function updateIssueList(id: number, title: string): Promise<IssueList> {
  const response = await fetch(`/api/issue-lists/${id}`, {
    method: 'PUT',
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

export async function deleteIssueList(id: number): Promise<void> {
  const response = await fetch(`/api/issue-lists/${id}`, {
    method: 'DELETE',
  })

  if (!response.ok) {
    throw new Error('Failed to delete issue list')
  }
}