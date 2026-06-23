package gitissues.demo.issue

import java.io.Serializable

data class IssueTagId(
    var issueId: Long = 0,
    var tagId: Long = 0,
) : Serializable
