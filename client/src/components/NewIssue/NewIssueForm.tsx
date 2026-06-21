import { useState } from 'react'
import Input from "../../ui/Input.tsx"
import Button from "../../ui/Button.tsx"
import TextArea from "../../ui/TextArea.tsx"

const containerDark = "bg-slate-900 border border-slate-700 text-white"


export default function NewIssueForm({
  onSubmit,
  onCancel,
  loading,
}: {
  onSubmit: (title: string, description: string) => void
  onCancel: () => void
  loading: boolean
}) {
  const [description, setDescription] = useState('')
  const [title, setTitle] = useState('')

  return (
    <div className={`mt-3 p-4 rounded-lg ${containerDark}`}>
      <div className="flex flex-col gap-3">
        <Input variant="primary" placeholder="Title" value={title} onChange={(e) => setTitle(e.target.value)} />

        <TextArea
          placeholder="Description"
          rows={4}
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />

        <div className="flex gap-2 justify-end"> 
          <Button variant="secondary" onClick={onCancel} disabled={loading}>
            Cancel
          </Button>

          <Button variant="secondary" onClick={() => onSubmit(title, description)} disabled={loading || !title.trim()}>
            {loading ? 'Submitting...' : 'Submit'}
          </Button>
        </div>
      </div>
    </div>
  )
}
