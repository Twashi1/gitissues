package gitissues.issue

import gitissues.dto.issue.IssueCreateRequest
import gitissues.dto.issue.IssuePatchRequest
import gitissues.dto.issue.IssueResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@ExtendWith(MockitoExtension::class)
class IssueServiceTest {

    @Mock
    private lateinit var repo: IssueRepository

    @InjectMocks
    private lateinit var service: IssueService

    private val testIssue = Issue(
        id = 1L,
        title = "Test Issue",
        description = "Test Description",
        status = "open",
        listId = 1L
    )

    private val testIssueResponse = IssueResponse(
        id = 1L,
        title = "Test Issue",
        description = "Test Description",
        status = "open",
        listId = 1L
    )

    @BeforeEach
    fun setup() {
        reset(repo)
    }

    @Test
    fun `test all returns all issues`() {
        // Arrange
        whenever(repo.findAllByOrderByIdDesc()).thenReturn(listOf(testIssue))

        // Act
        val result = service.all()

        // Assert
        assertEquals(1, result.size)
        assertEquals(testIssueResponse, result[0])
        verify(repo).findAllByOrderByIdDesc()
    }

    @Test
    fun `test getByListId returns issues for list`() {
        // Arrange
        whenever(repo.findByListId(1L)).thenReturn(listOf(testIssue))

        // Act
        val result = service.getByListId(1L)

        // Assert
        assertEquals(1, result.size)
        assertEquals(testIssueResponse, result[0])
        verify(repo).findByListId(1L)
    }

    @Test
    fun `test get returns issue when found`() {
        // Arrange
        whenever(repo.findById(1L)).thenReturn(java.util.Optional.of(testIssue))

        // Act
        val result = service.get(1L)

        // Assert
        assertEquals(testIssueResponse, result)
        verify(repo).findById(1L)
    }

    @Test
    fun `test get throws exception when issue not found`() {
        // Arrange
        whenever(repo.findById(999L)).thenReturn(java.util.Optional.empty())

        // Act & Assert
        val exception = assertThrows(ResponseStatusException::class.java) {
            service.get(999L)
        }
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason?.contains("Issue 999 not found") == true)
        verify(repo).findById(999L)
    }

    @Test
    fun `test create creates and returns issue`() {
        // Arrange
        val createRequest = IssueCreateRequest(
            title = "New Issue",
            description = "New Description",
            status = "open",
            listId = 1L
        )
        val issueToSave = Issue(
            id = 0L,
            title = "New Issue",
            description = "New Description",
            status = "open",
            listId = 1L
        )
        val savedIssue = Issue(
            id = 1L,
            title = "New Issue",
            description = "New Description",
            status = "open",
            listId = 1L
        )
        val expectedResponse = IssueResponse(
            id = 1L,
            title = "New Issue",
            description = "New Description",
            status = "open",
            listId = 1L
        )

        whenever(repo.save(any())).thenAnswer { invocation ->
            val issue = invocation.getArgument(0) as Issue
            // Create a new instance with the ID set since id is val in the entity
            Issue(
                id = 1L,
                title = issue.title,
                description = issue.description,
                status = issue.status,
                listId = issue.listId
            )
        }

        // Act
        val result = service.create(createRequest)

        // Assert
        assertEquals(expectedResponse, result)
        verify(repo).save(any())
    }

    @Test
    fun `test create handles null listId`() {
        // Arrange
        val createRequest = IssueCreateRequest(
            title = "New Issue",
            description = "New Description",
            status = "open",
            listId = null
        )
        val issueToSave = Issue(
            id = 0L,
            title = "New Issue",
            description = "New Description",
            status = "open",
            listId = null
        )
        val savedIssue = Issue(
            id = 1L,
            title = "New Issue",
            description = "New Description",
            status = "open",
            listId = null
        )
        val expectedResponse = IssueResponse(
            id = 1L,
            title = "New Issue",
            description = "New Description",
            status = "open",
            listId = null
        )

        whenever(repo.save(any())).thenAnswer { invocation ->
            val issue = invocation.getArgument(0) as Issue
            // Create a new instance with the ID set since id is val in the entity
            Issue(
                id = 1L,
                title = issue.title,
                description = issue.description,
                status = issue.status,
                listId = issue.listId
            )
        }

        // Act
        val result = service.create(createRequest)

        // Assert
        assertEquals(expectedResponse, result)
        verify(repo).save(any())
    }

    @Test
    fun `test delete calls repository when issue exists`() {
        // Arrange
        whenever(repo.existsById(1L)).thenReturn(true)

        // Act
        service.delete(1L)

        // Assert
        verify(repo).existsById(1L)
        verify(repo).deleteById(1L)
    }

    @Test
    fun `test delete throws exception when issue not found`() {
        // Arrange
        whenever(repo.existsById(999L)).thenReturn(false)

        // Act & Assert
        val exception = assertThrows(NoSuchElementException::class.java) {
            service.delete(999L)
        }
        assertNotNull(exception.message)
        assertTrue(exception.message!!.contains("Issue 999 not found"))
        verify(repo).existsById(999L)
        verify(repo, never()).deleteById(any())
    }

    @Test
    fun `test patch updates issue when found`() {
        // Arrange
        val patchRequest = IssuePatchRequest(
            title = "Updated Title",
            description = null,
            status = "in progress",
            listId = 2L
        )
        val issueToUpdate = Issue(
            id = 1L,
            title = "Original Title",
            description = "Original Description",
            status = "open",
            listId = 1L
        )
        val updatedIssue = Issue(
            id = 1L,
            title = "Updated Title",
            description = "Original Description",
            status = "in progress",
            listId = 2L
        )
        val expectedResponse = IssueResponse(
            id = 1L,
            title = "Updated Title",
            description = "Original Description",
            status = "in progress",
            listId = 2L
        )

        whenever(repo.findById(1L)).thenReturn(java.util.Optional.of(issueToUpdate))

        // Act
        val result = service.patch(1L, patchRequest)

        // Assert
        assertEquals(expectedResponse, result)
        verify(repo).findById(1L)
        // Note: We don't verify save() because the service modifies the entity directly
        // and Spring Data JPA will auto-flush at transaction completion
    }

    @Test
    fun `test patch throws exception when issue not found`() {
        // Arrange
        val patchRequest = IssuePatchRequest(
            title = "Updated Title",
            description = null,
            status = null,
            listId = null
        )
        whenever(repo.findById(999L)).thenReturn(java.util.Optional.empty())

        // Act & Assert
        val exception = assertThrows(ResponseStatusException::class.java) {
            service.patch(999L, patchRequest)
        }
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason?.contains("Issue 999 not found") == true)
        verify(repo).findById(999L)
    }
}
