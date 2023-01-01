package com.pnam.note.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pnam.note.R
import com.pnam.note.database.data.locals.entities.EmailPassword
import com.pnam.note.databinding.ActivityLoginBinding
import com.pnam.note.ui.adapters.login.LoginAdapter
import com.pnam.note.ui.adapters.login.LoginItemClickListener
import com.pnam.note.ui.base.BaseActivity
import com.pnam.note.ui.dashboard.DashboardActivity
import com.pnam.note.ui.forgotpassword.ForgotPasswordActivity
import com.pnam.note.ui.register.RegisterActivity
import com.pnam.note.utils.AppConstants.APP_NAME
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@SuppressLint("ClickableViewAccessibility")
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val loginAdapter: LoginAdapter by lazy {
        LoginAdapter(
            object : LoginItemClickListener {
                override fun onClick(emailPassword: EmailPassword) {
                    binding.edtEmail.setText(emailPassword.email)
                    binding.edtPassword.setText(emailPassword.password)
                    popupWindow.dismiss()
                }

                override fun onDeleteClick(email: String, position: Int) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.loginDao.deleteLogin(email)
                    }
                    loginAdapter.currentList.removeAt(position)
                    if (loginAdapter.currentList.size == 0) {
                        popupWindow.dismiss()
                    }
                }
            }
        )
    }

    private val popupTouchInterceptor: View.OnTouchListener by lazy {
        View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                popupWindow.dismiss()
                true
            } else {
                false
            }
        }
    }

    private val emailTouchInterceptor: View.OnTouchListener by lazy {
        View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                binding.edtEmail.requestFocus()
                popupWindow.showAsDropDown(binding.edtEmail)
                true
            } else {
                false
            }
        }
    }

    private val popupWindow: PopupWindow by lazy {
        PopupWindow(this@LoginActivity).also { window ->
            window.setBackgroundDrawable(resources.getDrawable(R.drawable.rectangle_transparent, theme))
            val view = layoutInflater.inflate(R.layout.popup_window_saved_login, null)
            val rcv = view.findViewById<RecyclerView>(R.id.rcv_logins)
            rcv.layoutManager = LinearLayoutManager(this@LoginActivity)
            rcv.adapter = loginAdapter
            window.contentView = view
            window.isOutsideTouchable = true
            window.setTouchInterceptor(popupTouchInterceptor)
        }
    }

    private val loginClick: View.OnClickListener     by lazy {
        View.OnClickListener {
            val email = binding.edtEmail.text?.trim().toString()
            val password = binding.edtPassword.text?.trim().toString()
            if (email.isEmpty()) {
                binding.tilEmail.showError("Email is required.")
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.showError("Your email is invalid.")
            } else {
                binding.tilEmail.isErrorEnabled = false
                binding.tilEmail.error = ""
            }
            if (password.isEmpty()) {
                binding.tilPassword.showError("Password is required.")
            } else {
                binding.tilPassword.isErrorEnabled = false
                binding.tilPassword.error = ""
            }
            if (!(binding.tilEmail.isErrorEnabled ||
                        binding.tilPassword.isErrorEnabled)
            ) {
                binding.btnLogin.windowToken?.let { btn -> hideKeyboard(btn) }
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.login(email, password)
                }
            }
        }
    }

    private val registerClick: View.OnClickListener by lazy {
        View.OnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(
                com.google.android.material.R.anim.abc_slide_in_bottom,
                com.google.android.material.R.anim.abc_slide_out_top
            )
        }
    }

    private val forgotClick: View.OnClickListener by lazy {
        View.OnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.let { bd ->
            bd.btnLogin.setOnClickListener(loginClick)
            bd.btnRegister.setOnClickListener(registerClick)
            bd.btnForgot.setOnClickListener(forgotClick)
            bd.edtEmail.setOnTouchListener(emailTouchInterceptor)
        }
        initLoginObserver()
        initSavedLoginObserver()
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getSavedLogin()
        }
    }

    private fun initLoginObserver() {
        viewModel.login.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.load.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.load.visibility = View.INVISIBLE
                    val data: Intent = Intent(this, DashboardActivity::class.java).apply {
                        applicationContext.getSharedPreferences(
                            APP_NAME,
                            Context.MODE_PRIVATE
                        ).edit().putString(EMAIL, it.data.email).apply()

                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(data)
                }
                is Resource.Error -> {
                    binding.load.visibility = View.INVISIBLE
                    binding.tilPassword.let { til ->
                        til.isErrorEnabled = true
                        til.errorContentDescription = it.message
                    }
                }
            }
        }
        viewModel.error.observe(this) {
            binding.load.visibility = View.INVISIBLE
            binding.tilPassword.let { til ->
                til.isErrorEnabled = true
                til.errorContentDescription = viewModel.error.value
            }
        }
    }

    private fun initSavedLoginObserver() {
        viewModel.savedLogin.observe(this) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    if (it.data.size != 0) {
                        loginAdapter.submitList(it.data)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        const val EMAIL: String = "email"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAfterTransition()
    }
}