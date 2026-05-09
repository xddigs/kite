package org.kite.app.core

import java.io.File

object FileManager {

    fun load(file: File): MutableList<String> {
        if (!file.exists()) {
            return mutableListOf("")
        }

        val lines = file.readLines().toMutableList()

        if (lines.isEmpty()) {
            lines.add("")
        }

        return lines
    }

    fun save(file: File, lines: List<String>) {
        file.writeText(lines.joinToString("\n"))
    }
}