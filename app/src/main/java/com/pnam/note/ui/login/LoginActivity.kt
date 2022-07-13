package com.pnam.note.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pnam.note.MainActivity
import com.pnam.note.R
import com.pnam.note.databinding.ActivityLoginBinding
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel:LoginViewModel by viewModels()

    private val loginClick: View.OnClickListener by lazy {
        View.OnClickListener {
            Thread {
                loginViewModel.login(
                    binding.edtEmail.text.toString(),
                    binding.edtPassword.text.toString()
                )
            }.start()
        }
    }
    private val registerClick: View.OnClickListener by lazy {
        View.OnClickListener {

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.let {
            it.btnLogin.setOnClickListener(loginClick)
            it.btnRegister.setOnClickListener(registerClick)
        }
        loginViewModel.login.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    Toast.makeText(this,"Loading",Toast.LENGTH_SHORT).show()
                    binding.loginError.visibility = View.INVISIBLE
                }
                is Resource.Success -> {
                    Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
                    binding.loginError.visibility = View.INVISIBLE
                    val data: Intent = Intent(this,MainActivity::class.java).apply {
                        // it la Resource<Login>
                        putExtra(UID,it.data.id)
                    }
                    startActivity(data)
                }
                is Resource.Error -> {
                    Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
                    binding.loginError.visibility = View.VISIBLE
                    binding.loginError.text = "Wrong password"
                }
            }
        }
        loginViewModel.internetError.observe(this) {
            binding.loginError.visibility = View.VISIBLE
            binding.loginError.setText(R.string.no_internet)
        }
    }
    companion object {
        const val UID: String = "uid"
    }
}