vec3 red = vec3(1., 0., 0.);
vec3 black = vec3(0., 0., 0.);
vec3 white = vec3(1., 1., 1.);
float PI = acos(-1.);

vec3 draw(float dx, float dy, float r, float scale, float phase)
{
    float phasedtime = uTime + phase * 2. * PI;
    phasedtime *= 50.;
    mat2 rotationmatrix = mat2(cos(r), -sin(r), sin(r), cos(r));
    vec2 uv;
    if(uResolution.x > uResolution.y) {
        uv = gl_FragCoord.xy / vec2(uResolution.y, uResolution.y);
        uv.x -= (uResolution.x / uResolution.y - 1.0) / 2.0;
    } else {
        uv = gl_FragCoord.xy / vec2(uResolution.x, uResolution.x);
        uv.y -= (uResolution.y / uResolution.x - 1.0) / 2.0;
    }
    vec2 xy = uv - vec2(0.5, 0.5);
    xy = xy * rotationmatrix;
    float x = xy.x - dx;
    float y = xy.y - dy;
    x = x / scale;
    y = y / scale;
    float val = pow(abs(x), 1.9) + pow(abs(y - pow(abs(x), 2.2 / 3.0)), 2.2);
    val = val * (sin(phasedtime * (3. + 2. * phase)) * 0.1 + 1.0);
    if (val < 0.01)
    {
        float t = val * 100.;
        t = pow(t, 2.);
        return (mix(red, mix(red, black, 0.6), t));
    }
    else {
        return (black);
    }
}

vec4 effect(vec2 uv) {
    vec3 pixc = vec3(0., 0., 0.);
    pixc += draw(0.1, 0.34, -0.2, 0.5, 0.5);
    pixc += draw(0.4, -0.3, 0.3, 0.8, 0.3);
    pixc += draw(-0.2, 0.3, 0.1, 0.9, 0.2);
    pixc += draw(0.3, -0.1, -0.15, 0.4, 2.5);
    pixc += draw(-0.4, -0.15, .2, 0.8, 0.55);
    pixc += draw(-0.1, -0.2, 0.3, 1.3, 0.87);
    pixc += draw(0.4, 0.19, -0.2, 1.0, 0.23);
    pixc += draw(0.05, -0.27, -0.01, 0.7, 0.9);

    return vec4(pixc, 1.);
}
