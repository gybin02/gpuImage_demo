package com.example.test1213_gpuimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test1213_gpuimage.R
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools
import java.io.File

//所有的滤镜 使用Rv 查看效果
class TestGlslActivity : AppCompatActivity() {

//    private lateinit var filterRecyclerView: RecyclerView
    private lateinit var originalBitmap: Bitmap
//    private lateinit var filterAdapter: FilterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_custom)

        // Load original image
        originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_image)

//        filterRecyclerView = findViewById(R.id.filterRecyclerView)
//        filterRecyclerView.layoutManager = LinearLayoutManager(this)

        // Create filter list
//        val filterList = GPUImageFilterTools.filterList
//
//        // Create and set adapter
//        filterAdapter = FilterAdapter(filterList, originalBitmap, this)
//        filterRecyclerView.adapter = filterAdapter

        val imageView = findViewById<ImageView>(R.id.iv_content)
       val  gpuImage = GPUImage(this)
        // 加载 GLSL 文件
        val fragmentShader = loadShaderFromFile("/mnt/data/animation_fragment_shader.glsl")
        // 创建自定义滤镜
        val customFilter = CustomGLSLFilter(fragmentShader)

        // 应用滤镜
        gpuImage.setFilter(customFilter)

        // 获取处理后的图片
        val filteredBitmap = gpuImage.bitmapWithFilterApplied

        // 显示结果
        imageView.setImageBitmap(filteredBitmap)
    }

    // 加载 GLSL 文件内容
    private fun loadShaderFromFile(filePath: String): String {
        return File(filePath).readText(Charsets.UTF_8)
    }
    //从asset 中加载 glsl文件
    private fun loadShaderFromAssets(fileName: String): String {
        val inputStream = assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charsets.UTF_8)
    }

}

class CustomGLSLFilter(fragmentShader: String) : GPUImageFilter(NO_FILTER_VERTEX_SHADER, fragmentShader)
