package gitissues.issuelist

import gitissues.dto.issuelist.IssueListCreateRequest
import gitissues.dto.issuelist.IssueListPatchRequest
import gitissues.dto.issuelist.IssueListResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/issue-lists")
class IssueListController(
    private val service: IssueListService,
) {
    @GetMapping
    fun all(): List<IssueListResponse> = service.all()

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
    ): IssueListResponse = service.get(id)

    @PostMapping
    fun create(
        @RequestBody req: IssueListCreateRequest,
    ): IssueListResponse = service.create(req)

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: Long,
        @RequestBody req: IssueListPatchRequest,
    ): IssueListResponse = service.patch(id, req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: Long,
    ) {
        service.delete(id)
    }
}
