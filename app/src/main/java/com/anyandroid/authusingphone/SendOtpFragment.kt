package com.anyandroid.authusingphone

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.anyandroid.authusingphone.databinding.FragmentSendOtpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SendOtpFragment : Fragment() {

    private lateinit var binding: FragmentSendOtpBinding
    private val viewModelPhoneAuth by viewModels<AuthPhoneNumberViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSendOtpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnRegister.setOnClickListener {
                val cc = ccp.selectedCountryCodeWithPlus
                val number = etPhoneNumberRegister.text.toString().trim()
                val phoneNumber = "$cc$number"
                viewModelPhoneAuth.sendVerificationCode(phoneNumber, requireActivity())
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModelPhoneAuth.isVerificationInProgress.collect { resource ->
                when (resource) {
                    is Resources.Loading -> {
                        binding.btnRegister.startAnimation()
                    }
                    is Resources.Success -> {
                        Log.d("SendOtpFragment", "Verification initiated.")
                        binding.btnRegister.revertAnimation()
                        val verificationId = viewModelPhoneAuth.verificationId.value
                        if (verificationId != null) {
                            Log.d("SendOtpFragment", "Verification ID is $verificationId.")
                            // Proceed to VerificationOtpFragment with the verification ID.
                            val action =
                                SendOtpFragmentDirections.actionSendOtpFragmentToVerificationOtpFragment(
                                    verificationId
                                )
                            Navigation.findNavController(view).navigate(action)
                        } else {
                            // Handle error: Verification ID is null.
                            Log.d("SendOtpFragment", "Verification ID is null.")
                        }
                    }
                    is Resources.Failed -> {
                        Log.d(
                            "SendOtpFragment",
                            "Verification initiation failed: ${resource.message}"
                        )
                        binding.btnRegister.revertAnimation()
                        // Show an error message to the user or take appropriate action.
                    }
                    else -> Unit
                }
            }
        }
    }
}
