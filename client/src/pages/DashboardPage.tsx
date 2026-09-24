import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'

import { getProjects, createProject } from '../services/projects'
import type { Project } from '../types/project'

export default function DashboardPage() {
  const [projects, setProjects] = useState<Project[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)
  const [newProjectName, setNewProjectName] = useState<string>('')
  const [showNewProjectForm, setShowNewProjectForm] = useState(false)
  const [creatingProject, setCreatingProject] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    fetchProjects()
  }, [])

  const fetchProjects = async () => {
    setLoading(true)
    setError(null)
    try {
      const projs = await getProjects()
      setProjects(projs)
    } catch (err) {
      console.error('Failed to fetch projects:', err)
      setError('Failed to fetch projects')
      setProjects([])
    } finally {
      setLoading(false)
    }
  }

  const handleCreateProject = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!newProjectName.trim()) return
    setCreatingProject(true)
    try {
      // Use a default export directory when creating from dashboard
      const defaultExportDirectory = `./projects/${newProjectName.toLowerCase().replace(/\s+/g, '-')}`
      const newProject = await createProject(newProjectName, defaultExportDirectory)
      setProjects(prev => [...prev, newProject])
      setNewProjectName('')
      setShowNewProjectForm(false)
    } catch (err) {
      console.error('Failed to create project:', err)
      setError('Failed to create project')
    } finally {
      setCreatingProject(false)
    }
  }

  return (
    <section id="projects" className="mb-6">
      {loading && projects.length === 0 ? (
        <p className="text-center text-slate-400">Loading projects...</p>
      ) : error ? (
        <p className="text-center text-red-400">{error}</p>
      ) : projects.length === 0 ? (
        <>
          <p className="text-center text-slate-400">No projects found</p>
          <div className="mt-6 text-center">
            <button
              onClick={() => setShowNewProjectForm(true)}
              className="px-4 py-2 bg-slate-600 text-slate-100 hover:bg-slate-500 rounded"
            >
              New Project
            </button>
          </div>
        </>
      ) : (
        <>
          <h2 className="text-xl font-semibold mb-4">Projects</h2>
          <div className="space-y-4">
            {projects.map(project => (
              <div
                key={project.id}
                onClick={() => navigate(`/project/${project.id}`)}
                className="cursor-pointer p-4 border border-slate-600/50 rounded hover:border-slate-500 transition-colors"
              >
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="font-medium">{project.name}</h3>
                    <p className="text-slate-400 text-sm">
                      Export Directory: {project.exportDirectory}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
          {projects.length > 0 && (
            <div className="mt-6 flex justify-end">
              <button
                onClick={() => setShowNewProjectForm(true)}
                className="px-4 py-2 bg-slate-600 text-slate-100 hover:bg-slate-500 rounded"
              >
                New Project
              </button>
            </div>
          )}
        </>
      )}
      {showNewProjectForm && (
        <form onSubmit={handleCreateProject} className="mt-6 flex flex-col gap-4">
          <div>
            <label className="block text-sm font-medium mb-2">Project Name</label>
            <input
              type="text"
              value={newProjectName}
              onChange={(e) => setNewProjectName(e.target.value)}
              placeholder="Enter project name"
              className="w-full px-3 py-2 border border-slate-600 rounded bg-slate-800 text-slate-100"
              autoFocus
            />
          </div>
          <div className="flex gap-3 justify-end">
            <button
              type="button"
              onClick={() => {
                setNewProjectName('')
                setShowNewProjectForm(false)
              }}
              className="px-4 py-2 bg-slate-600 text-slate-100 hover:bg-slate-500 rounded"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={creatingProject}
              className="px-4 py-2 bg-slate-600 text-slate-100 hover:bg-slate-500 rounded"
            >
              {creatingProject ? 'Creating...' : 'Create Project'}
            </button>
          </div>
        </form>
      )}
    </section>
  )
}