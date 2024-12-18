package com.example.test1213_gpuimage.video

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.test1213_gpuimage.transition.TransitionRippleFilter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import jp.co.cyberagent.android.gpuimage.GPUImage
import java.io.File
import java.nio.ByteBuffer

class VideoTransitionRenderer3(private val context: Context) {
    private var width = 720
    private var height = 1280

    fun renderTransitionVideo(bitmap1: Bitmap, bitmap2: Bitmap, outputPath: String? = null) {
        // 1. 创建临时图片序列
        val tempDir = File(context.externalCacheDir, "transition_frames")
        tempDir.mkdirs()

        val transitionFilter = TransitionRippleFilter()
        val gpuImage = GPUImage(context).apply {
            setFilter(transitionFilter)
            setImage(bitmap1)
//            gpuImage.setFilter(transitionFilter)
            transitionFilter.bitmap = bitmap2
        }


        val frameFiles = createTransitionFrames(gpuImage, bitmap1, bitmap2,transitionFilter, tempDir)

        // 2. 使用 ExoPlayer 创建视频
        val outputFile = outputPath ?: generateOutputPath()
//        createVideoFromFrames(frameFiles, outputFile)
//        encodeVideo(frameFiles, File(outputFile),bitmap1.width,bitmap1.height)
        convertBitmapListToMp4(frameFiles, bitmap1.width,bitmap1.height,outputFile,object :ConversionListener{
            override fun onProgressUpdate(currentFrame: Int, totalFrames: Int) {
//                TODO("Not yet implemented")
            }

            override fun onConversionFinished() {
//                TODO("Not yet implemented")
            }

            override fun onConversionError(exception: Exception) {
//                TODO("Not yet implemented")
            }

        })
        // 3. 清理临时文件
        tempDir.deleteRecursively()
    }

    private fun createTransitionFrames(
        gpuImage: GPUImage,
        bitmap1: Bitmap,
        bitmap2: Bitmap,
        transitionFilter: TransitionRippleFilter,
        tempDir: File
    ): List<Bitmap> {
        val frameFiles = mutableListOf<Bitmap>()
        val totalFrames = 60 // 2秒 @ 30fps

//        val transitionFilter = TransitionFilter(context)

        for (frame in 0 until totalFrames) {
            val progress = frame.toFloat() / totalFrames

            // 设置过渡进度
            transitionFilter.setProgress(progress)
//            gpuImage.setImage(bitmap1)
//            gpuImage.setFilter(transitionFilter)
//            transitionFilter.bitmap = bitmap2

            // 渲染并保存帧
            val renderedBitmap = gpuImage.getBitmapWithFilterApplied()
//            val frameFile = File(tempDir, "frame_${frame}.png")

//            FileOutputStream(frameFile).use { out ->
//                renderedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//            }
            Log.d("com.example.test1213_gpuimage.video.VideoTransitionRenderer", "createTransitionFrames: $frame")
            frameFiles.add(renderedBitmap)
        }

        return frameFiles
    }

//    private fun createVideoFromFrames(frameFiles: List<File>, outputPath: String) {
//        try {
//            // 使用 FFmpeg 或其他视频编码工具将图片序列转换为视频
//            val command = arrayOf(
//                "-framerate", "30",
//                "-i", "${frameFiles[0].parent}/frame_%d.png",
//                "-c:v", "libx264",
//                "-pix_fmt", "yuv420p",
//                outputPath
//            )
//
//            val result =FFmpeg.execute(command)
////            Runtime.getRuntime().exec(command)
//
////            process.waitFor()
//
//            Log.d("com.example.test1213_gpuimage.video.VideoTransitionRenderer", "Video created successfully at $outputPath")
//        } catch (e: Exception) {
//            Log.e("com.example.test1213_gpuimage.video.VideoTransitionRenderer", "Error creating video", e)
//        }
//    }

    private fun createVideoFromFrames(frames: List<File>, outputPath: String): Boolean {
        return try {
            // 打印详细的 FFmpeg 日志
//            FFmpeg.enableStatisticsCallback(true)
//            FFmpeg.enableLogCallback { config ->
//                Log.d("FFmpegLog", "Status: ${config.message}")
//            }

            // 详细的 FFmpeg 命令
            val command = arrayOf(
                "-y",                           // 覆盖输出文件
                "-framerate", "30",
                "-pattern_type", "sequence",    // 使用序列模式
                "-start_number", "0",           // 从0开始的序列号
                "-i", "${frames.first().parent}/frame_%04d.jpg", // 使用更标准的序列文件名模式
                "-c:v", "libx264",              // 视频编码器
                "-pix_fmt", "yuv420p",          // 像素格式
                "-preset", "fast",              // 编码预设
                "-crf", "23",                   // 压缩质量
                "-vf", "scale=${width}:${height}", // 缩放到指定分辨率
                outputPath
            )

            // 执行 FFmpeg 命令并获取返回码
            val rc = FFmpeg.execute(command)

            if (rc == 0) {
                Log.d("com.example.test1213_gpuimage.video.VideoTransitionRenderer", "Video conversion successful")
                Log.d("com.example.test1213_gpuimage.video.VideoTransitionRenderer", "Video saved at: $outputPath")
                true
            } else {
                Log.e("com.example.test1213_gpuimage.video.VideoTransitionRenderer", "Video conversion failed with return code: $rc")
                false
            }
        } catch (e: Exception) {
            Log.e("com.example.test1213_gpuimage.video.VideoTransitionRenderer", "Error converting frames to video", e)
            false
        }
    }

    private fun generateOutputPath(): String {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        directory.mkdirs()
        return File(directory, "transition_video_${System.currentTimeMillis()}.mp4").absolutePath
    }

    // 播放生成的视频
    fun playVideo(videoPath: String): ExoPlayer {
        val player = ExoPlayer.Builder(context).build()

        val mediaItem = MediaItem.fromUri(Uri.parse(videoPath))
        val mediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(context)
        ).createMediaSource(mediaItem)

        player.setMediaSource(mediaSource)
        player.prepare()

        return player
    }


    private fun encodeVideo(bitmaps: List<Bitmap>, file: File, width: Int, height: Int, frameRate: Int = 1_000_000) {
        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 8000000) // 设置码率

        val mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        val mediaMuxer = MediaMuxer(file.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        mediaCodec.start()
        var videoTrackIndex = -1
        val bufferInfo = MediaCodec.BufferInfo()
        var presentationTimeUs = 0L

        for (bitmap in bitmaps) {
            val byteBuffer = convertBitmapToByteBuffer(bitmap, width, height)
            val inputBufferIndex = mediaCodec.dequeueInputBuffer(-1)

            if (inputBufferIndex >= 0) {
                val inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex)
                inputBuffer?.clear()
                inputBuffer?.put(byteBuffer)
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, byteBuffer.capacity(), presentationTimeUs, 0)
                presentationTimeUs += (1000000 / frameRate).toLong()

                var outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0)
                while (outputBufferIndex >= 0) {
                    val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)
                    if (videoTrackIndex == -1) {
                        val newFormat = mediaCodec.outputFormat
                        videoTrackIndex = mediaMuxer.addTrack(newFormat)
                        mediaMuxer.start()
                    }
                    mediaMuxer.writeSampleData(videoTrackIndex, outputBuffer!!, bufferInfo)
                    mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
                    outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0)
                }
            }
        }

        mediaCodec.signalEndOfInputStream()
        var outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0)
        while (outputBufferIndex >= 0) {
            val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)
            mediaMuxer.writeSampleData(videoTrackIndex, outputBuffer!!, bufferInfo)
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0)
        }

        mediaCodec.stop()
        mediaCodec.release()
        mediaMuxer.stop()
        mediaMuxer.release()
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap, width: Int, height: Int): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(width * height * 3 / 2) // YUV420SP requires this size
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        var yIndex = 0
        var uvIndex = width * height

        for (j in 0 until height) {
            for (i in 0 until width) {
                val rgb = pixels[j * width + i]
                val r = rgb and 0xFF0000 shr 16
                val g = rgb and 0xFF00 shr 8
                val b = rgb and 0xFF

                var y = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                var u = (-0.169 * r - 0.331 * g + 0.5 * b).toInt()
                var v = (0.5 * r - 0.419 * g - 0.081 * b).toInt()

                y = Math.max(0, Math.min(255, y))
                u = Math.max(0, Math.min(255, u))
                v = Math.max(0, Math.min(255, v))

                byteBuffer.put(y.toByte())
                if (j % 2 == 0 && i % 2 == 0) {
                    byteBuffer.put(u.toByte())
                    byteBuffer.put(v.toByte())
                }
            }
        }
        byteBuffer.flip()
        return byteBuffer
    }


    private  val MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC // H.264 Advanced Video Coding
    private  val FRAME_RATE = 30 // 帧率
    private  val IFRAME_INTERVAL = 1 // 关键帧间隔
    private  val TIMEOUT_US = 10000L

    fun convertBitmapListToMp4(bitmapList: List<Bitmap>, width: Int, height: Int, outputFilePath: String, listener: ConversionListener) {
        try {
            val mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, width, height).apply {
                setInteger(MediaFormat.KEY_COLOR_FORMAT, COLOR_FormatYUV420Flexible)
                setInteger(MediaFormat.KEY_BIT_RATE, 8000000) // 码率
                setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
                setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL)
            }

            val mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE).apply {
                configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                start()
            }

            val mediaMuxer = MediaMuxer(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4).apply {
                setOrientationHint(90) // 设置视频旋转角度，根据需要调整
                start()
            }

            var trackIndex = -1
            val bufferInfo = MediaCodec.BufferInfo()
            var pts = 0L // Presentation Time Stamp

            bitmapList.forEachIndexed { index, bitmap ->
                val inputBufferIndex = mediaCodec.dequeueInputBuffer(TIMEOUT_US)
                if (inputBufferIndex >= 0) {
                    val inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex)!!
                    inputBuffer.clear()

                    val yuvBytes = convertBitmapToByteBuffer(bitmap,width,bitmap.height)
                    inputBuffer.put(yuvBytes)


                    mediaCodec.queueInputBuffer(inputBufferIndex, 0, yuvBytes.capacity(), pts, 0)
                    pts += (1000000L / FRAME_RATE).toLong() // 计算下一帧的时间戳
                }

                var outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_US)
                while (outputBufferIndex >= 0) {
                    val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)!!
                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                        // Configuration data, ignore
                        bufferInfo.size = 0
                    }

                    if (bufferInfo.size != 0) {
                        if (trackIndex == -1) {
                            trackIndex = mediaMuxer.addTrack(mediaCodec.outputFormat)
                            mediaMuxer.start()
                        }
                        outputBuffer.position(bufferInfo.offset)
                        outputBuffer.limit(bufferInfo.offset + bufferInfo.size)
                        mediaMuxer.writeSampleData(trackIndex, outputBuffer, bufferInfo)
                    }

                    mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
                    outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_US)
                }
                listener.onProgressUpdate(index + 1, bitmapList.size)
            }

            mediaCodec.signalEndOfInputStream()

            var outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_US)
            while (outputBufferIndex >= 0) {
                val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)!!
                if (bufferInfo.size != 0) {
                    outputBuffer.position(bufferInfo.offset)
                    outputBuffer.limit(bufferInfo.offset + bufferInfo.size)
                    mediaMuxer.writeSampleData(trackIndex, outputBuffer, bufferInfo)
                }

                mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_US)
            }

            mediaCodec.stop()
            mediaCodec.release()
            mediaMuxer.stop()
            mediaMuxer.release()
            listener.onConversionFinished()
            Log.i(Companion.TAG, "MP4 file saved to: $outputFilePath")
        } catch (e: Exception) {
            Log.e(Companion.TAG, "Error converting bitmaps to MP4", e)
            listener.onConversionError(e)
        }
    }


//    fun getYUVBytes(bitmap: Bitmap): ByteArray {
//        val width = bitmap.width
//        val height = bitmap.height
//        val yuv = IntArray(width * height)
//        bitmap.getPixels(yuv, 0, width, 0, 0, width, height)
//
//        val y = ByteArray(width * height)
//        val u = ByteArray(width * height / 4)
//        val v = ByteArray(width * height / 4)
//        for (i in 0 until height) {
//            for (j in 0 until width) {
//                val rgb = yuv[i * width + j]
//                val r = rgb and 0xFF
//                val g = rgb shr 8 and 0xFF
//                val b = rgb shr 16 and 0xFF
//                val yIndex = i * width + j
//                y[yIndex] = ((66 * r + 129 * g + 25 * b + 128) shr 8) + 16
//                if (i % 2 == 0 && j % 2 == 0) {
//                    val uvIndex = i / 2 * width / 2 + j / 2
//                    v[uvIndex] = ((112 * r - 94 * g - 18 * b + 128) shr 8) + 128
//                    u[uvIndex] = ((-38 * r - 74 * g + 112 * b + 128) shr 8) + 128
//                }
//            }
//        }
//        val yuvBytes = ByteArray(y.size + u.size + v.size)
//        System.arraycopy(y, 0, yuvBytes, 0, y.size)
//        System.arraycopy(u, 0, yuvBytes, y.size, u.size)
//        System.arraycopy(v, 0, yuvBytes, y.size + u.size, v.size)
//        return yuvBytes
//    }

    interface ConversionListener {
        fun onProgressUpdate(currentFrame: Int, totalFrames: Int)
        fun onConversionFinished()
        fun onConversionError(exception: Exception)
    }

    companion object {
        private const val TAG = "BitmapToMp4Converter"
    }

}
