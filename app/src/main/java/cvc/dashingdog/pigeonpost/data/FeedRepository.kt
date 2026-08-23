package cvc.dashingdog.pigeonpost.data

class FeedRepository(
    private val api: BloggerApi,
    private val store: FeedStore
) {
    fun isRelevant(title: String): Boolean =
        !title.contains("Canary", ignoreCase = true)

    /** Result of a single check: full filtered list, plus which entries are new since last save. */
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

        val filtered = fetched.filter { isRelevant(it.title) }

        val previous = store.loadSavedItems()
        val previousTitles = previous.map { it.title }.toSet()
        val newItems = filtered.filter { it.title !in previousTitles }

        store.saveItems(filtered)

        return CheckResult(filteredItems = filtered, newItems = newItems)
    }
}