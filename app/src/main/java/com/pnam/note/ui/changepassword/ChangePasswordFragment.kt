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

    private val backClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            activity?.onBackPressed()
        }
    }

    private val saveChangeClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val newPass = binding.edtNewPassword.text.toString()
            val newPass2 = binding.edtNewPassword2.text.toString()
            if (newPass.contentEquals(newPass2)) {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.changePassword(
                        binding.edtCurrPassword.text.toString(),
                        newPass
                    )
                }
            } else {
                Toast.makeText(activity,"New password not matched",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        binding.btnBack.setOnClickListener(backClickListener)
        binding.btnSaveChange.setOnClickListener(saveChangeClickListener)
        initObservers()
        return binding.root
    }

    private fun initObservers() {
        viewModel.changePassword.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.load.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    Toast.makeText(activity,"Change password successfully",Toast.LENGTH_SHORT).show()
                    binding.load.visibility = View.INVISIBLE
                    activity?.onBackPressed()
                }
                is Resource.Error -> {
                    Toast.makeText(activity,"Error",Toast.LENGTH_SHORT).show()
                    binding.load.visibility = View.INVISIBLE
                }
            }
        }

        viewModel.internetError.observe(viewLifecycleOwner) {
            binding.changePasswordError.text = "No internet connection"
            binding.load.visibility = View.INVISIBLE
        }
    }
}