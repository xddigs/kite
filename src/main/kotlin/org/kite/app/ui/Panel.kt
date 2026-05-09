package org.kite.app.ui

import org.kite.app.core.Editor
import java.awt.BorderLayout
import javax.swing.JPanel

class Panel : JPanel() {

    init {
        layout = BorderLayout()
        background = EditorTheme.BACKGROUND_COLOR

        val editor = Editor()
        add(editor, BorderLayout.CENTER)
    }
}