package com.example.androidespcolorpicker.helpers

import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class NetworkManager(context: Context) {

    private val myRequestQueue: RequestQueue = Volley.newRequestQueue(context)
    val colorData: MutableMap<String, String> = HashMap()
    lateinit var ip: String

    fun sendFFTPost() {
        val myStringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "http://$ip/FFTPressed",
            Response.Listener { response: String? -> },
            Response.ErrorListener { error: VolleyError? -> }
        ) {
            override fun getParams(): Map<String, String> {
                return emptyMap()
            }
        }
        myRequestQueue.add(myStringRequest)
    }

    //FIXME green and blue are inverted
    fun sendColorPost(r: String, g: String, b: String) {
        val myStringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "http://$ip/colorChanged",
            Response.Listener { response: String? -> },
            Response.ErrorListener { error: VolleyError? -> }
        ) {
            override fun getParams(): Map<String, String> {
                colorData["r"] = r
                //FIXME b and g seem to be inverted for some reason
                colorData["g"] = b
                colorData["b"] = g
                return colorData
            }
        }
        myRequestQueue.add(myStringRequest)
    }
}