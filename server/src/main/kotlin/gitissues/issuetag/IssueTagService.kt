package gitissues.issuetag

import gitissues.dto.issuetag.IssueTagResponse
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class IssueTagService(
    private val repo: IssueTagRepository,
    private val objectMapper: ObjectMapper,
) {
    fun allForIssue(
        projectId: Long,
        issueId: Long,
    ): List<IssueTagResponse> =
        repo
            .findAllByProjectIdAndIssueId(projectId, issueId)
            .map { it.toResponse(objectMapper) }

    fun allForTag(
        projectId: Long,
        tagId: Long,
    ): List<IssueTagResponse> =
        repo
            .findAllByProjectIdAndTagId(projectId, tagId)
            .map { it.toResponse(objectMapper) }

    fun get(
        projectId: Long,
        issueId: Long,
        tagId: Long,
    ): IssueTagResponse? =
        repo
            .findByProjectIdAndIssueIdAndTagId(projectId, issueId, tagId)
            ?.toResponse(objectMapper)
}
