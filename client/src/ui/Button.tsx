import { focusRing } from "./focusRing.ts"
import clsx from 'clsx'
import { twMerge } from 'tailwind-merge'
import type { ButtonHTMLAttributes } from 'react'

type Variant = 'primary' | 'secondary' | 'danger' | 'tertiary'

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
          'bg-slate-600 text-white hover:bg-slate-500': variant === 'primary',
          'bg-slate-700 text-white hover:bg-slate-650': variant === 'secondary',
          'bg-sky-700 text-white hover:bg-sky-600': variant === 'tertiary',
          'bg-red-600 text-white hover:bg-red-500': variant === 'danger',
        },
        className
      ))}
      {...props}
    />
  )
}
