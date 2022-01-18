plugins {
    id("java")
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.2.23")
}

tasks.withType<Jar>() {
    manifest {
        attributes["Main-Class"] = "ru.ashkart.Main"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.path))
    }
}