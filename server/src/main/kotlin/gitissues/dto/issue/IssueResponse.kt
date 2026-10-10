package gitissues.dto.issue

data class IssueResponse(
    val uuid7: String,
    val title: String,
    val description: String,
    val status: String,
    val listId: Long?,
    val displayOrder: Int = 0,
)