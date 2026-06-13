package gitissues.demo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class HtmlControllerTests(@Autowired private val mvc: MockMvc) {

  @Test
  fun getBlog() {
    mvc.get("/") {
      accept(MediaType.APPLICATION_JSON)
    }.andExpect {
      status { isOk() }
    }
  }
}
