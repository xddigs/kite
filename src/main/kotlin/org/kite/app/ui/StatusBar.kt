package org.kite.app.ui

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel

class StatusBar : JPanel() {

    private val statusLabel = JLabel("Ln 1, Col 1")
    private var padding = 16

    init {
        layout = BorderLayout()
        var height = 32
        var width = 0
        preferredSize = Dimension(width, height)
        background = EditorTheme.BACKGROUND_COLOR
        border = BorderFactory
            .createEmptyBorder(0, padding, 0, padding)

        statusLabel.foreground = EditorTheme.TEXT_COLOR
        statusLabel.font = FontManager.JETBRAINS_MONO
        add(statusLabel, BorderLayout.WEST)
    }

    fun setCaretPosition(row: Int, col: Int) {
        statusLabel.text = "Ln ${row + 1}, Col ${col + 1}"
        revalidate()
        repaint()
    }
}