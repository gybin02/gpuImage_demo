package com.example.test1213_gpuimage.rv

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test1213_gpuimage.R
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools
import jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools.FilterAdjuster
import jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools.FilterRepo

class FilterAdapter(
    private val filterRepo: FilterRepo,
    private val originalBitmap: Bitmap,
    private val context: Context,
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filterNameTextView: TextView = itemView.findViewById(R.id.filterNameTextView)
        val filteredImageView: ImageView = itemView.findViewById(R.id.filteredImageView)
        val filterAdjustSeekBar: SeekBar = itemView.findViewById(R.id.filterAdjustSeekBar)
    }

    val gpuImage = GPUImage(context)

    init {
        gpuImage.setImage(originalBitmap)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.filter_item_layout, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filterItem = filterRepo.filters[position]

        // Set filter name
        holder.filterNameTextView.text = filterRepo.names[position]

        // Setup GPUImage
        val imageView  = holder.filteredImageView

        val filter = GPUImageFilterTools.createFilterForType(context, filterItem)
        gpuImage.setFilter(filter)

//        val filterAdjuster = FilterAdjuster(filter)
        // Set filtered image
//        holder.filteredImageView.setImage(gpuImage.gpuImage())

        // Setup filter adjuster if possible
        val adjuster = FilterAdjuster(filter)
        if (adjuster.canAdjust()) {
            holder.filterAdjustSeekBar.visibility = View.VISIBLE
            holder.filterAdjustSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    adjuster.adjust(progress)

                   val bitmap =  gpuImage.getBitmapWithFilterApplied()
                    imageView.setImageBitmap(bitmap)
//                    gpuImage.setFilter(filterItem.filter)
//                    holder.filteredImageView.setImage(gpuImage.gpuImage())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        } else {
            holder.filterAdjustSeekBar.visibility = View.GONE
        }
        val bitmap =  gpuImage.getBitmapWithFilterApplied()
        imageView.setImageBitmap(bitmap)
    }

    override fun getItemCount() = filterRepo.filters.size


}
