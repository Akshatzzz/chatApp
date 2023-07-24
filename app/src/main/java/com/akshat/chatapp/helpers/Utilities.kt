package com.akshat.chatapp.helpers

import android.content.Context
import android.util.Log
import android.widget.Toast

private const val TAG = "Utilities"
fun showToast(context: Context,string: String) {
    if(string.isEmpty()){
        return
    }
    runCatching{
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }.onFailure {
        Log.d(TAG,"$it")
    }
}
