package com.pnam.note.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.activity.viewModels
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.pnam.note.R
import com.pnam.note.base.BaseActivity
import com.pnam.note.databinding.ActivityScrollingBinding
import com.pnam.note.ui.changepassword.ChangePasswordFragment
import com.pnam.note.ui.login.LoginActivity
import com.pnam.note.utils.AppUtils
import com.pnam.note.utils.AppUtils.Companion.APP_NAME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityScrollingBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        initFragmentController()
        initNavigationView()
    }

    fun collapseToolbar() {
        binding.appBarMain.appBar.setExpanded(false, true)
    }

    private fun initFragmentController() {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_change_password, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun initNavigationView() {
        val sp = applicationContext.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        val navView = binding.navView
        val headerView = navView.getHeaderView(0)
        val txtEmail = headerView
            .findViewById<TextView>(R.id.navigation_header_email)
        txtEmail.text = sp.getString(LoginActivity.EMAIL, "")
        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            logout()
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun logout() {
        lifecycleScope.launch(Dispatchers.IO) {
            val sp =
                applicationContext.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
            sp.edit().remove(AppUtils.ACCESS_TOKEN).apply()
            sp.edit().remove(AppUtils.LOGIN_TOKEN).apply()
            sp.edit().remove(LoginActivity.EMAIL).apply()
            viewModel.noteLocals.deleteAllNote()
            viewModel.noteLocals.deleteAllNoteStatus()
        }
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

}