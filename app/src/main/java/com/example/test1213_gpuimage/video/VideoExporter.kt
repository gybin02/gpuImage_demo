//package com.example.test1213_gpuimage.video
//
//import android.content.ContentValues
//import android.content.Context
//import android.graphics.Bitmap
//import android.media.MediaCodec
//import android.media.MediaCodec.BufferInfo
//import android.media.MediaFormat
//import android.media.MediaMuxer
//import android.net.Uri
//import android.opengl.GLES20
//import android.os.Environment
//import android.provider.MediaStore
//import jp.co.cyberagent.android.gpuimage.GPUImage
//import java.io.File
//import java.nio.ByteBuffer
//
//class VideoExporter(private val context: Context) {
//    private lateinit var mediaCodec: MediaCodec
//    private lateinit var mediaMuxer: MediaMuxer
//    private var trackIndex = -1
//    private var isRecording = false
//
//    fun exportTransitionToVideo(
//        gpuImage: GPUImage,
//        startBitmap: Bitmap,
//        endBitmap: Bitmap,
//        width: Int,
//        height: Int
//    ) {
//        // 配置视频参数
//        val mimeType = MediaFormat.MIMETYPE_VIDEO_AVC
//        val bitRate = width * height * 3 * 8
//        val frameRate = 30
//        val iframeInterval = 1
//
//        // 创建 MediaFormat
//        val mediaFormat = MediaFormat.createVideoFormat(mimeType, width, height).apply {
//            setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
//            setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
//            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iframeInterval)
//            setInteger(MediaFormat.KEY_COLOR_FORMAT,  MediaCodec.VideoFormat.COLOR_FormatYUV420Flexible)
//        }
//
//        // 准备文件保存
//        val videoFileName = "transition_${System.currentTimeMillis()}.mp4"
//        val videoFile = createVideoFile(videoFileName)
//
//        try {
//            // 初始化 MediaCodec 和 MediaMuxer
//            mediaCodec = MediaCodec.createEncoderByType(mimeType)
//            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
//
//            mediaMuxer = MediaMuxer(videoFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
//
//            // 开始编码
//            mediaCodec.start()
//            trackIndex = mediaMuxer.addTrack(mediaFormat)
//            mediaMuxer.start()
//
//            // 创建绘制和编码线程
//            Thread {
//                encodeTransitionFrames(gpuImage, startBitmap, endBitmap, width, height)
//            }.start()
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun encodeTransitionFrames(
//        gpuImage: GPUImage,
//        startBitmap: Bitmap,
//        endBitmap: Bitmap,
//        width: Int,
//        height: Int
//    ) {
//        val bufferInfo = BufferInfo()
//        val frameInterval = 1000 / 30 // 30 fps
//        val totalDuration = 2000 // 总时长 2 秒
//
//        // 准备 GPU 图像
//        gpuImage.setImage(startBitmap)
//
//        for (progress in 0..100 step 1) {
//            // 模拟动画进度
//            val progressFloat = progress / 100f
//
//            // 在这里应用过渡效果
//            // 具体实现取决于您的 GPUImage 设置和滤镜
//
//            // 捕获帧
//            val frameBitmap = captureFrameBitmap(gpuImage, width, height)
//
//            // 编码帧
//            encodeFrame(frameBitmap, bufferInfo)
//
//            Thread.sleep(frameInterval.toLong())
//        }
//
//        // 结束编码
//        finishEncoding(bufferInfo)
//    }
//
//    private fun captureFrameBitmap(gpuImage: GPUImage, width: Int, height: Int): Bitmap {
//        // 使用 GPUImage 捕获当前帧
//        val frameBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        gpuImage.saveToBitmap(frameBitmap)
//        return frameBitmap
//    }
//
//    private fun encodeFrame(bitmap: Bitmap, bufferInfo: BufferInfo) {
//        // 视频编码逻辑
//        val inputBufferIndex = mediaCodec.dequeueInputBuffer(10000)
//        if (inputBufferIndex >= 0) {
//            // 将 Bitmap 转换为字节数组
//            val inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex)
//            inputBuffer?.clear()
//
//            // 将 Bitmap 转换并写入
//            val byteBuffer = ByteBuffer.allocate(bitmap.byteCount)
//            bitmap.copyPixelsToBuffer(byteBuffer)
//            inputBuffer?.put(byteBuffer.array())
//
//            mediaCodec.queueInputBuffer(inputBufferIndex, 0, byteBuffer.array().size,
//                System.nanoTime() / 1000, 0)
//        }
//
//        // 输出编码后的帧
//        val outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000)
//        if (outputBufferIndex >= 0) {
//            val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)
//
//            if (outputBuffer != null) {
//                mediaMuxer.writeSampleData(trackIndex, outputBuffer, bufferInfo)
//                mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
//            }
//        }
//    }
//
//    private fun finishEncoding(bufferInfo: BufferInfo) {
//        mediaCodec.stop()
//        mediaCodec.release()
//        mediaMuxer.stop()
//        mediaMuxer.release()
//
//        // 通知相册更新
//        val contentValues = ContentValues().apply {
//            put(MediaStore.Video.Media.DISPLAY_NAME, "transition_video")
//            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
//            put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
//        }
//
//        val contentResolver = context.contentResolver
//        contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
//    }
//
//    private fun createVideoFile(fileName: String): File {
//        val moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
//        return File(moviesDir, fileName)
//    }
//}