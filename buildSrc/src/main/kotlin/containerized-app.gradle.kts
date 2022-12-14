plugins {
    application
    id("com.google.cloud.tools.jib")
}

jib {
    from {
        image = "openjdk:17-alpine"
    }
    to {
        image = "be.rm.secu.tp2/${project.name}"
    }
}
