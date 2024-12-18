package com.example.test1213_gpuimage.rv

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test1213_gpuimage.R
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools

//所有的滤镜 使用Rv 查看效果
class FilterGalleryActivity : AppCompatActivity() {

    private lateinit var filterRecyclerView: RecyclerView
    private lateinit var originalBitmap: Bitmap
    private lateinit var filterAdapter: FilterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_gallery)

        // Load original image
        originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_image)

        filterRecyclerView = findViewById(R.id.filterRecyclerView)
        filterRecyclerView.setItemViewCacheSize(Int.MAX_VALUE) // 无限缓存
        filterRecyclerView.recycledViewPool.setMaxRecycledViews(0, 0) // 禁用池
        filterRecyclerView.layoutManager = LinearLayoutManager(this)

        // Create filter list
        val filterList = GPUImageFilterTools.filterList

        // Create and set adapter
        filterAdapter = FilterAdapter(filterList, originalBitmap, this)
        filterRecyclerView.adapter = filterAdapter
    }
}
