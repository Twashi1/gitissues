package gitissues.issuelist

import gitissues.dto.issuelist.IssueListCreateRequest
import gitissues.dto.issuelist.IssueListResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IssueListService(
    private val repository: IssueListRepository,
) {
    private val log = LoggerFactory.getLogger(IssueListService::class.java)

    fun all(): List<IssueListResponse> = repository.findAll().map { it.toResponse() }

    fun get(id: Long): IssueListResponse =
        repository
            .findById(id)
            .orElseThrow { IllegalArgumentException("IssueList $id not found") }
            .toResponse()

    @Transactional
    fun create(req: IssueListCreateRequest): IssueListResponse {
        val issueList = IssueList(
            title = req.title
        )
        return repository.save(issueList).toResponse()
    }

    @Transactional
    fun update(id: Long, req: IssueListCreateRequest): IssueListResponse {
        val issueList =
            repository
                .findById(id)
                .orElseThrow { IllegalArgumentException("IssueList $id not found") }

        val updatedList = issueList.copy(title = req.title)
        return repository.save(updatedList).toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw IllegalArgumentException("IssueList $id not found")
        }
        repository.deleteById(id)
    }
}

private fun IssueList.toResponse(): IssueListResponse = IssueListResponse(
    id = id,
    title = title,
    createdAt = createdAt
)