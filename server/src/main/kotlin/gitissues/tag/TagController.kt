package gitissues.tag

import gitissues.dto.tag.TagCreateRequest
import gitissues.dto.tag.TagResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/projects/{projectId}/tag")
class TagController(
    private val service: TagService,
) {
    @GetMapping
    fun all(@PathVariable projectId: Long): List<TagResponse> = service.all(projectId)

    @GetMapping("/{id}")
    fun get(
        @PathVariable projectId: Long,
        @PathVariable id: Long,
    ): TagResponse = service.get(projectId, id)

    @PostMapping
    fun create(
        @PathVariable projectId: Long,
        @RequestBody req: TagCreateRequest,
    ): TagResponse = service.create(projectId, req)

    @PutMapping("/{id}")
    fun update(
        @PathVariable projectId: Long,
        @PathVariable id: Long,
        @RequestBody req: TagCreateRequest,
    ): TagResponse = service.patch(projectId, id, req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable projectId: Long,
        @PathVariable id: Long,
    ) {
        service.delete(projectId, id)
    }
}
