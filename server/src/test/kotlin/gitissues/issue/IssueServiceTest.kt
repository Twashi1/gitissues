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

    private val testUuid7 = "a1b2c3d4-e5f6-0708-090a-0b0c0d0e0f11"

    private val savedIssue = Issue(
        uuid7 = testUuid7,
        title = "Test Issue",
        description = "Test Description",
        status = "open",
        listId = 1L,
        projectId = 1L,
    )
    private val testIssue =
        Issue(
            uuid7 = testUuid7,
            title = "Test Issue",
            description = "Test Description",
            status = "open",
            listId = 1L,
            projectId = 1L,
        )

    private val testIssueResponse =
        IssueResponse(
            uuid7 = testUuid7,
            title = "Test Issue",
            description = "Test Description",
            status = "open",
            listId = 1,
        )

    
    @Test
    fun `test all returns all issues`() {
        // Arrange
        whenever(repo.findAllByUuid7OrderByUuid7Desc(1L)).thenReturn(listOf(testIssue))

        // Act
        val result = service.all(1L)

        // Assert
        assertEquals(1, result.size)
        assertEquals(testIssueResponse, result[0])
        verify(repo).findAllByUuid7OrderByUuid7Desc(1L)
    }

    @Test
    fun `test getByUuid7 returns issues for uuid7`() {
        // Arrange
        whenever(repo.findByProjectIdAndUuid7(1L, testUuid7)).thenReturn(listOf(testIssue))

        // Act
        val result = service.getByUuid7(1L, testUuid7)

        // Assert
        assertEquals(1, result.size)
        assertEquals(testIssueResponse, result[0])
        verify(repo).findByProjectIdAndUuid7(1L, testUuid7)
    }

    @Test
    fun `test get returns issue when found`() {
        // Arrange
        whenever(repo.findByProjectIdAndUuid7(1L, testUuid7)).thenReturn(listOf(testIssue))

        // Act
        val result = service.get(1L, testUuid7)

        // Assert
        assertEquals(testIssueResponse, result)
        verify(repo).findByProjectIdAndUuid7(1L, testUuid7)
    }

    @Test
    fun `test get throws exception when issue not found`() {
        // Arrange
        val issue: Issue? = null
        whenever(repo.findByProjectIdAndUuid7(1L, "non-existent")).thenReturn(emptyList())

        // Act & Assert
        val exception =
            assertThrows(ResponseStatusException::class.java) {
                service.get(1L, "non-existent")
            }
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason?.contains("Issue non-existent not found") == true)
    }

    @Test
    fun `test create creates and returns issue`() {
        // Arrange
        whenever(repo.save(any<Issue>())).thenReturn(savedIssue)
        val createRequest =
            IssueCreateRequest(
                title = "New Issue",
                description = "New Description",
                status = "open",
                listId = 1,
                uuid7 = testUuid7,
            )

        // Act
        val result = service.create(1L, createRequest)

        // Assert
        assertEquals(testIssueResponse, result)
        verify(repo).save(any<Issue>())
    }

    @Test
    fun `test create handles null listId`() {
        // Arrange
        whenever(repo.save(any<Issue>())).thenReturn(savedIssue)
        val createRequest =
            IssueCreateRequest(
                title = "New Issue",
                description = "New Description",
                status = "open",
                listId = null,
                uuid7 = testUuid7,
            )

        // Act
        val result = service.create(1L, createRequest)

        // Assert
        // Note: stub returns savedIssue with listId=1, so we verify other fields
        assertEquals(testIssueResponse.title, result.title)
        assertEquals(testIssueResponse.description, result.description)
        assertEquals(testIssueResponse.status, result.status)
        assertEquals(testUuid7, result.uuid7)
    }

    @Test
    fun `test delete calls repository when issue exists`() {
        // Arrange
        whenever(repo.findByUuid7(testUuid7)).thenReturn(testIssue)

        // Act
        service.delete(testUuid7)

        // Assert
        verify(repo).findByUuid7(testUuid7)
        verify(repo).delete(testIssue)
    }

    @Test
    fun `test delete throws exception when issue not found`() {
        // Arrange
        whenever(repo.findByUuid7("non-existent")).thenReturn(null)

        // Act & Assert
        val exception =
            assertThrows(ResponseStatusException::class.java) {
                service.delete("non-existent")
            }
        assertNotNull(exception.message)
        assertTrue(exception.message!!.contains("Issue non-existent not found"))
    }

    @Test
    fun `test patch updates issue when found`() {
        // Arrange
        val patchRequest =
            IssuePatchRequest(
                title = "Updated Title",
                description = null,
                status = "in progress",
                listId = 2,
                uuid7 = testUuid7,
            )
        val issueToUpdate =
            Issue(
                uuid7 = testUuid7,
                title = "Original Title",
                description = "Original Description",
                status = "open",
                listId = 1L,
                projectId = 1L,
            )
        val expectedResponse =
            IssueResponse(
                uuid7 = testUuid7,
                title = "Updated Title",
                description = "Original Description",
                status = "in progress",
                listId = 2,
            )

        whenever(repo.findByProjectIdAndUuid7(1L, testUuid7)).thenReturn(listOf(issueToUpdate))
        whenever(repo.save(any<Issue>())).thenAnswer { invocation -> invocation.getArgument(0) as Issue }

        // Act
        val result = service.patch(1L, patchRequest)

        // Assert
        assertEquals(expectedResponse, result)
        verify(repo).findByProjectIdAndUuid7(1L, testUuid7)
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
                uuid7 = testUuid7,
            )

        whenever(repo.findByProjectIdAndUuid7(1L, testUuid7)).thenReturn(null)

        // Act & Assert
        val exception =
            assertThrows(ResponseStatusException::class.java) {
                service.patch(1L, patchRequest)
            }
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertTrue(exception.reason?.contains("not found") == true)
        verify(repo).findByProjectIdAndUuid7(1L, testUuid7)
    }
}