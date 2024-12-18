package com.example.test1213_gpuimage

import android.animation.ValueAnimator
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.test1213_gpuimage.transition.TransitionTestFilter
import com.example.test1213_gpuimage.video.VideoTransitionRenderer3
import jp.co.cyberagent.android.gpuimage.GPUImageView

//GPUImage 加载动画 + TwoInputFilter 实现
class TransitionActivity : AppCompatActivity() {

    private lateinit var gpuImageView: GPUImageView
    private lateinit var transitionFilter: TransitionTestFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_custom)

        gpuImageView = findViewById(R.id.gpuimage)

        // 准备两张图片
        val bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.sample_image)
        val bitmap2 = BitmapFactory.decodeResource(resources, R.drawable.sample_image2)

        // 创建过渡滤镜
        transitionFilter = TransitionTestFilter()

        gpuImageView.setImage(bitmap1)
        // 使用 setInputTexture 方法设置两个纹理
        transitionFilter.bitmap = bitmap2

        // 设置 GPUImageView
        gpuImageView.setFilter(transitionFilter)

        // 动态更新过渡进度
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            //线性
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                transitionFilter.setProgress(progress)
                gpuImageView.requestRender() // 刷新视图
            }
        }
        animator.start()

        this.findViewById<Button>(R.id.btn_save).setOnClickListener {
            // com.example.test1213_gpuimage.video.VideoTransitionRenderer 类 把过渡动画转为 mp4视频
            val renderer = VideoTransitionRenderer3(this)
            renderer.renderTransitionVideo(bitmap1, bitmap2)
        }
    }
}


