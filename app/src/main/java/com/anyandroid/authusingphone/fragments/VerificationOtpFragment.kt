package com.anyandroid.authusingphone.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.anyandroid.authusingphone.R
import com.anyandroid.authusingphone.VerificationOtpFragmentArgs
import com.anyandroid.authusingphone.databinding.FragmentVerificationOtpBinding
import com.anyandroid.authusingphone.utils.Resources
import com.anyandroid.authusingphone.viewModels.AuthPhoneNumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerificationOtpFragment : Fragment() {

    private lateinit var binding: FragmentVerificationOtpBinding
    private val viewModelPhoneAuth by viewModels<AuthPhoneNumberViewModel>()
    private val args: VerificationOtpFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerificationOtpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnVerify.setOnClickListener {
                val smsCode = etCodeSendVerification.text.toString()
                val verificationId = args.verification
                viewModelPhoneAuth.signInWithVerificationCode(verificationId, smsCode)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPhoneAuth.isVerificationInProgress.collect { resource ->
                    when (resource) {
                        is Resources.Loading -> {
                            binding.btnVerify.startAnimation()
                        }

                        is Resources.Success -> {
                            Log.d(
                                "VerificationOtpFragment",
                                "Verification success: ${resource.data}"
                            )
                            binding.btnVerify.revertAnimation()
                            Navigation.findNavController(view)
                                .navigate(R.id.action_verificationOtpFragment_to_homePageFragment)
                            // Proceed to the next screen or perform necessary actions.
                        }

                        is Resources.Failed -> {
                            Log.d(
                                "VerificationOtpFragment",
                                "Verification failed: ${resource.message}"
                            )
                            binding.btnVerify.revertAnimation()
                            // Show an error message to the user or take appropriate action.
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}