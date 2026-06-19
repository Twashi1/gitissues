package gitissues.demo.issue

import jakarta.persistence.*

import gitissues.demo.dto.issue.IssueResponse

@Entity
@Table(name = "issues")
class Issue(

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long = 0,

  @Column(nullable = false, length=128)
  var title: String,

  @Column(nullable = false, length=5000)
  var description: String,

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  var status: IssueStatus
)

fun Issue.toResponse() = IssueResponse(
    id = id,
    title = title,
    description = description,
    status = status
)
