import { useState } from 'react'

export default function RandomButton() {
  const [value, setValue] = useState<number | null>(null)
  const [loading, setLoading] = useState(false)

  const fetchRandom = async () => {
    setLoading(true)

    try {
      const res = await fetch('/api/random')

      if (!res.ok) {
        throw new Error('Request failed')
      }

      const data = await res.json()
      setValue(data.value)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <button onClick={fetchRandom} disabled={loading}>
        {loading ? 'Loading...' : 'Get Random Integer'}
      </button>

      {value !== null && <p>Random value: {value}</p>}
    </div>
  )
}
