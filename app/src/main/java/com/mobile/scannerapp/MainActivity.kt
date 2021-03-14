package com.mobile.scannerapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {
    var animationVertical: Animation?= null
    var barcodeDetector: BarcodeDetector?= null
    var cameraSource: CameraSource ?= null
    var foundData: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraAnimation()
        init()

    }

    private fun init(){
        barcodeDetector = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        cameraSource = CameraSource.Builder(applicationContext, barcodeDetector)
            .setAutoFocusEnabled(true)
            .build()


        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            prepareCamera()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),1001)
        }

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        cameraSource?.stop()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            try{
                cameraSource!!.start(cameraView.holder)
            }catch (e: Exception){
                Log.e("TAG",e.toString())
            }
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),1001)
        }
    }

    override fun onPause() {
        super.onPause()
        if(!foundData){
            cameraSource!!.release()
        }
    }

    override fun onResume() {
        super.onResume()
        foundData = true
        init()
    }

    private fun cameraAnimation(){
        animationVertical = TranslateAnimation(
            TranslateAnimation.ABSOLUTE,0f,
            TranslateAnimation.ABSOLUTE,0f,
            TranslateAnimation.RELATIVE_TO_PARENT,0.85f,
            TranslateAnimation.RELATIVE_TO_PARENT,0f
        )

        animationVertical?.duration = 1100
        animationVertical?.repeatCount = -1
        animationVertical?.repeatMode = Animation.REVERSE
        animationVertical?.setInterpolator(LinearInterpolator())
        greenline.animation = animationVertical
    }

    private fun prepareCamera(){
        cameraView.holder.addCallback(this)

        barcodeDetector?.setProcessor(object : Detector.Processor<Barcode>{
            override fun release() {
                Log.i("TAG","release")
            }

            override fun receiveDetections(p0: Detector.Detections<Barcode>) {

                val barcodeItem = p0.detectedItems
                if(barcodeItem.size() != 0 && foundData){
                    Log.e("TAG0",barcodeItem.toString())
                    Log.e("TAG",barcodeItem.valueAt(0).displayValue)

                    if(barcodeItem.valueAt(0).displayValue.contains("http")){
                        foundData = false

                        val inte = Intent(applicationContext,ResultView::class.java)
                        inte.putExtra("website",barcodeItem.valueAt(0).displayValue)
                        startActivity(inte)
//                        cameraSource?.stop()
                    }else{


                        foundData = false
                        val inte = Intent(applicationContext,ResultView::class.java)
                        inte.putExtra("text",barcodeItem.valueAt(0).displayValue)
                        startActivity(inte)
//                        cameraSource?.stop()
                    }
                }
            }

        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        try{
            if(grantResults[0] != 1001){
                if(ActivityCompat.checkSelfPermission(applicationContext,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
                        Toast.makeText(applicationContext,"Please give permission for camera",Toast.LENGTH_LONG).show()
                    return
                }else{
                    surfaceDestroyed(cameraView.holder)
                    surfaceCreated(cameraView.holder)
                }
            }
        }catch(e: Exception){
            Log.e("TAG",e.toString())
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
