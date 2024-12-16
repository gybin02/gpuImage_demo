//package com.example.test1213_gpuimage.video
//
//import android.content.ContentValues
//import android.content.Context
//import android.graphics.BitmapFactory
//import android.media.MediaCodec
//import android.media.MediaCodecInfo
//import android.media.MediaFormat
//import android.media.MediaMuxer
//import android.opengl.EGL14
//import android.opengl.EGLSurface
//import android.provider.MediaStore
//import android.view.Surface
//import com.example.test1213_gpuimage.CustomTransitionFilter
//import com.example.test1213_gpuimage.R
//import jp.co.cyberagent.android.gpuimage.GPUImage
//import java.io.FileInputStream
//import java.nio.ByteBuffer
//
//class VideoEncoder(
//    private val outputPath: String,
//    private val width: Int,
//    private val height: Int,
//    private val fps: Int = 30,
//    private val bitrate: Int = 4000 * 1000,
//) {
//    private lateinit var mediaCodec: MediaCodec
//    private lateinit var mediaMuxer: MediaMuxer
//    private var trackIndex: Int = -1
//    private var isMuxerStarted = false
//
//    private var eglSurface: EGLSurface? = null
//
//    init {
//        setupEncoder()
//    }
//
//    private fun setupEncoder() {
//        // 配置 MediaCodec
//        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
//        format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate)
//        format.setInteger(MediaFormat.KEY_FRAME_RATE, fps)
//        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
//
//        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
//        mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
//    }
//
//    fun start() {
//        mediaMuxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
//        mediaCodec.start()
//    }
//
//    fun getInputSurface(): Surface {
//        return mediaCodec.createInputSurface()
//    }
//
//    fun encodeFrame() {
//        val bufferInfo = MediaCodec.BufferInfo()
//        while (true) {
//            val outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0)
//            if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                break
//            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                if (isMuxerStarted) throw RuntimeException("Format changed twice")
//                val newFormat = mediaCodec.outputFormat
//                trackIndex = mediaMuxer.addTrack(newFormat)
//                mediaMuxer.start()
//                isMuxerStarted = true
//            } else if (outputBufferIndex >= 0) {
//                val encodedData = mediaCodec.getOutputBuffer(outputBufferIndex)
//                    ?: throw RuntimeException("Encoded data is null")
//
//                if (bufferInfo.size > 0) {
//                    encodedData.position(bufferInfo.offset)
//                    encodedData.limit(bufferInfo.offset + bufferInfo.size)
//                    mediaMuxer.writeSampleData(trackIndex, encodedData, bufferInfo)
//                }
//
//                mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
//            }
//        }
//    }
//
//    fun stop() {
//        mediaCodec.stop()
//        mediaCodec.release()
//        mediaMuxer.stop()
//        mediaMuxer.release()
//    }
//}
//
//class VideoHelper() {
//
//    fun encode(context: Context) {
//        val outputFilePath = "${context.getExternalFilesDir(null)}/output_video.mp4"
//        val videoEncoder = VideoEncoder(outputFilePath, 720, 1280)
//
//        videoEncoder.start()
//
//        val gpuImage = GPUImage(context)
//        val customFilter = CustomTransitionFilter(this)
//
//        // 初始化第一张图片
//        gpuImage.setImage(BitmapFactory.decodeResource(context.resources, R.drawable.sample_image))
//        customFilter.setSecondTexture(BitmapFactory.decodeResource(context.resources, R.drawable.sample_image2))
//        gpuImage.setFilter(customFilter)
//
//        //  获取编码器的输入 Surface
//        val inputSurface = videoEncoder.getInputSurface()
//        gpuImage.getRenderer().setRenderSurface(inputSurface)
//
//        // 渲染每一帧
//        val totalFrames = 30 * 2 // 2 秒动画，30 FPS
//        for (frame in 0 until totalFrames) {
//            val progress = frame / totalFrames.toFloat()
//            customFilter.setProgress(progress)
//            gpuImage.requestRender()
//            videoEncoder.encodeFrame()
//        }
//
//        videoEncoder.stop()
//
//    }
//
//    fun save(applicationContext: Context, outputFilePath: String) {
//        val values = ContentValues().apply {
//            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/MyApp")
//            put(MediaStore.Video.Media.DISPLAY_NAME, "output_video.mp4")
//            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
//        }
//
//        val resolver = applicationContext.contentResolver
//        resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)?.let { uri ->
//            resolver.openOutputStream(uri)?.use { outputStream ->
//                FileInputStream(outputFilePath).use { inputStream ->
//                    inputStream.copyTo(outputStream)
//                }
//            }
//        }
//    }
//}
