package gitissues.issue

import gitissues.dto.issue.IssueCreateRequest
import gitissues.dto.issue.IssuePatchRequest
import gitissues.dto.issue.IssueResponse
import gitissues.dto.issuelist.IssueListResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
    ): IssueResponse = service.patch(projectId, listId, uuid7, req)

    @DeleteMapping("/{listId}/issues/{uuid7}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteInList(
        @PathVariable projectId: Long,
        @PathVariable listId: Long,
        @PathVariable uuid7: String,
    ) {
        service.delete(projectId, listId, uuid7)
    }

    /** Reorder displayOrders in a list to be sequential (1, 2, 3, ...).
     *  Useful when displayOrders have become fragmented or duplicated.
     *
     *  If orderAfter is provided, only issues with displayOrder >= orderAfter
     *  will be re-assigned sequentially; issues with displayOrder < orderAfter
     *  keep their current displayOrder.
     */
    @PatchMapping("/{listId}/issues/reorder")
    fun reorderDisplayOrders(
        @PathVariable projectId: Long,
        @PathVariable listId: Long,
        @RequestParam(required = false) orderAfter: Integer?,
    ): Unit = service.reorderDisplayOrders(projectId, listId, orderAfter)

    /** Reorder an issue within a list by moving it from sourceIndex to targetIndex.
     *  This reassigns all displayOrders sequentially (1, 2, 3, ...) after the move.
     */
    @PatchMapping("/{listId}/issues/reorder-within")
    fun reorderIssueWithinList(
        @PathVariable projectId: Long,
        @PathVariable listId: Long,
        @RequestBody request: ReorderWithinRequest
    ): Unit = service.reorderIssueWithinList(projectId, listId, request.sourceIndex, request.targetIndex)

    /** Move an issue from one list to another by specifying the source and target indices.
     *  This reassigns all displayOrders sequentially (1, 2, 3, ...) in both lists after the move.
     */
    @PatchMapping("/{listId}/issues/move-from")
    fun moveIssueToList(
        @PathVariable projectId: Long,
        @PathVariable listId: Long, // This is the target list ID
        @RequestBody request: MoveFromRequest
    ): Unit = service.moveIssueToList(
        projectId,
        request.sourceListId,
        listId, // target list ID
        request.sourceIndex,
        request.targetIndex
    )
}

// Data transfer objects for the new endpoints
data class ReorderWithinRequest(
    val sourceIndex: Int,
    val targetIndex: Int
)

data class MoveFromRequest(
    val sourceListId: Long,
    val sourceIndex: Int,
    val targetIndex: Int
)
