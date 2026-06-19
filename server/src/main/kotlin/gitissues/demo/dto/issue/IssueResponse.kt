package gitissues.demo.dto.issue

import gitissues.demo.issue.IssueStatus

data class IssueResponse(
    val id: Long,
    val title: String,
    val description: String,
    val status: IssueStatus,
)
