package com.example.dolarxreal.data

import android.content.Context

class SharedPreferences(context: Context){

    private val mSharedPreferences = context.getSharedPreferences("app", Context.MODE_PRIVATE)


    fun storeDolar (key: String, value: Float)
    {
        mSharedPreferences.edit().putFloat(key,value).apply()
    }

    fun getDolar (key: String) : Float
    {
        return mSharedPreferences.getFloat(key,0f)
    }

}