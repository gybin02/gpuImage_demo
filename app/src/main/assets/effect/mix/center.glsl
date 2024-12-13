vec2 noise(vec2 p) {
    return fract(1234.1234 * sin(1234.1234 * (fract(1234.1234 * p) + p.yx)));
}

float heart(vec2 p, float s) {
    p /= s;
    vec2 q = p;
    q.x *= 0.5 + 0.5 * q.y;
    q.y -= abs(p.x) * 0.63;
    return (length(q) - 0.7) * s;
}

vec3 hearts(vec2 polar, float uTime, float fft) {
    float l = clamp(polar.y, 0.0, 1.0);
    float tiling = 1.0 / 3.526 * 14.880;
    polar.y += abs(uTime / 1.704);
    vec2 polarID = (floor(polar * tiling));

    polar.x = polar.x + polarID.y * 0.03;
    polar.x = mod(polar.x + 3.14159 * 2.0, 3.14159 * 2.0);
    polarID = floor(polar * tiling);

    polar = fract(polar * tiling);

    polar = polar * 2.0 - 1.0;
    vec2 n = noise(polarID + 0.1) * 0.75 + 0.25;
    vec2 n2 = 2.0 * noise(polarID) - 1.0;
    vec2 offset = (1.0 - n.y) * n2;
    float heartDist = heart(polar + offset, n.y * 0.6);
    float a = smoothstep(0.0, 0.25, n.x * n.x);
    float heartGlow = smoothstep(0.0, -0.05, heartDist) * 0.5 * a + smoothstep(0.3, -0.4, heartDist) * 0.75;
    vec3 heartCol = vec3(smoothstep(0.0, -0.05, heartDist), 0.0, 0.0) * a + heartGlow * vec3(0.9, 0.5, 0.7);
    vec3 bgCol = vec3(0.15 + l / 2.0, 0.0, 0.0);
    return bgCol * (0.2 + fft) + heartCol * step(0.45, noise(polarID + 0.4).x);
}

vec4 effect(vec2 uvOrigin) {
    vec2 uv = (2. * gl_FragCoord.xy - uResolution.xy) / uResolution.y;

    vec2 polar = vec2(atan(uv.y, uv.x), log(length(uv)));
    float speed = 1.354;
    vec3 h = max(hearts(polar, uTime * speed, 0.1),
            hearts(polar, uTime * speed * 0.3 + 3.0, -9.192));
    return vec4(h, 1.0);
}
