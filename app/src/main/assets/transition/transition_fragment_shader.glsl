precision highp float;

uniform vec2 uResolution;
uniform float uTime;
uniform float progress;
uniform sampler2D uTextureFrom;
uniform sampler2D uTextureTo;

varying vec2 texCoord;
varying float inAlpha;

vec4 getFromColor(vec2 uv) {
    return texture2D(uTextureFrom, uv);
}

vec4 getToColor(vec2 uv) {
    return texture2D(uTextureTo, uv);
}

vec4 transition(vec2 uv);

void main()
{
    vec2 uv = texCoord;
    gl_FragColor = transition(uv);
}
