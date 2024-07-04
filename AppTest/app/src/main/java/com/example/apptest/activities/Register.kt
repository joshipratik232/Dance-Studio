package com.example.apptest.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.apptest.R
import com.example.apptest.activities.Utils.Companion.hasPhonePermission
import com.example.apptest.databinding.ActivityRegisterBinding

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    var telephonyManager: TelephonyManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if (hasPhonePermission(this@Register)) {
            val tMgr: TelephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE), PERMISSION_REQUEST_CODE)
            } else {
                val mPhoneNumber: String = tMgr.line1Number
                binding.phone.editText?.setText(mPhoneNumber)
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE ->
                if (hasPhonePermission(this@Register)) {
                    val tMgr: TelephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    val mPhoneNumber: String = tMgr.line1Number
                    binding.phone.editText?.setText(mPhoneNumber)
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                                    Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        return
                    } else {
                        binding.phone.editText!!.setText(telephonyManager?.line1Number)
                    }
                }

        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

}