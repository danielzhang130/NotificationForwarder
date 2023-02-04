package com.example.notificationforwarder

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private val vm by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.send).setOnClickListener {
            vm.sendNotification(
                getSharedPreferences("default", 0).getString("code", null)
                    ?: return@setOnClickListener
            )
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<ZXingScannerView>(R.id.scanner).apply {
            setResultHandler(this@MainActivity) // Register ourselves as a handler for scan results.
            startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        findViewById<ZXingScannerView>(R.id.scanner).stopCamera() // Stop camera on pause
    }

    override fun handleResult(rawResult: Result) {
        val code = rawResult.text
        Toast.makeText(this, code, Toast.LENGTH_SHORT).show()
        getSharedPreferences("default", 0)
            .edit {
                putString("code", code)
            }
        findViewById<ZXingScannerView>(R.id.scanner).stopCamera()
    }
}