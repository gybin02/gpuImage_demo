mat2 rot(float a)
{
    float sa = sin(a), ca = cos(a);
    return mat2(ca, -sa, sa, ca);
}

float light(vec2 pos, float ang)
{
    pos = pos * rot(ang);
    pos.y -= 0.5;
    float mask = 1. - (pos.x * pos.x * 60. - pos.y + 0.5);
    float brightness = clamp(pow(0.1 / (pos.y + .5), 2.), 0., 1.);
    return mask * brightness * 15.;
}

vec4 effect(vec2 uv) {
    vec2 p = (uv - vec2(0.5));
    p = -p;
    vec3 color = vec3(0.3, 0.3, 0.6);
    color *= (1.0 - p.x * p.x + p.y * p.y) / 2.;
    vec2 p2 = p;
    p2.y += 0.5;
    color += clamp(light(vec2(p.x - 0.2, p.y + 0.7), sin(uTime * 2. + 1.) / 2.), 0., 1.);

    color += clamp(light(vec2(p.x + 0.2, p.y + 0.7), sin(uTime * 2. + 2.) / 2.), 0., 1.);
    color += clamp(light(vec2(p.x, p.y + 0.7), sin(uTime * 2. + 3.) / 2.), 0., 1.);

    return vec4(color, 1);
}