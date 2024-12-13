float speed = 2.0;
float circle(vec2 uv) {
    return smoothstep(0.1, 0.09, length(uv)) - smoothstep(0.09, 0.08, length(uv));
}

vec4 effect(vec2 uvOrigin) {
    vec2 uv;
    if (uResolution.x > uResolution.y) {
        uv = gl_FragCoord.xy / vec2(uResolution.y, uResolution.y);
        uv.x -= (uResolution.x / uResolution.y - 1.0) / 2.0;
    } else {
        uv = gl_FragCoord.xy / vec2(uResolution.x, uResolution.x);
        uv.y -= (uResolution.y / uResolution.x - 1.0) / 2.0;
    }
    vec2 p = uv;
    vec2 g = uv;
    uv = 1. - uv;
    p -= vec2(0.1, 0.5);
    g -= vec2(0.9, 0.5);
    uv -= vec2(0.5);
    uv.y = uv.y - (sqrt(abs(uv.x / 1.4)));
    p.y = p.y - (sqrt(abs(p.x / 1.4)));
    g.y = g.y - (sqrt(abs(g.x / 1.4)));
    float k = circle(uv);
    float m = circle(p);
    float r = circle(g);

    return vec4(k) * vec4(sin(uTime * speed * 8.), 0., 0., 1.) +
    vec4(m) * vec4(1., 0.4, 0.7, 1.) * (cos(uTime * speed * 8.)) +
    vec4(r) * vec4(0., 1., 0.0, 1.) * (cos(uTime * speed * 8.));
}
