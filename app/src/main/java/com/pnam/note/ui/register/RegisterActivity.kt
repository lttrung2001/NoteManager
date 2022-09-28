package com.pnam.note.ui.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pnam.note.databinding.ActivityRegisterBinding
import com.pnam.note.ui.login.LoginActivity
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    private val loginClick: View.OnClickListener by lazy {
        View.OnClickListener {
            onBackPressed()
        }
    }
    private val registerClick: View.OnClickListener by lazy {
        View.OnClickListener {
            val email = binding.edtEmail.text.trim().toString()
            val password = binding.edtPassword.text.trim().toString()
            val password2 = binding.edtPassword2.text.trim().toString()
            if (email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                binding.registerError.visibility = View.VISIBLE
                binding.registerError.text = "All input is required"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.registerError.visibility = View.VISIBLE
                binding.registerError.text = "Your email is invalid"
            } else if (!password.contentEquals(password2)) {
                binding.registerError.visibility = View.VISIBLE
                binding.registerError.text = "Password not matched"
            } else {
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
                    binding.registerError.visibility = View.INVISIBLE
                }
                is Resource.Success -> {
                    binding.registerError.visibility = View.INVISIBLE
                    binding.load.visibility = View.INVISIBLE
                    val data: Intent = Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(data)
                }
                is Resource.Error -> {
                    binding.load.visibility = View.INVISIBLE
                    binding.registerError.let { tvError ->
                        tvError.visibility = View.VISIBLE
                        tvError.text = it.message
                    }
                }
            }
        }
        viewModel.error.observe(this) {
            binding.registerError.visibility = View.VISIBLE
            binding.load.visibility = View.INVISIBLE
            binding.registerError.text = viewModel.error.value
        }
    }

    private fun hideKeyboard(element: IBinder) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(element, 0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAfterTransition()
    }
}