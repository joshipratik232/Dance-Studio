package com.dance.materialdialoglib

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.dance.studio.materialdialoglib.R
import java.lang.Exception

@SuppressLint("InflateParams")
open class ProgressDialogHelper(context: Context) : BaseDialogHelper() {

    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.progress_indicator, null)
    }

    override val builder: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView)

    /*Views initializer*/
    private val proIndicator: DotProgressIndicator by lazy {
        dialogView.findViewById<DotProgressIndicator>(R.id.proIndicator)
    }

    /*Views initializer*/
    private fun View.setClickListenerToDialogIcon(func: (() -> Unit)?) = setOnClickListener {
        func?.invoke()
        dialog?.dismiss()
    }

    fun startProgressIndicator(isCancelable:Boolean){
        try {
            cancelable=isCancelable
            this@ProgressDialogHelper.create().show()
            proIndicator.startAnimation()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun stopProgressIndicator(){
        try {
            proIndicator.stopAnimation()
            dialog?.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}