package com.ianstudios.asmipa_bisnis.dev.constants

class Snip {

    var url = String()

    fun baseUrl() : String{
        url = if (mode == 0){
            "https://dev-apps.asmipa.id" //development
        } else {
            "https://asmipa.id" //production
        }
        return url
    }

    companion object {
        const val mode = 0 //can switch

        const val LEGACY_LOGIN = "/api/v1/login/legacy"
        const val GOOGLE_LOGIN = "/api/v1/login/google"
    }
}