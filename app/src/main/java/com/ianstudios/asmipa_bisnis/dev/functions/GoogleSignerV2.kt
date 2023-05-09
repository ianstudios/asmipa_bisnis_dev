package com.ianstudios.asmipa_bisnis.dev.functions

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.ianstudios.asmipa_bisnis.dev.constants.Strings
import com.ianstudios.asmipa_bisnis.dev.interfaces.IGoogleSign

class GoogleSignerV2(private var context: Context) {

    private fun prepareSign(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(Strings.GOOGLE_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .requestScopes(Scope(Scopes.PROFILE))
            .build()

        return GoogleSignIn.getClient(context, gso)
    }


    fun isUserSignedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account != null
    }

    fun lastAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun signIn(iGoogleSign: IGoogleSign) {
        if (!isUserSignedIn()){
            val signInIntent = prepareSign().signInIntent
            iGoogleSign.onSuccess(signInIntent)
        }
    }

    fun signOut() {
        if (isUserSignedIn()){
            prepareSign().signOut().addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(context, " Signed out ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, " Error ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun handleAccountData(data: Intent?, iGoogleSign: IGoogleSign) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    iGoogleSign.onData(it.result)
                } else {
                    "exception ${it.exception}".print()
                }
            }

    }

    private fun Any.print(){
        Log.v(Strings.LOG, " $this")
    }

}