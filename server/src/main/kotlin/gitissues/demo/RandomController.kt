package gitissues.demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

@RestController
class RandomController {

    @GetMapping("/api/random")
    fun randomNumber(): Map<String, Int> {
        return mapOf("value" to Random.nextInt(0, 100))
    }
}
