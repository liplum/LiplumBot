package net.liplum.util

import java.io.File
import java.util.*

val workingDir = File(System.getProperty("user.dir"))
const val TokenKey = "LIPLUM_TOKEN"

object Token {
    fun load(): String {
        val envFile = workingDir.resolve(".env")
        if (envFile.isFile) {
            val env = Properties().apply {
                this.load(envFile)
            }
            val token = env.getProperty(TokenKey)
            if (token != null)
                return token
        }
        return System.getenv(TokenKey)
    }
}

fun Properties.load(file: File) = file.reader().use {
    this.load(it)
}