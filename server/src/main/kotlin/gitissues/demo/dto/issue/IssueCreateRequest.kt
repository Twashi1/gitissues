package gitissues.demo.dto.issue

import gitissues.demo.issue.IssueStatus

data class IssueCreateRequest(
    val title: String,
    val description: String,
    val status: IssueStatus,
)
