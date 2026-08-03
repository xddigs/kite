package org.kite.app.ui

import java.awt.BorderLayout
import javax.swing.JFrame

class Frame : JFrame("Untitled - Kite") {
    private val swingPanel = Panel(this)

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()

        add(swingPanel, BorderLayout.CENTER)
        setSize(800, 600)
        setLocationRelativeTo(null)
        isVisible = true
    }
}