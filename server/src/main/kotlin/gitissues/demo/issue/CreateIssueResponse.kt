package gitissues.demo.issue

data class CreateIssueResponse(
    val title: String,
    val description: String,
    val status: IssueStatus,
)
