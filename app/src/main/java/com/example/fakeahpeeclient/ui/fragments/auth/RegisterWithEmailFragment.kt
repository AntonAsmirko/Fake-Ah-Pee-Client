package com.example.fakeahpeeclient.ui.fragments.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.singleton.FakeAhPeeClient
import com.example.fakeahpeeclient.ui.activities.AuthActivity
import kotlinx.android.synthetic.main.fragment_register_with_email.*
import kotlinx.android.synthetic.main.fragment_register_with_email.enter_email
import kotlinx.android.synthetic.main.fragment_register_with_email.enter_password

class RegisterWithEmailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_with_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        register_button.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = enter_email.text.toString().trim()
        val password = enter_password.text.toString().trim()
        val name = enter_name.text.toString().trim()
        if (name.isEmpty()) {
            enter_name.error = "Name is required"
            enter_name.requestFocus()
            return
        }
        if (!passwordValidation(password)) {
            enter_password.error = "unacceptable password"
            enter_password.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            enter_email.error = "incorrect email"
            enter_email.requestFocus()
            return
        }
        FakeAhPeeClient.instance.mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("NEW_USER", "signInWithEmail:success")
                    (activity as AuthActivity).finishActivity()
                } else {
                    Log.w("NEW_USER", "signInWithEmail:failure", it.exception)
                    Toast.makeText(
                        activity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun passwordValidation(password: String): Boolean {
        return password.length >= 6
    }
}