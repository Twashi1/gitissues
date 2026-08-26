import { useEffect, useRef } from 'react'

export default function WorleyBackground() {
  const canvasRef = useRef<HTMLCanvasElement | null>(null)

  useEffect(() => {
    const canvasMaybeNull = canvasRef.current
    if (!canvasMaybeNull) return

    const canvas = canvasMaybeNull
    const glMaybeNull = canvas.getContext('webgl')
    if (!glMaybeNull) return

    const gl = glMaybeNull

    gl.enable(gl.BLEND)
    gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA)

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
        const vertexShader = compileShader(gl.VERTEX_SHADER, vertexShaderSource)
        const fragmentShader = compileShader(gl.FRAGMENT_SHADER, fragmentShaderSource)

        if (!vertexShader || !fragmentShader) {
          // Clean up shaders if compilation failed
          if (vertexShader) gl.deleteShader(vertexShader)
          if (fragmentShader) gl.deleteShader(fragmentShader)
          return
        }

        const program = gl.createProgram()
        if (!program) {
          console.error('Failed to create shader program')
          gl.deleteShader(vertexShader)
          gl.deleteShader(fragmentShader)
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
          return
        }

        gl.useProgram(program)

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
        const scaleLocation = gl.getUniformLocation(program, 'u_Scale')
        const seedLocation = gl.getUniformLocation(program, 'u_Seed')
        const angularVelocityLocation = gl.getUniformLocation(program, 'u_AngularVelocity')
        const cellularDensityLocation = gl.getUniformLocation(program, 'u_CellularDensity')
        const octavesLocation = gl.getUniformLocation(program, 'u_Octaves')
        const fractalScalingLocation = gl.getUniformLocation(program, 'u_FractalScaling')
        const sharpnessLocation = gl.getUniformLocation(program, 'u_Sharpness')
        const colorGammaLocation = gl.getUniformLocation(program, 'u_ColorGamma')
        const luminosityOffsetLocation = gl.getUniformLocation(program, 'u_LuminosityOffset')
        const baseColorLocation = gl.getUniformLocation(program, 'u_BaseColor')
        const borderSizeLocation = gl.getUniformLocation(program, 'u_BorderSize')

        // Set default uniform values
        if (scaleLocation !== null) gl.uniform1f(scaleLocation, 1.0)
        if (seedLocation !== null) gl.uniform1i(seedLocation, 0)
        if (angularVelocityLocation !== null) gl.uniform1f(angularVelocityLocation, 0.05)
        if (cellularDensityLocation !== null) gl.uniform1f(cellularDensityLocation, 4.0)
        if (octavesLocation !== null) gl.uniform1i(octavesLocation, 4)
        if (fractalScalingLocation !== null) gl.uniform1f(fractalScalingLocation, 2.0)
        if (sharpnessLocation !== null) gl.uniform1f(sharpnessLocation, 4.5)
        if (colorGammaLocation !== null) gl.uniform1f(colorGammaLocation, 1.75)
        if (luminosityOffsetLocation !== null) gl.uniform1f(luminosityOffsetLocation, 0.7)
        if (baseColorLocation !== null) gl.uniform3f(baseColorLocation, 0.3, 0.3, 0.5)
        if (borderSizeLocation !== null) gl.uniform1f(borderSizeLocation, 0.0)

        function resize() {
          if (!canvas) return

          const dpr = window.devicePixelRatio || 1
          canvas.width = window.innerWidth * dpr
          canvas.height = window.innerHeight * dpr
          gl.viewport(0, 0, canvas.width, canvas.height)
        }

        window.addEventListener('resize', resize)
        resize()

        let start = performance.now()

        function render(now: number) {
          if (!gl) return

          const time = (now - start) * 0.01

          gl.clear(gl.COLOR_BUFFER_BIT)

          if (resolutionLocation) gl.uniform2f(resolutionLocation, canvas.width, canvas.height)
          if (timeLocation) gl.uniform1f(timeLocation, time)

          gl.drawArrays(gl.TRIANGLES, 0, 6)

          requestAnimationFrame(render)
        }

        requestAnimationFrame(render)

        return () => {
          window.removeEventListener('resize', resize)
          if (program) {
            gl.deleteProgram(program)
          }
          // Shaders are deleted when the program is deleted
        }
      })
      .catch(error => {
        console.error('Failed to load shaders:', error)
      })
  }, [])

  return (
    <canvas
      ref={canvasRef}
      className="absolute inset-0 z-0 w-full h-full"
    />
  )
}
