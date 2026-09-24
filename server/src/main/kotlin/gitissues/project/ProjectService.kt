package gitissues.project

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectService(
    private val repository: ProjectRepository,
) {

    fun all(): List<Project> = repository.findAll()

    fun search(name: String): List<Project> = repository.findByNameContainingIgnoreCase(name)

    @Transactional
    fun create(name: String, exportDirectory: String): Project {
        val project = Project(name = name, exportDirectory = exportDirectory)
        return repository.save(project)
    }

    fun get(id: Long): Project = repository.findById(id)
        .orElseThrow { IllegalArgumentException("Project $id not found") }

    @Transactional
    fun delete(id: Long) {
        if (!repository.existsById(id)) {
            throw IllegalArgumentException("Project $id not found")
        }
        repository.deleteById(id)
    }

    @Transactional
    fun update(id: Long, name: String, exportDirectory: String): Project {
        val project = get(id)
        project.name = name
        project.exportDirectory = exportDirectory
        return repository.save(project)
    }
}