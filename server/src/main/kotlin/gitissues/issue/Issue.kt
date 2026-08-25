package gitissues.issue

import jakarta.persistence.*
import gitissues.dto.issue.IssueResponse

@Entity
@Table(name = "issues")
class Issue(

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, length = 128)
    var title: String,

    @Column(nullable = false, length = 5000)
    var description: String,

    @Column(nullable = false, length = 50)
    var status: String,

    @Column(name = "list_id")
    var listId: Long? = null

) {

    fun toResponse() = IssueResponse(
        id = id,
        title = title,
        description = description,
        status = status,
    )
}
