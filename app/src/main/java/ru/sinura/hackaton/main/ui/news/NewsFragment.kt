package ru.sinura.hackaton.main.ui.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.sinura.hackaton.R
import ru.sinura.hackaton.main.MainActivity

class NewsFragment : Fragment() {

    private lateinit var homeViewModel: NewsViewModel
    private val adapter = NewsAdapter()

    private val responseCallback = object : NewsResponse {
        override fun onSuccess(news: Array<NewsModel>) {
            for (n in news) {
                Log.e("NewsFragment", "Link: ${n.link} Header: ${n.header}")
                adapter.dataList = news.toList()
            }
        }

        override fun onError() {
            Toast.makeText(this@NewsFragment.requireContext(), "Error", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_news, container, false)
        homeViewModel = NewsViewModel()

        val rvNews = root.findViewById<RecyclerView>(R.id.rvNews)

        rvNews.layoutManager = LinearLayoutManager(requireContext())
        rvNews.adapter = adapter
        val repo = (requireActivity() as MainActivity).repo
        repo.newsResponse = responseCallback
        repo.getNews()
        return root
    }

    interface NewsResponse {
        fun onSuccess(news: Array<NewsModel>)
        fun onError()
    }
}