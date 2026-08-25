package gitissues.issue

import gitissues.dto.issue.IssueCreateRequest
import gitissues.dto.issue.IssuePatchRequest
import gitissues.dto.issue.IssueResponse
import gitissues.issuelist.IssueList
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class IssueService(
    private val repo: IssueRepository,
) {
    private val log = LoggerFactory.getLogger(IssueService::class.java)

    fun all(): List<IssueResponse> = repo.findAllByOrderByIdDesc().map(Issue::toResponse)

    fun getByListId(listId: Long): List<IssueResponse> = repo.findByListId(listId).map { it.toResponse() }

    fun get(id: Long): IssueResponse =
        repo
            .findById(id)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Issue $id not found",
                )
            }.toResponse()

    fun create(req: IssueCreateRequest): IssueResponse {
        val issue = Issue(
            title = req.title,
            description = req.description,
            status = req.status,
            listId = if (req.listId > 0) req.listId else null
        )
        return repo.save(issue).toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        if (!repo.existsById(id)) {
            throw NoSuchElementException("Issue $id not found")
        }

        repo.deleteById(id)
    }

    @Transactional
    fun patch(
        id: Long,
        req: IssuePatchRequest,
    ): IssueResponse {
        log.info("Patch request: {}", req)

        val issue =
            repo
                .findById(id)
                .orElseThrow {
                    ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Issue $id not found",
                    )
                }

        issue.title = req.title ?: issue.title
        issue.description = req.description ?: issue.description

        return issue.toResponse()
    }
}
