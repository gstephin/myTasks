package com.app.mytasks.util

import android.content.Context

class FileUtil {


    fun loadJSONFromAssets(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}