package com.example.test1213_gpuimage

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import java.nio.FloatBuffer

//GPUImage 测试 加载 translate.glsl文件
class TestGpuGlslActivity : AppCompatActivity() {

    private lateinit var originalBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_custom)

        // Load original image
        originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_image)
       val  inputBitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_image2)
        val gpuimageView = findViewById<GPUImageView>(R.id.gpuimage)
        gpuimageView.setImage(originalBitmap)
        // 创建自定义滤镜
        val customFilter = CustomTransitionFilter(this)
        customFilter.setProgress(0f)  // 起始过渡进度
        customFilter.setSecondTexture(inputBitmap)
        // 应用滤镜
        gpuimageView.setFilter(customFilter)

        // 动态更新过渡进度
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                customFilter.setProgress(progress)
                gpuimageView.requestRender() // 刷新视图
            }
        }
        animator.start()
    }

//    //从asset 中加载 glsl文件
//    private fun loadShaderFromAssets(fileName: String): String {
//        val inputStream = assets.open(fileName)
//        val size = inputStream.available()
//        val buffer = ByteArray(size)
//        inputStream.read(buffer)
//        inputStream.close()
//        return String(buffer, Charsets.UTF_8)
//    }



}

class CustomTransitionFilter(context: Context) : GPUImageFilter(NO_FILTER_VERTEX_SHADER, loadShaderFromAssets(context)) {

    private var progressLocation: Int = 0
    private var dotsLocation: Int = 0
    private var centerLocation: Int = 0
    private var secondTextureHandle: Int = -1
    private var secondTextureLocation: Int = 0

    override fun onInit() {
        super.onInit()
        // 获取 Uniform 参数的位置
        progressLocation = GLES20.glGetUniformLocation(program, "progress")
        dotsLocation = GLES20.glGetUniformLocation(program, "dots")
        centerLocation = GLES20.glGetUniformLocation(program, "center")
        secondTextureLocation = GLES20.glGetUniformLocation(program, "inputImageTexture2")
    }

    override fun onInitialized() {
        super.onInitialized()
        // 初始化 Uniform 参数
        setProgress(0f)
        setDots(20f)
        setCenter(floatArrayOf(0.5f, 0.5f))
    }

    override fun onDraw(textureId: Int, cubeBuffer: FloatBuffer, textureBuffer: FloatBuffer) {
        // 激活并绑定第一个纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(uniformTexture, 0)

        // 激活并绑定第二个纹理
        if (secondTextureHandle != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, secondTextureHandle)
            GLES20.glUniform1i(secondTextureLocation, 1)
        }

        super.onDraw(textureId, cubeBuffer, textureBuffer)
    }

    fun setSecondTexture(secondBitmap: Bitmap) {
        // 加载第二张纹理
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        secondTextureHandle = textures[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, secondTextureHandle)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, secondBitmap, 0)

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat())
    }

    fun setProgress(progress: Float) {
        setFloat(progressLocation, progress)
    }

    fun setDots(dots: Float) {
        setFloat(dotsLocation, dots)
    }

    fun setCenter(center: FloatArray) {
        setFloatVec2(centerLocation, center)
    }

//    private fun loadShaderFromAssets(context: Context): String {
//        return context.assets.open("shaders/transition.glsl").bufferedReader().use { it.readText() }
//    }
}


// 加载 GLSL 文件内容
fun loadShaderFromAssets(context: Context): String {
//        return File(filePath).readText(Charsets.UTF_8)
    return """
            precision mediump float;

            varying vec2 textureCoordinate;

            uniform sampler2D inputImageTexture;   // 当前纹理
            uniform sampler2D inputImageTexture2; // 下一个纹理

            uniform float progress;   // 过渡进度
            uniform float dots;       // 参数 dots
            uniform vec2 center;      // 参数中心点

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


