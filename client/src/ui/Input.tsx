import { focusRing } from "./focusRing.ts"
import clsx from 'clsx'
import { twMerge } from 'tailwind-merge'
import type { InputHTMLAttributes } from 'react'

type Variant = 'primary' | 'secondary'

type Props = InputHTMLAttributes<HTMLInputElement> & {
  variant?: Variant
  placeholder?: string
}

export default function Input({
  variant = 'primary',
  placeholder = 'Enter text',
  className,
  ...props
}: Props) {
  return (
    <input
      className={twMerge(clsx(
        'w-full p-2 rounded-md shadow-sm text-sm font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed disable:hover:bg-slate-800 focus:outline-none transition',
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

