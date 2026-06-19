import { useEffect, useRef } from 'react'
import worleyFrag from '../../assets/shaders/worley_frag.glsl?raw'
import worleyVert from '../../assets/shaders/worley_vert.glsl?raw'

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
      if (!gl) return

      const shader = gl.createShader(type)!
      gl.shaderSource(shader, source)
      gl.compileShader(shader)
      return shader
    }

    console.log(worleyVert)
    console.log(worleyFrag)

    const vertexShader = compileShader(gl.VERTEX_SHADER, worleyVert)
    const fragmentShader = compileShader(gl.FRAGMENT_SHADER, worleyFrag)

    if (!vertexShader) return
    if (!fragmentShader) return

    const program = gl.createProgram()!
    gl.attachShader(program, vertexShader)
    gl.attachShader(program, fragmentShader)
    gl.linkProgram(program)
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

    const resolutionLocation = gl.getUniformLocation(program, 'u_resolution')
    const timeLocation = gl.getUniformLocation(program, 'u_Time')


// uniform float u_Scale = 1.0;
// uniform uint  u_Seed = 0;
// uniform float u_AngularVelocity = 0.5;
// uniform float u_Time = 0.0;
// uniform float u_CellularDensity = 3.0;
// uniform float u_Sharpness = 5.0;
// uniform float u_ColorGamma = 1.75; 
// uniform float u_LuminosityOffset = 0.5;
// uniform vec3  u_BaseColor = vec3(0.9, 0.1, 0.1);
// uniform float u_PI = 3.14159265358;
//
// uniform vec2 u_resolution;
// uniform float u_Pixelation = 40.0;
// uniform float u_LessPixelation = 160.0;

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

      const time = (now - start) * 0.001

      gl.clear(gl.COLOR_BUFFER_BIT)

      gl.uniform2f(resolutionLocation, canvas.width, canvas.height)
      gl.uniform1f(timeLocation, time)

      gl.drawArrays(gl.TRIANGLES, 0, 6)

      requestAnimationFrame(render)
    }

    requestAnimationFrame(render)

    return () => window.removeEventListener('resize', resize)
  }, [])

  return (
    <canvas
      ref={canvasRef}
      className="absolute inset-0 z-0 w-full h-full"
    />
  )
}
