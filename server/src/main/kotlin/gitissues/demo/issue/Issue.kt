package gitissues.demo.issue

import jakarta.persistence.*

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
