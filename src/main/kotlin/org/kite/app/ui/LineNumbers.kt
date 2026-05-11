package org.kite.app.ui

import org.kite.app.core.Editor
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JComponent

class LineNumbers(private val editor: Editor) : JComponent() {

    init {
        background = EditorTheme.BACKGROUND_COLOR
        preferredSize = Dimension(60, 0)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D

        g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        )

        g2.color = EditorTheme.BACKGROUND_COLOR
        g2.fillRect(0, 0, width, height)

        g2.font = FontManager.JETBRAINS_MONO
        val metrics = g2.fontMetrics
        val lineHeight = metrics.height

        val lines = editor.getLines()
        var y = Editor.PADDING - getEditorScrollY()

        for (i in lines.indices) {
            val lineNumber = (i + 1).toString()
            val stringWidth = metrics.stringWidth(lineNumber)

            val baseline = y + metrics.ascent

            g2.color = EditorTheme.LINE_NUMBER_COLOR
            g2.drawString(lineNumber, width - stringWidth - 8,
                baseline)
            y += lineHeight
        }
    }

    private fun getEditorScrollY(): Int {
        return try {
            val field = Editor::class.java.getDeclaredField("scrollY")
            field.isAccessible = true
            field.get(editor) as Int
        } catch (_: Exception) {
            0
        }
    }
}