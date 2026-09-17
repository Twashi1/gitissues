rootProject.name = "gitissues"

include(
    ":gitissues-cli:kotlin:jni",
    ":gitissues-cli:kotlin:bindings",
)

project(":gitissues-cli:kotlin:jni").projectDir =
    file("gitissues-cli/kotlin/jni")

project(":gitissues-cli:kotlin:bindings").projectDir =
    file("gitissues-cli/kotlin/bindings")
