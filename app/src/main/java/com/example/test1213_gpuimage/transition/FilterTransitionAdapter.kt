package com.example.test1213_gpuimage.transition

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test1213_gpuimage.R
import jp.co.cyberagent.android.gpuimage.GPUImageView

class FilterTransitionAdapter(
    private val fromBitmap: Bitmap,
    private val toBitmap: Bitmap,
    private val context: Context,
) : RecyclerView.Adapter<FilterTransitionAdapter.FilterViewHolder>() {

    class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filterNameTextView: TextView = itemView.findViewById(R.id.filterNameTextView)
        val gpuImageView: GPUImageView = itemView.findViewById(R.id.gpu_image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.filter_item_gpu_layout, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        // Set filter name
        holder.filterNameTextView.text = "Test"

        // Setup GPUImage
        val imageView = holder.gpuImageView
//        imageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        imageView.setImage(fromBitmap)
//        测试图片 gl_动画变化
        val transitionFilter = TransitionTestFilter()
        transitionFilter.bitmap = toBitmap
        imageView.setFilter(transitionFilter)


        //测试图片像素变化
//        val transitionFilter = GPUImagePixelationFilter()
//        gpuImage.setFilter(transitionFilter)

        //测试图片混合
//        val transitionFilter = GPUImageDissolveBlendFilter()
//        transitionFilter.bitmap = toBitmap
//        gpuImage.setFilter(transitionFilter)

        // 动态更新过渡进度
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            //线性
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                //测试图片 gl_动画变化
                transitionFilter.setProgress(progress)

                //测试 progress 转为alpha，生成新的图片，刷新到ImageView
//                val newBitmap = alphaBitmap(toBitmap,progress)
                //测试 GPUImageDissolveBlendFilter变化
//                transitionFilter.setMix(progress)
                imageView.requestRender() // 渲染
////                //生成新图片
//                val newBitmap = gpuImage.getBitmapWithFilterApplied()
//                imageView.setImageBitmap(newBitmap)

//                 gpuImage.requestRender()
            }
        }
        animator.start()
    }

    fun alphaBitmap(fromBitmap:Bitmap,progress: Float): Bitmap {
        val alpha = (progress * 255).toInt()
        val newBitmap = Bitmap.createBitmap(fromBitmap.width, fromBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(newBitmap)
        canvas.drawBitmap(toBitmap, 0f, 0f, android.graphics.Paint().apply {
            setAlpha(alpha)
        })
        return newBitmap
    }

    override fun getItemCount() = 10

    protected fun range(percentage: Int, start: Float, end: Float): Float {
        return (end - start) * percentage / 100.0f + start
    }

//    fun pixFilterTest() {
//        val transitionFilter = GPUImagePixelationFilter()
//        gpuImage.setFilter(transitionFilter)
//        transitionFilter.setPixel(range((progress*100).toInt(), 1.0f, 100.0f))
//        val newBitmap = gpuImage.getBitmapWithFilterApplied()
//    }

}
