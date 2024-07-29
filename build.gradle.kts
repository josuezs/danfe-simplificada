plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1" // add shadow task to generate "fat-jar"
}

group = "danfe"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

var itextpdfVersion = "8.0.4"
var lombokVersion = "1.18.34"
var slf4jVersion = "2.0.13"
var logbackVersion = "1.5.6"
var jaxbApiVersion = "2.3.1"
var jaxbRuntimeVersion = "2.3.9"
var junitVersion = "5.10.3"

dependencies {
    // https://mvnrepository.com/artifact/com.itextpdf/itext-core
    implementation("com.itextpdf:itext-core:$itextpdfVersion")

    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    // Log
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    // Log implementation
    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // https://mvnrepository.com/artifact/javax.xml.bind/jaxb-api
    implementation("javax.xml.bind:jaxb-api:$jaxbApiVersion")

    // https://mvnrepository.com/artifact/org.glassfish.jaxb/jaxb-runtime
    implementation("org.glassfish.jaxb:jaxb-runtime:$jaxbRuntimeVersion")

    //

    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "danfe.Application"
    }
}
