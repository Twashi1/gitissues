package gitissues.issue

import gitissues.dto.issue.IssueCreateRequest
import gitissues.dto.issue.IssuePatchRequest
import gitissues.dto.issue.IssueResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/projects/{projectId}/issue")
class IssueController(
    private val service: IssueService,
) {
    @GetMapping
    fun all(
        @PathVariable projectId: Long,
        @RequestParam(required = false) listId: Long?,
    ): List<IssueResponse> = if (listId != null) service.getByListId(projectId, listId!!) else service.all(projectId)

    @GetMapping("/{id}")
    fun get(
        @PathVariable projectId: Long,
        @PathVariable id: Long,
    ): IssueResponse = service.get(projectId, id)

    @PostMapping
    fun create(
        @PathVariable projectId: Long,
        @RequestBody req: IssueCreateRequest,
    ): IssueResponse = service.create(projectId, req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable projectId: Long,
        @PathVariable id: Long,
    ) {
        service.delete(projectId, id)
    }

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable projectId: Long,
        @PathVariable id: Long,
        @RequestBody req: IssuePatchRequest,
    ): IssueResponse = service.patch(projectId, id, req)
}
