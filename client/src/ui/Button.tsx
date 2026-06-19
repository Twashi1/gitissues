import { focusRing } from "./focusRing.ts"
import clsx from 'clsx'
import { twMerge } from 'tailwind-merge'
import type { ButtonHTMLAttributes } from 'react'

type Variant = 'primary' | 'secondary' | 'danger'

type Props = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: Variant
}

export default function Button({
  variant = 'primary',
  className,
  ...props
}: Props) {
  return (
    <button
      type='button'
      className={twMerge(clsx(
        'px-3 py-2 rounded-md shadow-sm text-sm font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-slate-800',
        focusRing,
        {
          'bg-sky-600 text-white hover:bg-sky-500': variant === 'primary',
          'bg-slate-700 text-white hover:bg-slate-600': variant === 'secondary',
          'bg-red-600 text-white hover:bg-red-500': variant === 'danger',
        },
        className
      ))}
      {...props}
    />
  )
}
