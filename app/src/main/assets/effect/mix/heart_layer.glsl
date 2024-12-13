float speed = 5.0;
float ratioOrigin = 16.0 / 9.0;

vec3 paintSpecialHeart(vec3 col, vec3 col1, float x, float y) {
    float r = x * x + pow((y - pow(x * x, 1.0 / 3.0)), 2.0);
    r -= sin(uTime * speed) - 0.6;
    if ((r < 2.0 && r > 1.5) || (r < 1.0 && r > 0.6) || (r < 0.3 && r > 0.0)) {
        col = col1 * r * 1.5 * (sin(uTime * speed) + 1.0);
    }
    return col;
}

vec4 effect(vec2 uvOrigin) {
    vec2 uv;
    if (uResolution.x > uResolution.y) {
        uv = gl_FragCoord.xy / vec2(uResolution.y);
    } else {
        uv = gl_FragCoord.xy / vec2(uResolution.x);
    }
    uv.y = 1.0 - uv.y;
    vec2 p = 4.0 * uv;
    vec2 p2 = 45.0 * uv;
    p.y = 4. - p.y;
    p2.y = 4. - p2.y;

    vec3 col = vec3(0.0, 0.0, 0.0);
    vec3 col1 = mix(vec3(1.0, 0.0, 0.6), vec3(1.0, 0.0, 0.4), sqrt(p.y));

    float x;
    float y;
    if (uResolution.x > uResolution.y) {
        x = p.x - 2.0 * uResolution.x / uResolution.y;
        y = p.y - 1.65;
    } else {
        x = p.x - 2.0;
        y = p.y - 1.65 * uResolution.y / uResolution.x;
    }
    col = paintSpecialHeart(col, col1, x, y);

    return vec4(col, 1.0);
}
