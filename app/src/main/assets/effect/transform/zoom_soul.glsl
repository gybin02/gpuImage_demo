vec4 effect(vec2 uv) {
    //uv.x = uv.x * 0.5 + 0.5;
    //uv.y = uv.y * 0.5 + 0.5;

    float duration = 0.7;
    float maxAlpha = 0.4;
    float maxScale = 1.8;

    float progress = mod(uTime, duration) / duration; // 0~1
    float alpha = maxAlpha * (1.0 - progress);
    float scale = 1.0 + (maxScale - 1.0) * progress;

    float weakX = 0.5 + (uv.x - 0.5) / scale;
    float weakY = 0.5 + (uv.y - 0.5) / scale;
    vec2 weakTextureCoords = vec2(weakX, weakY);

    vec4 weakMask = texture2D(texture, weakTextureCoords);

    vec4 mask = texture2D(texture, uv);

    return mask * (1.0 - alpha) + weakMask * alpha;
}