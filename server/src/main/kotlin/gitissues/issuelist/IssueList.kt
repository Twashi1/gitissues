package gitissues.issuelist

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

@Entity
@Table(name = "issue_lists", uniqueConstraints = [UniqueConstraint(columnNames = ["project_id", "title"])])
@IdClass(IssueListId::class)
class IssueList(
    @Id
    @Column(name = "project_id", nullable = false)
    var projectId: Long = 0L,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0L,

    @Column(nullable = false)
    var title: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
)
