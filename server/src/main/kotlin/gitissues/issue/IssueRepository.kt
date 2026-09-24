package gitissues.issue

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface IssueRepository : JpaRepository<Issue, IssueId> {
    fun findAllByProjectIdOrderByIdDesc(projectId: Long): List<Issue>

    fun findByProjectIdAndListId(projectId: Long, listId: Long): List<Issue>

    @Query("SELECT MAX(i.id) FROM Issue i WHERE i.projectId = ?1")
    fun findMaxIdByProjectId(projectId: Long): Long?

    @Transactional
    @Modifying
    @Query("update Issue i set i.listId = null where i.listId = ?1")
    fun updateListIdToNullByListId(listId: Long): Int
}
