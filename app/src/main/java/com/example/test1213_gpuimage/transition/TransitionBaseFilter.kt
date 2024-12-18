package com.example.test1213_gpuimage.transition

import android.opengl.GLES20
import jp.co.cyberagent.android.gpuimage.filter.GPUImageTwoInputFilter

/**
 * 双图片过渡动画，自定义的 ( https://gl-transitions.com/editor/PolkaDotsCurtain)
 *  @param fragmentShader 着色器
 * */
class TransitionBaseFilter(fragmentShader: String) : GPUImageTwoInputFilter(fragmentShader) {

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

    companion object {
//        fun buildFragmentShader(context: Context, shaderFile: ShaderFile): String {
//            return GlslRepo.loadShaderFromAssets(context, shaderFile)
//        }
    }

}

