import { focusRing } from "./focusRing.ts"
import clsx from 'clsx'
import { twMerge } from 'tailwind-merge'
import { useState, useEffect } from 'react'
import type { InputHTMLAttributes, ChangeEvent } from 'react'

type Variant = 'primary' | 'secondary'

type InputProps = Omit<InputHTMLAttributes<HTMLInputElement>, 'onChange' | 'onBlur' | 'onKeyDown'> & {
  variant?: Variant
  placeholder?: string
  /** Called on every input change with the current value */
  onChange?: (value: string) => void
  /** Called on save (Enter or blur). If not provided, defaults to calling onChange with current value */
  onSave?: (value: string) => void
  /** Called on cancel (Escape). If not provided, defaults to resetting to original value and calling onChange */
  onCancel?: () => void
}

export default function Input({
  variant = 'primary',
  placeholder = 'Enter text',
  className,
  value,
  defaultValue,
  onChange,
  onSave,
  onCancel,
  ...props
}: InputProps) {
  // Draft value for internal state
  const [draft, setDraft] = useState(() => {
    // Initialize from value prop if provided, else defaultValue, else empty string
    if (value !== undefined) return String(value)
    if (defaultValue !== undefined) return String(defaultValue)
    return ''
  })

  // Keep draft in sync with controlled value prop
  useEffect(() => {
    if (value !== undefined) {
      setDraft(String(value))
    }
  }, [value])

  // Handle internal input change
  const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value
    setDraft(val)
    onChange?.(val)
  }

  // Handle save (blur or Enter)
  const handleSave = () => {
    const val = draft
    if (onSave !== undefined) {
      onSave(val)
    } else {
      // Default save behavior: call onChange if provided
      onChange?.(val)
    }
  }

  // Handle cancel (Escape)
  const handleCancel = () => {
    // Determine original value to reset to
    const original = String(value ?? defaultValue ?? '')
    setDraft(original)
    if (onCancel !== undefined) {
      onCancel()
    } else {
      // Default cancel behavior: call onChange with original value if provided
      onChange?.(original)
    }
  }

  // Handle key down for Enter/Esc
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      handleSave()
    } else if (e.key === 'Escape') {
      e.preventDefault()
      handleCancel()
    }
  }

  return (
    <input
      value={draft}
      onChange={handleInputChange}
      onBlur={handleSave}
      onKeyDown={handleKeyDown}
      className={twMerge(clsx(
        'w-full p-2 rounded-md shadow-sm text-sm font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed disable:hover:bg-slate-800 focus:outline-none transition border border-slate-800',
        focusRing,
        {
          'bg-slate-600 border-slate-500 text-white hover:bg-slate-500': variant === 'primary',
          'bg-slate-800 border-slate-700 text-white hover:bg-slate-700': variant === 'secondary',
        },
        className
      ))}
      placeholder={placeholder}
      {...props}
    />
  )
}