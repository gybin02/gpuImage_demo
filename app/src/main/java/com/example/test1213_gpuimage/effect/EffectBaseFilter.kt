package com.example.test1213_gpuimage.effect

import android.opengl.GLES20
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

/**
 * 图片特效滤镜
 */
class EffectBaseFilter(shader:String) : GPUImageFilter(NO_FILTER_VERTEX_SHADER, shader) {
    private var uTime = 0f
    private var uTimeLocation: Int = 0
    private var ratioLocation: Int = 0
    private var uResolutionLocation: Int = 0

//    uniform float uTime;
//    uniform float ratio;
//    uniform vec2 uResolution;

    override fun onInit() {
        super.onInit()
        uTimeLocation = GLES20.glGetUniformLocation(program, "uTime")
        ratioLocation = GLES20.glGetUniformLocation(program, "ratio")
        uResolutionLocation = GLES20.glGetUniformLocation(program, "uResolution")
    }

    override fun onInitialized() {
        super.onInitialized()
        setUTime(0f)
        setRatio(0f)
        setUResolution(1f, 1f)
    }

    fun setUTime(uTime: Float) {
        this.uTime = uTime
        setFloat(uTimeLocation, this.uTime)
    }
    fun setRatio(ratio: Float) {
        setFloat(ratioLocation, ratio)
    }

    fun setUResolution(width: Float, height: Float) {
        setFloatVec2(uResolutionLocation, floatArrayOf(width, height))
    }

    companion object {
        const val BRIGHTNESS_FRAGMENT_SHADER: String = "" +
                "varying highp vec2 textureCoordinate;\n" +
                " \n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform lowp float brightness;\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "     \n" +
                "     gl_FragColor = vec4((textureColor.rgb + vec3(brightness)), textureColor.w);\n" +
                " }"
    }
}
