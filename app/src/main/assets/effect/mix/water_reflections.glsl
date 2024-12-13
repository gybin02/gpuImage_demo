const float UVSCALE = 8.0;

vec2 getpos(vec2 uv)
{
    uv = UVSCALE * (uv / uResolution.xy);
    float aspect = uResolution.x / uResolution.y;
    vec2 ratio = vec2(aspect, 1.0);
    return (2.0 * uv - 1.0) * ratio;
}

float lightningNoise(vec2 detailPos, vec2 detailPos2, vec2 shapePos)
{
    const float offsetScalar = 0.91;
    const float offsetScalar2 = 0.213;
    const float noiseMultiplier = 1.0;

    float t = uTime * 4.0;
    float t2 = t * 1.646579370;

    vec2 noiseXY = abs(fract(detailPos + t) - 0.5);
    float noiseZ = abs(fract(detailPos.y - t2 + noiseXY.x) - 0.5);
    vec2 offset = vec2(noiseZ, noiseXY.x + noiseXY.y);

    shapePos += (offset * offsetScalar);

    noiseXY = abs(fract(detailPos2 - t2) - 0.5);
    noiseZ = abs(fract(detailPos2.y - t2 + noiseXY.y) - 0.5);
    offset = vec2(noiseXY.x + noiseXY.y, noiseZ);

    shapePos += (offset * offsetScalar2);

    noiseZ = abs(fract((shapePos.x) + abs(fract(shapePos.y) - 0.5)) - 0.5);

    return noiseZ * noiseMultiplier;
}

float lightningNoiseFBM(vec2 p)
{
    p *= 0.15;

    const float intensity = 10.0;
    const float power = 4.0;
    const float detailScalar = 0.46213;
    const float detail2Scalar = 1.96213;
    const float shapeScalar = 1.06213;
    const float decay = 0.3;
    float amplitude = 0.8;
    float noise = 0.0;
    vec2 detailPos = p;
    vec2 detailPos2 = p;
    vec2 shapePos = p;

    for (int i = 0; i < 5; i++) {
        detailPos *= detailScalar;
        detailPos2 *= detail2Scalar;
        shapePos *= shapeScalar;

        noise += (lightningNoise(detailPos, detailPos2, shapePos) * amplitude);

        amplitude *= decay;
    }

    return min(1.0, intensity * pow(noise, power));
}

vec4 lightningShader(vec2 p)
{
    float noise = lightningNoiseFBM(p);
    vec3 color3 = mix(vec3(0.2, 0.2, 0.8), vec3(1.0, 1.0, 1.0), noise);
    return vec4(color3 * noise, 1.0);
}

vec4 effect(vec2 uv) {
    uv /= vec2(0.06);
    vec4 color = lightningShader(uv);
    if (color.rgb == vec3(0.0)) {
        return vec4(0.0);
    } else {
        return color;
    }
}
