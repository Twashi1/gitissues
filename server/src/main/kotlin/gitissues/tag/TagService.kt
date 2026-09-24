package gitissues.tag

import gitissues.dto.tag.TagCreateRequest
import gitissues.dto.tag.TagResponse
import gitissues.tag.Tag
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TagService(
    private val repository: TagRepository,
) {
    private val log = LoggerFactory.getLogger(TagService::class.java)

    fun all(projectId: Long): List<TagResponse> =
        repository.findAllByProjectIdOrderByIdDesc(projectId).map { it.toResponse() }

    fun get(projectId: Long, id: Long): TagResponse =
        repository
            .findById(TagId(projectId, id))
            .orElseThrow { IllegalArgumentException("Tag $id not found in project $projectId") }
            .toResponse()

    @Transactional
    fun create(projectId: Long, req: TagCreateRequest): TagResponse {
        val maxId = repository.findMaxIdByProjectId(projectId)
        val nextId = if (maxId == null) 1L else maxId + 1
        val tag =
            Tag(
                projectId = projectId,
                id = nextId,
                name = req.name,
            )
        return repository.save(tag).toResponse()
    }

    @Transactional
    fun delete(projectId: Long, id: Long) {
        val tagId = TagId(projectId, id)
        if (!repository.existsById(tagId)) {
            throw IllegalArgumentException("Tag $id not found in project $projectId")
        }
        repository.deleteById(tagId)
    }

    @Transactional
    fun patch(
        projectId: Long,
        id: Long,
        req: TagCreateRequest,
    ): TagResponse {
        val tagId = TagId(projectId, id)
        val tag =
            repository
                .findById(tagId)
                .orElseThrow { IllegalArgumentException("Tag $id not found in project $projectId") }

        val updatedName = req.name ?: tag.name
        val updatedTag = Tag(
            projectId = tag.projectId,
            id = tag.id,
            name = updatedName
        )
        return repository.save(updatedTag).toResponse()
    }
}

private fun Tag.toResponse(): TagResponse = TagResponse(id = id, name = name)
