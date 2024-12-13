precision mediump float;
uniform float uTime;
uniform float uRatio;
uniform sampler2D textTexture;
uniform vec2 uResolution;
varying vec2 texCoord;

void main() {
    gl_FragColor = texture2D(textTexture, texCoord);
}
