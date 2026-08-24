package cvc.dashingdog.pigeonpost

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op either way */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))

        NotificationHelper.createChannel(this)
        requestNotificationPermissionIfNeeded()

        val api = BloggerApi.create()
        val store = FeedStore(applicationContext)
        repository = FeedRepository(api, store)

        adapter = FeedAdapter(emptyList()) { item ->
            item.link?.let { url ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
        findViewById<RecyclerView>(R.id.recyclerFeed).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener { checkFeed() }

        WorkScheduler.schedule(WorkManager.getInstance(applicationContext))

        checkFeed() // initial load on app open
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
            } finally {
                swipeRefresh.isRefreshing = false
            }
        }
    }
}