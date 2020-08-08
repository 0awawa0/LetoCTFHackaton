package ru.sinura.hackaton.main.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.w3c.dom.Text
import ru.sinura.hackaton.R
import ru.sinura.hackaton.main.MainActivity
import ru.sinura.hackaton.repo.retrofit.models.UserModel

class ProfileFragment: Fragment() {

    private val responseCallback = object: ProfileResponse {
        override fun onSuccess(userData: UserModel) {
            tvName.text = userData.name
            tvSurname.text = userData.surname
            tvOms.text = userData.omsNumber
            tvPassport.text = userData.passport
            tvCity.text = userData.city
            tvStreet.text = userData.street
            tvEmail.text = userData.email
            tvPhone.text = userData.phone
            tvDateBirth.text = userData.birthDate
        }

        override fun onError() {
            Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
        }
    }

    lateinit var tvName: TextView
    lateinit var tvSurname: TextView
    lateinit var tvOms: TextView
    lateinit var tvPassport: TextView
    lateinit var tvCity: TextView
    lateinit var tvStreet: TextView
    lateinit var tvEmail: TextView
    lateinit var tvPhone: TextView
    lateinit var tvDateBirth: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        tvName = root.findViewById(R.id.tvName)
        tvSurname = root.findViewById(R.id.tvSurname)
        tvOms = root.findViewById(R.id.tvOmsNumber)
        tvPassport = root.findViewById(R.id.tvPassport)
        tvCity = root.findViewById(R.id.tvCity)
        tvStreet = root.findViewById(R.id.tvStreet)
        tvEmail = root.findViewById(R.id.tvEmail)
        tvPhone = root.findViewById(R.id.tvPhone)
        tvDateBirth = root.findViewById(R.id.tvDateBirth)

        val repo = (requireActivity() as MainActivity).repo
        repo.profileResponse = responseCallback
        repo.getUserData((requireActivity() as MainActivity).token)

        return root
    }

    interface ProfileResponse {
        fun onSuccess(userData: UserModel)
        fun onError()
    }
}