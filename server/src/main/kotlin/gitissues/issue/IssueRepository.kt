package gitissues.issue

import org.springframework.data.jpa.repository.JpaRepository

interface IssueRepository : JpaRepository<Issue, Long> {
    fun findAllByOrderByIdDesc(): List<Issue>
}
