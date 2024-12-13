float rand(float n) {
    return fract(sin(n) * 43758.5453123);
}

vec4 effect(vec2 uv) {
    float duration = 0.7;
    float maxScale = 1.1;
    float offset = 0.01;
    float progress = mod(uTime, duration) / duration; // 0~1
    vec2 offsetCoords = vec2(offset, offset) * progress;
    float scale = 1.0 + (maxScale - 1.0) * progress;

    vec2 ScaleTextureCoords = vec2(0.5, 0.5) + (uv - vec2(0.5, 0.5)) / scale;

    vec4 maskR = texture2D(texture, ScaleTextureCoords + offsetCoords);
    vec4 maskB = texture2D(texture, ScaleTextureCoords - offsetCoords);
    vec4 mask = texture2D(texture, ScaleTextureCoords);

    return vec4(maskR.r, mask.g, maskB.b, mask.a);
}