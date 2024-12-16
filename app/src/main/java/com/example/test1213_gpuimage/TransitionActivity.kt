package com.example.test1213_gpuimage

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.GPUImageTwoInputFilter

//GPUImage + TwoInputFilter 实现
class TransitionActivity : AppCompatActivity() {

    private lateinit var gpuImageView: GPUImageView
    private lateinit var transitionFilter: TransitionFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_custom)

        gpuImageView = findViewById(R.id.gpuimage)

        // 准备两张图片
        val bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.sample_image)
        val bitmap2 = BitmapFactory.decodeResource(resources, R.drawable.sample_image2)

        // 创建过渡滤镜
        transitionFilter = TransitionFilter(this)

        gpuImageView.setImage(bitmap1)
        // 使用 setInputTexture 方法设置两个纹理
        transitionFilter.bitmap = bitmap2

        // 设置 GPUImageView
        gpuImageView.setFilter(transitionFilter)

        // 动态更新过渡进度
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                transitionFilter.setProgress(progress)
                gpuImageView.requestRender() // 刷新视图
            }
        }
        animator.start()
    }

//    private fun startTransitionAnimation() {
//        Thread {
//            for (progress in 0..100 step 1) {
//                runOnUiThread {
//                    transitionFilter.setProgress(progress / 100f)
//                    gpuImageView.requestRender()
//                }
//                Thread.sleep(20) // 控制动画速度
//            }
//        }.start()
//    }
}

class TransitionFilter(private val context: Context) : GPUImageTwoInputFilter(loadShaderFromAssets()) {
    private var progressLocation: Int = 0
    private var dotsLocation: Int = 0
    private var centerLocation: Int = 0

    private var progress: Float = 0f
    private var dots: Float = 10f
    private var center: FloatArray = floatArrayOf(0.5f, 0.5f)

//    init {
    // 从 assets 加载自定义 GLSL 着色器
//        val vertexShader = loadShader("default_vertex.glsl")
//        val fragmentShader = loadShader("transition_fragment.glsl")

//        super.setFragmentShader(fragmentShader)
//        super.setVertexShader(vertexShader)
//    }

    private fun loadShader(filename: String): String {
        return context.assets.open(filename).bufferedReader().use { it.readText() }
    }

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

//    // 重写父类的 setBitmap2 方法
//    override fun setBitmap(bitmap: Bitmap) {
//        super.setBitmap(bitmap)
//    }
}