// Author: gre
// License: MIT
// forked from https://gist.github.com/benraziel/c528607361d90a072e98



vec4 transition(vec2 uv) {
    ivec2 squaresMin = ivec2(20.0); // minimum number of squares (when the effect is at its higher level)
    float steps = 50.0; // zero disable the stepping

    float d = min(progress, 1.0 - progress);
    float dist = steps > 0.0 ? ceil(d * float(steps)) / float(steps) : d;
    vec2 squareSize = 2.0 * dist / vec2(squaresMin);
    vec2 p = dist > 0.0 ? (floor(uv / squareSize) + 0.5) * squareSize : uv;
    return mix(getFromColor(p), getToColor(p), progress);
}
