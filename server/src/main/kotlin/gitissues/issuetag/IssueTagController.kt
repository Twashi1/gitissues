package gitissues.issuetag

import gitissues.dto.issuetag.IssueTagResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/projects/{projectId}/issue-tag")
class IssueTagController(
    private val service: IssueTagService,
) {
    @GetMapping("/issue/{issueId}")
    fun byIssue(
        @PathVariable projectId: Long,
        @PathVariable issueId: Long,
    ): List<IssueTagResponse> = service.allForIssue(projectId, issueId)

    @GetMapping("/tag/{tagId}")
    fun byTag(
        @PathVariable projectId: Long,
        @PathVariable tagId: Long,
    ): List<IssueTagResponse> = service.allForTag(projectId, tagId)

    @GetMapping("/issue/{issueId}/tag/{tagId}")
    fun get(
        @PathVariable projectId: Long,
        @PathVariable issueId: Long,
        @PathVariable tagId: Long,
    ): IssueTagResponse? = service.get(projectId, issueId, tagId)
}
