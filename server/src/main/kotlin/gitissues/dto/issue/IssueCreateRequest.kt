package gitissues.dto.issue

data class IssueCreateRequest(
    val title: String,
    val description: String,
)
