package com.example.apptest.cache

import android.content.Context
import android.os.Environment
import com.example.apptest.activities.BaseActivity
import com.example.apptest.fragment.DownloadsFragment
import com.example.apptest.model.downloaded.VideoModel
import timber.log.Timber
import java.io.*

class Cache {
    /*val file = File(context.getExternalFilesDir(
            Environment.getDownloadCacheDirectory().absolutePath), "Cache")*/
    fun saveObject(mVideoList: List<VideoModel>, file: File): Boolean {
        Timber.d(file.absolutePath)
        var fos: FileOutputStream? = null
        var oos: ObjectOutputStream? = null
        var keep = true
        try {
            fos = FileOutputStream(file)
            oos = ObjectOutputStream(fos)
            oos.writeObject(mVideoList)
        } catch (e: Exception) {
            keep = false
        } finally {
            try {
                oos?.close()
                fos?.close()
                if (!keep) file.delete()
            } catch (e: Exception) { /* do nothing */
            }
        }
        return keep
    }

    fun getObject(file: File): List<VideoModel>? {
        var simpleClass: List<VideoModel>? = null
        var fis: FileInputStream? = null
        var `is`: ObjectInputStream? = null
        try {
            fis = FileInputStream(file)
            `is` = ObjectInputStream(fis)
            simpleClass = `is`.readObject() as List<VideoModel>
        } catch (e: java.lang.Exception) {
            val `val` = e.message
        } finally {
            try {
                fis?.close()
                `is`?.close()
            } catch (e: java.lang.Exception) {
            }
        }
        return simpleClass
    }
}