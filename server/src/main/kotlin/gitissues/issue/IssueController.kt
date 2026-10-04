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
@RequestMapping("/api/issues")
class IssueController(
    private val service: IssueService,
    private val importService: IssueImportService,
    private val exportService: IssueExportService,
) {
    @GetMapping
    fun all(
        @RequestParam(required = false) uuid7: String?,
    ): List<IssueResponse> = if (uuid7 != null) service.getByUuid7(1L, uuid7) else service.all(1L)

    @GetMapping("/{uuid7}")
    fun get(
        @PathVariable uuid7: String,
    ): IssueResponse = service.get(uuid7)

    @PostMapping
    fun create(
        @RequestBody req: IssueCreateRequest,
    ): IssueResponse = service.create(1L, req)

    @PostMapping("/loadIFF")
    fun loadIFF(
        @RequestParam projectId: Long,
        @RequestParam path: String,
    ): List<IssueResponse> = importService.loadIFF(projectId, path)

    @PostMapping("/exportIFF")
    fun exportIFF(
        @RequestParam projectId: Long,
        @RequestParam path: String,
    ): Boolean = exportService.exportIFF(projectId, path)

    @DeleteMapping("/{uuid7}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable uuid7: String,
    ) {
        service.delete(uuid7)
    }

    @PatchMapping("/{uuid7}")
    fun patch(
        @PathVariable uuid7: String,
        @RequestBody req: IssuePatchRequest,
    ): IssueResponse = service.patch(req)
}
