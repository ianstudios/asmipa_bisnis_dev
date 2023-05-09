package com.ianstudios.asmipa_bisnis.dev.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import com.ianstudios.asmipa_bisnis.dev.constants.Snip
import com.ianstudios.asmipa_bisnis.dev.models.Auth


/**
 * Created by MG on 04-03-2018.
 */

class V1(private var mc: Context) {
    private var mRequestQueue: RequestQueue?

    private val requestQueue: RequestQueue
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mc.applicationContext)
            }
            return mRequestQueue!!
        }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.setShouldCache(false)
        requestQueue.add(req)
    }

    companion object {

        @Synchronized
        fun getInstance(context: Context): V1 {
            return V1(context)
        }

        fun authorize(
            ctx: Context,
            auth: Auth,
            listener: Response.Listener<String>?,
            errorListener: Response.ErrorListener?
        ) {
            if (auth.mode == 0){
                val url = Snip().baseUrl() + Snip.LEGACY_LOGIN
                val stringRequest: StringRequest = object : StringRequest(Method.POST, url, listener, errorListener) {
                    override fun getParams(): Map<String, String> {
                        val jsonBody: MutableMap<String, String> = HashMap()
                        jsonBody["username"] = auth.username
                        jsonBody["password"] = auth.password
                        return jsonBody
                    }

                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["Content-Type"] = "application/x-www-form-urlencoded"
                        return params
                    }
                }
                getInstance(ctx).addToRequestQueue(stringRequest)
            } else {
                val url = Snip().baseUrl() + Snip.GOOGLE_LOGIN
                val stringRequest: StringRequest = object : StringRequest(Method.POST, url, listener, errorListener) {
                    override fun getParams(): Map<String, String> {
                        val jsonBody: MutableMap<String, String> = HashMap()
                        jsonBody["google_auth_code"] = auth.authcode
                        return jsonBody
                    }

                    override fun getHeaders(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["Authorization"] = "Bearer"
                        params["Content-Type"] = "application/x-www-form-urlencoded"
                        return params
                    }
                }
                getInstance(ctx).addToRequestQueue(stringRequest)
            }
        }
    }

    init {
        mRequestQueue = requestQueue
    }
}