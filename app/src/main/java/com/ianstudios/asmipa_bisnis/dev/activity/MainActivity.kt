package com.ianstudios.asmipa_bisnis.dev.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.GsonBuilder
import com.ianstudios.asmipa_bisnis.dev.database.DBAuth
import com.ianstudios.asmipa_bisnis.dev.databinding.ActivityMainBinding
import com.ianstudios.asmipa_bisnis.dev.functions.GoogleSignerV1
import com.ianstudios.asmipa_bisnis.dev.functions.GoogleSignerV2
import com.ianstudios.asmipa_bisnis.dev.interfaces.IAuth
import com.ianstudios.asmipa_bisnis.dev.interfaces.IGoogleSign
import com.ianstudios.asmipa_bisnis.dev.models.Auth
import com.ianstudios.asmipa_bisnis.dev.utils.StringUtils

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        build()
    }

    private fun build(){

        if (!GoogleSignerV2(this).isUserSignedIn()) {
            binding.loginRegister.btnLogin.text = "Masuk Ke Google"
            binding.loginRegister.btnLogin.setOnClickListener {
                GoogleSignerV2(this).signIn(object : IGoogleSign{
                    override fun onSuccess(result: Intent?) {
                        super.onSuccess(result)
                        loginLauncherV2.launch(result)
                    }
                })
            }
        } else {
            binding.loginRegister.btnLogin.text = "Keluar Dari Google"
            binding.loginRegister.btnLogin.setOnClickListener {
                GoogleSignerV2(this).signOut()
            }
        }

      /*  binding.loginRegister.btnLogin.setOnClickListener {
          /*  Login Method V1
          lifecycleScope.launchWhenStarted {
                val sign = GoogleSignerV1(this@MainActivity).signInV1()
                val isr = IntentSenderRequest.Builder(sign.intentSender).build()
                loginLauncherV1.launch(isr)
           }
           */
        } */
    }

    private val loginLauncherV2 = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            GoogleSignerV2(this).handleAccountData(result.data, object : IGoogleSign{
                override fun onData(profile: GoogleSignInAccount) {
                    super.onData(profile)
                    val map = HashMap<String, String>()
                    map["email"] = "${profile.email}"
                    map["name"] = "${profile.displayName}"
                    map["picture"] = "${profile.photoUrl}"
                    map["serverAuthCode"] = "${profile.serverAuthCode}"
                    val gson = GsonBuilder().setPrettyPrinting().create()

                    with(binding.loginRegister) {
                        txtResponseGoogle.text = gson.toJson(map).toString()
                        val auth = Auth()
                        auth.mode = 1
                        auth.authcode = "${profile.serverAuthCode}"

                        DBAuth(this@MainActivity).login(auth, object : IAuth{
                            override fun onData(arrayList: ArrayList<String>) {
                                super.onData(arrayList)

                                with(binding.loginRegister.txtResponseVerify){
                                    visibility = View.VISIBLE
                                    text = arrayList[0]
                                }
                            }

                            override fun onNull() {
                                super.onNull()
                                with(binding.loginRegister.txtResponseVerify){
                                    visibility = View.VISIBLE
                                    text = "no response"
                                }
                            }

                            override fun onError(message: String?) {
                                super.onError(message)
                                with(binding.loginRegister.txtResponseVerify){
                                    visibility = View.VISIBLE
                                    text = "$message Error"
                                }
                            }

                        })
                    }
                }
            })
        }
    }

    private val loginLauncherV1 = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            GoogleSignerV1(this).accountProfileV1(result.data!!, object : IGoogleSign {
                override fun onData(credential: SignInCredential, profile: GoogleSignInAccount?) {
                    super.onData(credential, profile)
                    val map = HashMap<String, String>()
                    map["email"] = credential.id
                    map["name"] = "${credential.displayName}"
                    map["picture"] = "${credential.profilePictureUri}"
                    map["token"] = StringUtils().truncate("${credential.googleIdToken}", "...", 40)
                    val gson = GsonBuilder().setPrettyPrinting().create()

                    with(binding.loginRegister) {
                        txtResponseGoogle.text = gson.toJson(map).toString()

                        btnVerify.visibility = View.VISIBLE
                        btnVerify.setOnClickListener {
                            val auth = Auth()
                            auth.mode = 1
                            auth.authcode = "${credential.googleIdToken}"
                            Log.d("TOKEN", auth.authcode)
                            DBAuth(this@MainActivity).login(auth, object : IAuth{
                                override fun onData(arrayList: ArrayList<String>) {
                                    super.onData(arrayList)

                                    var string = String()
                                    arrayList.forEach {
                                        string += "${it[0]} \n ${it[1]}"
                                    }

                                    with(binding.loginRegister.txtResponseVerify){
                                        visibility = View.VISIBLE
                                        text = string
                                    }
                                }

                                override fun onNull() {
                                    super.onNull()
                                    with(binding.loginRegister.txtResponseVerify){
                                        visibility = View.VISIBLE
                                        text = "Null"
                                    }
                                }

                                override fun onError(message: String?) {
                                    super.onError(message)
                                    with(binding.loginRegister.txtResponseVerify){
                                        visibility = View.VISIBLE
                                        text = "$message Error"
                                    }
                                }

                            })
                        }
                    }
                }
            })
        }
    }
}