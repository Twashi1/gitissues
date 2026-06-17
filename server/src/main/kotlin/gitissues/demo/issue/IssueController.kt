package gitissues.demo.issue

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/issues")
class IssueController(
    private val repo: IssueRepository
) {

    @PostMapping
    fun create(@RequestBody req: CreateIssueRequest): CreateIssueResponse {
        val databaseObject: Issue = repo.save(
            Issue(
                title = req.title,
                description = req.description,
                status = req.status
            )
        )
        
        return CreateIssueResponse(
          title = databaseObject.title,
          description = databaseObject.description,
          status = databaseObject.status
        )
    }

    @GetMapping
    fun all(): List<Issue> = repo.findAll()
}
