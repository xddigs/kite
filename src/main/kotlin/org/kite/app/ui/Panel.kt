package org.kite.app.ui

import org.kite.app.core.CodeEditor
import java.awt.BorderLayout
import javax.swing.JPanel

class Panel : JPanel() {

    init {
        layout = BorderLayout()
        background = EditorTheme.BACKGROUND_COLOR

        val editor = CodeEditor()
        add(editor, BorderLayout.CENTER)
    }
}