package gitissues.bindingsdemo

import gitissues.Codec
import gitissues.GitIssues
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/bindings-demo")
class BindingsDemoController {
    @GetMapping
    fun demo(): BindingsDemoResponse {
        try {
            GitIssues.init()
            GitIssues.createRegistry().use { registry ->
                val titleTag = registry.registerTag("title", StringCodec)
                val statusTag = registry.registerTag("status", StringCodec)

                registry.createIssue().use { issue ->
                    issue.attachTag(titleTag, "Created through JNI bindings")
                    issue.attachTag(statusTag, "open")

                    return BindingsDemoResponse(
                        title = issue.getTag(titleTag),
                        status = issue.getTag(statusTag),
                    )
                }
            }
        } catch (e: UnsatisfiedLinkError) {
            throw ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Native gitissues_jni library is not available. Build it with `cmake --preset kotlin && cmake --build --preset kotlin` and add it to java.library.path.",
                e,
            )
        } finally {
            runCatching { GitIssues.terminate() }
        }
    }
}

data class BindingsDemoResponse(
    val title: String?,
    val status: String?,
)

private object StringCodec : Codec<String> {
    override fun encode(value: String): String = value

    override fun decode(value: String): String = value
}
