package gitissues

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Controller
class SpaController {
    @GetMapping(
        value = [
            "/",
            "/{path:[^\\.]*}",
            "/**/{path:[^\\.]*}",
        ],
    )
    fun index(): String = "forward:/index.html"
}
