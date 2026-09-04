package gitissues.issue

import com.fasterxml.jackson.databind.ObjectMapper
import gitissues.GlobalExceptionHandler
import gitissues.dto.issue.IssueCreateRequest
import gitissues.dto.issue.IssuePatchRequest
import gitissues.dto.issue.IssueResponse
import gitissues.issue.IssueController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.server.ResponseStatusException
import java.util.NoSuchElementException
import java.util.*

@ExtendWith(MockitoExtension::class)
class IssueControllerTest {

    @Mock
    private lateinit var service: IssueService

    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    private val testIssueResponse = IssueResponse(
        id = 1L,
        title = "Test Issue",
        description = "Test Description",
        status = "open",
        listId = 1L
    )

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
        mockMvc = MockMvcBuilders.standaloneSetup(IssueController(service))
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    @Test
    fun `test get all issues returns list`() {
        // Arrange
        whenever(service.all()).thenReturn(listOf(testIssueResponse))

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/issue")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test Issue"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Test Description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("open"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].listId").value(1))

        verify(service).all()
    }

    @Test
    fun `test get all issues with listId filter`() {
        // Arrange
        whenever(service.getByListId(1L)).thenReturn(listOf(testIssueResponse))

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/issue")
                .param("listId", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test Issue"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Test Description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("open"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].listId").value(1))

        verify(service).getByListId(1L)
    }

    @Test
    fun `test get issue by id returns issue`() {
        // Arrange
        whenever(service.get(1L)).thenReturn(testIssueResponse)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/issue/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Issue"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Test Description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("open"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId").value(1))

        verify(service).get(1L)
    }

    @Test
    fun `test get issue by id not found returns 404`() {
        // Arrange
        whenever(service.get(999L)).thenThrow(
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Issue 999 not found"
            )
        )

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/issue/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound)

        verify(service).get(999L)
    }

    @Test
    fun `test create issue returns created issue`() {
        // Arrange
        val createRequest = IssueCreateRequest(
            title = "New Issue",
            description = "New Description",
            status = "open",
            listId = 1L
        )
        val createdResponse = IssueResponse(
            id = 1L,
            title = "New Issue",
            description = "New Description",
            status = "open",
            listId = 1L
        )

        whenever(service.create(any())).thenReturn(createdResponse)
        val requestJson = objectMapper.writeValueAsString(createRequest)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("New Issue"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("New Description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("open"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId").value(1))

        verify(service).create(any())
    }

    @Test
    fun `test delete issue returns 204`() {
        // Arrange
        doNothing().whenever(service).delete(1L)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/issue/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)

        verify(service).delete(1L)
    }

    @Test
    fun `test delete issue not found returns 404`() {
        // Arrange
        doThrow(NoSuchElementException("Issue 999 not found"))
            .whenever(service)
            .delete(999L)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/issue/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound)

        verify(service).delete(999L)
    }

    @Test
    fun `test patch issue returns updated issue`() {
        // Arrange
        val patchRequest = IssuePatchRequest(
            title = "Updated Title",
            description = "Updated Description",
            status = "in progress",
            listId = 2L
        )
        val updatedResponse = IssueResponse(
            id = 1L,
            title = "Updated Title",
            description = "Updated Description",
            status = "in progress",
            listId = 2L
        )

        whenever(service.patch(eq(1L), any())).thenReturn(updatedResponse)
        val requestJson = objectMapper.writeValueAsString(patchRequest)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/issue/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Title"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated Description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("in progress"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId").value(2))

        verify(service).patch(eq(1L), any())
    }

    @Test
    fun `test patch issue not found returns 404`() {
        // Arrange
        val patchRequest = IssuePatchRequest(
            title = "Updated Title",
            description = null,
            status = null,
            listId = null
        )
        whenever(service.patch(eq(999L), any())).thenThrow(
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Issue 999 not found"
            )
        )
        val requestJson = objectMapper.writeValueAsString(patchRequest)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/issue/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound)

        verify(service).patch(eq(999L), any())
    }
}