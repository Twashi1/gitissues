package gitissues.issuelist

import gitissues.dto.issuelist.IssueListCreateRequest
import gitissues.dto.issuelist.IssueListPatchRequest
import gitissues.dto.issuelist.IssueListResponse
import gitissues.issuelist.IssueList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IssueListService(
    private val repository: IssueListRepository,
    private val issueRepository: gitissues.issue.IssueRepository,
) {
    private val log = LoggerFactory.getLogger(IssueListService::class.java)

    fun all(projectId: Long): List<IssueListResponse> = repository.findAllByProjectIdOrderByIdDesc(projectId).map { it.toResponse() }

    fun get(projectId: Long, id: Long): IssueListResponse =
        repository
            .findById(IssueListId(projectId, id))
            .orElseThrow { IllegalArgumentException("IssueList $id not found in project $projectId") }
            .toResponse()

    @Transactional
    fun create(projectId: Long, req: IssueListCreateRequest): IssueListResponse {
        val maxId = repository.findMaxIdByProjectId(projectId)
        val nextId = if (maxId == null) 1L else maxId + 1
        val issueList =
            IssueList(
                projectId = projectId,
                id = nextId,
                title = req.title,
            )
        return repository.save(issueList).toResponse()
    }

    @Transactional
    fun delete(projectId: Long, id: Long) {
        val issueListId = IssueListId(projectId, id)
        if (!repository.existsById(issueListId)) {
            throw IllegalArgumentException("IssueList $id not found in project $projectId")
        }
        // First, remove the listId from all issues that reference this list
        issueRepository.updateListIdToNullByListId(id)
        // Then delete the list
        repository.deleteById(issueListId)
    }

    @Transactional
    fun patch(
        projectId: Long,
        id: Long,
        req: IssueListPatchRequest,
    ): IssueListResponse {
        val issueListId = IssueListId(projectId, id)
        val issueList =
            repository
                .findById(issueListId)
                .orElseThrow { IllegalArgumentException("IssueList $id not found in project $projectId") }

        val updatedTitle = req.title ?: issueList.title
        val updatedList = IssueList(
            projectId = issueList.projectId,
            id = issueList.id,
            title = updatedTitle,
            createdAt = issueList.createdAt
        )
        return repository.save(updatedList).toResponse()
    }
}

private fun IssueList.toResponse(): IssueListResponse = IssueListResponse(id = id, title = title, createdAt = createdAt)
