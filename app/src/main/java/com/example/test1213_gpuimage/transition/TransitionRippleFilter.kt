package com.example.test1213_gpuimage.transition

import android.opengl.GLES20
import com.example.test1213_gpuimage.transition.glsl.GlslRepo
import jp.co.cyberagent.android.gpuimage.filter.GPUImageTwoInputFilter

// 水波纹 图片过渡动画，自定义的 ( https://gl-transitions.com/editor/PolkaDotsCurtain)
class TransitionRippleFilter : GPUImageTwoInputFilter(fragmentShader) {
    companion object{

        val fragmentShader = GlslRepo.fragmentShader
            .replace(
                "vec4 transition(vec2 uv);",
                GlslRepo.ripple
            )
    }

    private var progressLocation: Int = 0

    private var progress: Float = 0f

    override fun onInit() {
        super.onInit()
        progressLocation = GLES20.glGetUniformLocation(program, "progress")
    }

    override fun onInitialized() {
        super.onInitialized()
        setProgress(0f)
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        setFloat(progressLocation, progress)
    }

}

