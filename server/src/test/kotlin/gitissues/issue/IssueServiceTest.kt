package gitissues.issue

import gitissues.dto.issue.IssueCreateRequest
import gitissues.dto.issue.IssuePatchRequest
import gitissues.dto.issue.IssueResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@ExtendWith(MockitoExtension::class)
class IssueServiceTest {
    @Mock
    private lateinit var repo: IssueRepository

    @Mock
    private lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMocks
    private lateinit var service: IssueService

    private val testIssueId = IssueId(1L, 1L)
    private val testIssue =
        Issue(
            projectId = 1L,
            id = 1L,
            title = "Test Issue",
            description = "Test Description",
            status = "open",
            listId = 1L,
        )

    private val testIssueResponse =
        IssueResponse(
            id = 1L,
            title = "Test Issue",
            description = "Test Description",
            status = "open",
            listId = 1L,
        )

    @BeforeEach
    fun setup() {
        reset(repo)
    }

    @Test
    fun `test all returns all issues`() {
        // Arrange
        whenever(repo.findAllByProjectIdOrderByIdDesc(1L)).thenReturn(listOf(testIssue))

        // Act
        val result = service.all(1L)

        // Assert
        assertEquals(1, result.size)
        assertEquals(testIssueResponse, result[0])
        verify(repo).findAllByProjectIdOrderByIdDesc(1L)
    }

    @Test
    fun `test getByListId returns issues for list`() {
        // Arrange
        whenever(repo.findByProjectIdAndListId(1L, 1L)).thenReturn(listOf(testIssue))

        // Act
        val result = service.getByListId(1L, 1L)

        // Assert
        assertEquals(1, result.size)
        assertEquals(testIssueResponse, result[0])
        verify(repo).findByProjectIdAndListId(1L, 1L)
    }

    @Test
    fun `test get returns issue when found`() {
        // Arrange
        whenever(repo.findById(testIssueId)).thenReturn(java.util.Optional.of(testIssue))

        // Act
        val result = service.get(1L, 1L)

        // Assert
        assertEquals(testIssueResponse, result)
        verify(repo).findById(testIssueId)
    }

    @Test
    fun `test get throws exception when issue not found`() {
        // Arrange
        val nonExistentId = IssueId(1L, 999L)
        whenever(repo.findById(nonExistentId)).thenReturn(java.util.Optional.empty())

        // Act & Assert
        val exception =
            assertThrows(ResponseStatusException::class.java) {
                service.get(1L, 999L)
            }
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason?.contains("Issue 999 not found") == true)
        verify(repo).findById(nonExistentId)
    }

    @Test
    fun `test create creates and returns issue`() {
        // Arrange
        val createRequest =
            IssueCreateRequest(
                title = "New Issue",
                description = "New Description",
                status = "open",
                listId = 1L,
            )
        val issueToSave =
            Issue(
                projectId = 1L,
                id = 0L,
                title = "New Issue",
                description = "New Description",
                status = "open",
                listId = 1L,
            )
        val savedIssue =
            Issue(
                projectId = 1L,
                id = 1L,
                title = "New Issue",
                description = "New Description",
                status = "open",
                listId = 1L,
            )
        val expectedResponse =
            IssueResponse(
                id = 1L,
                title = "New Issue",
                description = "New Description",
                status = "open",
                listId = 1L,
            )

        whenever(repo.save(any())).thenAnswer { invocation ->
            val issue = invocation.getArgument(0) as Issue
            // Create a new instance with the ID set since id is val in the entity
            Issue(
                projectId = issue.projectId,
                id = 1L,
                title = issue.title,
                description = issue.description,
                status = issue.status,
                listId = issue.listId,
            )
        }

        // Act
        val result = service.create(1L, createRequest)

        // Assert
        assertEquals(expectedResponse, result)
        verify(repo).save(any())
    }

    @Test
    fun `test create handles null listId`() {
        // Arrange
        val createRequest =
            IssueCreateRequest(
                title = "New Issue",
                description = "New Description",
                status = "open",
                listId = null,
            )
        val issueToSave =
            Issue(
                projectId = 1L,
                id = 0L,
                title = "New Issue",
                description = "New Description",
                status = "open",
                listId = null,
            )
        val savedIssue =
            Issue(
                projectId = 1L,
                id = 1L,
                title = "New Issue",
                description = "New Description",
                status = "open",
                listId = null,
            )
        val expectedResponse =
            IssueResponse(
                id = 1L,
                title = "New Issue",
                description = "New Description",
                status = "open",
                listId = null,
            )

        whenever(repo.save(any())).thenAnswer { invocation ->
            val issue = invocation.getArgument(0) as Issue
            // Create a new instance with the ID set since id is val in the entity
            Issue(
                projectId = issue.projectId,
                id = 1L,
                title = issue.title,
                description = issue.description,
                status = issue.status,
                listId = issue.listId,
            )
        }

        // Act
        val result = service.create(1L, createRequest)

        // Assert
        assertEquals(expectedResponse, result)
        verify(repo).save(any())
    }

    @Test
    fun `test delete calls repository when issue exists`() {
        // Arrange
        val issueId = IssueId(1L, 1L)
        whenever(repo.existsById(issueId)).thenReturn(true)
        whenever(repo.findById(issueId)).thenReturn(java.util.Optional.of(testIssue))

        // Act
        service.delete(1L, 1L)

        // Assert
        verify(repo).existsById(issueId)
        verify(repo).findById(issueId)
        verify(repo).deleteById(issueId)
    }

    @Test
    fun `test delete throws exception when issue not found`() {
        // Arrange
        val nonExistentId = IssueId(1L, 999L)
        whenever(repo.existsById(nonExistentId)).thenReturn(false)

        // Act & Assert
        val exception =
            assertThrows(NoSuchElementException::class.java) {
                service.delete(1L, 999L)
            }
        assertNotNull(exception.message)
        assertTrue(exception.message!!.contains("Issue 999 not found"))
        verify(repo).existsById(nonExistentId)
        verify(repo, never()).deleteById(any())
    }

    @Test
    fun `test patch updates issue when found`() {
        // Arrange
        val patchRequest =
            IssuePatchRequest(
                title = "Updated Title",
                description = null,
                status = "in progress",
                listId = 2L,
            )
        val issueToUpdate =
            Issue(
                projectId = 1L,
                id = 1L,
                title = "Original Title",
                description = "Original Description",
                status = "open",
                listId = 1L,
            )
        val updatedIssue =
            Issue(
                projectId = 1L,
                id = 1L,
                title = "Updated Title",
                description = "Original Description",
                status = "in progress",
                listId = 2L,
            )
        val expectedResponse =
            IssueResponse(
                id = 1L,
                title = "Updated Title",
                description = "Original Description",
                status = "in progress",
                listId = 2L,
            )

        val issueId = IssueId(1L, 1L)
        whenever(repo.findById(issueId)).thenReturn(java.util.Optional.of(issueToUpdate))
        whenever(repo.save(any())).thenAnswer { invocation ->
            val issue = invocation.getArgument(0) as Issue
            // Return the issue with any modifications applied
            issue
        }

        // Act
        val result = service.patch(1L, 1L, patchRequest)

        // Assert
        assertEquals(expectedResponse, result)
        verify(repo).findById(issueId)
        verify(repo).save(any())
    }

    @Test
    fun `test patch throws exception when issue not found`() {
        // Arrange
        val patchRequest =
            IssuePatchRequest(
                title = "Updated Title",
                description = null,
                status = null,
                listId = null,
            )
        val nonExistentId = IssueId(1L, 999L)
        whenever(repo.findById(nonExistentId)).thenReturn(java.util.Optional.empty())

        // Act & Assert
        val exception =
            assertThrows(ResponseStatusException::class.java) {
                service.patch(1L, 999L, patchRequest)
            }
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason?.contains("Issue 999 not found") == true)
        verify(repo).findById(nonExistentId)
    }
}
