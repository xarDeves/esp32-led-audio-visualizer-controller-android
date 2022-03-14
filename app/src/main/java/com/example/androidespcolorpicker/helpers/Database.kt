package com.example.androidespcolorpicker.helpers

import java.io.File
import java.io.FileOutputStream

class Database(filesDir: File?) {

    private val file: File

    init {
        val dir = File(filesDir, "colorsDir")
        dir.mkdirs()
        file = File(dir, "colors.txt")
        file.createNewFile()
    }

    fun write(data: MutableSet<Int>) {
        file.delete()
        file.createNewFile()

        FileOutputStream(file, true).bufferedWriter().use { writer ->
            data.forEach {
                writer.write(it.toString())
                writer.newLine()
            }
        }
    }

    fun read(data: MutableSet<Int>) {

        file.readLines().forEach {
            data.add(it.toInt())
        }
    }

}
