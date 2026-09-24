package gitissues.issuelist

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IssueListRepository : JpaRepository<IssueList, IssueListId> {
    fun findAllByProjectIdOrderByIdDesc(projectId: Long): List<IssueList>

    @Query("SELECT MAX(i.id) FROM IssueList i WHERE i.projectId = ?1")
    fun findMaxIdByProjectId(projectId: Long): Long?
}
