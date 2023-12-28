rootProject.name="wge"

include("backend")
project(":backend").projectDir = file("web/backend")

include("frontend")
project(":frontend").projectDir = file("web/frontend")

include("native_client")
project(":native_client").projectDir = file("native/client")