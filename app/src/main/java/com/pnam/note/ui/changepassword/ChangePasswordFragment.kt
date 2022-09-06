package com.pnam.note.ui.changepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.pnam.note.databinding.FragmentChangePasswordBinding
import com.pnam.note.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by viewModels()

    private val saveChangeClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val currentPass = binding.edtCurrPassword.text.trim().toString()
            val newPass = binding.edtNewPassword.text.trim().toString()
            val newPass2 = binding.edtNewPassword2.text.trim().toString()
            if (currentPass.isEmpty() || newPass.isEmpty() || newPass2.isEmpty()) {
                binding.changePasswordError.text = "All input is required"
            } else if (newPass.contentEquals(newPass2)) {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.changePassword(currentPass, newPass)
                }
            } else {
                binding.changePasswordError.visibility = View.VISIBLE
                binding.changePasswordError.text = "New password not matched"
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        binding.btnSaveChange.setOnClickListener(saveChangeClickListener)
        initObservers()
        return binding.root
    }

    private fun initObservers() {
        viewModel.changePassword.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.changePasswordError.visibility = View.INVISIBLE
                    binding.load.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    Toast.makeText(activity, "Change password successfully", Toast.LENGTH_SHORT)
                        .show()
                    binding.load.visibility = View.INVISIBLE
                    activity?.onBackPressed()
                }
                is Resource.Error -> {
                    binding.load.visibility = View.INVISIBLE
                    binding.changePasswordError.visibility = View.VISIBLE
                    binding.changePasswordError.text = it.message
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            binding.load.visibility = View.INVISIBLE
            binding.changePasswordError.visibility = View.VISIBLE
            binding.changePasswordError.text = viewModel.error.value
        }
    }
}