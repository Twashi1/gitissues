import { focusRing } from "./focusRing"
import clsx from 'clsx'
import { twMerge } from 'tailwind-merge'
import type { InputHTMLAttributes } from 'react'

type Variant = 'primary' | 'secondary'

type Props = InputHTMLAttributes<HTMLInputElement> & {
  variant?: Variant
  label?: string
  valueLabel?: boolean
  min?: number
  max?: number
  step?: number
}

export default function Slider({
  variant = 'primary',
  label,
  valueLabel = true,
  min = 0,
  max = 100,
  step = 1,
  value,
  className,
  onChange,
  ...props
}: Props & {
  value: number | string
  onChange: (value: string) => void
}) {
  const valueToString = typeof value === 'number' ? value.toString() : value;

  return (
    <div className="space-y-2">
      {label && (
        <label className="text-sm font-medium text-slate-100">
          {label}
        </label>
      )}
      <div className="flex items-center space-x-2">
        <input
          type="range"
          className={twMerge(clsx(
            'h-2 w-full bg-slate-600 rounded',
            focusRing,
            {
              'bg-slate-600': variant === 'primary',
              'bg-slate-500': variant === 'secondary',
            },
            className
          ))}
          min={min}
          max={max}
          step={step}
          value={valueToString}
          onChange={(e) => onChange(e.target.value)}
          {...props}
        />
        {valueLabel && (
          <span className="text-sm font-mono text-slate-400 min-w-8">
            {valueToString}
          </span>
        )}
      </div>
    </div>
  )
}