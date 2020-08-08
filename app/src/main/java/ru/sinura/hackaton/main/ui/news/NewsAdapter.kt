package ru.sinura.hackaton.main.ui.news

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.sinura.hackaton.R


class NewsAdapter: RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    var dataList: List<NewsModel>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_view, parent, false) as LinearLayout
        return NewsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.tvHeader).text = dataList?.get(position)?.header
        holder.view.findViewById<TextView>(R.id.tvLink).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dataList?.get(position)?.link))
            holder.view.context.startActivity(intent)
        }
        Log.e("NewsAdapter", "onBindViewHolder")
    }

    class NewsViewHolder(val view: LinearLayout): RecyclerView.ViewHolder(view)
}