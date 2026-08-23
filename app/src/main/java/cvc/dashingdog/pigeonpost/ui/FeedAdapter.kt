package cvc.dashingdog.pigeonpost.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cvc.dashingdog.pigeonpost.R

class FeedAdapter(private var items: List<FeedItem>) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textTitle)
        val published: TextView = view.findViewById(R.id.textPublished)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_entry, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.published.text = item.published
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<FeedItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}