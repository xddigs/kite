package org.kite.app.core

import org.kite.app.ui.EditorTheme
import java.awt.*
import java.awt.event.*
import javax.swing.JComponent

class CodeEditor : JComponent(),
    KeyListener,
    MouseListener,
    MouseMotionListener,
    MouseWheelListener {

    private val lines = mutableListOf("")

    private var caretRow = 0
    private var caretCol = 0

    private var scrollY = 0

    companion object {
        const val LINE_HEIGHT = 18
        const val PADDING = 8
    }

    init {
        isFocusable = true
        background = EditorTheme.BACKGROUND_COLOR

        addKeyListener(this)
        addMouseListener(this)
        addMouseMotionListener(this)
        addMouseWheelListener(this)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2 = g as Graphics2D
        g2.font = Font("Monospaced", Font.PLAIN, 16)

        var y = PADDING - scrollY

        for (i in lines.indices) {
            val line = lines[i]

            g2.color = EditorTheme.TEXT_COLOR
            g2.drawString(line, PADDING, y + LINE_HEIGHT)

            if (i == caretRow) {
                val caretX = PADDING + g2.fontMetrics.stringWidth(
                    line.substring(0, caretCol.coerceAtMost(
                        line.length))
                )

                g2.color = EditorTheme.CURSOR_COLOR
                g2.drawLine(caretX, y, caretX,
                    y + LINE_HEIGHT)
            }

            y += LINE_HEIGHT
        }
    }

    override fun keyTyped(e: KeyEvent?) {
        val c = e?.keyChar ?: return

        when (c) {
            '\b' -> backspace()
            '\n' -> newLine()
            else -> insertChar(c)
        }

        repaint()
    }

    override fun keyPressed(e: KeyEvent?) {
        when (e?.keyCode) {
            KeyEvent.VK_LEFT -> moveLeft()
            KeyEvent.VK_RIGHT -> moveRight()
            KeyEvent.VK_UP -> moveUp()
            KeyEvent.VK_DOWN -> moveDown()
        }

        repaint()
    }

    override fun keyReleased(e: KeyEvent?) {}

    override fun mouseClicked(e: MouseEvent?) {
        if (e == null) return

        requestFocus()

        val y = (e.y + scrollY) / LINE_HEIGHT
        caretRow = y.coerceIn(0, lines.size - 1)

        val line = lines[caretRow]

        val fontMetrics = getFontMetrics(
            Font("Monospaced", Font.PLAIN, 16))

        var x = PADDING
        caretCol = 0

        for (i in line.indices) {
            val charWidth = fontMetrics.charWidth(line[i])
            if (x + charWidth / 2 > e.x) break
            x += charWidth
            caretCol++
        }

        caretCol = caretCol.coerceIn(0, line.length)

        repaint()
    }

    override fun mousePressed(e: MouseEvent?) {
        requestFocus()
    }

    override fun mouseReleased(e: MouseEvent?) {}
    override fun mouseEntered(e: MouseEvent?) {}
    override fun mouseExited(e: MouseEvent?) {}

    override fun mouseDragged(e: MouseEvent?) {
        mouseClicked(e)
    }

    override fun mouseMoved(e: MouseEvent?) {}

    override fun mouseWheelMoved(e: MouseWheelEvent?) {
        if (e == null) return

        scrollY += e.wheelRotation * LINE_HEIGHT
        scrollY = scrollY.coerceAtLeast(0)

        repaint()
    }

    private fun insertChar(c: Char) {
        val line = lines[caretRow]

        val updated =
            line.substring(0, caretCol) +
                    c +
                    line.substring(caretCol)

        lines[caretRow] = updated
        caretCol++
    }

    private fun backspace() {
        if (caretRow == 0 && caretCol == 0) return

        val line = lines[caretRow]

        if (caretCol > 0) {
            lines[caretRow] =
                line.substring(0, caretCol - 1) +
                        line.substring(caretCol)

            caretCol--
        } else {
            val prev = lines[caretRow - 1]
            caretCol = prev.length

            lines[caretRow - 1] = prev + line
            lines.removeAt(caretRow)
            caretRow--
        }
    }

    private fun newLine() {
        val line = lines[caretRow]

        val left = line.substring(0, caretCol)
        val right = line.substring(caretCol)

        lines[caretRow] = left
        lines.add(caretRow + 1, right)

        caretRow++
        caretCol = 0
    }

    private fun moveLeft() {
        if (caretCol > 0) {
            caretCol--
        } else if (caretRow > 0) {
            caretRow--
            caretCol = lines[caretRow].length
        }
    }

    private fun moveRight() {
        if (caretCol < lines[caretRow].length) {
            caretCol++
        } else if (caretRow < lines.size - 1) {
            caretRow++
            caretCol = 0
        }
    }

    private fun moveUp() {
        if (caretRow > 0) {
            caretRow--
            caretCol = caretCol.coerceAtMost(
                lines[caretRow].length)
        }
    }

    private fun moveDown() {
        if (caretRow < lines.size - 1) {
            caretRow++
            caretCol = caretCol.coerceAtMost(
                lines[caretRow].length)
        }
    }
}