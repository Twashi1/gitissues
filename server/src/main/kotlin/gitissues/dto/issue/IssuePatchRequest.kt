package gitissues.dto.issue

data class IssuePatchRequest(
    val title: String? = null,
    val description: String? = null,
    val status: String? = null,
    val listId: Long? = null,
)
