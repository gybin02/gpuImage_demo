package com.example.test1213_gpuimage.effect

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test1213_gpuimage.R
import com.example.test1213_gpuimage.transition.glsl.GlslRepo
import com.example.test1213_gpuimage.transition.glsl.ShaderFile
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageView

/**
 * @param shaderPath "basic"
 * @param context
 * @param fromBitmap 图片1
 * @param toBitmap 图片2
 */
class EffectFilterAdapter(
    private val fromBitmap: Bitmap,
    private val toBitmap: Bitmap,
    private val context: Context,
    val shaderPath: String,
) : RecyclerView.Adapter<EffectFilterAdapter.FilterViewHolder>() {

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
        val shaderFile = getShaderList()[position]
        holder.filterNameTextView.text = shaderFile.name

        // Setup GPUImage
        val imageView = holder.gpuImageView
        //居中下
        imageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        imageView.setImage(fromBitmap)
        val fragmentShader = EffectGlslRepo.getFragmentShader(context,shaderFile)
//        测试图片 gl_动画变化
        val transitionFilter = EffectBaseFilter(fragmentShader)
//        transitionFilter.setUTime(System.currentTimeMillis().toFloat())
        imageView.setFilter(transitionFilter)


        //测试图片像素变化
//        val transitionFilter = GPUImagePixelationFilter()
//        gpuImage.setFilter(transitionFilter)

        //测试图片混合
//        val transitionFilter = GPUImageDissolveBlendFilter()
//        transitionFilter.bitmap = toBitmap
//        gpuImage.setFilter(transitionFilter)

//        imageView.requestRender() // 渲染
    }

    fun alphaBitmap(fromBitmap: Bitmap, progress: Float): Bitmap {
        val alpha = (progress * 255).toInt()
        val newBitmap = Bitmap.createBitmap(fromBitmap.width, fromBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(newBitmap)
        canvas.drawBitmap(toBitmap, 0f, 0f, android.graphics.Paint().apply {
            setAlpha(alpha)
        })
        return newBitmap
    }

    override fun getItemCount() = GlslRepo.basicList.size

    protected fun range(percentage: Int, start: Float, end: Float): Float {
        return (end - start) * percentage / 100.0f + start
    }

    fun getShaderList(): List<ShaderFile> {
        return EffectGlslRepo.getShaderList(shaderPath)
    }

//    fun pixFilterTest() {
//        val transitionFilter = GPUImagePixelationFilter()
//        gpuImage.setFilter(transitionFilter)
//        transitionFilter.setPixel(range((progress*100).toInt(), 1.0f, 100.0f))
//        val newBitmap = gpuImage.getBitmapWithFilterApplied()
//    }

}
