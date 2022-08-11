package net.liplum

import java.io.File

object FileSystem {
    val root = File("").ensureDir()
    val temp = root.resolve("temp").ensureDir()
}

fun File.ensureDir() = apply {
    mkdirs()
}

fun File.ensureParent() = apply {
    parentFile?.ensureDir()
}