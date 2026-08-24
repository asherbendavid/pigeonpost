package cvc.dashingdog.pigeonpost.data

class FeedRepository(
    private val api: BloggerApi,
    private val store: FeedStore,
    private val settings: SettingsStore
) {
    data class CheckResult(
        val filteredItems: List<FeedItem>,
        val newItems: List<FeedItem>
    )

    suspend fun checkForUpdates(): CheckResult {
        val response = api.getFeed()
        val fetched = response.feed.entry?.map {
            FeedItem(
                title = it.title.`$t`,
                published = it.published.`$t`,
                link = it.link?.firstOrNull { l -> l.rel == "alternate" }?.href
            )
        } ?: emptyList()

        val excludeKeywords = settings.getExcludeKeywords()
        val filtered = fetched.filter { item ->
            excludeKeywords.none { keyword -> item.title.contains(keyword, ignoreCase = true) }
        }

        val previous = store.loadSavedItems()
        val previousTitles = previous.map { it.title }.toSet()
        val newItems = filtered.filter { it.title !in previousTitles }

        store.saveItems(filtered)

        return CheckResult(filteredItems = filtered, newItems = newItems)
    }
}