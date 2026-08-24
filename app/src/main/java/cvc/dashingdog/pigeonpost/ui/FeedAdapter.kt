package cvc.dashingdog.pigeonpost.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cvc.dashingdog.pigeonpost.R
import cvc.dashingdog.pigeonpost.data.FeedItem

class FeedAdapter(
    private var items: List<FeedItem>,
    private var newTitles: Set<String> = emptySet(),
    private val onItemClick: (FeedItem) -> Unit
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

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
        val isNew = item.title in newTitles
        holder.title.text = if (isNew) "🆕 ${item.title}" else item.title
        holder.published.text = item.published
        holder.title.setTextColor(if (isNew) Color.rgb(0, 120, 0) else Color.BLACK)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<FeedItem>, newlyAdded: Set<String> = emptySet()) {
        items = newItems
        newTitles = newlyAdded
        notifyDataSetChanged()
    }
}