precision highp float;
uniform float uTime;
uniform sampler2D texture;
uniform vec2 uResolution;
varying vec2 texCoord;
uniform float ratio;
uniform float opacity;
uniform float cropX;
uniform float cropY;
uniform float isIntro;

void main() {
    gl_FragColor = texture2D(texture, texCoord);
    gl_FragColor.a = opacity;
    if (cropX > 0.0){
        if (texCoord.x > cropX && isIntro == 1.0){
            gl_FragColor = vec4(0);
            gl_FragColor.a = 1.0;
        } else if (texCoord.x < cropX && isIntro == 2.0){
            gl_FragColor = vec4(0);
            gl_FragColor.a = 1.0;
        }
    } else if (cropX < 0.0){
        if (texCoord.x < 1.0 - (-1.0 * cropX) && isIntro == 1.0){
            gl_FragColor = vec4(0);
            gl_FragColor.a = 1.0;
        } else if (texCoord.x > 1.0 - (-1.0 * cropX) && isIntro == 2.0){
            gl_FragColor = vec4(0);
            gl_FragColor.a = 1.0;
        }
    }

    if (cropY > 0.0){
        if (texCoord.y > cropY && isIntro == 1.0){
            gl_FragColor = vec4(0);
            gl_FragColor.a = 1.0;
        } else if (texCoord.y < cropY && isIntro == 2.0){
            gl_FragColor = vec4(0);
            gl_FragColor.a = 1.0;
        }
    } else if (cropY < 0.0){
        if (texCoord.y < 1.0- (-1.0 * cropY) && isIntro == 1.0){
            gl_FragColor = vec4(0);
            gl_FragColor.a = 1.0;
        } else if (texCoord.y > 1.0- (-1.0 * cropY) && isIntro == 2.0){
            gl_FragColor = vec4(0);
            gl_FragColor.a = 1.0;
        }
    }
}