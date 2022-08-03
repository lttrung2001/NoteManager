package com.pnam.note.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pnam.note.R
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
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }
    private val registerClick: View.OnClickListener by lazy {
        View.OnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.register(
                    binding.edtEmail.text.toString().trim(),
                    binding.edtPassword.text.toString().trim()
                )
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
                    binding.registerError.visibility = View.VISIBLE
                    binding.registerError.text = "Password not match"
                    binding.load.visibility = View.INVISIBLE
                }
            }
        }
        viewModel.internetError.observe(this) {
            binding.registerError.visibility = View.VISIBLE
            binding.registerError.setText(R.string.no_internet)
        }
    }
}