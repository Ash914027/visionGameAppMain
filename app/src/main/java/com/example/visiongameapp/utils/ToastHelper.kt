package com.example.visiongameapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.visiongameapp.R

object ToastHelper {

    fun showCustomToast(context: Context, message: String, isSuccess: Boolean = true) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.layout_custom_toast, null)

        val text: TextView = layout.findViewById(R.id.tvToastMessage)
        val icon: ImageView = layout.findViewById(R.id.ivToastIcon)

        text.text = message
        
        if (isSuccess) {
            icon.setImageResource(R.drawable.baseline_check_circle_24)
            layout.background.setTint(context.getColor(R.color.blue_main))
        } else {
            icon.setImageResource(R.drawable.baseline_error_24)
            layout.background.setTint(context.getColor(android.R.color.holo_red_light))
        }

        with(Toast(context)) {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }
}
