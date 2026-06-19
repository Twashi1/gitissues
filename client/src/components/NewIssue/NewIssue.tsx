import { useState } from 'react'
import { useQueryClient, useMutation } from '@tanstack/react-query'
import clsx from 'clsx'

import NewIssueButton from './NewIssueButton'
import NewIssueForm from './NewIssueForm'
import { createIssue } from '../../services/issues'

export default function NewIssue() {
  const queryClient = useQueryClient()
  const [open, setOpen] = useState(false)

  const mutation = useMutation({
    mutationFn: createIssue,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['issues'] })
      setOpen(false)
    },
  })

  const handleSubmit = async (title: string, description: string) => {
    mutation.mutate({
      title,
      description,
      status: 'UNASSIGNED',
    })
  }

  return (
    <div className="max-w-lg">
      {!open && <NewIssueButton onClick={() => setOpen(true)} />}

      <div
        className={clsx('transition-all duration-200', {
          'max-h-96 opacity-100': open,
          'max-h-0 opacity-0': !open,
        })}
      >
        {open && (
          <NewIssueForm
            onSubmit={handleSubmit}
            onCancel={() => setOpen(false)}
            loading={mutation.isPending}
          />
        )}
      </div>

      {mutation.error && (
        <p className="text-red-500 text-sm">
          {(mutation.error as Error).message}
        </p>
      )}
    </div>
  )
}
