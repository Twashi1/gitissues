package gitissues.issue

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface IssueRepository : JpaRepository<Issue, String> {
    fun findAllByUuid7OrderByUuid7Desc(projectId: Long? = null): List<Issue>

    fun findByProjectIdAndUuid7(projectId: Long, uuid7: String): List<Issue>

    @Query("SELECT MAX(i.uuid7) FROM Issue i WHERE i.projectId = ?1")
    fun findMaxUuid7ByProjectId(projectId: Long): String?

    fun findByUuid7(uuid7: String): Issue?

    @Transactional
    @Modifying
    @Query("update Issue i set i.listId = null where i.listId = ?1")
    fun updateListIdToNullByListId(listId: Long): Int
}
