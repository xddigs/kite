package org.kite.app.ui

import org.kite.app.core.Editor
import java.awt.BorderLayout
import javax.swing.*

class Panel(private val frame: JFrame) : JPanel() {

    private val editors = mutableListOf<Editor>()
    private var currentEditorIndex = 0

    init {
        layout = BorderLayout()
        background = EditorTheme.BACKGROUND_COLOR
        
        val statusBar = StatusBar()
        val tabBar = TabBar(this)

        fun setupEditor(editor: Editor) {
            editor.onCaretMoved = { row, col ->
                statusBar.setCaretPosition(row, col)
            }

            editor.onContentChanged = {
                val lineNumbers = components.filterIsInstance<LineNumbers>().firstOrNull()
                lineNumbers?.repaint()
                val fileName = editor.currentFile?.name ?: "Untitled"
                val extension = editor.currentFile?.extension
                tabBar.updateTabName(editors.indexOf(editor), fileName, extension)
                if (editor == editors[currentEditorIndex]) {
                    frame.title = "$fileName - Kite"
                }
            }

            editor.onSaveRequested = {
                val topPanel =
                    components.firstOrNull { it is JPanel && it !is TabBar
                            && it !is MenuBar && it !is StatusBar } as? JPanel
                val menuBar = if (topPanel != null) {
                    topPanel.components.filterIsInstance<MenuBar>().firstOrNull()
                } else {
                    components.filterIsInstance<MenuBar>().firstOrNull()
                }
                menuBar?.saveFile()
            }
        }

        val firstEditor = Editor()
        editors.add(firstEditor)
        setupEditor(firstEditor)

        val lineNumbers = LineNumbers(firstEditor)
        val menuBar = MenuBar(firstEditor, frame)

        val topPanel = JPanel()
        topPanel.layout = BoxLayout(topPanel, BoxLayout.Y_AXIS)
        topPanel.add(menuBar)
        topPanel.add(tabBar)

        add(topPanel, BorderLayout.NORTH)
        add(firstEditor, BorderLayout.CENTER)
        add(statusBar, BorderLayout.SOUTH)
        add(lineNumbers, BorderLayout.WEST)

        val inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW)
        val actionMap = actionMap

        inputMap.put(KeyStroke.getKeyStroke("control S"), "save")
        actionMap.put("save", object : AbstractAction() {
            override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                getCurrentEditor().save()
            }
        })

        inputMap.put(KeyStroke.getKeyStroke("control W"), "close")
        actionMap.put("close", object : AbstractAction() {
            override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                tabBar.closeCurrentTab()
            }
        })

        inputMap.put(KeyStroke.getKeyStroke("control T"), "newTab")
        actionMap.put("newTab", object : AbstractAction() {
            override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                addNewTab()
            }
        })
    }

    fun addNewTab() {
        val newEditor = Editor()
        editors.add(newEditor)

        val statusBar = components.filterIsInstance<StatusBar>().first()
        val topPanel =
            components.firstOrNull { it is JPanel && it !is TabBar && it !is MenuBar && it !is StatusBar } as? JPanel
        val tabBar = if (topPanel != null) {
            topPanel.components.filterIsInstance<TabBar>().firstOrNull()
        } else {
            components.filterIsInstance<TabBar>().firstOrNull()
        }

        newEditor.onCaretMoved = { row, col ->
            statusBar.setCaretPosition(row, col)
        }

        newEditor.onContentChanged = {
            val lineNumbers = components.filterIsInstance<LineNumbers>().firstOrNull()
            lineNumbers?.repaint()
            val fileName = newEditor.currentFile?.name ?: "Untitled"
            val extension = newEditor.currentFile?.extension
            tabBar?.updateTabName(editors.indexOf(newEditor), fileName, extension)
            if (newEditor == editors[currentEditorIndex]) {
                frame.title = "$fileName - Kite"
            }
        }

        newEditor.onSaveRequested = {
            val topPanel2 =
                components.firstOrNull { it is JPanel && it !is TabBar && it !is MenuBar && it !is StatusBar } as? JPanel
            val menuBar = if (topPanel2 != null) {
                topPanel2.components.filterIsInstance<MenuBar>().firstOrNull()
            } else {
                components.filterIsInstance<MenuBar>().firstOrNull()
            }
            menuBar?.saveFile()
        }

        tabBar?.addTab("Untitled", null)
        switchToTab(editors.size - 1)
    }

    fun switchToTab(index: Int) {
        if (index !in editors.indices) return

        val oldEditor = editors[currentEditorIndex]
        remove(oldEditor)

        currentEditorIndex = index
        val newEditor = editors[currentEditorIndex]
        add(newEditor, BorderLayout.CENTER)

        val lineNumbers = components.filterIsInstance<LineNumbers>().firstOrNull()
        if (lineNumbers != null) {
            remove(lineNumbers)
        }
        val newLineNumbers = LineNumbers(newEditor)
        add(newLineNumbers, BorderLayout.WEST)

        val topPanel =
            components.firstOrNull {
                @Suppress("USELESS_IS_CHECK")
                it is JPanel && it !is TabBar && it !is MenuBar && it !is
                        StatusBar && it !is Editor && it !is LineNumbers
            } as? JPanel
        val menuBar = if (topPanel != null) {
            topPanel.components.filterIsInstance<MenuBar>().firstOrNull()
        } else {
            components.filterIsInstance<MenuBar>().firstOrNull()
        }
        menuBar?.updateEditor(newEditor)

        val fileName = newEditor.currentFile?.name ?: "Untitled"
        frame.title = "$fileName - Kite"

        revalidate()
        repaint()
        newEditor.requestFocusInWindow()
    }

    fun closeTab(index: Int) {
        if (editors.size <= 1) {
            editors[0].setLines(mutableListOf(""), null)
            val topPanel = components.firstOrNull { it is JPanel && it !is
                    TabBar && it !is MenuBar && it !is StatusBar } as? JPanel
            val tabBar = if (topPanel != null) {
                topPanel.components.filterIsInstance<TabBar>().firstOrNull()
            } else {
                components.filterIsInstance<TabBar>().firstOrNull()
            }
            tabBar?.updateTabName(0, "Untitled", null)
            return
        }

        editors.removeAt(index)
        if (currentEditorIndex >= editors.size) {
            currentEditorIndex = editors.size - 1
        }
        switchToTab(currentEditorIndex)
    }
    
    fun getCurrentEditor() = editors[currentEditorIndex]
}