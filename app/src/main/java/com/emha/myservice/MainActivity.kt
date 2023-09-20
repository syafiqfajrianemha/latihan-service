package com.emha.myservice

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.emha.myservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Bound Service
    private var boundStatus = false
    private lateinit var boundService: MyBoundService

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val myBinder = service as MyBoundService.MyBinder
            boundService = myBinder.getService
            boundStatus = true
            getNumberFromService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            boundStatus = false
        }

    }

    private fun getNumberFromService() {
        boundService.numberLiveData.observe(this) { number ->
            binding.tvBoundServiceNumber.text = number.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Background Service
        val serviceIntent = Intent(this, MyBackgroundService::class.java)
        binding.btnStartBackgroundService.setOnClickListener {
            startService(serviceIntent)
        }
        binding.btnStopBackgroundService.setOnClickListener {
            stopService(serviceIntent)
        }

        // Foreground Service
        val foregroundService = Intent(this, MyForegroundService::class.java)
        binding.btnStartForegroundService.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= 26) {
//                startForegroundService(foregroundService)
//            } else {
//                startService(foregroundService)
//            }
            ContextCompat.startForegroundService(this, foregroundService)
        }
        binding.btnStopForegroundService.setOnClickListener {
            stopService(foregroundService)
        }

        // Bound Service
        val boundServiceIntent = Intent(this, MyBoundService::class.java)
        binding.btnStartBoundService.setOnClickListener {
            bindService(boundServiceIntent, connection, BIND_AUTO_CREATE)
        }
        binding.btnStopBoundService.setOnClickListener {
            unbindService(connection)
        }
    }

    override fun onStop() {
        super.onStop()
        if (boundStatus) {
            unbindService(connection)
            boundStatus = false
        }
    }
}