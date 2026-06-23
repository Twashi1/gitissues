package gitissues.dto.issuetag

import tools.jackson.databind.JsonNode

data class IssueTagResponse(
    val issueId: Long,
    val tagId: Long,
    val value: JsonNode?,
)
