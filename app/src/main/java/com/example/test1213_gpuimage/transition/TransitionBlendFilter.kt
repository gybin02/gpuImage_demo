package com.example.test1213_gpuimage.transition

import android.opengl.GLES20
import jp.co.cyberagent.android.gpuimage.filter.GPUImageTwoInputFilter

//双图片过渡动画，代码其实是从 GPUImage类 GPUImageDissolveBlendFilter
class TransitionBlendFilter : GPUImageTwoInputFilter(fragmentShader) {
    companion object {
        // 加载 GLSL 文件内容
        val fragmentShader = """
    varying highp vec2 textureCoordinate;
    varying highp vec2 textureCoordinate2;
    
    uniform sampler2D inputImageTexture;
    uniform sampler2D inputImageTexture2;
    uniform lowp float mixturePercent;
    
    void main() {
        lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
        lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);
        
        gl_FragColor = mix(textureColor, textureColor2, mixturePercent);
    }
""".trimIndent()
    }


    private var mixLocation = 0
    private var mix = 0f


    override fun onInit() {
        super.onInit()
        mixLocation = GLES20.glGetUniformLocation(program, "mixturePercent")
    }

    override fun onInitialized() {
        super.onInitialized()
        setMix(mix)
    }

    /**
     * @param mix ranges from 0.0 (only image 1) to 1.0 (only image 2), with 0.5 (half of either) as the normal level
     */
    fun setMix(mix: Float) {
        this.mix = mix
        setFloat(mixLocation, this.mix)
    }
}


//open class GPUImageMixBlendFilter @JvmOverloads constructor(fragmentShader: String?, private var mix: Float = 0.5f) : GPUImageTwoInputFilter(fragmentShader) {
//    private var mixLocation = 0
//
//    override fun onInit() {
//        super.onInit()
//        mixLocation = GLES20.glGetUniformLocation(program, "mixturePercent")
//    }
//
//    override fun onInitialized() {
//        super.onInitialized()
//        setMix(mix)
//    }
//
//    /**
//     * @param mix ranges from 0.0 (only image 1) to 1.0 (only image 2), with 0.5 (half of either) as the normal level
//     */
//    fun setMix(mix: Float) {
//        this.mix = mix
//        setFloat(mixLocation, this.mix)
//    }
//}

//class GPUImageAlphaBlendFilter : GPUImageMixBlendFilter {
//    constructor() : super(ALPHA_BLEND_FRAGMENT_SHADER)
//
//    constructor(mix: Float) : super(ALPHA_BLEND_FRAGMENT_SHADER, mix)
//
//    companion object {
//        const val ALPHA_BLEND_FRAGMENT_SHADER: String = "varying highp vec2 textureCoordinate;\n" +
//                " varying highp vec2 textureCoordinate2;\n" +
//                "\n" +
//                " uniform sampler2D inputImageTexture;\n" +
//                " uniform sampler2D inputImageTexture2;\n" +
//                " \n" +
//                " uniform lowp float mixturePercent;\n" +
//                "\n" +
//                " void main()\n" +
//                " {\n" +
//                "   lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
//                "   lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
//                "\n" +
//                "   gl_FragColor = vec4(mix(textureColor.rgb, textureColor2.rgb, textureColor2.a * mixturePercent), textureColor.a);\n" +
//                " }"
//    }
//}



