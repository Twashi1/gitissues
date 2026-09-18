#version 300 es

precision highp float;
precision highp int;

uniform float u_Scale;
uniform int   u_Seed;
uniform float u_AngularVelocity;
uniform float u_Time;
uniform float u_CellularDensity;
uniform int   u_Octaves;
uniform float u_FractalScaling;
uniform float u_Sharpness;
uniform float u_ColorGamma;
uniform float u_LuminosityOffset;
uniform vec3  u_BaseColor;
uniform float u_BorderSize;
uniform vec2  u_resolution;

out vec4 fragColor;

const float PI = 3.14159265359;
const float UINT_TO_FLOAT = 1.0 / 4294967296.0;

uint hash(uint value) {
    value ^= value >> 16u;
    value *= 0x7feb352du;

    value ^= value >> 15u;
    value *= 0x846ca68bu;

    value ^= value >> 16u;

    return value;
}

uint hashCell(uvec2 cell) {
    uint value =
        cell.x * 0x9E3779B9u ^
        cell.y * 0x85EBCA6Bu ^
        uint(u_Seed);

    return hash(value);
}

float cellRandom(uvec2 cell) {
    return float(hashCell(cell)) * UINT_TO_FLOAT;
}

vec2 cellRandom2(uvec2 cell) {
    uint x = hashCell(cell);
    uint y = hashCell(
        cell + uvec2(0x1234567Bu, 0x9E3779B9u)
    );

    return vec2(
        float(x) * UINT_TO_FLOAT,
        float(y) * UINT_TO_FLOAT
    );
}

vec2 rotateCellPoint(vec2 point, float angle) {
    float c = cos(angle);
    float s = sin(angle);

    vec2 offset = point - vec2(0.5);

    // Standard 2D rotation matrix.
    offset = vec2(
        offset.x * c - offset.y * s,
        offset.x * s + offset.y * c
    );

    return offset + vec2(0.5);
}

vec4 cellularNoise(float density) {
    // Normalize fragment coordinates to [0, 1].
    vec2 uv = gl_FragCoord.xy / u_resolution;

    // Correct for viewport aspect ratio so cells remain approximately square.
    uv.x *= u_resolution.x / u_resolution.y;

    uv *= u_Scale;

    // Position in the cellular grid.
    vec2 gridPosition = uv * density;

    vec2 cell = floor(gridPosition);
    vec2 localPosition = fract(gridPosition);

    float nearestDistance = 1000.0;
    vec2 nearestPoint = vec2(0.0);

    // One angle for this entire noise layer.
    // TODO: each cell should have its own rotation, this needs to be
    // generated from the cell coordinate instead.
    float rotationAngle = u_AngularVelocity * u_Time;

    // Search neighboring cells.
    for (int y = -2; y <= 2; ++y) {
        for (int x = -2; x <= 2; ++x) {

            ivec2 cellOffset = ivec2(x, y);
            ivec2 cellCoordinate = ivec2(cell) + cellOffset;

            // Convert signed lattice coordinate to uint hash input.
            uvec2 hashCoordinate = uvec2(cellCoordinate);

            // Generate deterministic point inside this cell.
            vec2 randomPoint = cellRandom2(hashCoordinate);

            // Rotate the point around the center of its cell.
            randomPoint = rotateCellPoint(
                randomPoint,
                rotationAngle
            );

            // Position relative to the current cell.
            vec2 pointOffset =
                vec2(cellOffset) + randomPoint;

            float distanceToPoint =
                distance(pointOffset, localPosition);

            if (distanceToPoint < nearestDistance) {
                nearestDistance = distanceToPoint;
                nearestPoint = randomPoint;
            }
        }
    }

    // Shape the distance field.
    float shapedDistance =
        pow(nearestDistance, u_Sharpness);

    // Convert distance to color.
    vec3 color = pow(
        u_BaseColor * (shapedDistance + u_LuminosityOffset),
        vec3(u_ColorGamma)
    );

    return vec4(color, 1.0);
}

vec4 fractalCellularNoise() {
    float amplitude = 1.0;
    float density = u_CellularDensity;

    float amplitudeSum = 0.0;
    vec4 accumulatedColor = vec4(0.0);

    for (int octave = 0; octave < 8; ++octave) {
        if (octave >= u_Octaves) {
            break;
        }

        accumulatedColor +=
            cellularNoise(density) * amplitude;

        amplitudeSum += amplitude;

        amplitude /= u_FractalScaling;
        density *= u_FractalScaling;
    }

    return accumulatedColor / amplitudeSum;
}

void main() {
    fragColor = fractalCellularNoise();
}
