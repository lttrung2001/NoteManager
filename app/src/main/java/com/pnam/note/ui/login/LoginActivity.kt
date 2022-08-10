package com.pnam.note.ui.login

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pnam.note.R
import com.pnam.note.database.data.models.EmailPassword
import com.pnam.note.databinding.ActivityLoginBinding
import com.pnam.note.ui.adapters.LoginAdapter
import com.pnam.note.ui.adapters.LoginItemClickListener
import com.pnam.note.ui.dashboard.DashboardActivity
import com.pnam.note.ui.register.RegisterActivity
import com.pnam.note.utils.AppUtils.Companion.APP_NAME
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var loginAdapter: LoginAdapter

    private val loginClick: View.OnClickListener by lazy {
        View.OnClickListener {
            val email = binding.edtEmail.text.trim().toString()
            val password = binding.edtPassword.text.trim().toString()
            if (email.isEmpty() || password.isEmpty()) {
                binding.loginError.visibility = View.VISIBLE
                binding.loginError.text = "All input is required"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.loginError.visibility = View.VISIBLE
                binding.loginError.text = "Your email is invalid"
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.login(email,password)
                }
            }
        }
    }

    private val emailClick: View.OnClickListener by lazy {
        View.OnClickListener {
            if (binding.rcvLogins.visibility != View.VISIBLE) {
                binding.rcvLogins.visibility = View.VISIBLE
            }
        }
    }

    private val registerClick: View.OnClickListener by lazy {
        View.OnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rcvLogins.layoutManager = LinearLayoutManager(this)
        binding.let {
            it.btnLogin.setOnClickListener(loginClick)
            it.btnRegister.setOnClickListener(registerClick)
            it.edtEmail.setOnClickListener(emailClick)
            it.edtEmail.addTextChangedListener {
                binding.rcvLogins.visibility = View.GONE
            }
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
                    binding.loginError.visibility = View.INVISIBLE
                }
                is Resource.Success -> {
                    binding.loginError.visibility = View.INVISIBLE
                    binding.load.visibility = View.INVISIBLE
                    val data: Intent = Intent(this, DashboardActivity::class.java).apply {
                        applicationContext.getSharedPreferences(
                            APP_NAME,
                            Context.MODE_PRIVATE
                        ).edit().putString(EMAIL,it.data.email).apply()

                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(data)
                }
                is Resource.Error -> {
                    binding.loginError.visibility = View.VISIBLE
                    binding.load.visibility = View.INVISIBLE
                }
            }
        }
        viewModel.internetError.observe(this) {
            binding.loginError.visibility = View.VISIBLE
            binding.load.visibility = View.INVISIBLE
            binding.loginError.text = viewModel.internetError.value
        }
    }

    private fun initSavedLoginObserver() {
        viewModel.savedLogin.observe(this) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    if (it.data.size != 0) {
                        loginAdapter = LoginAdapter(
                            it.data,
                            object : LoginItemClickListener {
                                override fun onClick(emailPassword: EmailPassword) {
                                    binding.edtEmail.setText(emailPassword.email)
                                    binding.edtPassword.setText(emailPassword.password)
                                    binding.rcvLogins.visibility = View.GONE
                                }

                                override fun onDeleteClick(email: String, position: Int) {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        viewModel.loginDao.deleteLogin(email)
                                    }
                                    loginAdapter.removeAt(position)
                                    if (loginAdapter.itemCount == 0) {
                                        binding.rcvLogins.visibility = View.GONE
                                    }
                                }
                            }
                        )
                        binding.rcvLogins.adapter = loginAdapter
                    }
                }
                is Resource.Error -> {

                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val rect = Rect()
        binding.rcvLogins.getGlobalVisibleRect(rect)
        if (!rect.contains(ev!!.rawX.toInt(), ev.rawY.toInt())) {
            binding.rcvLogins.visibility = View.GONE
        }
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        const val EMAIL: String = "email"
    }
}