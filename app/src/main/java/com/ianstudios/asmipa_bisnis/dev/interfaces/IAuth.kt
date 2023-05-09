package com.ianstudios.asmipa_bisnis.dev.interfaces

interface IAuth {
    fun onData(arrayList: ArrayList<String>) {}
    fun onError(message: String?) {}
    fun onResponse(){}
    fun onNull(){}
}