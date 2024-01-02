rootProject.name="wge"

include("backend")
project(":backend").projectDir = file("web/backend")

include("frontend")
project(":frontend").projectDir = file("web/frontend")