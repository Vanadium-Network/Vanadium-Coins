plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.vanadium"
version = "1.0-RELEASE"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local/")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
    implementation("org.mongodb:mongodb-driver-reactivestreams:4.8.1")
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    compileOnly("eu.thesimplecloud.simplecloud:simplecloud-api:2.4.1")
    compileOnly("eu.thesimplecloud.simplecloud:simplecloud-plugin:2.4.1")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {

            var u = "https://maven.vanadium.dev/repository/vanadium-"

            if (version.toString().toLowerCase().endsWith("snapshot")) {
                u += "snapshot"
            } else {
                u += "release"
            }

            url = uri(u)

            credentials {
                username = if (project.hasProperty("vanadiumMavenUser")) project.property("vanadiumMavenUser").toString() else ""
                password = if (project.hasProperty("vanadiumMavenPassword")) project.property("vanadiumMavenPassword").toString() else ""
            }

        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = this.groupId
            artifactId = this.artifactId
            version = project.version.toString()

            from(components["java"])
            artifact(sourcesJar.get())
        }

    }


}





tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>() {
    dependencies {
        exclude(dependency("org.projectlombok:lombok:1.18.24"))
        exclude(dependency("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT"))
        exclude(dependency("eu.thesimplecloud.simplecloud:simplecloud-api:2.4.1"))
        exclude(dependency("eu.thesimplecloud.simplecloud:simplecloud-plugin:2.4.1"))
    }

    minimize()
}


tasks.getByName<Test>("test") {
    useJUnitPlatform()
}