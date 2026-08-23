package cvc.dashingdog.pigeonpost.data

data class BloggerFeedResponse(val feed: Feed)
data class Feed(val entry: List<Entry>?)
data class Entry(
    val title: TextField,
    val published: TextField,
    val link: List<LinkField>?
)
data class TextField(val `$t`: String)
data class LinkField(val rel: String, val href: String)