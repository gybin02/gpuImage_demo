float makePoint(float x, float y, float fx, float fy, float sx, float sy, float t)
{
    float xx = x + sin(t * fx) * cos(t * sx);
    float yy = y + cos(t * fy) * sin(t * sy);

    return 0.4 / sqrt(abs(xx * xx + yy * yy));
}

vec4 effect(vec2 uv) {
    vec2 p = (gl_FragCoord.xy / uResolution.x) * 2.0 - vec2(1.0, uResolution.y / uResolution.x);

    p = p * 3.0;

    float x = p.x;
    float y = p.y;

    float a = makePoint(x, y, 3.3, 2.9, 1.3, 0.3, uTime);
    a += makePoint(x, y, 1.9, 2.0, 0.4, 0.4, uTime);
    a += makePoint(x, y, 0.2, 0.7, 0.4, 0.5, uTime);

    float b = makePoint(x, y, 1.2, 1.9, 0.3, 0.3, uTime);
    b += makePoint(x, y, 0.7, 2.7, 0.4, 4.0, uTime);
    b += makePoint(x, y, 1.4, 0.6, 0.4, 0.5, uTime);
    b += makePoint(x, y, 2.6, 0.4, 0.6, 0.3, uTime);
    b += makePoint(x, y, 0.1, 1.4, 0.5, 0.4, uTime);
    b += makePoint(x, y, 0.7, 1.7, 0.4, 0.4, uTime);
    b += makePoint(x, y, 0.8, 0.5, 0.4, 0.5, uTime);
    b += makePoint(x, y, 1.4, 0.9, 0.6, 0.3, uTime);
    b += makePoint(x, y, 0.7, 1.3, 0.5, 0.4, uTime);

    float c = makePoint(x, y, 3.7, 0.3, 0.3, 0.3, uTime);
    c += makePoint(x, y, 1.9, 1.3, 0.4, 0.4, uTime);
    c += makePoint(x, y, 0.8, 0.9, 0.4, 0.5, uTime);
    c += makePoint(x, y, 1.2, 1.7, 0.6, 0.3, uTime);
    c += makePoint(x, y, 0.3, 0.6, 0.5, 0.4, uTime);
    c += makePoint(x, y, 0.3, 0.3, 0.4, 0.4, uTime);
    c += makePoint(x, y, 1.4, 0.8, 0.4, 0.5, uTime);
    c += makePoint(x, y, 0.2, 0.6, 0.6, 0.3, uTime);
    c += makePoint(x, y, 1.3, 0.5, 0.5, 0.4, uTime);

    vec3 d = vec3(b * c, a * c, a * b) / 100.0;

    return vec4(d.x, d.y, d.z, 1.0);
}