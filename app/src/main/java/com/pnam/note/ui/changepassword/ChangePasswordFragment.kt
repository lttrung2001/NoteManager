package com.pnam.note.ui.changepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pnam.note.R
import com.pnam.note.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding

    private val backClickListener: View.OnClickListener by lazy {
        View.OnClickListener {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentChangePasswordBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

}