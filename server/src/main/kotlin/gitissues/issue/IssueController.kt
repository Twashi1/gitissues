package gitissues.issue

import gitissues.dto.issue.IssueCreateRequest
import gitissues.dto.issue.IssuePatchRequest
import gitissues.dto.issue.IssueResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/projects/{projectId}/issue-lists")
class IssueController(
    private val service: IssueService,
) {

    @GetMapping("/{listId}/issues")
    fun allByList(
        @PathVariable projectId: Long,
        @PathVariable listId: Long,
    ): List<IssueResponse> = service.allByProjectIdAndListId(projectId, listId)

    @GetMapping("/{listId}/issues/{uuid7}")
    fun getByList(
        @PathVariable projectId: Long,
        @PathVariable listId: Long,
        @PathVariable uuid7: String,
    ): IssueResponse = service.getByUuid7(projectId, listId, uuid7)

    @PostMapping("/{listId}/issues")
    fun createInList(
        @PathVariable projectId: Long,
        @PathVariable listId: Long,
        @RequestBody req: IssueCreateRequest,
    ): IssueResponse = service.create(projectId, listId, req)

    @PatchMapping("/{listId}/issues/{uuid7}")
    fun patchInList(
        @PathVariable projectId: Long,
        @PathVariable listId: Long,
        @PathVariable uuid7: String,
        @RequestBody req: IssuePatchRequest,
    ): IssueResponse = service.patch(projectId, listId, req)

    @DeleteMapping("/{listId}/issues/{uuid7}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteInList(
        @PathVariable projectId: Long,
        @PathVariable listId: Long,
        @PathVariable uuid7: String,
    ) {
        service.delete(projectId, listId, uuid7)
    }
}