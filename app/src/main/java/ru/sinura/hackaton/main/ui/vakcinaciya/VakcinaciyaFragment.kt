package ru.sinura.hackaton.main.ui.vakcinaciya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.sinura.hackaton.R

class VakcinaciyaFragment : Fragment() {

    private lateinit var notificationsViewModel: VakcinaciyaViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProviders.of(this).get(VakcinaciyaViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_vakcinaciya, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}