package org.kite.app.ui

import org.kite.app.core.Editor
import java.awt.BorderLayout
import javax.swing.*

class Panel(private val frame: JFrame) : JPanel() {

    init {
        layout = BorderLayout()
        background = EditorTheme.BACKGROUND_COLOR
        val editor = Editor()
        val statusBar = StatusBar()
        val lineNumbers = LineNumbers(editor)
        val menuBar = MenuBar(editor, frame)
        val tabBar = TabBar(editor)

        editor.onCaretMoved = { row, col ->
            statusBar.setCaretPosition(row, col)
        }

        editor.onContentChanged = {
            lineNumbers.repaint()
            val fileName = editor.currentFile?.name ?: "Untitled"
            tabBar.updateFileName(fileName)
            frame.title = "$fileName - Kite"
        }

        val topPanel = JPanel()
        topPanel.layout = BoxLayout(topPanel, BoxLayout.Y_AXIS)
        topPanel.add(menuBar)
        topPanel.add(tabBar)

        add(topPanel, BorderLayout.NORTH)
        add(editor, BorderLayout.CENTER)
        add(statusBar, BorderLayout.SOUTH)
        add(lineNumbers, BorderLayout.WEST)

        // Shortcuts
        val inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        val actionMap = actionMap

        inputMap.put(KeyStroke.getKeyStroke("control S"), "save")
        actionMap.put("save", object : AbstractAction() {
            override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                menuBar.saveFile()
            }
        })

        inputMap.put(KeyStroke.getKeyStroke("control W"), "close")
        actionMap.put("close", object : AbstractAction() {
            override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                tabBar.closeCurrentTab()
            }
        })
    }
}