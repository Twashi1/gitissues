import { useEffect, useRef, useState } from 'react'
import Slider from '@/ui/Slider'

interface UniformConfig {
  name: string
  type: 'float' | 'int' | 'vec2' | 'vec3'
  min: number
  max: number
  step: number
  default: number | [number, number] | [number, number, number]
}

// We'll expose a subset of uniforms for tuning
const uniformConfigs: UniformConfig[] = [
  { name: 'u_Scale', type: 'float', min: 0.1, max: 2.0, step: 0.01, default: 0.5 },
  { name: 'u_AngularVelocity', type: 'float', min: 0.0, max: 0.5, step: 0.001, default: 0.05 },
  { name: 'u_CellularDensity', type: 'float', min: 0.5, max: 10.0, step: 0.1, default: 4.0 },
  { name: 'u_Octaves', type: 'int', min: 1, max: 8, step: 1, default: 4 },
  { name: 'u_FractalScaling', type: 'float', min: 1.0, max: 4.0, step: 0.1, default: 2.0 },
  { name: 'u_Sharpness', type: 'float', min: 1.0, max: 8.0, step: 0.1, default: 3.0 },
  { name: 'u_ColorGamma', type: 'float', min: 0.5, max: 3.0, step: 0.01, default: 1.75 },
  { name: 'u_LuminosityOffset', type: 'float', min: 0.0, max: 1.0, step: 0.01, default: 0.5 },
  { name: 'u_BaseColor', type: 'vec3', min: 0, max: 1, step: 0.01, default: [0.3, 0.3, 0.5] },
  { name: 'u_BorderSize', type: 'float', min: 0.0, max: 0.5, step: 0.01, default: 0.0 },
]

export default function ShaderDevPage() {
  const canvasRef = useRef<HTMLCanvasElement | null>(null)
  const [uniforms, setUniforms] = useState<Record<string, number | [number, number] | [number, number, number]>>({})
  const [isLoading, setIsLoading] = useState(true)
  const glRef = useRef<WebGLRenderingContext | null>(null)
  const programRef = useRef<WebGLProgram | null>(null)
  const uniformLocationsRef = useRef<Record<string, WebGLUniformLocation | null>>({})
  const resolutionLocationRef = useRef<WebGLUniformLocation | null>(null)
  const timeLocationRef = useRef<WebGLUniformLocation | null>(null)
  const renderRequestedRef = useRef(false)
  const startTimeRef = useRef<number>(0)

  // Initialize uniforms with default values
  useEffect(() => {
    const initialUniforms: Record<string, number | [number, number] | [number, number, number]> = {}
    uniformConfigs.forEach(config => {
      initialUniforms[config.name] = config.default
    })
    setUniforms(initialUniforms)
  }, [])

  // Set up WebGL, compile shaders, and get uniform locations (run once)
  useEffect(() => {
    const canvasMaybeNull = canvasRef.current
    if (!canvasMaybeNull) return

    const canvas = canvasMaybeNull
    const glMaybeNull = canvas.getContext('webgl')
    if (!glMaybeNull) return

    const gl = glMaybeNull
    glRef.current = gl

    gl.enable(gl.BLEND)
    gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA)

    // Fetch shaders dynamically
    fetch('/shaders/worley_vert.glsl')
      .then(response => response.text())
      .then(vertexShaderSource => {
        return fetch('/shaders/worley_frag.glsl')
          .then(response => response.text())
          .then(fragmentShaderSource => {
            return { vertexShaderSource, fragmentShaderSource }
          })
      })
      .then(({ vertexShaderSource, fragmentShaderSource }) => {
        function compileShader(type: number, source: string) {
          if (!gl) return null

          const shader = gl.createShader(type)
          if (!shader) {
            console.error('Failed to create shader')
            return null
          }

          gl.shaderSource(shader, source)
          gl.compileShader(shader)

          // Check if shader compiled successfully
          const success = gl.getShaderParameter(shader, gl.COMPILE_STATUS)
          if (!success) {
            const infoLog = gl.getShaderInfoLog(shader)
            console.error('Shader compilation failed:', infoLog)
            gl.deleteShader(shader)
            return null
          }

          return shader
        }

        const vertexShader = compileShader(gl.VERTEX_SHADER, vertexShaderSource)
        const fragmentShader = compileShader(gl.FRAGMENT_SHADER, fragmentShaderSource)

        if (!vertexShader || !fragmentShader) {
          // Clean up shaders if compilation failed
          if (vertexShader) gl.deleteShader(vertexShader)
          if (fragmentShader) gl.deleteShader(fragmentShader)
          setIsLoading(false)
          return
        }

        const program = gl.createProgram()
        if (!program) {
          console.error('Failed to create shader program')
          gl.deleteShader(vertexShader)
          gl.deleteShader(fragmentShader)
          setIsLoading(false)
          return
        }

        gl.attachShader(program, vertexShader)
        gl.attachShader(program, fragmentShader)
        gl.linkProgram(program)

        // Check if program linked successfully
        const linkSuccess = gl.getProgramParameter(program, gl.LINK_STATUS)
        if (!linkSuccess) {
          const infoLog = gl.getProgramInfoLog(program)
          console.error('Shader program linking failed:', infoLog)
          gl.deleteProgram(program)
          gl.deleteShader(vertexShader)
          gl.deleteShader(fragmentShader)
          setIsLoading(false)
          return
        }

        gl.useProgram(program)
        programRef.current = program

        const positionBuffer = gl.createBuffer()
        gl.bindBuffer(gl.ARRAY_BUFFER, positionBuffer)

        const vertices = new Float32Array([
          -1, -1,
           1, -1,
          -1,  1,
          -1,  1,
           1, -1,
           1,  1,
        ])

        gl.bufferData(gl.ARRAY_BUFFER, vertices, gl.STATIC_DRAW)

        const positionLocation = gl.getAttribLocation(program, 'position')
        gl.enableVertexAttribArray(positionLocation)
        gl.vertexAttribPointer(positionLocation, 2, gl.FLOAT, false, 0, 0)

        // Get uniform locations
        const resolutionLocation = gl.getUniformLocation(program, 'u_resolution')
        const timeLocation = gl.getUniformLocation(program, 'u_Time')
        resolutionLocationRef.current = resolutionLocation
        timeLocationRef.current = timeLocation

        uniformConfigs.forEach(config => {
          uniformLocationsRef.current[config.name] = gl.getUniformLocation(program, config.name)
        })

        // Set initial uniforms
        Object.entries(uniforms).forEach(([name, value]) => {
          const location = uniformLocationsRef.current[name]
          if (!location || !glRef.current) return

          const config = uniformConfigs.find(c => c.name === name)
          if (!config) return

          switch (config.type) {
            case 'float':
              if (typeof value === 'number') {
                glRef.current.uniform1f(location, value)
              }
              break
            case 'int':
              if (typeof value === 'number') {
                glRef.current.uniform1i(location, value)
              }
              break
            case 'vec2':
              if (Array.isArray(value) && value.length === 2) {
                glRef.current.uniform2f(location, value[0], value[1])
              }
              break
            case 'vec3':
              if (Array.isArray(value) && value.length === 3) {
                glRef.current.uniform3f(location, value[0], value[1], value[2])
              }
              break
          }
        })

        // Set up resize handler
        function resize() {
          if (!canvas) return

          const dpr = window.devicePixelRatio || 1
          canvas.width = window.innerWidth * dpr
          canvas.height = window.innerHeight * dpr
          gl.viewport(0, 0, canvas.width, canvas.height)
        }

        window.addEventListener('resize', resize)
        resize()

        // Start render loop if not already started
        if (!renderRequestedRef.current) {
          renderRequestedRef.current = true
          startTimeRef.current = performance.now()

          function render(now: number) {
            if (!glRef.current || !programRef.current) return

            const time = (now - startTimeRef.current) * 0.01

            gl.clear(gl.COLOR_BUFFER_BIT)

            if (resolutionLocationRef.current) {
              gl.uniform2f(resolutionLocationRef.current, canvas.width, canvas.height)
            }
            if (timeLocationRef.current) {
              gl.uniform1f(timeLocationRef.current, time)
            }

            // Update uniforms
            Object.entries(uniforms).forEach(([name, value]) => {
              const location = uniformLocationsRef.current[name]
              if (!location) return

              const config = uniformConfigs.find(c => c.name === name)
              if (!config) return

              switch (config.type) {
                case 'float':
                  if (typeof value === 'number' && glRef.current) {
                    glRef.current.uniform1f(location, value)
                  }
                  break
                case 'int':
                  if (typeof value === 'number' && glRef.current) {
                    glRef.current.uniform1i(location, value)
                  }
                  break
                case 'vec2':
                  if (Array.isArray(value) && value.length === 2 && glRef.current) {
                    glRef.current.uniform2f(location, value[0], value[1])
                  }
                  break
                case 'vec3':
                  if (Array.isArray(value) && value.length === 3 && glRef.current) {
                    glRef.current.uniform3f(location, value[0], value[1], value[2])
                  }
                  break
              }
            })

            gl.drawArrays(gl.TRIANGLES, 0, 6)

            requestAnimationFrame(render)
          }

          requestAnimationFrame(render)
        }

        setIsLoading(false)
        return () => {
          window.removeEventListener('resize', resize)
          if (programRef.current) {
            gl.deleteProgram(programRef.current)
          }
          // Shaders are deleted when the program is deleted
        }
      })
      .catch(error => {
        console.error('Failed to load shaders:', error)
        setIsLoading(false)
      })
  }, [])

  // Update uniforms when they change (without recompiling shaders)
  useEffect(() => {
    if (!glRef.current || !programRef.current || isLoading) return

    glRef.current.useProgram(programRef.current)
    Object.entries(uniforms).forEach(([name, value]) => {
      const location = uniformLocationsRef.current[name]
      if (!location) return

      const config = uniformConfigs.find(c => c.name === name)
      if (!config) return

      switch (config.type) {
        case 'float':
          if (typeof value === 'number' && glRef.current) {
            glRef.current.uniform1f(location, value)
          }
          break
        case 'int':
          if (typeof value === 'number' && glRef.current) {
            glRef.current.uniform1i(location, value)
          }
          break
        case 'vec2':
          if (Array.isArray(value) && value.length === 2 && glRef.current) {
            glRef.current.uniform2f(location, value[0], value[1])
          }
          break
        case 'vec3':
          if (Array.isArray(value) && value.length === 3 && glRef.current) {
            glRef.current.uniform3f(location, value[0], value[1], value[2])
          }
          break
      }
    })
  }, [uniforms, isLoading])

  const handleReset = () => {
    const resetValues: Record<string, number | [number, number] | [number, number, number]> = {}
    uniformConfigs.forEach(config => {
      resetValues[config.name] = config.default
    })
    setUniforms(resetValues)
  }

  return (
    <div className="flex h-screen bg-slate-900 text-slate-100 p-6 gap-6">
      {/* Controls Panel */}
      <div className="w-64 flex flex-col gap-4">
        <h2 className="text-xl font-bold">Shader Controls</h2>

        <div className="space-y-4">
          {uniformConfigs.map(config => {
            // For vec2/vec3 sliders, we only show/edit the first component
            const value = Array.isArray(uniforms[config.name])
              ? (uniforms[config.name] as number[])[0]
              : (uniforms[config.name] as number)

            return (
              <div key={config.name} className="space-y-2">
                <Slider
                  label={config.name}
                  valueLabel={true}
                  min={config.min}
                  max={config.max}
                  step={config.step}
                  value={value}
                  onChange={(value: string | React.ChangeEvent<HTMLInputElement>) => {
                    const eventValue = typeof value === 'string' ? value : value.target.value;
                    const parsedValue = parseFloat(eventValue)
                    setUniforms(prev => {
                      const updated = {...prev}
                      if (config.type === 'vec2') {
                        updated[config.name] = [parsedValue, (prev[config.name] as [number, number])[1]]
                      } else if (config.type === 'vec3') {
                        updated[config.name] = [parsedValue, (prev[config.name] as [number, number, number])[1], (prev[config.name] as [number, number, number])[2]]
                      } else {
                        updated[config.name] = parsedValue
                      }
                      return updated
                    })
                  }}
                />
              </div>
            )
          })}

          <button
            onClick={handleReset}
            className="w-full px-4 py-2 bg-slate-600 hover:bg-slate-500 rounded text-sm"
          >
            Reset to Defaults
          </button>
        </div>
      </div>

      {/* Preview Canvas */}
      <div className="flex-1 relative">
        <canvas
          ref={canvasRef}
          className="w-full h-full border border-slate-600 bg-black"
        />
        {isLoading && (
          <div className="absolute inset-0 flex items-center justify-center text-slate-500 text-sm pointer-events-none">
            Loading shaders...
          </div>
        )}
        {!isLoading && (
          <div className="absolute inset-0 flex items-center justify-center text-slate-500 text-sm pointer-events-none">
            Shader Preview
          </div>
        )}
      </div>
    </div>
  )
}
