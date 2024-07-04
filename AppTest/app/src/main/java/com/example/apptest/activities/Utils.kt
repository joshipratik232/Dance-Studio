package com.example.apptest.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.apptest.R
import com.google.gson.JsonObject
import java.io.*
import java.net.URL
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.HashMap


class Utils {
    companion object {
        lateinit var name: String
        lateinit var url: String
        lateinit var desc: String
        var fullScreen = false
        var exit:Boolean = true
        val clientKey = HashMap<String?, String?>()
        val contentType = HashMap<String?, String?>()
        val authorization = HashMap<String?, String?>()
        lateinit var token: String
        lateinit var fcmToken: String
        val jsonObject = JsonObject()
        val notifyObject = JsonObject()
        val deviceId = UUID.randomUUID().toString()
        fun randomString(sizeOfRandomString: Int): String {
            val ALLOWED_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            val random = Random()
            val sb = StringBuilder(sizeOfRandomString)
            for (i in 0 until sizeOfRandomString)
                sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
            return sb.toString()
        }

        @JvmStatic
        fun encodeBitmap(bm: Bitmap): String {
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos) //bm is the bitmap object
            val b = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        }

        fun decodeBitmap(encodeStr: String): Bitmap {
            val decodedBytes: ByteArray = Base64.decode(encodeStr, 0)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }

        fun hideKeyboardFrom(context: Context, view: View) {
            val imm: InputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun hasPhonePermission(act: Activity): Boolean {
            return (checkPermission(act, Manifest.permission.READ_SMS)
                    && checkPermission(act, Manifest.permission.READ_PHONE_NUMBERS)
                    && checkPermission(act, Manifest.permission.READ_PHONE_STATE))
        }

        private fun checkPermission(activity: Activity, permission: String): Boolean {
            val result = ContextCompat.checkSelfPermission(activity, permission)
            return result == PackageManager.PERMISSION_GRANTED
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun requestPhonePermission(activity: Activity, reqCode: Int) {
            ActivityCompat.requestPermissions(activity, getPhonePermissions().toTypedArray(), reqCode)
        }

        fun getPhonePermissions(): ArrayList<String> {
            val perArray = ArrayList<String>()
            perArray.add(Manifest.permission.READ_SMS)
            perArray.add(Manifest.permission.READ_PHONE_NUMBERS)
            perArray.add(Manifest.permission.READ_PHONE_STATE)
            return perArray
        }

        fun download(link: String?, path: String) {
            URL(link).openStream().use { input ->
                FileOutputStream(File(path).absolutePath).use { output ->
                    input.copyTo(output)
                }
            }
        }

        fun slide_down(ctx: Context?, v: View?) {
            val a: Animation = AnimationUtils.loadAnimation(ctx, R.anim.slide_down_anim)
            a.reset()
            if (v != null) {
                v.clearAnimation()
                v.startAnimation(a)
            }
        }

        fun slide_up(ctx: Context?, v: View?) {
            val a: Animation = AnimationUtils.loadAnimation(ctx, R.anim.slide_up_anim)
            a.reset()
            if (v != null) {
                v.clearAnimation()
                v.startAnimation(a)
            }
        }

        class MyEncrypter {
            private val read_write_block_buffer: Int = 1024
            private val algo_image_encryptor = "AES/CBC/PKCS5Padding"
            private val algo_secret_key = "AES"

            @Throws(Exception::class)
            fun encryptToFile(keyStr: String, specStr: String, inputStream: InputStream, out: OutputStream) {
                out.use { out ->
                    val iv = IvParameterSpec(specStr.toByteArray(Charsets.UTF_8))
                    val keySpec = SecretKeySpec(keyStr.toByteArray(Charsets.UTF_8), algo_secret_key)

                    val c = Cipher.getInstance(algo_image_encryptor)
                    c.init(Cipher.ENCRYPT_MODE, keySpec, iv)
                    val output = CipherOutputStream(out, c)
                    var count = 0
                    val buffer = ByteArray(read_write_block_buffer)
                    while ((inputStream.read(buffer)) > 0)
                        count = inputStream.read(buffer)
                    output.write(buffer, 0, count)
                }
            }

            fun decryptToFile(keyStr: String, specStr: String, inputStream: InputStream, out: OutputStream) {
                out.use { out ->
                    val iv = IvParameterSpec(specStr.toByteArray(Charsets.UTF_8))
                    val keySpec = SecretKeySpec(keyStr.toByteArray(Charsets.UTF_8), algo_secret_key)

                    val c = Cipher.getInstance(algo_image_encryptor)
                    c.init(Cipher.DECRYPT_MODE, keySpec, iv)
                    val output = CipherOutputStream(out, c)
                    var count = 0
                    val buffer = ByteArray(read_write_block_buffer)
                    while ((inputStream.read(buffer)) > 0)
                        count = inputStream.read(buffer)
                    output.write(buffer)
                }
            }
        }
    }
}