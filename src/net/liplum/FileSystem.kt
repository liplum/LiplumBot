package net.liplum

import java.io.File

object FileSystem {
    val root = File("LiplumData").ensureDir()
    val temp = root.resolve("temp").ensureDir()
    val data = root.resolve("data").ensureDir()
}

fun File.ensureDir() = apply {
    mkdirs()
}

fun File.ensureParent() = apply {
    parentFile?.ensureDir()
}