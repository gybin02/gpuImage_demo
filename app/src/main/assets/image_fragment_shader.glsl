precision highp float;
varying vec2 texCoord;
uniform sampler2D texture;

/**float plot1(vec2 st) {
    return smoothstep(0.01, 0.0, abs(st.x - 0.5));
}

float plot2(vec2 st) {
    return smoothstep(0.01, 0.0, abs(st.y - 0.5));
}*/

void main() {
    //vec4 lala = vec4(vec3(0.0, 1.0, 0.0) * plot1(texCoord), 1.0) + vec4(vec3(0.0, 1.0, 0.0) * plot2(texCoord), 1.0);
    gl_FragColor = texture2D(texture, texCoord);
}