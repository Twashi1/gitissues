package gitissues

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

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
