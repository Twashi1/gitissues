package gitissues.demo.issue

import gitissues.demo.dto.issue.IssueTagResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/issue-tag")
class IssueTagController(
    private val service: IssueTagService,
) {
    @GetMapping("/issue/{issueId}")
    fun byIssue(
        @PathVariable issueId: Long,
    ): List<IssueTagResponse> = service.allForIssue(issueId)

    @GetMapping("/tag/{tagId}")
    fun byTag(
        @PathVariable tagId: Long,
    ): List<IssueTagResponse> = service.allForTag(tagId)

    @GetMapping("/issue/{issueId}/tag/{tagId}")
    fun get(
        @PathVariable issueId: Long,
        @PathVariable tagId: Long,
    ): IssueTagResponse? = service.get(issueId, tagId)
}
