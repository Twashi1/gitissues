package gitissues.demo.issue

data class IssueResponse(
    val id: Long,
    val title: String,
    val description: String,
    val status: IssueStatus,
)
