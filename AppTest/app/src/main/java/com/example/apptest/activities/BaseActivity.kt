package com.example.apptest.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.dance.materialdialoglib.ConfirmDialogHelper
import com.example.apptest.R
import com.example.apptest.activities.Utils.Companion.clientKey
import com.example.apptest.activities.Utils.Companion.exit
import com.example.apptest.activities.Utils.Companion.fullScreen
import com.example.apptest.cache.Encryption
import com.example.apptest.databinding.ActivityBaseBinding
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.system.exitProcess


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class BaseActivity : AppCompatActivity() {
    lateinit var cache: File
    private lateinit var dstudio: File

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityBaseBinding>(this, R.layout.activity_base)
        sendBroadcast(Intent("com.example.apptest"))
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val navController = this.findNavController(R.id.fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        dstudio = File(Environment.getDataDirectory(), "DStudio")
        if (!dstudio.exists()) dstudio.mkdir()

        cache = File(Environment.getDataDirectory(), "Cache")
        if (!cache.exists()) cache.mkdir()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.fragment)
        return navController.navigateUp()
    }

    @SuppressLint("CutPasteId")
    override fun onBackPressed() {
        if ((this.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) && (fullScreen)) {
            val playerview = findViewById<View>(R.id.video_player)
            val fullScreenButton = playerview.findViewById<ImageView>(R.id.exo_fullscreen_icon)
            fullScreenButton.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.full_screen_open))
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE)
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            val scale = applicationContext.resources.displayMetrics.density
            val pixels = (300 * scale + 0.5f).toInt()
            findViewById<ConstraintLayout>(R.id.top_image_container).layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
            findViewById<ConstraintLayout>(R.id.top_image_container).layoutParams.height = pixels
            val params: ConstraintLayout.LayoutParams = playerview.layoutParams as ConstraintLayout.LayoutParams

            params.width = RelativeLayout.LayoutParams.MATCH_PARENT
            params.height = pixels
            playerview.layoutParams = params
        }
        if (exit) {
            val exitApp = ConfirmDialogHelper(this)
            exitApp.setNegativeClickListener {
                exitApp.dialog!!.dismiss()
            }
            exitApp.setPositiveClickListener {
                exitApp.dialog!!.dismiss()
                finish()
                exitProcess(0)
            }
            exitApp.create().show()
            exitApp.setMessage("Are you sure you want to exit?")
            exitApp.setPositiveButtonText("Yes")
            exitApp.setPositiveButtonTextColor(R.color.primaryLightColor)
            exitApp.setNegativeButtonTextColor(R.color.primaryTextColor)
            exitApp.setMessageTextColor(R.color.primaryTextColor)
        } else {
            super.onBackPressed()
        }
    }

    @Throws(Exception::class)
    override fun onStart() {
        super.onStart()
        clientKey["ClientKey"] = "A7209A4E1635475DAC45BBF4A5AA8F73"

        val shared = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
                ?: return
        with(shared.edit()) {
            putBoolean("isRun", true)
            apply()
        }
        val cache2 = File(applicationContext.getExternalFilesDir(
                Environment.getDataDirectory().absolutePath),
                "Cache2")
        if (!cache2.listFiles().isNullOrEmpty()) {
            var i = cache2.listFiles().size - 1
            while (i != -1) {
                val file = File(applicationContext.getExternalFilesDir(
                        Environment.getDataDirectory().absolutePath),
                        "DStudio/${cache2.listFiles()[i].name}.mp4")
                Encryption.dec("kdjfhAS5D4ASsa3d", "as1d2wS21DASfdg3",
                        FileInputStream(cache2.listFiles()[i]), FileOutputStream(file))
                cache2.listFiles()[i].delete()
                i = cache2.listFiles().size - 1
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val shared = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
                ?: return
        with(shared.edit()) {
            putBoolean("isRun", false)
            apply()
        }
        val file = File(applicationContext.getExternalFilesDir(
                Environment.getDataDirectory().absolutePath), "DStudio")
        if (!file.listFiles().isNullOrEmpty()) {
            var size = file.listFiles().size - 1
            var i = size
            val sharedPref = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
            Timber.d("isDownloading %s", sharedPref!!.getString("isDownloading", null).toString())
            while (i != -1) {
                if (sharedPref.getString("isDownloading", null).toString() == file.listFiles()[i].name) {
                    size -= 1
                } else {
                    val temp = file.listFiles()[i].name.substring(0, file.listFiles()[i].name.length - 4)
                    val file2 = File(applicationContext.getExternalFilesDir(
                            Environment.getDataDirectory().absolutePath + File.separator + "Cache2"),
                            temp)
                    Encryption.enc(
                            "kdjfhAS5D4ASsa3d", "as1d2wS21DASfdg3",
                            FileInputStream(file.listFiles()[i]), FileOutputStream(file2)
                    )
                    file.listFiles()[i].delete()
                    size -= 1
                }
                i = size
            }
        }
    }
}
