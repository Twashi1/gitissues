package gitissues.issuelist

import gitissues.dto.issuelist.IssueListCreateRequest
import gitissues.dto.issuelist.IssueListPatchRequest
import gitissues.dto.issuelist.IssueListResponse
import gitissues.issuelist.IssueList
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class IssueListServiceTest {

    @Mock
    private lateinit var repository: IssueListRepository

    @Mock
    private lateinit var issueRepository: gitissues.issue.IssueRepository

    @InjectMocks
    private lateinit var service: IssueListService

    private val testIssueList = IssueList(
        id = 1L,
        title = "Test List",
        createdAt = java.time.LocalDateTime.now()
    )

    private val testIssueListResponse = IssueListResponse(
        id = 1L,
        title = "Test List",
        createdAt = testIssueList.createdAt
    )

    @BeforeEach
    fun setup() {
        reset(repository, issueRepository)
    }

    @Test
    fun `test all returns all issue lists`() {
        // Arrange
        whenever(repository.findAll()).thenReturn(listOf(testIssueList))

        // Act
        val result = service.all()

        // Assert
        assertEquals(1, result.size)
        assertEquals(testIssueListResponse, result[0])
        verify(repository).findAll()
    }

    @Test
    fun `test get returns issue list when found`() {
        // Arrange
        whenever(repository.findById(1L)).thenReturn(java.util.Optional.of(testIssueList))

        // Act
        val result = service.get(1L)

        // Assert
        assertEquals(testIssueListResponse, result)
        verify(repository).findById(1L)
    }

    @Test
    fun `test get throws exception when issue list not found`() {
        // Arrange
        whenever(repository.findById(999L)).thenReturn(java.util.Optional.empty())

        // Act & Assert
        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.get(999L)
        }
        assertNotNull(exception.message)
        assertTrue(exception.message!!.contains("IssueList 999 not found"))
        verify(repository).findById(999L)
    }

    @Test
    fun `test create creates and returns issue list`() {
        // Arrange
        val createRequest = IssueListCreateRequest(title = "New List")
        val issueListToSave = IssueList(
            id = 0L,
            title = "New List",
            createdAt = java.time.LocalDateTime.now()
        )
        val savedIssueList = IssueList(
            id = 1L,
            title = "New List",
            createdAt = java.time.LocalDateTime.now()
        )
        val expectedResponse = IssueListResponse(
            id = 1L,
            title = "New List",
            createdAt = savedIssueList.createdAt
        )

        whenever(repository.save(any())).thenAnswer { invocation ->
            val issueList = invocation.getArgument(0) as IssueList
            // Create a new instance with the ID set since id is val in the entity
            IssueList(
                id = 1L,
                title = issueList.title,
                createdAt = issueList.createdAt
            )
        }

        // Act
        val result = service.create(createRequest)

        // Assert
        assertEquals(expectedResponse.title, result.title)
        assertEquals(expectedResponse.id, result.id)
        assertNotNull(result.createdAt)
        verify(repository).save(any())
    }

    @Test
    fun `test delete calls repository when list exists`() {
        // Arrange
        whenever(repository.existsById(1L)).thenReturn(true)

        // Act
        service.delete(1L)

        // Assert
        verify(repository).existsById(1L)
        verify(issueRepository).updateListIdToNullByListId(1L)
        verify(repository).deleteById(1L)
    }

    @Test
    fun `test delete throws exception when list not found`() {
        // Arrange
        whenever(repository.existsById(999L)).thenReturn(false)

        // Act & Assert
        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.delete(999L)
        }
        assertNotNull(exception.message)
        assertTrue(exception.message!!.contains("IssueList 999 not found"))
        verify(repository).existsById(999L)
        verify(issueRepository, never()).updateListIdToNullByListId(any())
        verify(repository, never()).deleteById(any())
    }

    @Test
    fun `test patch updates issue list when found`() {
        // Arrange
        val patchRequest = IssueListPatchRequest(title = "Updated List")
        val issueListToUpdate = IssueList(
            id = 1L,
            title = "Original List",
            createdAt = java.time.LocalDateTime.now()
        )
        val updatedIssueList = IssueList(
            id = 1L,
            title = "Updated List",
            createdAt = issueListToUpdate.createdAt
        )
        val expectedResponse = IssueListResponse(
            id = 1L,
            title = "Updated List",
            createdAt = updatedIssueList.createdAt
        )

        whenever(repository.findById(1L)).thenReturn(java.util.Optional.of(issueListToUpdate))
        whenever(repository.save(any())).thenAnswer { invocation ->
            val issueList = invocation.getArgument(0) as IssueList
            // Create a new instance with the ID set since id is val in the entity
            IssueList(
                id = 1L,
                title = issueList.title,
                createdAt = issueList.createdAt
            )
        }

        // Act
        val result = service.patch(1L, patchRequest)

        // Assert
        assertEquals(expectedResponse, result)
        verify(repository).findById(1L)
        verify(repository).save(any())
    }

    @Test
    fun `test patch throws exception when issue list not found`() {
        // Arrange
        val patchRequest = IssueListPatchRequest(title = "Updated List")
        whenever(repository.findById(999L)).thenReturn(java.util.Optional.empty())

        // Act & Assert
        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.patch(999L, patchRequest)
        }
        assertNotNull(exception.message)
        assertTrue(exception.message!!.contains("IssueList 999 not found"))
        verify(repository).findById(999L)
        verify(repository, never()).save(any())
    }
}