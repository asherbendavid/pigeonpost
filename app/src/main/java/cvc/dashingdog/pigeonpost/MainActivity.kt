package cvc.dashingdog.pigeonpost

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import cvc.dashingdog.pigeonpost.data.BloggerApi
import cvc.dashingdog.pigeonpost.data.FeedRepository
import cvc.dashingdog.pigeonpost.data.FeedStore
import cvc.dashingdog.pigeonpost.notification.NotificationHelper
import cvc.dashingdog.pigeonpost.ui.FeedAdapter
import cvc.dashingdog.pigeonpost.worker.WorkScheduler
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: FeedAdapter
    private lateinit var repository: FeedRepository

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op either way */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationHelper.createChannel(this)
        requestNotificationPermissionIfNeeded()

        val api = BloggerApi.create()
        val store = FeedStore(applicationContext)
        repository = FeedRepository(api, store)

        adapter = FeedAdapter(emptyList())
        findViewById<RecyclerView>(R.id.recyclerFeed).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        findViewById<Button>(R.id.buttonFetch).setOnClickListener {
            checkFeed()
        }

        WorkScheduler.schedule(WorkManager.getInstance(applicationContext))
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkFeed() {
        lifecycleScope.launch {
            try {
                val result = repository.checkForUpdates()
                val newTitles = result.newItems.map { it.title }.toSet()
                adapter.submitList(result.filteredItems, newTitles)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}