package gitissues.issuelist

import gitissues.dto.issuelist.IssueListCreateRequest
import gitissues.dto.issuelist.IssueListPatchRequest
import gitissues.dto.issuelist.IssueListResponse
import gitissues.issuelist.IssueList
import gitissues.issuelist.IssueListId
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

@ExtendWith(MockitoExtension::class)
class IssueListServiceTest {
    @Mock
    private lateinit var repository: IssueListRepository

    @Mock
    private lateinit var issueRepository: gitissues.issue.IssueRepository

    @InjectMocks
    private lateinit var service: IssueListService

    private val testIssueListId = IssueListId(1L, 1L)
    private val testIssueList =
        IssueList(
            projectId = 1L,
            id = 1L,
            title = "Test List",
            createdAt = java.time.LocalDateTime.now(),
        )

    private val testIssueListResponse =
        IssueListResponse(
            id = 1L,
            title = "Test List",
            createdAt = testIssueList.createdAt,
        )

    @BeforeEach
    fun setup() {
        reset(repository, issueRepository)
    }

    @Test
    fun `test all returns all issue lists`() {
        // Arrange
        whenever(repository.findAllByProjectIdOrderByIdDesc(1L)).thenReturn(listOf(testIssueList))

        // Act
        val result = service.all(1L)

        // Assert
        assertEquals(1, result.size)
        assertEquals(testIssueListResponse, result[0])
        verify(repository).findAllByProjectIdOrderByIdDesc(1L)
    }

    @Test
    fun `test get returns issue list when found`() {
        // Arrange
        whenever(repository.findById(testIssueListId)).thenReturn(java.util.Optional.of(testIssueList))

        // Act
        val result = service.get(1L, 1L)

        // Assert
        assertEquals(testIssueListResponse, result)
        verify(repository).findById(testIssueListId)
    }

    @Test
    fun `test get throws exception when issue list not found`() {
        // Arrange
        val nonExistentId = IssueListId(1L, 999L)
        whenever(repository.findById(nonExistentId)).thenReturn(java.util.Optional.empty())

        // Act & Assert
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.get(1L, 999L)
            }
        assertNotNull(exception.message)
        assertTrue(exception.message!!.contains("IssueList 999 not found"))
        verify(repository).findById(nonExistentId)
    }

    @Test
    fun `test create creates and returns issue list`() {
        // Arrange
        val createRequest = IssueListCreateRequest(title = "New List")
        val issueListToSave =
            IssueList(
                projectId = 1L,
                id = 0L,
                title = "New List",
                createdAt = java.time.LocalDateTime.now(),
            )
        val savedIssueList =
            IssueList(
                projectId = 1L,
                id = 1L,
                title = "New List",
                createdAt = java.time.LocalDateTime.now(),
            )
        val expectedResponse =
            IssueListResponse(
                id = 1L,
                title = "New List",
                createdAt = savedIssueList.createdAt,
            )

        whenever(repository.save(any())).thenAnswer { invocation ->
            val issueList = invocation.getArgument(0) as IssueList
            // Create a new instance with the ID set since id is val in the entity
            IssueList(
                projectId = issueList.projectId,
                id = 1L,
                title = issueList.title,
                createdAt = issueList.createdAt,
            )
        }

        // Act
        val result = service.create(1L, createRequest)

        // Assert
        assertEquals(expectedResponse.title, result.title)
        assertEquals(expectedResponse.id, result.id)
        assertNotNull(result.createdAt)
        verify(repository).save(any())
    }

    @Test
    fun `test delete calls repository when list exists`() {
        // Arrange
        val listId = IssueListId(1L, 1L)
        whenever(repository.existsById(listId)).thenReturn(true)

        // Act
        service.delete(1L, 1L)

        // Assert
        verify(repository).existsById(listId)
        verify(issueRepository).updateListIdToNullByListId(1L)
        verify(repository).deleteById(listId)
    }

    @Test
    fun `test delete throws exception when list not found`() {
        // Arrange
        val nonExistentId = IssueListId(1L, 999L)
        whenever(repository.existsById(nonExistentId)).thenReturn(false)

        // Act & Assert
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.delete(1L, 999L)
            }
        assertNotNull(exception.message)
        assertTrue(exception.message!!.contains("IssueList 999 not found"))
        verify(repository).existsById(nonExistentId)
        verify(issueRepository, never()).updateListIdToNullByListId(any())
        verify(repository, never()).deleteById(any())
    }

    @Test
    fun `test patch updates issue list when found`() {
        // Arrange
        val patchRequest = IssueListPatchRequest(title = "Updated List")
        val issueListToUpdate =
            IssueList(
                projectId = 1L,
                id = 1L,
                title = "Original List",
                createdAt = java.time.LocalDateTime.now(),
            )
        val updatedIssueList =
            IssueList(
                projectId = 1L,
                id = 1L,
                title = "Updated List",
                createdAt = issueListToUpdate.createdAt,
            )
        val expectedResponse =
            IssueListResponse(
                id = 1L,
                title = "Updated List",
                createdAt = updatedIssueList.createdAt,
            )

        val issueListId = IssueListId(1L, 1L)
        whenever(repository.findById(issueListId)).thenReturn(java.util.Optional.of(issueListToUpdate))
        whenever(repository.save(any())).thenAnswer { invocation ->
            val issueList = invocation.getArgument(0) as IssueList
            // Create a new instance with the ID set since id is val in the entity
            IssueList(
                projectId = issueList.projectId,
                id = 1L,
                title = issueList.title,
                createdAt = issueList.createdAt,
            )
        }

        // Act
        val result = service.patch(1L, 1L, patchRequest)

        // Assert
        assertEquals(expectedResponse, result)
        verify(repository).findById(issueListId)
        verify(repository).save(any())
    }

    @Test
    fun `test patch throws exception when issue list not found`() {
        // Arrange
        val patchRequest = IssueListPatchRequest(title = "Updated List")
        val nonExistentId = IssueListId(1L, 999L)
        whenever(repository.findById(nonExistentId)).thenReturn(java.util.Optional.empty())

        // Act & Assert
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.patch(1L, 999L, patchRequest)
            }
        assertNotNull(exception.message)
        assertTrue(exception.message!!.contains("IssueList 999 not found"))
        verify(repository).findById(nonExistentId)
        verify(repository, never()).save(any())
    }
}
