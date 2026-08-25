package gitissues.dto.issuelist

import java.time.LocalDateTime

data class IssueListResponse(
    val id: Long,
    val title: String,
    val createdAt: LocalDateTime,
)
