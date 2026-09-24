package gitissues.tag

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : JpaRepository<Tag, TagId> {
    fun findAllByProjectIdOrderByIdDesc(projectId: Long): List<Tag>

    @Query("SELECT MAX(t.id) FROM Tag t WHERE t.projectId = ?1")
    fun findMaxIdByProjectId(projectId: Long): Long?
}
