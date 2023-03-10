import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("org.springframework.boot") version "2.5.7"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.graalvm.buildtools.native") version "0.9.18"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
    kotlin("kapt") version "1.7.21"

    //swagger plugin
    id("org.openapi.generator") version "5.1.1"
}

group = "com.kyu9"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }

}

repositories {
    mavenCentral()
    maven{
        url = uri("https://repo.maven.apache.org/maven2/")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    //generated from https://start.spring.io/
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-web-services")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.google.guava:guava:30.1.1-jre")

    implementation ("org.springframework:spring-context:5.3.15")
    //view
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    //cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")


    //db
    runtimeOnly("com.oracle.database.jdbc:ojdbc8")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")
    implementation("org.hibernate:hibernate-core:5.4.1.Final")
    implementation("org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.0.Final")

    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    //Swagger - plugin
    implementation("org.openapitools:openapi-generator-gradle-plugin:6.0.0")

    //Swagger - ui
    implementation("org.springdoc:springdoc-openapi-ui:1.6.8")
    implementation("org.springdoc:springdoc-openapi-common:1.6.8")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.8")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.0")


    //Openapi Generator
    implementation("io.swagger:swagger-annotations:1.6.2")
    implementation(group="javax.validation", name="validation-api", version="2.0.1.Final")
    implementation(group="org.openapitools", name="jackson-databind-nullable", version="0.2.1")
    //config > https://openapi-generator.tech/docs/generators/spring/


    //test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2:2.0.202")
    //kotest
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.4.3")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.4.3")
    implementation("io.kotest:kotest-extensions-spring:4.4.3")
    //mockk
    testImplementation("io.mockk:mockk:1.9.3")



    //mockk
    testImplementation("io.mockk:mockk:1.12.4")


}

springBoot{
    //kotlin ?????? ?????? ???????????? ???????????? ??? ???????????? ???????????? ?????? Kt??? ??????
    mainClass.set("com.kyu9.accountbook.AccountBookApplicationKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }

}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("generateFromYaml"){
    inputSpec.set("${projectDir}/src/main/resources/spec/AccountBook.yaml")
    outputDir.set("${projectDir}/generated")
    configFile.set("${projectDir}/src/main/resources/spec/config.json")
    generatorName.set("kotlin-spring")
    group = "0.action"

    //?????? config.json??? ????????? ???????????? >> ????????? ???????????????????????? ????????? ??????
    //????????? ApiUtils?????? javax??? ???????????? ????????? boot3.0.0????????? javax??? ????????? jakarata??? ???????????? ????????? ????????? ????????????????????? ????????? ??????????????? ?????? ??????
//    configOptions.put("useSpringBoot3", "true")
}

task<Delete>("removeGeneratedFromYaml"){
    group = "0.action"
    delete(
        fileTree("${projectDir}/generated/src/main/kotlin/com/kyu9/accountbook/swagger/model"),
        fileTree("${projectDir}/generated/src/main/kotlin/com/kyu9/accountbook/swagger/api")
    )
}

configure<SourceSetContainer>{
    named("main"){
        java.srcDir("${projectDir}/generated/src/main/kotlin")
    }
}

tasks.named("generateFromYaml").configure {
    dependsOn("removeGeneratedFromYaml")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.create<Test>("fullTest"){
    useJUnitPlatform()
    group = "0.action"
}

task<Exec>("Merge all branches into MASTER"){
    group = "1.git"

    commandLine("./script/merge_all_branch_into_MASTER.sh")
    //for windows
//    commandLine("cmd", "/c", "merge_all_branch_into_MASTER.sh")
}

task<Exec>("Merge master into ALL_BRANCHES"){
    group = "1.git"

    commandLine("./script/sync_all_branch_by_MASTER.sh")
    //for windows
//    commandLine("cmd", "/c", "merge_master_to_all_branches.sh")
}

task<Exec>("Sync Branches"){
    group = "0.action"

    commandLine("./script/integration_sync.sh")
}

//tasks.named("test").configure{ group = "0.action" }
tasks.named("build").configure{ group = "0.action" }
tasks.named("clean").configure{ group = "0.action" }
tasks.named("check").configure{ enabled = false }
tasks.named("bootRun").configure{ group = "0.action" }
tasks.named("compileKotlin").configure{
    group = "0.action"
    dependsOn(tasks.named("generateFromYaml"))
}