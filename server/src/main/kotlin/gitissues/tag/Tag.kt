package gitissues.tag

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(name = "tags", uniqueConstraints = [UniqueConstraint(columnNames = ["project_id", "name"])])
@IdClass(TagId::class)
class Tag(
    @Id
    @Column(name = "project_id", nullable = false)
    var projectId: Long = 0L,
    @Id
    @Column(name = "id", nullable = false)
    var id: Long = 0L,
    @Column(nullable = false, length = 128)
    var name: String,
)
