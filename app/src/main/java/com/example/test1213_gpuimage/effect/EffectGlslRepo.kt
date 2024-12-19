package com.example.test1213_gpuimage.effect

import android.content.Context
import com.example.test1213_gpuimage.transition.glsl.GlslRepo
import com.example.test1213_gpuimage.transition.glsl.ShaderFile

//单图图片效果
object EffectGlslRepo {
    //Fragment 过渡动画着色器
//    val fragmentShader = """
//        precision highp float;
//        uniform float uTime;
//        uniform float ratio;
//        uniform sampler2D inputImageTexture;
//        uniform vec2 uResolution;
//        varying vec2 textureCoordinate;
//
//        vec4 effect(vec2 uv);
//
//        void main()
//        {
//            gl_FragColor = effect(textureCoordinate);
//        }
//    """.trimIndent()
    val fragmentShader = """
         precision highp float;
        uniform float uTime;
        uniform float ratio;
        uniform sampler2D inputImageTexture;
        uniform vec2 uResolution;
        varying vec2 textureCoordinate;
        
        vec4 effect(vec2 uv);
        
        void main() {
            gl_FragColor = texture2D(inputImageTexture, textureCoordinate) + effect(textureCoordinate);
        }
    """.trimIndent()
    //顶点
    val vertexShader = """
        precision highp float;

        uniform mat4 uMatrix;
        attribute vec4 position;
        attribute vec2 inputTextureCoordinate;
        attribute float alpha;
        
        varying vec2 textureCoordinate;
        varying float inAlpha;

        void main(){
            gl_Position = position;
            textureCoordinate = inputTextureCoordinate.xy;
            inAlpha = alpha;
        }
    """.trimIndent()


    val mixList = listOf(
        ShaderFile("mix", "center.glsl"),
        ShaderFile("mix", "galaxy_rainbow.glsl"),
        ShaderFile("mix", "heart_flicker.glsl"),
        ShaderFile("mix", "heart_layer.glsl"),
        ShaderFile("mix", "heart_raining.glsl"),
        ShaderFile("mix", "heart_zoom_out.glsl"),
        ShaderFile("mix", "light_river.glsl"),
        ShaderFile("mix", "rainbow.glsl"),
        ShaderFile("mix", "round_rainbow.glsl"),
        ShaderFile("mix", "stage.glsl"),
        ShaderFile("mix", "water_reflections.glsl"),
        ShaderFile("mix", "whirl_pool.glsl")
    )

    val transformList = listOf(
        ShaderFile("transform", "four_screens.glsl"),
        ShaderFile("transform", "light_filter.glsl"),
        ShaderFile("transform", "mirror_three.glsl"),
        ShaderFile("transform", "nine_screens.glsl"),
        ShaderFile("transform", "screen_pulse.glsl"),
        ShaderFile("transform", "screen_pulse_2.glsl"),
        ShaderFile("transform", "shake.glsl"),
        ShaderFile("transform", "two_screens_horizontal.glsl"),
        ShaderFile("transform", "two_screens_vertical.glsl"),
        ShaderFile("transform", "zoom_lens.glsl"),
        ShaderFile("transform", "zoom_soul.glsl"),
        ShaderFile("transform", "zoom_stab.glsl")
    )

    //获取动画列表
    fun getShaderList(shaderPath: String): List<ShaderFile> {
        return when (shaderPath) {
            "mix" -> mixList
            "transform" -> transformList
            else -> mixList
        }
    }


    //真实的片段着色器内容
    fun getFragmentShader(context: Context, shaderFile: ShaderFile): String {
        if (shaderFile.content.isEmpty()) {
            val shaderContent = GlslRepo.loadFromAssets(context, "effect/${shaderFile.path}/${shaderFile.name}")
            val newContent = shaderContent.replace("texture","inputImageTexture").apply {
                replace("gl_FragCoord", "textureCoordinate")
            }
            shaderFile.content = fragmentShader
                .replace(
                    "vec4 effect(vec2 uv);",
                    newContent
                )
        }
        return shaderFile.content
    }


}


