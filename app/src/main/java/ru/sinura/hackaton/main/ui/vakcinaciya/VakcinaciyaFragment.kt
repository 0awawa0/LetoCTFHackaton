package ru.sinura.hackaton.main.ui.vakcinaciya

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.sinura.hackaton.R
import ru.sinura.hackaton.repo.Repo
import ru.sinura.hackaton.repo.retrofit.models.RecepModel
import java.util.*

class VakcinaciyaFragment : Fragment() {

    private val repo = Repo.getInstance()
    private lateinit var notificationsViewModel: VakcinaciyaViewModel
    private val responseCallback = object : RecepResponse {

        override fun onSuccess(receps: Array<RecepModel>?) {
            if (receps == null) return
            for (recep in receps) {
                Log.e("VakcinaciyaFragment", "Fio: ${recep.fio}\nCabinet: ${recep.num_cabinet}\nDate: ${recep.date}")
            }
        }

        override fun onError() {
            Toast.makeText(this@VakcinaciyaFragment.requireContext(), "Error", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_vakcinaciya, container, false)
        repo.recepResponse = responseCallback
        repo.getRecep(Date().time / 1000 + 60 * 60 * 24)
        return root
    }

    interface RecepResponse {
        fun onSuccess(receps: Array<RecepModel>?)
        fun onError()
    }
}