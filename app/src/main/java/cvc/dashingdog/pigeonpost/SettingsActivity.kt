package cvc.dashingdog.pigeonpost

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import cvc.dashingdog.pigeonpost.data.BloggerApi
import cvc.dashingdog.pigeonpost.data.SettingsStore
import cvc.dashingdog.pigeonpost.notification.NotificationHelper
import cvc.dashingdog.pigeonpost.worker.WorkScheduler
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private val intervalOptions = listOf(
        "Every 6 hours" to 6L,
        "Every 12 hours" to 12L,
        "Once a day" to 24L,
        "Every 2 days" to 48L,
        "Once a week" to 168L
    )

    private lateinit var settings: SettingsStore
    private lateinit var spinnerInterval: Spinner
    private lateinit var editKeywords: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settings = SettingsStore(applicationContext)
        spinnerInterval = findViewById(R.id.spinnerInterval)
        editKeywords = findViewById(R.id.editKeywords)

        spinnerInterval.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            intervalOptions.map { it.first }
        )

        loadCurrentSettings()

        findViewById<Button>(R.id.buttonSave).setOnClickListener {
            saveSettings()
        }

        findViewById<Button>(R.id.buttonTestNotification).setOnClickListener {
            testNotification()
        }
    }

    private fun loadCurrentSettings() {
        lifecycleScope.launch {
            val currentHours = settings.getIntervalHours()
            val index = intervalOptions.indexOfFirst { it.second == currentHours }
                .let { if (it == -1) 3 else it }
            spinnerInterval.setSelection(index)

            val keywords = settings.getExcludeKeywords()
            editKeywords.setText(keywords.joinToString(", "))
        }
    }

    private fun saveSettings() {
        lifecycleScope.launch {
            val selectedHours = intervalOptions[spinnerInterval.selectedItemPosition].second
            settings.setIntervalHours(selectedHours)

            val keywords = editKeywords.text.toString()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            settings.setExcludeKeywords(keywords)

            WorkScheduler.schedule(WorkManager.getInstance(applicationContext), selectedHours)

            Toast.makeText(this@SettingsActivity, "Settings saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun testNotification() {
        lifecycleScope.launch {
            try {
                val response = BloggerApi.create().getFeed()
                val latest = response.feed.entry?.firstOrNull()
                if (latest == null) {
                    Toast.makeText(this@SettingsActivity, "No feed entries found", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val item = cvc.dashingdog.pigeonpost.data.FeedItem(
                    title = latest.title.`$t`,
                    published = latest.published.`$t`,
                    link = latest.link?.firstOrNull { it.rel == "alternate" }?.href
                )
                NotificationHelper.notifyNewItems(this@SettingsActivity, listOf(item))
            } catch (e: Exception) {
                Toast.makeText(this@SettingsActivity, "Test failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}