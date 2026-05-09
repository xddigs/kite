package org.kite.app.ui

import java.awt.BorderLayout
import javax.swing.JFrame

class Frame : JFrame("Kite") {
    private val swingPanel = Panel()

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()

        add(swingPanel, BorderLayout.CENTER)
        setSize(800, 600)
        setLocationRelativeTo(null)
        isVisible = true
    }
}