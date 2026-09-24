package gitissues.project

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects")
class ProjectController(
    private val service: ProjectService,
) {

    @GetMapping
    fun all(): List<Project> = service.all()

    @GetMapping("/search")
    fun search(@RequestParam name: String): List<Project> = service.search(name)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Project = service.get(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestParam name: String,
        @RequestParam exportDirectory: String
    ): Project = service.create(name, exportDirectory)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        service.delete(id)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestParam name: String,
        @RequestParam exportDirectory: String
    ): Project = service.update(id, name, exportDirectory)
}