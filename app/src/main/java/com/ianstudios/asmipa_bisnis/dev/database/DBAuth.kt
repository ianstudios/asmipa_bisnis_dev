package com.ianstudios.asmipa_bisnis.dev.database

import android.content.Context
import com.google.gson.GsonBuilder
import com.ianstudios.asmipa_bisnis.dev.api.V1.Companion.authorize
import com.ianstudios.asmipa_bisnis.dev.interfaces.IAuth
import com.ianstudios.asmipa_bisnis.dev.models.Auth
import org.json.JSONObject

class DBAuth(private var context: Context) {

    fun login(auth: Auth, iAuth: IAuth){
        authorize(context, auth, {
            val response = JSONObject(it)
            val arrayList = ArrayList<String>()

            val gson = GsonBuilder().setPrettyPrinting().create()
            val gsonFinal = gson.toJson(response).toString()
            arrayList.add(gsonFinal)

            val rawData = response.optString("data")
            val data = JSONObject(rawData)
            arrayList.add(data.optString("token"))

            iAuth.onData(arrayList)
        }) {
            iAuth.onError("${it.networkResponse.statusCode}")
        }
    }
}