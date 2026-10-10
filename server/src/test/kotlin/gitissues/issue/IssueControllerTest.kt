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
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.server.ResponseStatusException
import java.util.NoSuchElementException

@ExtendWith(MockitoExtension::class)
class IssueControllerTest {
    @Mock
    private lateinit var service: IssueService

    @Mock
    private lateinit var importService: IssueImportService

    @Mock
    private lateinit var exportService: IssueExportService

    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    private val testUuid7 = "a1b2c3d4-e5f6-0708-090a-0b0c0d0e0f11"
    private val testIssueResponse =
        IssueResponse(
            uuid7 = testUuid7,
            title = "Test Issue",
            description = "Test Description",
            status = "open",
            listId = 1,
        )

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
        mockMvc =
            MockMvcBuilders
                .standaloneSetup(IssueController(service, importService, exportService))
                .setControllerAdvice(GlobalExceptionHandler())
                .build()
    }

    @Test
    fun `test get all issues returns list`() {
        // Arrange
        whenever(service.all(1L, null)).thenReturn(listOf(testIssueResponse))

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get("/api/projects/1/issues")
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].uuid7").value(testUuid7))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test Issue"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Test Description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("open"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].listId").value(1))

        verify(service).all(1L, null)
    }

    @Test
    fun `test get all issues with uuid7 filter`() {
        // Arrange
        whenever(service.getByUuid7(1L, testUuid7)).thenReturn(listOf(testIssueResponse))

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get("/api/projects/1/issues")
                    .param("uuid7", testUuid7)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].uuid7").value(testUuid7))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Test Issue"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Test Description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("open"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].listId").value(1))

        verify(service).getByUuid7(1L, testUuid7)
    }

    @Test
    fun `test get issue by uuid7 returns issue`() {
        // Arrange
        whenever(service.get(1L, testUuid7)).thenReturn(testIssueResponse)

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get("/api/projects/1/issues/$testUuid7")
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.uuid7").value(testUuid7))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Issue"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Test Description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("open"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId").value(1))

        verify(service).get(1L, testUuid7)
    }

    @Test
    fun `test get issue by uuid7 not found returns 404`() {
        // Arrange
        whenever(service.get(1L, "non-existent")).thenThrow(
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Issue non-existent not found",
            ),
        )

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get("/api/projects/1/issues/non-existent")
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().isNotFound)

        verify(service).get(1L, "non-existent")
    }

    @Test
    fun `test create issue returns created issue`() {
        // Arrange
        val createRequest =
            IssueCreateRequest(
                title = "New Issue",
                description = "New Description",
                status = "open",
                listId = 1,
                uuid7 = testUuid7,
            )
        val createdResponse =
            IssueResponse(
                uuid7 = testUuid7,
                title = "New Issue",
                description = "New Description",
                status = "open",
                listId = 1,
            )

        whenever(service.create(1L, createRequest)).thenReturn(createdResponse)
        val requestJson = objectMapper.writeValueAsString(createRequest)

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post("/api/projects/1/issues")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.uuid7").value(testUuid7))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("New Issue"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("New Description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("open"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId").value(1))

        verify(service).create(1L, createRequest)
    }

    @Test
    fun `test delete issue returns 204`() {
        // Arrange
        doNothing().whenever(service).delete(testUuid7)

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .delete("/api/projects/1/issues/$testUuid7")
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().isNoContent)

        verify(service).delete(testUuid7)
    }

    @Test
    fun `test delete issue not found returns 404`() {
        // Arrange
        doThrow(NoSuchElementException("Issue not found"))
            .whenever(service)
            .delete("non-existent")

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .delete("/api/projects/1/issues/non-existent")
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().isNotFound)

        verify(service).delete("non-existent")
    }

    @Test
    fun `test patch issue returns updated issue`() {
        // Arrange
        val patchRequest =
            IssuePatchRequest(
                title = "Updated Title",
                description = "Updated Description",
                status = "in progress",
                listId = 2,
                uuid7 = testUuid7,
            )
        val updatedResponse =
            IssueResponse(
                uuid7 = testUuid7,
                title = "Updated Title",
                description = "Updated Description",
                status = "in progress",
                listId = 2,
            )

        whenever(service.patch(1L, patchRequest)).thenReturn(updatedResponse)
        val requestJson = objectMapper.writeValueAsString(patchRequest)

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .patch("/api/projects/1/issues/$testUuid7")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.uuid7").value(testUuid7))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Title"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated Description"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("in progress"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.listId").value(2))

        verify(service).patch(1L, patchRequest)
    }

    @Test
    fun `test patch issue not found returns 404`() {
        // Arrange
        val patchRequest =
            IssuePatchRequest(
                title = "Updated Title",
                description = null,
                status = null,
                listId = null,
                uuid7 = testUuid7,
            )

        whenever(service.patch(1L, patchRequest)).thenThrow(
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Issue testUuid7 not found",
            ),
        )
        val requestJson = objectMapper.writeValueAsString(patchRequest)

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .patch("/api/projects/1/issues/$testUuid7")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(MockMvcResultMatchers.status().isNotFound)

        verify(service).patch(1L, patchRequest)
    }
}