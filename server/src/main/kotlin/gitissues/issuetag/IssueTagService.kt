package gitissues.issuetag

import gitissues.dto.issuetag.IssueTagResponse
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class IssueTagService(
    private val repo: IssueTagRepository,
    private val objectMapper: ObjectMapper,
) {
    fun allForIssue(issueId: Long): List<IssueTagResponse> =
        repo
            .findAllByIssueId(issueId)
            .map { it.toResponse(objectMapper) }

    fun allForTag(tagId: Long): List<IssueTagResponse> =
        repo
            .findAllByTagId(tagId)
            .map { it.toResponse(objectMapper) }

    fun get(
        issueId: Long,
        tagId: Long,
    ): IssueTagResponse? =
        repo
            .findByIssueIdAndTagId(issueId, tagId)
            ?.toResponse(objectMapper)
}
