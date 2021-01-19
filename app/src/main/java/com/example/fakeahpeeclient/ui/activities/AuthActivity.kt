package com.example.fakeahpeeclient.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.fakeahpeeclient.R

class AuthActivity : AppCompatActivity() {

    companion object {
        const val GET_RESULT = "RESULT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }

    fun finishActivity() {
        val i = Intent().also {
            it.putExtra(GET_RESULT, true)
            setResult(Activity.RESULT_OK, it)
            finish()
        }
    }
}