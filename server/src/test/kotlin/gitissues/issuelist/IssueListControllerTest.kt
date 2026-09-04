package gitissues.issuelist

import com.fasterxml.jackson.databind.ObjectMapper
import gitissues.GlobalExceptionHandler
import gitissues.dto.issuelist.IssueListCreateRequest
import gitissues.dto.issuelist.IssueListPatchRequest
import gitissues.dto.issuelist.IssueListResponse
import gitissues.issuelist.IssueListController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class IssueListControllerTest {

    @Mock
    private lateinit var service: IssueListService

    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    private val testInstant = LocalDateTime.now()
    private val testIssueListResponse = IssueListResponse(
        id = 1L,
        title = "Test List",
        createdAt = testInstant
    )

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
        mockMvc = MockMvcBuilders.standaloneSetup(IssueListController(service))
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    @Test
    fun `test get all issue lists returns list`() {
        // Arrange
        whenever(service.all()).thenReturn(listOf(testIssueListResponse))

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/issue-lists")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test List"))

        verify(service).all()
    }

    @Test
    fun `test get issue list by id returns list`() {
        // Arrange
        whenever(service.get(1L)).thenReturn(testIssueListResponse)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/issue-lists/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test List"))

        verify(service).get(1L)
    }

    @Test
    fun `test get issue list by id not found returns 404`() {
        // Arrange
        whenever(service.get(999L)).thenThrow(
            IllegalArgumentException("IssueList 999 not found")
        )

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/issue-lists/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound)

        verify(service).get(999L)
    }

    @Test
    fun `test create issue list returns created list`() {
        // Arrange
        val createRequest = IssueListCreateRequest(title = "New List")
        val createdResponse = IssueListResponse(
            id = 1L,
            title = "New List",
            createdAt = testInstant
        )

        whenever(service.create(any())).thenReturn(createdResponse)
        val requestJson = objectMapper.writeValueAsString(createRequest)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/issue-lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("New List"))

        verify(service).create(any())
    }

    @Test
    fun `test delete issue list returns 204`() {
        // Arrange
        doNothing().whenever(service).delete(1L)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/issue-lists/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)

        verify(service).delete(1L)
    }

    @Test
    fun `test delete issue list not found returns 404`() {
        // Arrange
        doThrow(IllegalArgumentException("IssueList 999 not found"))
            .whenever(service)
            .delete(999L)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/issue-lists/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound)

        verify(service).delete(999L)
    }

    @Test
    fun `test patch issue list returns updated list`() {
        // Arrange
        val patchRequest = IssueListPatchRequest(title = "Updated List")
        val updatedResponse = IssueListResponse(
            id = 1L,
            title = "Updated List",
            createdAt = testInstant
        )

        whenever(service.patch(eq(1L), any())).thenReturn(updatedResponse)
        val requestJson = objectMapper.writeValueAsString(patchRequest)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/issue-lists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated List"))

        verify(service).patch(eq(1L), any())
    }

    @Test
    fun `test patch issue list not found returns 404`() {
        // Arrange
        val patchRequest = IssueListPatchRequest(title = "Updated List")
        whenever(service.patch(eq(999L), any())).thenThrow(
            IllegalArgumentException("IssueList 999 not found")
        )
        val requestJson = objectMapper.writeValueAsString(patchRequest)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/issue-lists/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound)

        verify(service).patch(eq(999L), any())
    }
}