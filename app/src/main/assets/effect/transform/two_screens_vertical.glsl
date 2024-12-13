vec4 effect(vec2 uv) {
    float y;
    if (uv.y >= 0.0 && uv.y <= 0.5) {
        y = uv.y + 0.25;
    } else {
        y = uv.y - 0.25;
    }
    return texture2D(texture, vec2(uv.x, y));
}