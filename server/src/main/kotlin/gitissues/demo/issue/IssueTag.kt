package gitissues.demo.issue

import jakarta.persistence.*
import gitissues.demo.dto.issue.IssueTagResponse
import gitissues.demo.issue.IssueTagId
import tools.jackson.databind.ObjectMapper

@Entity
@Table(name = "issue_tags")
@IdClass(IssueTagId::class)
class IssueTag(
    @Id
    @Column(name = "issue_id")
    var issueId: Long = 0,

    @Id
    @Column(name = "tag_id")
    var tagId: Long = 0,

    @Column(columnDefinition = "jsonb")
    var value: String? = null,
)

// TODO: move to controller; keep separate from entity
fun IssueTag.toResponse(mapper: ObjectMapper): IssueTagResponse {
  val jsonValue = try {
    if (value != null) {
      mapper.readTree(value)
    } else {
      null
    }
  } catch (e: Exception) {
    throw RuntimeException("Invalid JSON for issueid=$issueId, tagId=$tagId")
  }

  return IssueTagResponse(
    issueId = issueId,
    tagId = tagId,
    value = jsonValue
  )
}
