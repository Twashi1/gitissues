import { useState, useRef } from 'react'
import Input from '../ui/Input'
import Button from '../ui/Button'

export default function ExportDirectory({ open, onClose, initial, onSubmit }: { open: boolean; onClose: () => void; initial: string; onSubmit: (value: string) => void }) {
  const [exportPath, setExportPath] = useState<string>(initial)
  const directoryInputRef = useRef<HTMLInputElement | null>(null)

  // useEffect(() => {
  //   directoryInputRef.current?.setAttribute('webkitdirectory', '')
  // })

  if (!open) return null;

  const handlePathChange = (value: string) => {
    setExportPath(value)
  }

  const handleSubmit = (value: string) => {
    onSubmit(value)
  }

  // const handleDirectoryClick = () => {
  //   directoryInputRef.current?.click()
  // }

  const handleBrowseDirectory = async () => {
    const path = await window.electronAPI.selectDirectory();

    if (path) {
      setExportPath(path);
      handleSubmit(path);
    }
  }

  // const handleDirectoryChange = (e: React.ChangeEvent<HTMLInputElement>) => {
  //   const files = e.target.files
  //   if (files && files.length > 0) {
  //     const file = files[0]
  //     // In Electron, we can access the file path
  //     // @ts-ignore: Electron provides path property on File objects
  //     if ((file as any).path) {
  //       // @ts-ignore: Electron provides path property on File objects
  //       setExportPath((file as any).path)
  //       handleSubmit((file as any).path)
  //     } else {
  //       // Fallback for non-Electron environments
  //       setExportPath(file.name || '')
  //       handleSubmit(file.name || '')
  //     }
  //   }
  //   // Reset the input value to allow selecting the same directory again
  //   if (directoryInputRef.current) {
  //     directoryInputRef.current.value = ''
  //   }
  // }

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
              onSave={handleSubmit}
              autoFocus
              className="w-auto border border-slate-600 bg-slate-800 text-slate-100 rounded mb-2 py-1 gap-1"
            />
            <Button
              variant="secondary"
              onClick={handleBrowseDirectory}
              className="bg-slate-800 text-slate-100 hover:bg-slate-600 px-2 py-1 text-sm flex items-center gap-1"
            >
              📁 Browse
            </Button>
            {/* Hidden file input for directory selection */}
            {// <input
            //   type="file"
            //   ref={directoryInputRef}
            //   style={{ display: 'none' }}
            //   onChange={handleBrowseDirectory}
            // />
            }
          </div>
        </div>
      </div>
    </div>
  )
}
