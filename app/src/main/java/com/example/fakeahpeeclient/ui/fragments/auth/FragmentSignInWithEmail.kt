package com.example.fakeahpeeclient.ui.fragments.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.singleton.FakeAhPeeClient
import com.example.fakeahpeeclient.ui.activities.AuthActivity
import kotlinx.android.synthetic.main.fragment_sign_in_with_email.*
import kotlinx.android.synthetic.main.fragment_sign_in_with_email.enter_email
import kotlinx.android.synthetic.main.fragment_sign_in_with_email.enter_password

class FragmentSignInWithEmail : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in_with_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login_button.setOnClickListener {
            signIn()
        }
        register.setOnClickListener {
            this.findNavController()
                .navigate(R.id.action_fragmentSignInWithEmail_to_registerWithEmailFragment)
        }
    }

    private fun signIn() {
        val email = enter_email.text.toString().trim()
        val password = enter_password.text.toString().trim()
        if (password.isEmpty()) {
            enter_password.error = "Password must not be empty"
            enter_password.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            enter_email.error = "incorrect email"
            enter_email.requestFocus()
            return
        }
        FakeAhPeeClient.instance.mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("SIGN_IN", "signInWithEmail:success")
                    FakeAhPeeClient.instance.loadUser()
                    (activity as AuthActivity).finishActivity()
                } else {
                    Log.w("SIGN_IN", "signInWithEmail:failure", it.exception)
                    Toast.makeText(
                        activity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }
    }
}