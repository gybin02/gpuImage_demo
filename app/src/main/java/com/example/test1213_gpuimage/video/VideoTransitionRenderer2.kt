package com.example.test1213_gpuimage.video

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.test1213_gpuimage.transition.TransitionRippleFilter
import jp.co.cyberagent.android.gpuimage.GPUImage
import java.io.File

class VideoTransitionRenderer(private val context: Context) {

    private val TAG = "VideoTransitionRenderer"
    private val MIME_TYPE = "video/avc" // H.264 Advanced Video Coding
    private val FRAME_RATE = 30 // 30fps
    private val IFRAME_INTERVAL = 1 // 1 second between I-frames

    fun renderTransitionVideo(bitmap1: Bitmap, bitmap2: Bitmap) {
        val videoFile = File(context.getExternalFilesDir(null), "transition.mp4")
        if (videoFile.exists()) {
            videoFile.delete()
        }
        try {
            val width = bitmap1.width
            val height = bitmap1.height

            val mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, width, height).apply {
                setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
                setInteger(MediaFormat.KEY_BIT_RATE, 1000000) // 1Mbps
                setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
                setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL)
            }

            val mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE).apply {
                configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                start()
            }

            val inputSurface = mediaCodec.createInputSurface()
            val gpuImage = GPUImage(context).apply {
                setGLSurfaceView(inputSurface as GLSurfaceView)
            }
            val transitionFilter = TransitionRippleFilter().apply {
                this.bitmap = bitmap2
            }
            gpuImage.setFilter(transitionFilter)
            gpuImage.setImage(bitmap1)

            val mediaMuxer = MediaMuxer(videoFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4).apply {
                setOrientationHint(90) //根据需要设置旋转角度
            }

            var videoTrackIndex = -1
            val bufferInfo = MediaCodec.BufferInfo()

            for (i in 0..60) { // 生成 2 秒 (60 帧) 的过渡动画
                val progress = i.toFloat() / 60f
                transitionFilter.setProgress(progress)
                gpuImage.requestRender()
                Thread.sleep(16)

                drainEncoder(mediaCodec, bufferInfo, mediaMuxer, videoTrackIndex, false)

                if (videoTrackIndex == -1) {
                    videoTrackIndex = mediaMuxer.addTrack(mediaCodec.outputFormat)
                    mediaMuxer.start()
                }
            }
            drainEncoder(mediaCodec, bufferInfo, mediaMuxer, videoTrackIndex, true)
            mediaCodec.stop()
            mediaCodec.release()
            mediaMuxer.stop()
            mediaMuxer.release()
            gpuImage.deleteImage()
            inputSurface.release()

            Log.i(TAG, "Video saved to: ${videoFile.absolutePath}")

        } catch (e: Exception) {
            Log.e(TAG, "Error rendering video", e)
        }
    }

    private fun drainEncoder(
        mediaCodec: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo,
        mediaMuxer: MediaMuxer,
        videoTrackIndex: Int,
        endOfStream: Boolean
    ) {
        val TIMEOUT_USEC = 10000

        if (endOfStream) {
            mediaCodec.signalEndOfInputStream()
        }

        while (true) {
            val encoderStatus = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC.toLong())
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) {
                    break // 没有可用的数据，稍后重试
                } else {
                    continue
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (videoTrackIndex == -1) {
                    mediaMuxer.addTrack(mediaCodec.outputFormat)
                    mediaMuxer.start()
                }
            } else if (encoderStatus < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: $encoderStatus")
            } else {
                val encodedData = mediaCodec.getOutputBuffer(encoderStatus)
                    ?: throw RuntimeException("encoderOutputBuffer $encoderStatus was null")

                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                    bufferInfo.size = 0
                }

                if (bufferInfo.size != 0) {
                    encodedData.position(bufferInfo.offset)
                    encodedData.limit(bufferInfo.offset + bufferInfo.size)
                    mediaMuxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo)
                }

                mediaCodec.releaseOutputBuffer(encoderStatus, false)

                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    break
                }
            }
        }
    }
}