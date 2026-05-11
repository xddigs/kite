package org.kite.app.core

import org.kite.app.ui.EditorTheme
import org.kite.app.ui.FontManager
import java.awt.*
import java.awt.event.*
import javax.swing.JComponent

class Editor : JComponent(),
    KeyListener,
    MouseListener,
    MouseMotionListener,
    MouseWheelListener {

    private val lines = mutableListOf("")

    private var caretRow = 0
    private var caretCol = 0

    private var anchorRow = 0
    private var anchorCol = 0
    private var hasSelection = false

    internal var scrollY = 0

    private val tabSize = 4
    private val useTabs = false

    private val lineHeight: Int
        get() = getFontMetrics(FontManager.JETBRAINS_MONO).height

    companion object {
        const val PADDING = 24
    }

    var onCaretMoved: ((Int, Int) -> Unit)? = null
    var onContentChanged: (() -> Unit)? = null

    init {
        isFocusable = true
        background = EditorTheme.BACKGROUND_COLOR
        focusTraversalKeysEnabled = false
        addKeyListener(this)
        addMouseListener(this)
        addMouseMotionListener(this)
        addMouseWheelListener(this)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D

        g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        )

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )

        g2.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY
        )

        g2.font = FontManager.JETBRAINS_MONO

        val metrics = g2.fontMetrics

        var y = PADDING - scrollY

        for (i in lines.indices) {
            val line = lines[i]
            val baseline = y + metrics.ascent

            if (hasSelection) {
                val start = getSelectionStart()
                val end = getSelectionEnd()

                if (i >= start.first && i <= end.first) {

                    val lineStart =
                        if (i == start.first) start.second else 0

                    val lineEnd =
                        if (i == end.first) end.second else line.length

                    val xStart = PADDING +
                            metrics.stringWidth(line.substring(0, lineStart))

                    val xEnd = PADDING +
                            metrics.stringWidth(line.substring(0, lineEnd))

                    g2.color = EditorTheme.SELECTION_COLOR

                    g2.fillRect(
                        xStart,
                        y,
                        xEnd - xStart,
                        metrics.height
                    )

                    if (i < end.first) {
                        g2.fillRect(
                            xEnd,
                            y,
                            10,
                            metrics.height
                        )
                    }
                }
            }

            g2.color = EditorTheme.TEXT_COLOR
            g2.drawString(line, PADDING, baseline)

            if (i == caretRow) {

                val safeCol = caretCol.coerceAtMost(line.length)

                val caretX = PADDING +
                        metrics.stringWidth(line.substring(0, safeCol))

                g2.color = EditorTheme.CURSOR_COLOR

                g2.drawLine(
                    caretX,
                    baseline - metrics.ascent,
                    caretX,
                    baseline + metrics.descent
                )
            }

            y += metrics.height
        }
    }

    override fun keyTyped(e: KeyEvent?) {
        val c = e?.keyChar ?: return
        if (e.isControlDown) return
        if (e.keyChar.code == KeyEvent.VK_ESCAPE) return
        if (e.keyChar.code == KeyEvent.VK_DELETE) return

        when (c) {
            '\b' -> backspace()
            '\n' -> newLine()
            '\t' -> tab()
            else -> insertChar(c)
        }

        repaint()
    }

    override fun keyPressed(e: KeyEvent?) {
        if (e == null) return

        if (e.keyCode == KeyEvent.VK_BACK_SPACE
            && e.isControlDown) {
            removeWord()
            repaint()
            return
        }

        if (e.keyCode == KeyEvent.VK_DELETE
            && hasSelection) {
            removeSelection()
            repaint()
            return
        }

        when (e.keyCode) {
            KeyEvent.VK_LEFT -> moveLeft()
            KeyEvent.VK_RIGHT -> moveRight()
            KeyEvent.VK_UP -> moveUp()
            KeyEvent.VK_DOWN -> moveDown()
        }

        updateCaret()
        repaint()
    }

    override fun keyReleased(e: KeyEvent?) {}

    override fun mouseClicked(e: MouseEvent?) {
        if (e == null) return
        requestFocus()
        hasSelection = false
        moveToMouse(e)
        updateCaret()
        repaint()
    }

    override fun mousePressed(e: MouseEvent?) {
        if (e == null) return
        requestFocus()
        hasSelection = false
        moveToMouse(e)
        anchorRow = caretRow
        anchorCol = caretCol
        repaint()
    }

    override fun mouseReleased(e: MouseEvent?) {}
    override fun mouseEntered(e: MouseEvent?) {}
    override fun mouseExited(e: MouseEvent?) {}

    override fun mouseDragged(e: MouseEvent?) {
        if (e == null) return
        hasSelection = true
        moveToMouse(e)
        updateCaret()
        repaint()
    }

    override fun mouseMoved(e: MouseEvent?) {}

    override fun mouseWheelMoved(e: MouseWheelEvent?) {
        if (e == null) return

        scrollY += e.wheelRotation * lineHeight
        scrollY = scrollY.coerceAtLeast(0)

        repaint()
    }

    private fun insertChar(c: Char) {
        if (hasSelection) {
            removeSelection()
            return
        }

        hasSelection = false
        val line = lines[caretRow]

        val updated = line.substring(0, caretCol) + c +
                line.substring(caretCol)

        lines[caretRow] = updated
        caretCol++
        updateCaret()
    }

    private fun backspace() {
        if (hasSelection) {
            removeSelection()
            return
        }

        hasSelection = false
        if (caretRow == 0 && caretCol == 0) return
        val line = lines[caretRow]

        if (caretCol > 0) {
            lines[caretRow] = line.substring(0, caretCol - 1) +
                    line.substring(caretCol)

            caretCol--
        } else {
            val prev = lines[caretRow - 1]
            caretCol = prev.length

            lines[caretRow - 1] = prev + line
            lines.removeAt(caretRow)
            caretRow--
        }

        updateCaret()
        onContentChanged?.invoke()
    }

    private fun removeSelection() {

        if (!hasSelection) {
            return
        }

        val start = getSelectionStart()
        val end = getSelectionEnd()

        val startRow = start.first
        val startCol = start.second

        val endRow = end.first
        val endCol = end.second

        if (startRow == endRow) {
            val line = lines[startRow]

            lines[startRow] =
                line.substring(0, startCol) +
                        line.substring(endCol)

        } else {

            val firstPart =
                lines[startRow].substring(0, startCol)

            val lastPart =
                lines[endRow].substring(endCol)

            lines[startRow] = firstPart + lastPart

            for (i in endRow downTo startRow + 1) {
                lines.removeAt(i)
            }
        }

        caretRow = startRow
        caretCol = startCol

        anchorRow = startRow
        anchorCol = startCol

        hasSelection = false

        updateCaret()
        onContentChanged?.invoke()

        repaint()
    }

    private fun removeWord() {
        if (caretRow == 0 && caretCol == 0) {
            return
        }

        val line = lines[caretRow]

        if (caretCol == 0) {
            val previous = lines[caretRow - 1]

            caretCol = previous.length

            lines[caretRow - 1] = previous + line
            lines.removeAt(caretRow)

            caretRow--

            return
        }

        var start = caretCol

        while (start > 0 && line[start - 1].isWhitespace()) {
            start--
        }

        while (start > 0 && !line[start - 1].isWhitespace()) {
            start--
        }

        lines[caretRow] = line.substring(0, start) +
                line.substring(caretCol)

        caretCol = start
    }

    private fun newLine() {
        if (hasSelection) {
            removeSelection()
            return
        }

        val line = lines[caretRow]

        val left = line.substring(0, caretCol)
        val right = line.substring(caretCol)

        lines[caretRow] = left
        lines.add(caretRow + 1, right)

        caretRow++
        caretCol = 0
        updateCaret()
        onContentChanged?.invoke()
    }

    private fun tab() {

        if (hasSelection) {
            removeSelection()
            return
        }

        val tabText = if (useTabs) "\t" else " ".repeat(tabSize)
        val line = lines[caretRow]
        val left = line.substring(0, caretCol)
        val right = line.substring(caretCol)

        lines[caretRow] = left + tabText + right
        caretCol += tabText.length
        updateCaret()
        onContentChanged?.invoke()
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
                lines[caretRow].length
            )
        }
    }

    private fun moveDown() {
        if (caretRow < lines.size - 1) {
            caretRow++
            caretCol = caretCol.coerceAtMost(
                lines[caretRow].length
            )
        }
    }

    private fun updateCaret() {
        onCaretMoved?.invoke(caretRow, caretCol)
    }

    private fun getSelectionStart(): Pair<Int, Int> {
        return if (caretRow < anchorRow || (caretRow == anchorRow &&
                    caretCol < anchorCol)
        ) {
            caretRow to caretCol
        } else {
            anchorRow to anchorCol
        }
    }

    private fun getSelectionEnd(): Pair<Int, Int> {
        return if (caretRow > anchorRow || (caretRow == anchorRow &&
                    caretCol > anchorCol)
        ) {
            caretRow to caretCol
        } else {
            anchorRow to anchorCol
        }
    }

    private fun moveToMouse(e: MouseEvent) {
        val y = (e.y + scrollY) / lineHeight
        caretRow = y.coerceIn(0, lines.size - 1)

        val line = lines[caretRow]

        val metrics = getFontMetrics(FontManager.JETBRAINS_MONO)

        var x = PADDING
        caretCol = 0

        for (i in line.indices) {
            val charWidth = metrics.charWidth(line[i])

            if (x + charWidth / 2 > e.x) {
                break
            }

            x += charWidth
            caretCol++
        }

        caretCol = caretCol.coerceIn(0, line.length)
    }

    fun setLines(newLines: MutableList<String>) {
        lines.clear()
        lines.addAll(newLines)

        caretRow = 0
        caretCol = 0
        scrollY = 0

        repaint()
    }

    fun getLines(): List<String> {
        return lines
    }
}