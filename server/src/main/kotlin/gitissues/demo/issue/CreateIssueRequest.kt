package gitissues.demo.issue

data class CreateIssueRequest(
    val title: String,
    val description: String,
    val status: IssueStatus,
)
