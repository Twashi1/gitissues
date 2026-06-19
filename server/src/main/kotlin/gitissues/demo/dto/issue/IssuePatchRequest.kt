package gitissues.demo.dto.issue

import gitissues.demo.issue.IssueStatus

data class IssuePatchRequest(
    val title: String? = null,
    val description: String? = null,
    val status: IssueStatus? = null,
)
