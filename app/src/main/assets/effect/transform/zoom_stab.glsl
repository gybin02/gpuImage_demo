const float PI = 3.1415926;

float rand(float n) {
    return fract(sin(n) * 43758.5453123);
}

vec4 effect(vec2 uv) {
    float maxJitter = 0.06;
    float duration = 0.3;
    float colorROffset = 0.01;
    float colorBOffset = -0.025;

    float uTime = mod(uTime, duration * 2.0);
    float amplitude = max(sin(uTime * (PI / duration)), 0.0);

    float jitter = rand(uv.y) * 2.0 - 1.0; // -1~1

    bool needOffset = abs(jitter) < maxJitter * amplitude;

    float textureX = uv.x + (needOffset ? jitter : (jitter * amplitude * 0.006));

    vec2 textureCoords = vec2(textureX, uv.y);

    vec4 mask = texture2D(texture, textureCoords);
    vec4 maskR = texture2D(texture, textureCoords + vec2(colorROffset * amplitude, 0.0));
    vec4 maskB = texture2D(texture, textureCoords + vec2(colorBOffset * amplitude, 0.0));

    return vec4(maskR.r, mask.g, maskB.b, mask.a);
}