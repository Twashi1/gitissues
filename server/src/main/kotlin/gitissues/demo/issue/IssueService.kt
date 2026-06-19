package gitissues.demo.issue

import gitissues.demo.dto.issue.IssueCreateRequest
import gitissues.demo.dto.issue.IssuePatchRequest
import gitissues.demo.dto.issue.IssueResponse
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class IssueService(
    private val repo: IssueRepository,
) {
    fun all(): List<IssueResponse> = repo.findAll().map(Issue::toResponse)

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
        val issue =
            Issue(
                title = req.title,
                description = req.description,
                status = req.status,
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
        issue.status = req.status ?: issue.status

        return issue.toResponse()
    }
}
