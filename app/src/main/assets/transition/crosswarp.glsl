precision mediump float;

uniform float progress;
uniform sampler2D from;
uniform sampler2D to;
varying vec2 vTexCoord;

void main() {
    vec2 p = mix(vTexCoord, vTexCoord, progress);
    vec4 colorFrom = texture2D(from, p);
    vec4 colorTo = texture2D(to, 1.0 - p);
    gl_FragColor = mix(colorFrom, colorTo, progress);
}
