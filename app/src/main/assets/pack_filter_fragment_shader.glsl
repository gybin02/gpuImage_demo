precision highp float;

const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);

varying vec2 texCoord;
uniform sampler2D texture;
uniform sampler2D curveTexture;

uniform float intensity;

uniform float brightness;
uniform float contrast;
uniform float saturation;
uniform float warmth;
uniform float tint;
uniform mat4 colorMatrix;
uniform float colorMatrixIntensity;

uniform float gamma;
uniform float vibrant;

uniform float sepia;
uniform mat4 sepiaMatrix;

vec4 applyBrightness(vec4 color, float value) {
    color.rgb += clamp(value, 0.0, 1.0);
    return color;
}

vec4 applyContrast(vec4 color, float value) {
    float a = clamp(value, 0.5, 1.5);
    return vec4(((color.rgb - vec3(0.5)) * a + vec3(0.5)), color.w);
}

vec4 applySaturation(vec4 color, float value) {
    float luminance = dot(color.rgb, luminanceWeighting);
    vec3 greyScaleColor = vec3(luminance);
    return vec4(mix(greyScaleColor, color.rgb, value), color.w);
}

vec4 applyWarmth(vec4 color, float value) {
    color.r += value;
    color.b -= value;
    return color;
}

vec4 applyTint(vec4 color, float value) {
    color.g += clamp(value, -0.2, 0.2);
    return color;
}

vec4 applyGamma(vec4 color, float value) {
    return vec4(pow(color.rgb, vec3(value)), color.w);
}

vec4 applyVibrant(vec4 color, float value) {
    float mx = max(max(color.r, color.g), color.b);
    float average = (color.r + color.g + color.b) / 3.0;
    float amt = (mx - average) * (-value * 3.0);
    color.rgb = mix(color.rgb, vec3(mx), amt);
    return color;
}

vec4 applyColorMatrix(vec4 color, mat4 matrix, float opaque) {
    return (color * matrix) * opaque + color * (1.0 - opaque);
}

vec4 applySepia(vec4 color, float value) {
    return applyColorMatrix(color, sepiaMatrix, value);
}

vec4 tone_curve(sampler2D curve, vec4 t) {
    lowp float r = texture2D(curve, vec2(t.r, 0.0)).r;
    lowp float g = texture2D(curve, vec2(t.g, 0.0)).g;
    lowp float b = texture2D(curve, vec2(t.b, 0.0)).b;
    return vec4(r, g, b, t.a);
}


void main() {

    vec4 color = texture2D(texture, texCoord);
    vec4 t = texture2D(texture, texCoord);

    t = applyBrightness(t, brightness);
    t = applySaturation(t, saturation);
    t = applyWarmth(t, warmth);
    t = applyTint(t, tint);
    t = applyGamma(t, gamma);
    t = applyVibrant(t, vibrant);
    t = applyColorMatrix(t, colorMatrix, colorMatrixIntensity);
    t = applySepia(t, sepia);
    t = applyContrast(t, contrast);
    t = tone_curve(curveTexture, t);

    gl_FragColor = t * intensity + color * (1.0 - intensity);
}