precision mediump float;

uniform mat4 uMatrix;
attribute vec4 vPosition;
attribute vec2 vTextureCoordinate;
attribute float alpha;
varying vec2 texCoord;
varying float inAlpha;

void main() {
    gl_Position = uMatrix * vPosition;
    texCoord = vTextureCoordinate;
    inAlpha = alpha;
}