plugins {
    val kotlinVersion = "1.7.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.14.0"
}

group = "net.lawaxi.jujuaddon"
version = "0.1.2-test7"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    api ("cn.hutool:hutool-all:5.8.18")
    api(files("libs/shitboy-0.1.11-dev5.mirai2.jar"))
}