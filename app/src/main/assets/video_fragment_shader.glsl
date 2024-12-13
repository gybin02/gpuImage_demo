#extension GL_OES_EGL_image_external: require
precision highp float;
varying vec2 texCoord;
uniform samplerExternalOES texture;

void main() {
    gl_FragColor = texture2D(texture, texCoord);
}