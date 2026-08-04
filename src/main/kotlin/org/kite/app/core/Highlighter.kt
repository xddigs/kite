package org.kite.app.core

import org.kite.app.ui.EditorTheme
import java.awt.Color

enum class TokenType {
    TEXT, BUILTIN, KEYWORD, STRING, COMMENT
}

data class Token(val text: String, val type: TokenType)

object Highlighter {
    private val java = setOf(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
        "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
        "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
    )
    private val python = setOf(
        "False", "None", "True", "and", "as", "assert", "async", "await", "break", "class", "continue",
        "def", "del", "elif", "else", "except", "finally", "for", "from", "global", "if", "import", "in",
        "is", "lambda", "nonlocal", "not", "or", "pass", "raise", "return", "try", "while", "with", "yield"
    )
    private val pythonBuiltins = setOf(
        "print", "input", "len", "range", "str", "int", "float", "list", "dict", "set", "type", "open", "abs"
    )
    private val kotlin = setOf(
        "as", "as?", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in",
        "!in", "is", "!is", "null", "object", "package", "return", "super", "this", "throw", "true",
        "try", "typealias", "val", "var", "when", "while"
    )
    fun tokenize(line: String, extension: String?): List<Token> {
        if (line.isEmpty()) return emptyList()
        val tokens = mutableListOf<Token>()
        val isPython = extension?.lowercase() == "py"

        val keywords = when (extension?.lowercase()) {
            "java" -> java
            "py" -> python
            "kt", "kts" -> kotlin
            else -> emptySet()
        }
        val builtins = if (isPython) pythonBuiltins else emptySet()

        var i = 0
        while (i < line.length) {
            val char = line[i]

            if (isPython) {
                if (char == '#') {
                    tokens.add(Token(line.substring(i), TokenType.COMMENT))
                    break
                }
            } else {
                if (char == '/' && i + 1 < line.length && line[i + 1] == '/') {
                    tokens.add(Token(line.substring(i), TokenType.COMMENT))
                    break
                }
            }

            if (char == '"' || char == '\'') {
                val start = i
                i++
                while (i < line.length && line[i] != char) {
                    if (line[i] == '\\' && i + 1 < line.length) i++
                    i++
                }
                if (i < line.length) i++
                tokens.add(Token(line.substring(start, i), TokenType.STRING))
                continue
            }

            if (char.isLetter() || char == '_') {
                val start = i
                while (i < line.length && (line[i].isLetterOrDigit() || line[i] == '_')) {
                    i++
                }
                when (val word = line.substring(start, i)) {
                    in keywords -> tokens.add(Token(word, TokenType.KEYWORD))
                    in builtins -> tokens.add(Token(word, TokenType.BUILTIN))
                    else -> tokens.add(Token(word, TokenType.TEXT))
                }
                continue
            }

            tokens.add(Token(char.toString(), TokenType.TEXT))
            i++
        }
        return tokens
    }
    fun getColor(type: TokenType): Color {
        return when (type) {
            TokenType.KEYWORD -> EditorTheme.KEYWORD_COLOR
            TokenType.BUILTIN -> Color(0x61, 0xAF, 0xEF)
            TokenType.STRING -> EditorTheme.STRING_COLOR
            TokenType.COMMENT -> EditorTheme.COMMENT_COLOR
            TokenType.TEXT -> EditorTheme.TEXT_COLOR
        }
    }
}