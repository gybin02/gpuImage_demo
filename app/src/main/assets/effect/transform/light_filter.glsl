const float PI = 3.1415926;
const float EPSILON = 1e-10;

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 RGBtoHCV(vec3 rgb)
{
    // RGB [0..1] to Hue-Chroma-Value [0..1]
    // Based on work by Sam Hocevar and Emil Persson
    vec4 p = (rgb.g < rgb.b) ? vec4(rgb.bg, -1., 2. / 3.) : vec4(rgb.gb, 0., -1. / 3.);
    vec4 q = (rgb.r < p.x) ? vec4(p.xyw, rgb.r) : vec4(rgb.r, p.yzx);
    float c = q.x - min(q.w, q.y);
    float h = abs((q.w - q.y) / (6. * c + EPSILON) + q.z);
    return vec3(h, c, q.x);
}

vec3 HUEtoRGB(float hue)
{
    // Hue [0..1] to RGB [0..1]
    // See http://www.chilliant.com/rgb2hsv.html
    vec3 rgb = abs(hue * 6. - vec3(3, 2, 4)) * vec3(1, -1, -1) + vec3(-1, 2, 2);
    return clamp(rgb, 0., 1.);
}

vec3 HSLtoRGB(vec3 hsl)
{
    // Hue-Saturation-Lightness [0..1] to RGB [0..1]
    vec3 rgb = HUEtoRGB(hsl.x);
    float c = (1. - abs(2. * hsl.z - 1.)) * hsl.y;
    return (rgb - 0.5) * c + hsl.z;
}

vec3 RGBtoHSL(vec3 rgb) {
    // RGB [0..1] to Hue-Saturation-Lightness [0..1]
    vec3 hcv = RGBtoHCV(rgb);
    float z = hcv.z - hcv.y * 0.5;
    float s = hcv.y / (1. - abs(z * 2. - 1.) + EPSILON);
    return vec3(hcv.x, s, z);
}

vec4 effect(vec2 uv) {
/*
    float hue = 100.0 / 360.0;
    float saturation = 100.0 / 100.0;
    float value = 50.0 / 100.0;
    */

    float duration = 1.0;
    float progress = mod(uTime, duration) / duration; // 0~1
    float amplitude = abs(sin(progress * (PI / duration)));

    float hue = amplitude * 360.0 / 360.0;
    float saturation = 30.0 / 100.0;
    float value = saturation / 10.0;
    float light = 0.0 / 100.0;

    vec3 vHSV = vec3(hue, saturation, value);

    vec4 textureColor = texture2D(texture, uv);

    vec3 fragRGB = textureColor.rgb;
    vec3 fragHSV = rgb2hsv(fragRGB);

    fragHSV.x += vHSV.x;
    fragHSV.z += vHSV.z;
    fragHSV.z = max(min(fragHSV.z, 1.0), 0.0);
    vec3 fragMidRGB = hsv2rgb(fragHSV);

    vec3 fragHSL = RGBtoHSL(fragMidRGB);
    fragHSL.y += vHSV.y * 0.5;

    fragHSL.y = max(min(fragHSL.y, 1.0), 0.0);
    vec3 fragRetRGB = HSLtoRGB(fragHSL);

    vec4 whiteMask = vec4(1.0, 1.0, 1.0, 1.0);
    return vec4(fragRetRGB, textureColor.w) * (1.0 - light) + whiteMask * light;
}