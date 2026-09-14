import { useState, useRef } from 'react'
import Input from '../ui/Input'
import Button from '../ui/Button'

export default function ExportDirectory({ open, onClose }: { open: boolean; onClose: () => void }) {
  const [exportPath, setExportPath] = useState('')
  const directoryInputRef = useRef<HTMLInputElement | null>(null)

  if (!open) return null;

  const handlePathChange = (value: string) => {
    setExportPath(value)
  }

  const handleDirectoryClick = () => {
    directoryInputRef.current?.click()
  }

  const handleDirectoryChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files
    if (files && files.length > 0) {
      const file = files[0]
      // In Electron, we can access the file path
      // @ts-ignore: Electron provides path property on File objects
      if ((file as any).path) {
        // @ts-ignore: Electron provides path property on File objects
        setExportPath((file as any).path)
      } else {
        // Fallback for non-Electron environments
        setExportPath(file.name || '')
      }
    }
    // Reset the input value to allow selecting the same directory again
    if (directoryInputRef.current) {
      directoryInputRef.current.value = ''
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="relative bg-slate-800 border border-slate-600 rounded-lg w-96 p-6">
        {/* Close button */}
        <Button
          variant="secondary"
          onClick={onClose}
          className="absolute top-2 right-2 text-lg text-slate-400 hover:text-slate-200 p-0 m-0 w-8 h-8"
          aria-label="Close"
        >
          ✕
        </Button>
        <div className="pt-4">
          <h1 className="text-lg font-bold mb-4">
            Issue directory
          </h1>
          <div className="flex flex-row space-x-4">
            <Input
              variant="secondary"
              placeholder="Enter path"
              value={exportPath}
              onChange={handlePathChange}
              autoFocus
              className="w-auto border border-slate-600 bg-slate-800 text-slate-100 rounded mb-2 py-1 gap-1"
            />
            <Button
              variant="secondary"
              onClick={handleDirectoryClick}
              className="bg-slate-800 text-slate-100 hover:bg-slate-600 px-2 py-1 text-sm flex items-center gap-1"
            >
              📁 Browse
            </Button>
            {/* Hidden file input for directory selection */}
            {/* @ts-ignore: webkitdirectory and directory attributes are not in React's HTMLInputElement type */}
            <input
              type="file"
              // @ts-ignore
              webkitdirectory
              // @ts-ignore
              directory
              ref={directoryInputRef}
              style={{ display: 'none' }}
              onChange={handleDirectoryChange}
            />
          </div>
        </div>
      </div>
    </div>
  )
}
