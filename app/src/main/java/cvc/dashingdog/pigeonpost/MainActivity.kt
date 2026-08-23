package cvc.dashingdog.pigeonpost

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cvc.dashingdog.pigeonpost.data.BloggerApi
import cvc.dashingdog.pigeonpost.ui.FeedAdapter
import cvc.dashingdog.pigeonpost.ui.FeedItem
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: FeedAdapter
    private val api = BloggerApi.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = FeedAdapter(emptyList())
        findViewById<RecyclerView>(R.id.recyclerFeed).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        findViewById<Button>(R.id.buttonFetch).setOnClickListener {
            fetchFeed()
        }
    }

    private fun fetchFeed() {
        lifecycleScope.launch {
            try {
                val response = api.getFeed()
                val items = response.feed.entry?.map {
                    FeedItem(
                        title = it.title.`$t`,
                        published = it.published.`$t`,
                        link = it.link?.firstOrNull { l -> l.rel == "alternate" }?.href
                    )
                } ?: emptyList()
                adapter.submitList(items)
            } catch (e: Exception) {
                // Phase 1: just log it, proper error handling comes later
                e.printStackTrace()
            }
        }
    }
}