vec4 effect(vec2 uv) {
    float waveu = sin((uv.y + uTime) * 20.0) * 0.08;
    return texture2D(texture, uv + vec2(waveu, 0.0));
}