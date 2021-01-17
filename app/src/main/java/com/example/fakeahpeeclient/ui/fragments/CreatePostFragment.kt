package com.example.fakeahpeeclient.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.model.Post
import com.example.fakeahpeeclient.singleton.FakeAhPeeClient
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreatePostFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        publish_button.setOnClickListener {
            val title = edit_title.text.toString()
            val content = edit_content.text.toString()
            var maxId = -1
            FakeAhPeeClient.instance.postsAdapter?.data?.forEach {
                if (it.id > maxId) maxId = it.id
            }
            CoroutineScope(Dispatchers.Main).launch {
                Post(content, ++maxId, title, 1).apply {
                    FakeAhPeeClient.instance.postPost(
                        this,
                        {
                            Toast.makeText(
                                activity,
                                "post $it was successfully published",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.i("POST", "post $it was successfully published")
                        },
                        {
                            Toast.makeText(
                                activity,
                                "Some error occured while post request",
                                Toast.LENGTH_LONG
                            ).show()
                        })
                    FakeAhPeeClient.instance.postsAdapter?.data?.add(this)
                    FakeAhPeeClient.instance.postsAdapter?.notifyDataSetChanged()
                }
                this@CreatePostFragment.findNavController()
                    .navigate(R.id.action_createPostFragment_to_feedFragment)
            }
        }
    }
}