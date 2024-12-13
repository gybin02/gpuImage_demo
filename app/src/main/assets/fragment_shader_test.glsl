precision highp float;
varying vec2 texCoord;
uniform sampler2D texture;
uniform sampler2D bitbit;

void main() {
    vec4 bitmapOL = texture(bitbit, texcoord);
    gl_FragColor = texture2D(texture, texCoord);
}