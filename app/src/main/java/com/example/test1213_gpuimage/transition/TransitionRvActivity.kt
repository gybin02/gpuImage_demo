package com.example.test1213_gpuimage.transition

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test1213_gpuimage.R

//GPUImage + TwoInputFilter 实现
class TransitionRvActivity : AppCompatActivity() {

    companion object{
        const val KEY_SHADER_PATH = "key_shader_path"
    }
    //从Intent 获取路径
    var shaderPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_gallery)
        shaderPath = intent.getStringExtra(KEY_SHADER_PATH) ?: ""
        // Load original image
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_image)
        val toBitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_image2)

        val recyclerView: RecyclerView = findViewById(R.id.filterRecyclerView)
        recyclerView.setItemViewCacheSize(Int.MAX_VALUE) // 无限缓存
        recyclerView.recycledViewPool.setMaxRecycledViews(0, 0) // 禁用池
        recyclerView.layoutManager = LinearLayoutManager(this)


        // Create and set adapter
        val filterAdapter = FilterTransitionAdapter(originalBitmap, toBitmap, this,shaderPath)
        recyclerView.adapter = filterAdapter
    }
}



