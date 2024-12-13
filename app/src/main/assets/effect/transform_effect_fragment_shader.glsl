precision highp float;
uniform float uTime;
uniform float ratio;
uniform sampler2D texture;
uniform vec2 uResolution;
varying vec2 texCoord;

vec4 effect(vec2 uv);

void main()
{
    gl_FragColor = effect(texCoord);
}
