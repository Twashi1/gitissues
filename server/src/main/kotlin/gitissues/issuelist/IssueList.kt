package gitissues.issuelist

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "issue_lists")
data class IssueList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val title: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)