#extension GL_OES_EGL_image_external: require
precision highp float;
uniform float uTime;
uniform samplerExternalOES texture;
uniform vec2 uResolution;
varying vec2 texCoord;
uniform float ratio;

void main() {
    gl_FragColor = texture2D(texture, texCoord);
}