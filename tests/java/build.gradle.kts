plugins {
    java
}

repositories {
    mavenCentral()
    flatDir {
        dirs("../../java/build/libs/")
    }
}

val configFile = File("../../config/versions.txt").readLines()
val configFileMap = configFile.associate { it.split("=")[0] to it.split("=")[1] }

val grpcVersion = configFileMap["JAVA_GRPC_VERSION"]
val baseVersion = configFileMap["BASE_PACKAGE_VERSION"]!!
val buildId: String? = System.getenv("GITHUB_RUN_NUMBER")
val t2iapiVersion: String = when (System.getenv("RELEASE_VERSION") == "1") {
    true -> baseVersion
    false -> baseVersion + ( buildId?.let { ".$it" } ?: "" ) + "-SNAPSHOT"
}

dependencies {
    testImplementation("io.grpc:grpc-protobuf:${grpcVersion}")
    testImplementation("io.grpc:grpc-stub:${grpcVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
    testImplementation("io.grpc:grpc-netty-shaded:${grpcVersion}")
    testImplementation("com.draeger.medical:t2iapi:${t2iapiVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    reports.junitXml.isEnabled = true
    reports.html.isEnabled = true
}