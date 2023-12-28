plugins {
    id("cpp-application")
}

application {
    targetMachines = listOf(machines.windows.x86_64)
}