import type { Project } from '../types/project'

export async function getProjects(): Promise<Project[]> {
  const response = await fetch('/api/projects')
  if (!response.ok) throw new Error('Failed to fetch projects')
  return response.json()
}

export async function searchProjects(name: string): Promise<Project[]> {
  const response = await fetch(`/api/projects/search?name=${encodeURIComponent(name)}`)
  if (!response.ok) throw new Error('Failed to search projects')
  return response.json()
}

export async function getProject(id: number): Promise<Project> {
  const response = await fetch(`/api/projects/${id}`)
  if (!response.ok) throw new Error('Failed to fetch project')
  return response.json()
}

export async function createProject(name: string, exportDirectory: string): Promise<Project> {
  const response = await fetch('/api/projects', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams({ name, exportDirectory }),
  })
  if (!response.ok) throw new Error('Failed to create project')
  return response.json()
}

export async function deleteProject(id: number): Promise<void> {
  const response = await fetch(`/api/projects/${id}`, {
    method: 'DELETE',
  })
  if (!response.ok) throw new Error('Failed to delete project')
}

export async function updateProject(
  id: number,
  name: string,
  exportDirectory: string
): Promise<Project> {
  const response = await fetch(`/api/projects/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams({ name, exportDirectory }),
  })
  if (!response.ok) throw new Error('Failed to update project')
  return response.json()
}