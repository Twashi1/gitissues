package gitissues.demo.issue

import gitissues.demo.dto.issue.IssueCreateRequest
import gitissues.demo.dto.issue.IssuePatchRequest
import gitissues.demo.dto.issue.IssueResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/issue")
class IssueController(
    private val service: IssueService,
) {
    @GetMapping
    fun all(): List<IssueResponse> = service.all()

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
    ): IssueResponse = service.get(id)

    @PostMapping
    fun create(
        @RequestBody req: IssueCreateRequest,
    ): IssueResponse = service.create(req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: Long,
    ) {
        service.delete(id)
    }

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: Long,
        @RequestBody req: IssuePatchRequest,
    ): IssueResponse = service.patch(id, req)
}
