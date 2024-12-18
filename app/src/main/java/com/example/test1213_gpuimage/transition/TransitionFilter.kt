package com.example.test1213_gpuimage.transition

import android.opengl.GLES20
import jp.co.cyberagent.android.gpuimage.filter.GPUImageTwoInputFilter

//双图片过渡动画，自定义的 ( https://gl-transitions.com/editor/PolkaDotsCurtain)
class TransitionFilter : GPUImageTwoInputFilter(fragmentShader) {
    companion object{

        val fragmentShader = """
            precision highp float;

            varying highp vec2 textureCoordinate;

            uniform sampler2D inputImageTexture;   // 当前纹理
            uniform sampler2D inputImageTexture2; // 下一个纹理

            uniform  float progress;   // 过渡进度
            uniform  float dots;       // 参数 dots
            uniform  vec2 center;      // 参数中心点

            const float SQRT_2 = 1.414213562373;

            vec4 getFromColor(vec2 uv) {
                return texture2D(inputImageTexture, uv);
            }

            vec4 getToColor(vec2 uv) {
                return texture2D(inputImageTexture2, uv);
            }

            vec4 transition(vec2 uv) {
                bool nextImage = distance(fract(uv * dots), vec2(0.5, 0.5)) < (progress / distance(uv, center));
                return nextImage ? getToColor(uv) : getFromColor(uv);
            }
            
            void main() {
                gl_FragColor = transition(textureCoordinate);
            }
        """.trimIndent()
    }

    private var progressLocation: Int = 0
    private var dotsLocation: Int = 0
    private var centerLocation: Int = 0

    private var progress: Float = 0f
    private var dots: Float = 10f
    private var center: FloatArray = floatArrayOf(0.5f, 0.5f)

    override fun onInit() {
        super.onInit()
        progressLocation = GLES20.glGetUniformLocation(program, "progress")
        dotsLocation = GLES20.glGetUniformLocation(program, "dots")
        centerLocation = GLES20.glGetUniformLocation(program, "center")
    }

    override fun onInitialized() {
        super.onInitialized()
        setProgress(0f)
        setDots(dots)
        setCenter(center)
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        setFloat(progressLocation, progress)
    }

    fun setDots(dots: Float) {
        this.dots = dots
        setFloat(dotsLocation, dots)
    }

    fun setCenter(center: FloatArray) {
        this.center = center
        setFloatVec2(centerLocation, center)
    }

}

