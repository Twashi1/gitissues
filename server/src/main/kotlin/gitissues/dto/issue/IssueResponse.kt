package gitissues.dto.issue

data class IssueResponse(
    val id: Long,
    val title: String,
    val description: String,
)
