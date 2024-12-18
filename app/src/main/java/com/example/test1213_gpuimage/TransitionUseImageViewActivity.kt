package com.example.test1213_gpuimage

import android.animation.ValueAnimator
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.test1213_gpuimage.transition.TransitionBlendFilter
import jp.co.cyberagent.android.gpuimage.GPUImage

//GPUImage + TwoInputFilter 实现
class TransitionUseImageViewActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_custom_image)

        val imageView: ImageView = findViewById(R.id.iv_content)

        // 准备两张图片
        val fromBitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_image)
        val toBitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_image2)

        val gpuImage = GPUImage(this)
        gpuImage.setImage(fromBitmap) // 原始图像
        // 创建过渡滤镜
//        val transitionFilter = TransitionFilter()
        val transitionFilter = TransitionBlendFilter()
        transitionFilter.bitmap = toBitmap
        //TODO 测试发现 TransitionBlendFilter 可以使用ImageView加载
        //但是自定义的另一个filter transitionFilter就失败，只能用GPUImageView加载。
        //唯一的差别的 glsl文件不一样
        // 设置 GPUImageView
        gpuImage.setFilter(transitionFilter)

        // 动态更新过渡进度
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE //循环
            repeatMode = ValueAnimator.REVERSE //翻转
            //线性
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
//                transitionFilter.setProgress(progress)
                transitionFilter.setMix(progress)
                //生成新图片
                val newBitmap = gpuImage.getBitmapWithFilterApplied()
                imageView.setImageBitmap(newBitmap)
            }
        }
        animator.start()

    }
}


