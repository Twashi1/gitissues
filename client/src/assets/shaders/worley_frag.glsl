precision mediump float;

const float u_Scale = 0.5;
const int u_Seed = 0;
const float u_AngularVelocity = 2.0;
uniform float u_Time;
const float u_CellularDensity = 4.0;
const float u_Sharpness = 4.0;
const float u_ColorGamma = 1.35; 
const float u_LuminosityOffset = 0.5;
const vec3  u_BaseColor = vec3(0.3, 0.3, 0.5);
const float u_PI = 3.14159265358;

uniform vec2 u_resolution;
const float u_Pixelation = 1000.0;
const float u_LessPixelation = 160.0;

vec2 random2(vec2 p) {
    return fract(sin(vec2(dot(p,vec2(127.1 + float(u_Seed),311.7)),dot(p,vec2(269.5,183.3))))*43758.5453);
}

vec2 rotated_random(vec2 p, vec2 matrix_left, vec2 matrix_right) {
    const vec2 pivot = vec2(0.5, 0.5);

    vec2 random = random2(p) * 0.5 - pivot;
    vec2 rotated = vec2(0.0, 0.0);
    rotated.x = random.x * matrix_left.x + random.y * matrix_right.x;
    rotated.y = random.x * matrix_left.y + random.y * matrix_right.y;
    rotated += pivot;

    return random;
}

float random_float (float h) {
    return fract(sin(h) * 43758.5453123);
}

vec4 get_fluid_texture() {
    float alpha = 1.0;

    vec2 v_texCoord = gl_FragCoord.xy / u_resolution;

    vec2 pixelated_uv = floor(v_texCoord * u_Pixelation + 0.5) / u_Pixelation;
    vec2 less_pixelated_uv = floor(v_texCoord * u_LessPixelation + 0.5) / u_LessPixelation;

    // Get angle
    float angle = u_AngularVelocity * u_Time;

    // Compute cos and sin of angle
    float cos_angle = cos(angle);
    float sin_angle = sin(angle);

    // Rotation matrix
    vec2 matrix_left = vec2(cos_angle, sin_angle);
    vec2 matrix_right = vec2(-sin_angle,  cos_angle);

    vec2 scaled_coords = pixelated_uv * u_CellularDensity;
    vec2 current_poll_lattice = floor(scaled_coords);
    vec2 current_poll_pos = fract(scaled_coords); // Position relative to lattice

    // Is a vec3 so we can pass voronoi stuff if needed in future
    vec3 result = vec3(1000.0, 0.0, 0.0);

    // Iterate each surrounding cell
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 lattice_offset = vec2(x, y);
            vec2 random_point = rotated_random(lattice_offset + current_poll_lattice, matrix_left, matrix_right);

            float dist = distance(lattice_offset + random_point, current_poll_pos);

            if (dist < result.x) {
                result = vec3(dist, random_point.xy);
            }
        }
    }

    result.x = pow(result.x, u_Sharpness);

    vec3 result_color = pow(u_BaseColor * (result.x + u_LuminosityOffset), vec3(u_ColorGamma));

    return vec4(result_color.xyz, alpha);
}

void main() {
    vec4 fluid_color = get_fluid_texture();

    gl_FragColor = fluid_color;
}
