vec4 effect(vec2 uv) {
    if (uv.x <= 1.0 / 3.0) {
        uv.x = uv.x * 3.0;
    } else if (uv.x <= 2.0 / 3.0) {
        uv.x = (uv.x - 1.0 / 3.0) * 3.0;
    } else {
        uv.x = (uv.x - 2.0 / 3.0) * 3.0;
    }

    if (uv.y <= 1.0 / 3.0) {
        uv.y = uv.y * 3.0;
    } else if (uv.y <= 2.0 / 3.0) {
        uv.y = (uv.y - 1.0 / 3.0) * 3.0;
    } else {
        uv.y = (uv.y - 2.0 / 3.0) * 3.0;
    }
    return texture2D(texture, uv);
}