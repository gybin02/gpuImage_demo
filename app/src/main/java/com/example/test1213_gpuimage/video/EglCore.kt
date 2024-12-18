package com.example.test1213_gpuimage.video

import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.view.Surface

class EglCore {
    private var eglDisplay: EGLDisplay = EGL14.EGL_NO_DISPLAY
    private var eglContext: EGLContext = EGL14.EGL_NO_CONTEXT
    private var eglConfig: EGLConfig? = null

    init {
        // 初始化 EGL 显示
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (eglDisplay === EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("eglGetDisplay failed")
        }

        // 初始化 EGL
        val version = IntArray(2)
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            throw RuntimeException("eglInitialize failed")
        }

        // 选择 EGL 配置
        val configAttribs = intArrayOf(
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_DEPTH_SIZE, 0,
            EGL14.EGL_STENCIL_SIZE, 0,
            EGL14.EGL_NONE
        )

        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        if (!EGL14.eglChooseConfig(
                eglDisplay, 
                configAttribs, 
                0, 
                configs, 
                0, 
                1, 
                numConfigs, 
                0
            )
        ) {
            throw RuntimeException("eglChooseConfig failed")
        }

        eglConfig = configs[0]

        // 创建 EGL 上下文
        val contextAttribs = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL14.EGL_NONE
        )

        eglContext = EGL14.eglCreateContext(
            eglDisplay, 
            eglConfig, 
            EGL14.EGL_NO_CONTEXT, 
            contextAttribs, 
            0
        )

        if (eglContext === EGL14.EGL_NO_CONTEXT) {
            throw RuntimeException("eglCreateContext failed")
        }
    }

    /**
     * 创建离屏渲染表面
     */
    fun createOffscreenSurface(width: Int, height: Int): EGLSurface {
        val surfaceAttribs = intArrayOf(
            EGL14.EGL_WIDTH, width,
            EGL14.EGL_HEIGHT, height,
            EGL14.EGL_NONE
        )

        val eglSurface = EGL14.eglCreatePbufferSurface(
            eglDisplay, 
            eglConfig, 
            surfaceAttribs, 
            0
        )

        if (eglSurface == null) {
            throw RuntimeException("eglCreatePbufferSurface failed")
        }

        return eglSurface
    }

    /**
     * 创建窗口表面
     */
    fun createWindowSurface(surface: Surface): EGLSurface {
        val surfaceAttribs = intArrayOf(EGL14.EGL_NONE)

        val eglSurface = EGL14.eglCreateWindowSurface(
            eglDisplay, 
            eglConfig, 
            surface, 
            surfaceAttribs, 
            0
        )

        if (eglSurface == null) {
            throw RuntimeException("eglCreateWindowSurface failed")
        }

        return eglSurface
    }

    /**
     * 设置当前 EGL 上下文为指定表面
     */
    fun makeCurrent(eglSurface: EGLSurface) {
        if (!EGL14.eglMakeCurrent(
                eglDisplay, 
                eglSurface, 
                eglSurface, 
                eglContext
            )
        ) {
            throw RuntimeException("eglMakeCurrent failed")
        }
    }

    /**
     * 交换缓冲区，显示渲染结果
     */
    fun swapBuffers(eglSurface: EGLSurface): Boolean {
        return EGL14.eglSwapBuffers(eglDisplay, eglSurface)
    }

    /**
     * 检查是否存在 EGL 错误
     */
    fun checkEglError(msg: String) {
        val error = EGL14.eglGetError()
        if (error != EGL14.EGL_SUCCESS) {
            throw RuntimeException("$msg: EGL error: ${error}")
        }
    }

    /**
     * 释放 EGL 资源
     */
    fun release() {
        if (eglDisplay !== EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(
                eglDisplay, 
                EGL14.EGL_NO_SURFACE, 
                EGL14.EGL_NO_SURFACE, 
                EGL14.EGL_NO_CONTEXT
            )
            EGL14.eglDestroyContext(eglDisplay, eglContext)
            EGL14.eglTerminate(eglDisplay)
        }

        eglDisplay = EGL14.EGL_NO_DISPLAY
        eglContext = EGL14.EGL_NO_CONTEXT
    }
}