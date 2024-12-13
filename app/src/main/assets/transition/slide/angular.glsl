// Author: Fernando Kuteken
// License: MIT

float PI = 3.14159265359;

float startingAngle = 90.0;

vec4 transition(vec2 uv) {

    float offset = startingAngle * PI / 180.0;
    float angle = atan(uv.y - 0.5, uv.x - 0.5) + offset;
    float normalizedAngle = (angle + PI) / (2.0 * PI);

    normalizedAngle = normalizedAngle - floor(normalizedAngle);

    return mix(
        getFromColor(uv),
        getToColor(uv),
        step(normalizedAngle, progress)
    );
}
