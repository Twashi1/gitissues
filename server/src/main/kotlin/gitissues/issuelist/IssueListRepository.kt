package gitissues.issuelist

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IssueListRepository : JpaRepository<IssueList, Long>