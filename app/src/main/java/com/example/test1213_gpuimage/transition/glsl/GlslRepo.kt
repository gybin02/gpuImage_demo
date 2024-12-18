package com.example.test1213_gpuimage.transition.glsl

object GlslRepo {

    val fragmentShader = """
        precision highp float;

        uniform vec2 uResolution;
        uniform float uTime;
        uniform float progress;
        uniform sampler2D inputImageTexture;
        uniform sampler2D inputImageTexture2;

        varying vec2 textureCoordinate;
        varying float inAlpha;

        vec4 getFromColor(vec2 uv) {
            return texture2D(inputImageTexture, uv);
        }

        vec4 getToColor(vec2 uv) {
            return texture2D(inputImageTexture2, uv);
        }

        vec4 transition(vec2 uv);

        void main()
        {
            vec2 uv = textureCoordinate;
            gl_FragColor = transition(uv);
        }

    """.trimIndent()



    val ripple = """
        float amplitude = 100.0;
        float speed = 50.0;

        vec4 transition(vec2 uv) {
            vec2 dir = uv - vec2(.5);
            float dist = length(dir);
            vec2 offset = dir * (sin(progress * dist * amplitude - progress * speed) + .5) / 30.;
            return mix(
                getFromColor(uv + offset),
                getToColor(uv),
                smoothstep(0.2, 1.0, progress)
            );
        }

    """.trimIndent()
}