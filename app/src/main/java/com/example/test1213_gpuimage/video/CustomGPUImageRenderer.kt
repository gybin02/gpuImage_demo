//package com.example.test1213_gpuimage.video
//
//import android.graphics.SurfaceTexture
//import android.opengl.EGL14
//import android.opengl.EGLContext
//import android.opengl.EGLDisplay
//import android.opengl.EGLSurface
//import android.opengl.GLES20
//import android.view.Surface
//import jp.co.cyberagent.android.gpuimage.GPUImageRenderer
//import javax.microedition.khronos.egl.EGLConfig
//import javax.microedition.khronos.opengles.GL10
//
//class CustomGPUImageRenderer : GPUImageRenderer() {
//    private var eglDisplay: EGLDisplay = EGL14.EGL_NO_DISPLAY
//    private var eglContext: EGLContext = EGL14.EGL_NO_CONTEXT
//    private var eglSurface: EGLSurface = EGL14.EGL_NO_SURFACE
//
//    private var outputSurface: Surface? = null
//
//    fun setRenderSurface(surface: Surface) {
//        outputSurface = surface
//    }
//
//
//    override fun onSurfaceCreated(glUnused: GL10?, config: EGLConfig?) {
//        super.onSurfaceCreated(glUnused, config)
//        setupEGL()
//    }
//
//    override fun onDrawFrame(glUnused: GL10?) {
//        if (outputSurface != null && eglSurface != EGL14.EGL_NO_SURFACE) {
//            // 绑定到外部 Surface
//            makeCurrent(eglSurface)
//            super.onDrawFrame(glUnused)
//            EGL14.eglSwapBuffers(eglDisplay, eglSurface)
//        }
//    }
//
//    override fun onSurfaceDestroyed() {
//        super.onSurfaceDestroyed()
//        releaseEGL()
//    }
//
//    private fun setupEGL() {
//        // 初始化 EGL
//        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
//        val version = IntArray(2)
//        EGL14.eglInitialize(eglDisplay, version, 0, version, 1)
//
//        val attribList = intArrayOf(
//            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
//            EGL14.EGL_NONE
//        )
//        val configs = arrayOfNulls<EGLConfig>(1)
//        val numConfigs = IntArray(1)
//        EGL14.eglChooseConfig(eglDisplay, attribList, 0, configs, 0, 1, numConfigs, 0)
//        val config = configs[0]
//
//        val contextAttribs = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
//        eglContext = EGL14.eglCreateContext(eglDisplay, config, EGL14.EGL_NO_CONTEXT, contextAttribs, 0)
//
//        // 创建 EGL Surface
//        if (outputSurface != null) {
//            val surfaceAttribs = intArrayOf(EGL14.EGL_NONE)
//            eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, config, outputSurface, surfaceAttribs, 0)
//        }
//    }
//
//    private fun makeCurrent(surface: EGLSurface) {
//        EGL14.eglMakeCurrent(eglDisplay, surface, surface, eglContext)
//    }
//
//    private fun releaseEGL() {
//        if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
//            EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
//            if (eglSurface != EGL14.EGL_NO_SURFACE) {
//                EGL14.eglDestroySurface(eglDisplay, eglSurface)
//            }
//            if (eglContext != EGL14.EGL_NO_CONTEXT) {
//                EGL14.eglDestroyContext(eglDisplay, eglContext)
//            }
//            EGL14.eglTerminate(eglDisplay)
//        }
//        eglDisplay = EGL14.EGL_NO_DISPLAY
//        eglContext = EGL14.EGL_NO_CONTEXT
//        eglSurface = EGL14.EGL_NO_SURFACE
//    }
//}
