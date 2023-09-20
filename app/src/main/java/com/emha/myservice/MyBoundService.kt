package com.emha.myservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyBoundService : Service() {

    private var binder = MyBinder()
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    val numberLiveData: MutableLiveData<Int> = MutableLiveData()

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind: Service Bound dijalankan...")

        serviceScope.launch {
            for (i in 1..50) {
                delay(1000)
                Log.d(TAG, "Do Something $i")
                numberLiveData.postValue(i)
            }
            Log.d(TAG,"Service Bound dihentikan")
        }

        return binder
    }

    internal inner class MyBinder : Binder() {
        val getService: MyBoundService = this@MyBoundService
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Service Bound dihentikan")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnBind: Service Bound dihentikan")
        serviceJob.cancel()
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "onRebind: Service Bound dijalankan...")
    }

    companion object {
        private val TAG = MyBoundService::class.java.simpleName
    }
}