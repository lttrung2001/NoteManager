package com.pnam.note.ui.forgotpassword

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.pnam.note.databinding.ActivityForgotPasswordBinding
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()

    private val loginClick: View.OnClickListener by lazy {
        View.OnClickListener {
            onBackPressed()
        }
    }

    private val resetPasswordCLick: View.OnClickListener by lazy {
        View.OnClickListener {
            val email = binding.edtEmail.text.toString()
            if (email.isEmpty()) {
                binding.forgotError.visibility = View.VISIBLE
                binding.forgotError.text = "Email is required"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.forgotError.visibility = View.VISIBLE
                binding.forgotError.text = "Your email is invalid"
            } else {
                hideKeyboard(binding.btnResetPassword.windowToken)
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.forgotPassword(binding.edtEmail.text.trim().toString())
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnResetPassword.setOnClickListener(resetPasswordCLick)
        binding.btnBack.setOnClickListener(loginClick)
        initObserver()
    }

    private fun initObserver() {
        viewModel.forgotPassword.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    binding.forgotError.visibility = View.INVISIBLE
                    binding.load.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.load.visibility = View.INVISIBLE
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Your password has been reset and send to your email",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
                is Resource.Error -> {
                    binding.load.visibility = View.INVISIBLE
                    binding.forgotError.let { tvError ->
                        tvError.visibility = View.VISIBLE
                        tvError.text = it.message
                    }
                }
            }
        }
        viewModel.error.observe(this) {
            binding.load.visibility = View.INVISIBLE
            binding.forgotError.visibility = View.VISIBLE
            binding.forgotError.text = viewModel.error.value
        }
    }

    private fun hideKeyboard(element: IBinder) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(element, 0)
    }
}