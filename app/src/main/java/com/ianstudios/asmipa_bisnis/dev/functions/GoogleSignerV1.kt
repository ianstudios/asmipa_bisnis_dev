package com.ianstudios.asmipa_bisnis.dev.functions

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.ianstudios.asmipa_bisnis.dev.constants.Strings
import com.ianstudios.asmipa_bisnis.dev.interfaces.IGoogleSign
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException


class GoogleSignerV1(private var context: Activity) {

    @ExperimentalCoroutinesApi
    suspend fun signInV1() : PendingIntent = suspendCancellableCoroutine { step ->
        val request = GetSignInIntentRequest.builder()
            .setServerClientId(Strings.GOOGLE_CLIENT_ID)
            .build()


        Identity.getSignInClient(context)
            .getSignInIntent(request)
            .addOnSuccessListener { pending ->
                step.resume(pending) {

                }
            }
            .addOnCanceledListener {
                step.cancel()
            }
            .addOnFailureListener { e ->
                step.resumeWithException(e)
            }

    }

    fun accountProfileV1(result: Intent, iGoogleSign: IGoogleSign){
        val credential =
            Identity.getSignInClient(context).getSignInCredentialFromIntent(result)
       val profile = GoogleSignIn.getLastSignedInAccount(context)
    //    val accprofile = GoogleSignInAccount.fromAccount(account)
        iGoogleSign.onData(credential, profile)
    }
}