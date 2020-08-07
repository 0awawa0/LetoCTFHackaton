package ru.sinura.hackaton.main.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.sinura.hackaton.R

class NewsFragment : Fragment() {

    private lateinit var homeViewModel: NewsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = NewsViewModel()

        return inflater.inflate(R.layout.fragment_news, container, false)
    }
}