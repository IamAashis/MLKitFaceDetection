package com.aashis.mlkitfacedetection.RealTimeFaceDetection

import android.graphics.*
import android.util.Log
import android.view.TextureView
import android.widget.ImageView
import androidx.camera.core.CameraX.LensFacing
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions

class MKKitFacesAnalyzer(tv: TextureView, iv: ImageView?, lens: LensFacing) : ImageAnalysis.Analyzer {

    private val TAG = "MLKitFacesAnalyzer"
    private var faceDetector: FirebaseVisionFaceDetector? = null
    private var tv: TextureView? = tv
    private var iv: ImageView? = iv
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var dotPaint: Paint? = null
    private var linePaint: Paint? = null
    private var widthScaleFactor = 1.0f
    private var heightScaleFactor = 1.0f
    private var fbImage: FirebaseVisionImage? = null
    private var lens: LensFacing? = lens



     fun MKKitFacesAnalyzerrr(tv: TextureView?, iv: ImageView?, lens: LensFacing?) {
        this.tv = tv
        this.iv = iv
        this.lens = lens
    }

    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {
        if (image == null || image.image == null) {
            return
        }
        val rotation: Int = degreesToFirebaseRotation(rotationDegrees)
        fbImage = FirebaseVisionImage.fromMediaImage(image.image!!, rotation)
        initDrawingUtils()
        initDetector()
        detectFaces()
    }

    private fun initDrawingUtils() {
        bitmap = Bitmap.createBitmap(tv!!.width, tv!!.height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        dotPaint = Paint()
        dotPaint!!.setColor(Color.RED)
        dotPaint!!.setStyle(Paint.Style.FILL)
        dotPaint!!.setStrokeWidth(2f)
        dotPaint!!.setAntiAlias(true)
        linePaint = Paint()
        linePaint!!.setColor(Color.GREEN)
        linePaint!!.setStyle(Paint.Style.STROKE)
        linePaint!!.setStrokeWidth(2f)
        widthScaleFactor = canvas!!.getWidth() / (fbImage!!.bitmap.width * 1.0f)
        heightScaleFactor = canvas!!.getHeight() / (fbImage!!.bitmap.height * 1.0f)
    }


    private fun initDetector() {
        val detectorOptions = FirebaseVisionFaceDetectorOptions.Builder()
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .build()
        faceDetector = FirebaseVision
            .getInstance()
            .getVisionFaceDetector(detectorOptions)
    }

    private fun detectFaces() {
        faceDetector
            ?.detectInImage(fbImage!!)
            ?.addOnSuccessListener { firebaseVisionFaces: List<FirebaseVisionFace> ->
                if (!firebaseVisionFaces.isEmpty()) {
                    processFaces(firebaseVisionFaces)
                } else {
                    canvas!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY)
                }
            }?.addOnFailureListener { e: Exception ->
                Log.i(
                    "Here",
                    e.toString()
                )
            }
    }


    private fun processFaces(faces: List<FirebaseVisionFace>) {
        for (face in faces) {
            drawContours(face.getContour(FirebaseVisionFaceContour.FACE).points)
            drawContours(face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM).points)
            drawContours(face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM).points)
            drawContours(face.getContour(FirebaseVisionFaceContour.LEFT_EYE).points)
            drawContours(face.getContour(FirebaseVisionFaceContour.RIGHT_EYE).points)
            drawContours(face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP).points)
            drawContours(face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP).points)
            drawContours(face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM).points)
            drawContours(face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP).points)
            drawContours(face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).points)
            drawContours(face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP).points)
            drawContours(face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE).points)
            drawContours(face.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM).points)
        }
        iv!!.setImageBitmap(bitmap)
    }

    private fun drawContours(points: List<FirebaseVisionPoint>) {
        var counter = 0
        for (point in points) {
            if (counter != points.size - 1) {
                canvas!!.drawLine(
                    translateX(point.x),
                    translateY(point.y),
                    translateX(points[counter + 1].x),
                    translateY(points[counter + 1].y),
                    linePaint!!
                )
            } else {
                canvas!!.drawLine(
                    translateX(point.x),
                    translateY(point.y),
                    translateX(points[0].x),
                    translateY(points[0].y),
                    linePaint!!
                )
            }
            counter++
            canvas!!.drawCircle(translateX(point.x), translateY(point.y), 6f, dotPaint!!)
        }
    }

    private fun translateY(y: Float): Float {
        return y * heightScaleFactor
    }

    private fun translateX(x: Float): Float {
        val scaledX = x * widthScaleFactor
        return if (lens == LensFacing.FRONT) {
            canvas!!.width - scaledX
        } else {
            scaledX
        }
    }

    private fun degreesToFirebaseRotation(degrees: Int): Int {
        return when (degrees) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> throw IllegalArgumentException("Rotation must be 0, 90, 180, or 270.")
        }
    }

}