package com.ianstudios.asmipa_bisnis.dev.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.GsonBuilder
import com.ianstudios.asmipa_bisnis.dev.databinding.ActivityMainBinding
import com.ianstudios.asmipa_bisnis.dev.functions.GoogleSigner
import com.ianstudios.asmipa_bisnis.dev.interfaces.IGoogleSign
import com.ianstudios.asmipa_bisnis.dev.utils.StringUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        build()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun build(){
        binding.loginRegister.btn.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                val sign = GoogleSigner(this@MainActivity).signIn()
                val isr = IntentSenderRequest.Builder(sign.intentSender).build()
                resultLoginLauncher.launch(isr)
            }
        }
    }

    private val resultLoginLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            GoogleSigner(this).accountProfile(result.data!!, object : IGoogleSign {
                override fun onData(credential: SignInCredential, profile: GoogleSignInAccount?) {
                    super.onData(credential, profile)
                    val map = HashMap<String, String>()
                    map["email"] = credential.id
                    map["name"] = "${credential.displayName}"
                    map["picture"] = "${credential.profilePictureUri}"
                    map["token"] = StringUtils().truncate("${credential.googleIdToken}", "...", 40)
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    binding.loginRegister.txt.text = gson.toJson(map).toString()
                }
            })
        }
    }
}