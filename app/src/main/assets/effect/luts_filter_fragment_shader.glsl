#extension GL_OES_EGL_image_external: require

precision highp float;

uniform vec2 uResolution;
uniform float uTime;
uniform sampler2D texture;
uniform sampler2D matrixTexture;

varying vec2 texCoord;
varying float inAlpha;

void main()
{
    vec2 uv = texCoord;

    vec4 textureColor = texture2D(texture,uv);
    float blueColor = textureColor.b * 63.0;

    vec2 quad1;
    quad1.y = floor(floor(blueColor) / 8.0);
    quad1.x = floor(blueColor) - (quad1.y * 8.0);

    vec2 quad2;
    quad2.y = floor(ceil(blueColor) / 8.0);
    quad2.x = ceil(blueColor) - (quad2.y * 8.0);

    vec2 texPos1;
    texPos1.x = (quad1.x * 0.125) + 0.5 / 512.0 + ((0.125 - 1.0 / 512.0) * textureColor.r);
    texPos1.y = (quad1.y * 0.125) + 0.5 / 512.0 + ((0.125 - 1.0 / 512.0) * textureColor.g);

    vec2 texPos2;
    texPos2.x = (quad2.x * 0.125) + 0.5 / 512.0 + ((0.125 - 1.0 / 512.0) * textureColor.r);
    texPos2.y = (quad2.y * 0.125) + 0.5 / 512.0 + ((0.125 - 1.0 / 512.0) * textureColor.g);

    vec4 newColor1 = texture2D(matrixTexture, texPos1);
    vec4 newColor2 = texture2D(matrixTexture, texPos2);
    vec4 newColor = mix(newColor1, newColor2, fract(blueColor));
    gl_FragColor = vec4(newColor.rgb, textureColor.w);
//    gl_FragColor = vec4(0.0,0.0,1.0,1.0);
//    gl_FragColor = texture2D(matrixTexture, uv);
}