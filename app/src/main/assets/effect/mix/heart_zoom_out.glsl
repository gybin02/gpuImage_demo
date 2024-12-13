mat2 makem2(in float theta) {
    float c = cos(theta);
    float s = sin(theta);
    return mat2(c, -s, s, c);
}

float fbm(in vec2 p)
{
    float z = 3.;
    float rz = 0.;
    vec2 bp = p;
    for (float i = 1.;i < 6.; i++)
    {
        rz += abs((0.4 - 0.5) * 30.) / z;
        z = z * 2.;
        p = p * 2.;
    }
    return rz;
}

float dualfbm(in vec2 p)
{
    vec2 p2 = p * .7;
    vec2 basis = vec2(fbm(p2 - uTime * 0.08 * 1.6), fbm(p2 + uTime * 0.08 * 1.7));
    basis = (basis - .5) * .2;
    p += basis;
    return fbm(p * makem2(uTime * 0.08 * 0.2));
}

float heart(vec2 p)
{
    float r = length(p);
    float a = atan(p.x, -p.y) / 3.141593;
    float h = abs(a);
    float d = (13.0 * h - 22.0 * h * h + 10.0 * h * h * h) / (6.0 - 5.0 * h);

    float s = 1.0 - 0.5 * clamp(r / d, 0.0, 1.0);
    s = 0.75 + 0.75 * p.x;
    s *= 1.0 - 0.25 * r;
    s = 0.5 + 0.6 * s;
    s *= 0.5 + 0.5 * pow(1.0 - clamp(r / d, 0.0, 1.0), 0.1);

    return abs(mod(s * 4.9, 6.2831853) - 3.14) * 5. + .1;

}

vec4 effect(vec2 uvOrigin) {
    vec2 uv;
    if(uResolution.x > uResolution.y) {
        uv = gl_FragCoord.xy / vec2(uResolution.y, uResolution.y);
        uv.x -= (uResolution.x / uResolution.y - 1.0) / 2.0;
    } else {
        uv = gl_FragCoord.xy / vec2(uResolution.x, uResolution.x);
        uv.y -= (uResolution.y / uResolution.x - 1.0) / 2.0;
    }
    uv.y = 1.0 - uv.y;
    vec2 p = uv - 0.5;
    p *= 8.936;
    float rz = dualfbm(p);
    p /= exp(mod(uTime * 0.08 * 10., 3.14159));
    rz *= pow(abs((.8 - heart(p))), .9);
    vec3 col;
    col = vec3(.9, 0.1, 0.4) / rz;
    col = pow(abs(col), vec3(.99));

    return vec4(col, 1.0);
}
