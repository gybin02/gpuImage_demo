vec4 effect(vec2 uv) {
    float x;
    if (uv.x >= 0.0 && uv.x <= 0.5) {
        x = uv.x + 0.25;
    }else {
        x = uv.x - 0.25;
    }

    return texture2D(texture, vec2(x, uv.y));
}