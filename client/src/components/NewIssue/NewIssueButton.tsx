import Button from '../../ui/Button'

export default function NewIssueButton({
  onClick,
  disabled = false,
}: {
  onClick: () => void
  disabled?: boolean
}) {
  return (
    <Button
      onClick={onClick}
      disabled={disabled}
      className='
        inline-flex
        items-center
        gap-2
        px-4
        py-2
        transition
      '
    >
      <span className="text-lg leading-none">+</span>
      New issue
    </Button>
  ) 
}
