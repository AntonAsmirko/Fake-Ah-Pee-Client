package com.example.fakeahpeeclient.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.fakeahpeeclient.R
import kotlinx.android.synthetic.main.fragment_sign_in_with_email.*

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
        register.setOnClickListener {
            this.findNavController()
                .navigate(R.id.action_fragmentSignInWithEmail_to_registerWithEmailFragment)
        }
    }
}