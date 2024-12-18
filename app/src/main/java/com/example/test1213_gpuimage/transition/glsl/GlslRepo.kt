package com.example.test1213_gpuimage.transition.glsl

import android.content.Context

object GlslRepo {
    //Fragment 框架着色器
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


    //测试水波纹动画
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

    val basicList = listOf(
        ShaderFile("basic", "multiply_blend.glsl"),
        ShaderFile("basic", "overexposure.glsl"),
        ShaderFile("basic", "ripple.glsl"),
        ShaderFile("basic", "rotate_scale_fade.glsl"),
        ShaderFile("basic", "scale_in.glsl"),
        ShaderFile("basic", "simple_zoom.glsl"),
        ShaderFile("basic", "tangent_motion_blur.glsl")
    )

    val effectList = listOf(
        ShaderFile("effect", "butterfly_ware_scrawler.glsl"),
        ShaderFile("effect", "crazy_parametric_fun.glsl"),
        ShaderFile("effect", "cross_hatch.glsl"),
        ShaderFile("effect", "cross_zoom.glsl"),
        ShaderFile("effect", "displacement.glsl"),
        ShaderFile("effect", "dreamy.glsl"),
        ShaderFile("effect", "flyeye.glsl"),
        ShaderFile("effect", "hexagonalize.glsl"),
        ShaderFile("effect", "kaleido_scope.glsl"),
        ShaderFile("effect", "linear_blur.glsl"),
        ShaderFile("effect", "morph.glsl"),
        ShaderFile("effect", "pixelize.glsl"),
        ShaderFile("effect", "power_kaleido.glsl"),
        ShaderFile("effect", "swirl.glsl")
    )

    val lightList = listOf(
        ShaderFile("light", "burn.glsl"),
        ShaderFile("light", "color_phase.glsl"),
        ShaderFile("light", "colour_distance.glsl"),
        ShaderFile("light", "coord_from_in.glsl"),
        ShaderFile("light", "fade.glsl"),
        ShaderFile("light", "fade_color.glsl"),
        ShaderFile("light", "fadegrayscale.glsl"),
        ShaderFile("light", "glitch_displace.glsl"),
        ShaderFile("light", "glitch_memories.glsl")
    )

    val maskList = listOf(
        ShaderFile("mask", "cannabisleaf.glsl"),
        ShaderFile("mask", "circle.glsl"),
        ShaderFile("mask", "circle_crop.glsl"),
        ShaderFile("mask", "circle_open.glsl"),
        ShaderFile("mask", "doom_screen.glsl"),
        ShaderFile("mask", "heart.glsl"),
        ShaderFile("mask", "luma.glsl"),
        ShaderFile("mask", "luminance_melt.glsl"),
        ShaderFile("mask", "perlin.glsl"),
        ShaderFile("mask", "pinwheel.glsl"),
        ShaderFile("mask", "polar_function.glsl"),
        ShaderFile("mask", "polka_dots_curtain.glsl"),
        ShaderFile("mask", "random_noise.glsl"),
        ShaderFile("mask", "random_squares.glsl"),
        ShaderFile("mask", "rotate.glsl"),
        ShaderFile("mask", "squares_wire.glsl"),
        ShaderFile("mask", "stereo_viewer.glsl"),
        ShaderFile("mask", "tv_static.glsl"),
        ShaderFile("mask", "undulating_burn_out.glsl"),
        ShaderFile("mask", "water_crop.glsl"),
        ShaderFile("mask", "wind.glsl"),
        ShaderFile("mask", "zoom_in_circles.glsl")
    )

    val slideList = listOf(
        ShaderFile("slide", "angular.glsl"),
        ShaderFile("slide", "bounce.glsl"),
        ShaderFile("slide", "bow_tie_horizontal.glsl"),
        ShaderFile("slide", "bow_tie_vertical.glsl"),
        ShaderFile("slide", "cross_warp.glsl"),
        ShaderFile("slide", "cube.glsl"),
        ShaderFile("slide", "directional.glsl"),
        ShaderFile("slide", "directional_easing.glsl"),
        ShaderFile("slide", "directional_warp.glsl"),
        ShaderFile("slide", "directional_wipe.glsl"),
        ShaderFile("slide", "doorway.glsl"),
        ShaderFile("slide", "grid_flip.glsl"),
        ShaderFile("slide", "inverted_page_curl.glsl"),
        ShaderFile("slide", "left_right.glsl"),
        ShaderFile("slide", "mosaic.glsl"),
        ShaderFile("slide", "radial.glsl"),
        ShaderFile("slide", "squeeze.glsl"),
        ShaderFile("slide", "swap.glsl"),
        ShaderFile("slide", "top_bottom.glsl"),
        ShaderFile("slide", "window_blinds.glsl"),
        ShaderFile("slide", "window_slice.glsl"),
        ShaderFile("slide", "wipe_down.glsl"),
        ShaderFile("slide", "wipe_left.glsl"),
        ShaderFile("slide", "wipe_right.glsl"),
        ShaderFile("slide", "wipe_up.glsl")
    )

    /**
     * 从Assert中加载数据
     */
    fun loadShaderFromAssets(context: Context, shaderFile: ShaderFile): String {
        val content = loadFromAssets(context, "transition/${shaderFile.path}/${shaderFile.name}")
        return content
    }

    /**
     * 从assert中加载文件
     */
    fun loadFromAssets(context: Context, path: String): String {
        //"shaders/transition.glsl"
        return context.assets.open(path).bufferedReader().use { it.readText() }
    }
}

data class ShaderFile(
    val path: String,        // 文件路径
    val name: String,        // 文件名称
    var content: String = "",      // 文件内容
) {
    //真实的片段着色器内容
    fun getFragmentShader(context: Context): String {
        if (content.isEmpty()) {
            val shaderContent = GlslRepo.loadShaderFromAssets(context, this)
            content = GlslRepo.fragmentShader
                .replace(
                    "vec4 transition(vec2 uv);",
                    shaderContent
                )
        }
        return content
    }
}


