import { useState } from 'react'
import { focusRing } from "../../ui/focusRing.ts"

const containerDark = "bg-slate-900 border border-slate-700 text-white"

const buttonBase = `px-3 py-2 rounded-md font-medium transition ${focusRing}`
const buttonDark = "bg-slate-600 text-white hover:bg-slate-500 disabled:opacity-50"

const inputBase = `w-full p-2 rounded-md border focus:outline-none transition ${focusRing}`
const inputDark = "bg-slate-800 border-slate-700 text-white"

export default function NewIssueForm({
  onSubmit,
  onCancel,
  loading,
}: {
  onSubmit: (title: string, description: string) => void
  onCancel: () => void
  loading: boolean
}) {
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')

  return (
    <div className={`mt-3 p-4 rounded-lg ${containerDark}`}>
      <div className="flex flex-col gap-3">
        <input
          className={`${inputBase} ${inputDark}`}
          placeholder="Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />

        <textarea
          className={`${inputBase} ${inputDark}`}
          placeholder="Description"
          rows={4}
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />

        <div className="flex gap-2 justify-end"> 
          <button
            className={`${buttonBase} ${buttonDark}`}
            onClick={onCancel}
            disabled={loading}
          >
            Cancel
          </button>

          <button
            className={`${buttonBase} ${buttonDark}`}
            onClick={() => onSubmit(title, description)}
            disabled={loading || !title.trim()}
          >
            {loading ? 'Submitting...' : 'Submit'}
          </button>
        </div>
      </div>
    </div>
  )
}
