vec3 Heart_col = vec3(1., .05, .05);

vec2 Rain(vec2 uv, float t) {

    vec2 b = vec2(3., 1.);
    vec2 bs = uv * b;

    vec2 id = floor(bs);
    bs.y += t * .22;
    bs.y += fract(sin(id.x * 735.23) * 772.23);
    id = floor(bs);
    bs = fract(bs) - .5;

    t += fract(sin(id.x * 77.35 + id.y * 1462.8) * 778.35) * 6.283;
    float y = -sin(t + sin(t + sin(t) * .5)) * .43;
    vec2 p1 = vec2(0., y);
    vec2 of1 = (bs - p1) / b;
    float d = length(of1);

    float m1 = smoothstep(.08, .0, d);

    vec2 of2 = (fract(uv * b.x * vec2(1., 2.)) - .5) / vec2(1., 2.);
    d = length(of2);

    float m2 = smoothstep(.25 * (.5 - bs.y), .0, d) * smoothstep(-.1, .1, bs.y - p1.y);
    return vec2(m1 * of1 * 30. + m2 * of2 * 15.);
}

vec4 effect(vec2 uvOrigin)
{
    //vec2 res = uResolution / vec2(9.0 / 16.0, 1.0);
    //vec2 uv = gl_FragCoord.xy / res;
    vec2 uv = (gl_FragCoord.xy - uResolution.xy * 0.5) / uResolution.y;

    vec3 col = vec3(0.0);

    float t = uTime * 2.0;
    vec2 rainDistort = (Rain(uv * 5.0, t) * 0.5);
    rainDistort += Rain(uv * 7.0, t) * 0.5;
    uv.x *= .7;
    uv.y -= sqrt(abs(uv.x))* .5;
    uv.y += .1;
    float d = length(uv - rainDistort);
    float c = smoothstep(0.2 + 0.1 * (t - floor(t)), 0.15, d);
    col = vec3(c * Heart_col);

    return vec4(col, 1.0);
}
