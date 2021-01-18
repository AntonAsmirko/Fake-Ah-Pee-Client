package com.example.fakeahpeeclient.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.example.fakeahpeeclient.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.fakeahpeeclient.extensions.*
import com.example.fakeahpeeclient.ui.fragments.ChatsFragment
import com.example.fakeahpeeclient.ui.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val MAKE_POST_REQUEST = 101
        const val PERSIST_BOTTOM_NAVIGATION_STATE = "YO"
        const val CUR_BACK_STACK = "OY"
    }

    private var currentNavController: LiveData<NavController>? = null
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var stackGlobal: ArrayList<Int>
    private var curBackStack = 0
    private val graphIdToTagMap = SparseArray<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("YO", "Activity was created")
        startActivityForResult(Intent(this, AuthActivity::class.java), 1)
        stackGlobal =
            savedInstanceState?.getIntegerArrayList(PERSIST_BOTTOM_NAVIGATION_STATE)
                ?: ArrayList<Int>().also { it -> it.add(R.id.profile_navigation) }
        curBackStack = savedInstanceState?.getInt(CUR_BACK_STACK) ?: 0
        setupBottomNavigation()
        savedInstanceState?.let {
            bottomNavigationView.selectedItemId = it.getInt(PERSIST_BOTTOM_NAVIGATION_STATE)
        }
        fab.setOnClickListener {
            currentNavController?.value!!.navigate(R.id.action_feedFragment_to_createPostFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntegerArrayList(PERSIST_BOTTOM_NAVIGATION_STATE, stackGlobal)
        outState.putInt(CUR_BACK_STACK, curBackStack)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (getCurCount(curBackStack) != 0 || stackGlobal.size == 1) {
            super.onBackPressed()
        } else {
            curBackStack = bottomNavigationView.handleBackButtonPressed(
                supportFragmentManager,
                stackGlobal,
                graphIdToTagMap
            )
        }
    }

    private fun getCurCount(num: Int): Int {
        return when (num) {
            0 -> ProfileFragment.globalCount
            else -> ChatsFragment.globalCount
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        val navGraphIds = listOf(
            R.navigation.profile_nevigation,
            R.navigation.chats_navigation,
            R.navigation.feed_navigation,
            R.navigation.settings_navigation
        )
        bottomNavigationView.selectedItemId = stackGlobal.last()
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.my_nav_host_fragment,
            intent = intent,
            stackGlobal = stackGlobal,
            graphIdToTagMap = graphIdToTagMap
        ) { newGraph -> curBackStack = newGraph }
        currentNavController = controller
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}