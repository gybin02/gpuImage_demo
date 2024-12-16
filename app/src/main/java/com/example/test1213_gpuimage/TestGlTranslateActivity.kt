package com.example.test1213_gpuimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
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
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

//所有的滤镜 使用Rv 查看效果
class TestGlTranslateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val glSurfaceView = CustomGLSurfaceView(this)
        setContentView(glSurfaceView)
    }

}

class CustomGLSurfaceView(context: Context) : GLSurfaceView(context) {
    init {
        setEGLContextClientVersion(2) // OpenGL ES 2.0
        setRenderer(CustomRenderer(context))
        renderMode = RENDERMODE_CONTINUOUSLY // 持续更新帧
    }
}

class CustomRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private lateinit var transitionShader: String
    private var program: Int = 0
    private var startTime: Long = 0
    private val textureHandles = IntArray(2)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 加载 GLSL 文件
        transitionShader = loadShaderFromAssets("transition/crosswarp.glsl", context)

        // 初始化 OpenGL 程序
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, DEFAULT_VERTEX_SHADER)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, transitionShader)
        program = GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)
        }

        // 加载图片纹理
        textureHandles[0] = loadTexture(context, R.drawable.sample_image)
        textureHandles[1] = loadTexture(context, R.drawable.sample_image)

        startTime = System.currentTimeMillis()
    }

    override fun onDrawFrame(gl: GL10?) {
        val currentTime = (System.currentTimeMillis() - startTime) / 1000.0f

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(program)

        // 绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandles[0])
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "from"), 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandles[1])
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "to"), 1)

        // 传递时间
        val progress = (currentTime % 2) / 2.0f // 0 到 1 的循环进度
        GLES20.glUniform1f(GLES20.glGetUniformLocation(program, "progress"), progress)

        // 绘制屏幕四边形
        drawQuad()
    }



    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    private fun loadShaderFromAssets(fileName: String, context: Context): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    fun loadShader(type: Int, shaderCode: String): Int {
        // 创建着色器对象
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode) // 将 GLSL 代码加载到着色器中
        GLES20.glCompileShader(shader) // 编译着色器

        // 检查编译是否成功
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val error = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Error compiling shader: $error")
        }
        return shader
    }

    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0) // 生成纹理句柄

        if (textureHandle[0] != 0) {
            // 加载位图
            val options = BitmapFactory.Options().apply { inScaled = false }
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

            // 绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            // 设置纹理过滤器
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

            // 加载位图数据到 OpenGL
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle() // 释放位图
        } else {
            throw RuntimeException("Error loading texture.")
        }

        return textureHandle[0]
    }

    fun drawQuad() {
        // 定义顶点数据
        val vertexData = floatArrayOf(
            -1.0f, -1.0f, // 左下角
            1.0f, -1.0f,  // 右下角
            -1.0f,  1.0f, // 左上角
            1.0f,  1.0f   // 右上角
        )

        val texCoordData = floatArrayOf(
            0.0f, 1.0f, // 左下角
            1.0f, 1.0f, // 右下角
            0.0f, 0.0f, // 左上角
            1.0f, 0.0f  // 右上角
        )

        // 创建顶点缓冲区
        val vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(vertexData).position(0) }

        // 创建纹理坐标缓冲区
        val texCoordBuffer = ByteBuffer.allocateDirect(texCoordData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(texCoordData).position(0) }

        // 启用顶点属性数组
        GLES20.glEnableVertexAttribArray(0)
        GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glEnableVertexAttribArray(1)
        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

        // 绘制四边形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // 禁用顶点属性数组
        GLES20.glDisableVertexAttribArray(0)
        GLES20.glDisableVertexAttribArray(1)
    }


    companion object{
        val DEFAULT_VERTEX_SHADER:String="""
            attribute vec4 position;
            attribute vec2 texCoord;
            varying vec2 vTexCoord;

            void main() {
                gl_Position = position;
                vTexCoord = texCoord;
            }
        """.trimIndent()
    }
}
