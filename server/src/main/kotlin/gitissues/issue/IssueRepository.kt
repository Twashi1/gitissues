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

    @Query("SELECT MAX(i.displayOrder) FROM Issue i WHERE i.projectId = ?1 AND (i.listId = ?2 OR (?2 IS NULL AND i.listId IS NULL))")
    fun findMaxDisplayOrderByListId(projectId: Long, listId: Long?): Int?

    @Query("SELECT i FROM Issue i WHERE i.projectId = ?1 AND (i.listId = ?2 OR (?2 IS NULL AND i.listId IS NULL)) ORDER BY i.displayOrder ASC")
    fun findAllByProjectIdAndListIdOrderByDisplayOrderAsc(projectId: Long, listId: Long?): List<Issue>

    fun findByProjectIdAndListIdAndUuid7(projectId: Long, listId: Long?, uuid7: String): List<Issue>
}