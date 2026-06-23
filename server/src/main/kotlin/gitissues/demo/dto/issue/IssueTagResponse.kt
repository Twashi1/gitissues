package gitissues.demo.dto.issue

import tools.jackson.databind.JsonNode

data class IssueTagResponse(
    val issueId: Long,
    val tagId: Long,
    val value: JsonNode?,
)
