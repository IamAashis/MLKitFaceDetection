package com.aashis.mlkitfacedetection

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.aashis.mlkitfacedetection.GalleryFaceDetection.GalleryFaceDetectionActivity
import com.aashis.mlkitfacedetection.RealTimeFaceDetection.RealTimeFaceDetectionActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btnGallery = findViewById(R.id.galleryPick) as Button
        var btnRealtime = findViewById(R.id.realTime) as Button

        btnGallery.setOnClickListener {
            val intent = Intent(baseContext, GalleryFaceDetectionActivity::class.java)
            startActivity(intent);
        }
        btnRealtime.setOnClickListener {
            val intent = Intent(baseContext, RealTimeFaceDetectionActivity::class.java)
            startActivity(intent);

        }


    }
}