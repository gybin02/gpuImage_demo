package com.example.test1213_gpuimage.transition

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test1213_gpuimage.R

//GPUImage + TwoInputFilter 实现
class TransitionRvActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_gallery)

        // Load original image
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_image)
        val toBitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_image2)

        val recyclerView: RecyclerView = findViewById(R.id.filterRecyclerView)
        recyclerView.setItemViewCacheSize(Int.MAX_VALUE) // 无限缓存
        recyclerView.recycledViewPool.setMaxRecycledViews(0, 0) // 禁用池
        recyclerView.layoutManager = LinearLayoutManager(this)


        // Create and set adapter
        val filterAdapter = FilterTransitionAdapter(originalBitmap, toBitmap, this)
        recyclerView.adapter = filterAdapter
    }
}



