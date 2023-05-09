package com.ianstudios.asmipa_bisnis.dev.interfaces

import android.content.Intent
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface IGoogleSign {

    fun onSuccess(result: IntentSenderRequest) {}
    fun onSuccess(result: Intent?){}
    fun onError(msg: String){}
    fun onCanceled(){}
    fun onData(credential: SignInCredential, profile: GoogleSignInAccount?){}
    fun onData(profile: GoogleSignInAccount){}
}