package com.example.apptest.activities

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.example.apptest.R

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private var telephonyManager: TelephonyManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.text)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        telephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(this, permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.READ_PHONE_NUMBERS) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission.READ_SMS, permission.READ_PHONE_NUMBERS, permission.READ_PHONE_STATE), PERMISSION_REQUEST_CODE)
        } else {
            val number : String = telephonyManager!!.line1Number
            textView.text = number
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (ActivityCompat.checkSelfPermission(this, permission.READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.READ_PHONE_STATE) !=
                    PackageManager.PERMISSION_GRANTED) {
                return
            } else {
                textView.text = telephonyManager!!.line1Number
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}