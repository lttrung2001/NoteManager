package com.pnam.note.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.pnam.note.databinding.ActivityRegisterBinding
import com.pnam.note.ui.base.BaseActivity
import com.pnam.note.ui.login.LoginActivity
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    private val loginClick: View.OnClickListener by lazy {
        View.OnClickListener {
            onBackPressed()
        }
    }
    private val registerClick: View.OnClickListener by lazy {
        View.OnClickListener {
            val email = binding.edtEmail.text?.trim().toString()
            val password = binding.edtPassword.text?.trim().toString()
            val password2 = binding.edtPassword2.text?.trim().toString()
            if (email.isEmpty()) {
                binding.tilEmail.showError("Email is required.")
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.showError("Email is invalid.")
            } else {
                binding.tilEmail.apply {
                    isErrorEnabled = false
                    error = ""
                }
            }
            if (password != password2) {
                binding.tilPassword.showError("Password not matched.")
                binding.tilPassword2.showError("Password not matched.")
            } else if (password.isEmpty() && password2.isEmpty()) {
                binding.tilPassword.showError("Password is required.")
                binding.tilPassword2.showError("Confirm password is required.")
            } else {
                binding.tilPassword.apply {
                    isErrorEnabled = false
                    error = ""
                }
                binding.tilPassword2.apply {
                    isErrorEnabled = false
                    error = ""
                }
                hideKeyboard(binding.btnRegister.windowToken)
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.register(email, password)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.let {
            it.btnLogin.setOnClickListener(loginClick)
            it.btnRegister.setOnClickListener(registerClick)
        }
        viewModel.register.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.load.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.load.visibility = View.INVISIBLE
                    val data: Intent = Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(data)
                }
                is Resource.Error -> {
                    binding.load.visibility = View.INVISIBLE
                    binding.tilPassword2.showError(it.message)
                }
            }
        }
        viewModel.error.observe(this) {
            binding.load.visibility = View.INVISIBLE
            binding.tilPassword2.showError(viewModel.error.value.toString())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAfterTransition()
        overridePendingTransition(
            com.google.android.material.R.anim.abc_slide_in_top,
            com.google.android.material.R.anim.abc_slide_out_bottom
        )
    }
}