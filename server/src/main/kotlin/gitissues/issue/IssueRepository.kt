package gitissues.issue

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface IssueRepository : JpaRepository<Issue, Long> {
    fun findAllByOrderByIdDesc(): List<Issue>

    fun findByListId(listId: Long): List<Issue>

    @Transactional
    @Modifying
    @Query("update Issue i set i.listId = null where i.listId = ?1")
    fun updateListIdToNullByListId(listId: Long): Int
}
