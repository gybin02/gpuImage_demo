//package com.example.test1213_gpuimage.video
//
//import android.content.ContentValues
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Canvas
//import android.graphics.Paint
//import android.graphics.Rect
//import android.media.MediaCodec
//import android.media.MediaCodecInfo
//import android.media.MediaFormat
//import android.media.MediaMuxer
//import android.net.Uri
//import android.opengl.GLES20
//import android.opengl.GLUtils
//import android.os.Environment
//import android.provider.MediaStore
//import android.view.Surface
//import androidx.appcompat.app.AppCompatActivity
//import com.example.test1213_gpuimage.TransitionFilter
//import jp.co.cyberagent.android.gpuimage.GPUImage
//import jp.co.cyberagent.android.gpuimage.filter.GPUImageTwoInputFilter
//import java.io.File
//import java.nio.ByteBuffer
////保存视频到磁盘
//class com.example.test1213_gpuimage.video.VideoTransitionRenderer(private val context: Context) {
//
//    fun renderTransitionVideo(bitmap1: Bitmap, bitmap2: Bitmap, outputPath: String = "${context.filesDir}/transition_video.mp4") {
//        val width = 600
////        bitmap1.width
//        val height = 800
////            bitmap1.height
//        val frameRate = 30 // FPS
//        val duration = 2000 // milliseconds
//
//        val videoFile = File(outputPath)
//        val mediaMuxer = MediaMuxer(videoFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
//
//        val videoFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height).apply {
//            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
//            setInteger(MediaFormat.KEY_BIT_RATE, 5 * width * height)
//            setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
//            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
//        }
//
//        val mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC).apply {
//            configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
//        }
//
//        val inputSurface = mediaCodec.createInputSurface()
//        mediaCodec.start()
//        val transitionFilter =  TransitionFilter(context)
//        val gpuImage = GPUImage(context).apply {
//            setFilter(transitionFilter)
////            setGLSurfaceView(null) // Offscreen rendering
//        }
//
////        val transitionFilter = gpuImage.filter as TransitionFilter
//
//        // Calculate the total frame count based on duration and frame rate
//        val totalFrames = (duration / 1000f * frameRate).toInt()
//
//        // Start encoding video
//        var trackIndex = -1
//        var presentationTimeUs = 0L
//        var progress = 0f
//
//        val bufferInfo = MediaCodec.BufferInfo()
//        mediaCodec.setCallback(object : MediaCodec.Callback() {
//            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {}
//
//            override fun onOutputBufferAvailable(codec: MediaCodec, index: Int, info: MediaCodec.BufferInfo) {
//                if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) return
//
//                val encodedData = codec.getOutputBuffer(index) ?: return
//
//                if (trackIndex == -1) {
//                    trackIndex = mediaMuxer.addTrack(codec.outputFormat)
//                    mediaMuxer.start()
//                }
//
//                mediaMuxer.writeSampleData(trackIndex, encodedData, info)
//                codec.releaseOutputBuffer(index, false)
//            }
//
//            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
//                if (trackIndex == -1) {
//                    trackIndex = mediaMuxer.addTrack(format)
//                }
//            }
//
//            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
//                e.printStackTrace()
//            }
//        })
//        gpuImage.setImage(bitmap1)
//        for (frameIndex in 0 until totalFrames) {
//            progress = frameIndex.toFloat() / totalFrames
//            transitionFilter.setProgress(progress)
//            transitionFilter.bitmap = bitmap2
//
//            val renderedBitmap = gpuImage.getBitmapWithFilterApplied()
//            uploadBitmapToSurface(renderedBitmap, inputSurface)
//
//            presentationTimeUs = (frameIndex * 1_000_000L / frameRate)
//            mediaCodec.queueInputBuffer(0, 0, 0, presentationTimeUs, 0)
//        }
//
//        mediaCodec.signalEndOfInputStream()
//        mediaCodec.stop()
//        mediaCodec.release()
//        mediaMuxer.stop()
//        mediaMuxer.release()
//    }
//
//    private fun uploadBitmapToSurface(bitmap: Bitmap, surface: Surface) {
//        val canvas = surface.lockCanvas(null)
//        val paint = Paint()
//        canvas.drawBitmap(bitmap, null, Rect(0, 0, canvas.width, canvas.height), paint)
//        surface.unlockCanvasAndPost(canvas)
//    }
//}
