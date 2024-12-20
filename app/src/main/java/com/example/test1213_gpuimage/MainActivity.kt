/*
 * Copyright (C) 2018 CyberAgent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.test1213_gpuimage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.test1213_gpuimage.effect.EffectRvActivity
import com.example.test1213_gpuimage.rv.FilterGalleryActivity
import com.example.test1213_gpuimage.transition.TransitionRvActivity

class MainActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.button_gallery).setOnClickListener {
            startActivity(Intent(this, GalleryActivity::class.java))
        }
        findViewById<View>(R.id.button_camera).setOnClickListener {
            if (!hasCameraPermission() || !hasStoragePermission()) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CAMERA
                )
            } else {
                startActivity(Intent(this, CameraActivity::class.java))
            }
        }

        findViewById<View>(R.id.button_gallery_rv).setOnClickListener {
            startActivity(Intent(this, FilterGalleryActivity::class.java))
        }
        findViewById<View>(R.id.button_transition_rv).setOnClickListener {
            startActivity(Intent(this, TransitionRvActivity::class.java))
        }
        findViewById<View>(R.id.button_gl_translate).setOnClickListener {
            startActivity(Intent(this, TransitionUseImageViewActivity::class.java))

        }

        findViewById<View>(R.id.button_gpu_gl_translate).setOnClickListener {
//            startActivity(Intent(this, TestGpuGlslActivity::class.java))
            startActivity(Intent(this, TransitionActivity::class.java))
//            startActivity(Intent(this, TransitionUseImageViewActivity::class.java))
        }

        val linearLayout = findViewById<LinearLayout>(R.id.ll_content)
        val button = Button(this)
        button.text = "Basic 基础过渡动画"
        linearLayout.addView(button)
        button.setOnClickListener {
            val intent = Intent(this, TransitionRvActivity::class.java)
//            "basic" -> GlslRepo.basicList
//            "effect" -> GlslRepo.effectList
//            "light" -> GlslRepo.lightList
//            "mask" -> GlslRepo.maskList
//            "slide" -> GlslRepo.slideList
            intent.putExtra(TransitionRvActivity.KEY_SHADER_PATH,"slide")
            startActivity(intent)
        }

        //图片效果
        Button(this).apply {
            text = "自定义filter 图片效果测试"
            linearLayout.addView(this)
            setOnClickListener {
                val intent = Intent(this@MainActivity, EffectRvActivity::class.java)
//                "mix" -> mixList
//                "transform" -> transformList
                intent.putExtra(TransitionRvActivity.KEY_SHADER_PATH,"mix")
                startActivity(intent)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (requestCode == REQUEST_CAMERA && grantResults.size == 2
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(Intent(this, CameraActivity::class.java))
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CAMERA = 1
    }
}
