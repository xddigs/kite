package org.kite.app.ui

import org.kite.app.core.Editor
import org.kite.app.core.FileManager
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import kotlin.system.exitProcess

class MenuBar(private var editor: Editor, private val frame: JFrame) : JPanel() {

    fun updateEditor(newEditor: Editor) {
        this.editor = newEditor
    }

    init {
        layout = FlowLayout(FlowLayout.LEFT, 10, 5)
        background = EditorTheme.BAR_BACKGROUND
        preferredSize = Dimension(0, 35)

        add(createMenuLabel("File", createFileMenu()))
        add(createMenuLabel("Editor", createEditorMenu()))
        add(createMenuLabel("Help", createHelpMenu()))
    }

    private fun createMenuLabel(text: String, menu: JPopupMenu): JLabel {
        val label = JLabel(text)
        label.foreground = EditorTheme.TEXT_COLOR
        label.font = FontManager.JETBRAINS_MONO
        label.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                menu.show(label, 0, label.height)
            }
            override fun mouseEntered(e: MouseEvent) {
                label.isOpaque = true
                label.background = EditorTheme.BACKGROUND_COLOR
                label.repaint()
            }
            override fun mouseExited(e: MouseEvent) {
                label.isOpaque = false
                label.repaint()
            }
        })
        return label
    }

    private fun createFileMenu(): JPopupMenu {
        val menu = JPopupMenu()
        menu.background = EditorTheme.BAR_BACKGROUND
        menu.border = BorderFactory.createLineBorder(EditorTheme.BACKGROUND_COLOR)

        val openItem = createMenuItem("Open") { openFile() }
        val saveItem = createMenuItem("Save") { saveFile() }
        val saveAsItem = createMenuItem("Save As") { saveFileAs() }
        val exitItem = createMenuItem("Exit") { exitProcess(0) }

        menu.add(openItem)
        menu.add(saveItem)
        menu.add(saveAsItem)
        menu.addSeparator()
        menu.add(exitItem)

        return menu
    }

    private fun createEditorMenu(): JPopupMenu {
        val menu = JPopupMenu()
        menu.background = EditorTheme.BAR_BACKGROUND
        menu.border = BorderFactory.createLineBorder(EditorTheme.BACKGROUND_COLOR)
        menu.add(createMenuItem("Cut") {})
        menu.add(createMenuItem("Copy") {})
        menu.add(createMenuItem("Paste") {})
        return menu
    }

    private fun createHelpMenu(): JPopupMenu {
        val menu = JPopupMenu()
        menu.background = EditorTheme.BAR_BACKGROUND
        menu.border = BorderFactory.createLineBorder(EditorTheme.BACKGROUND_COLOR)
        menu.add(createMenuItem("About") {
            JOptionPane.showMessageDialog(frame, "Kite Editor v1.0", "About", JOptionPane.INFORMATION_MESSAGE)
        })
        return menu
    }

    private fun createMenuItem(text: String, action: () -> Unit): JMenuItem {
        val item = JMenuItem(text)
        item.isOpaque = true
        item.background = EditorTheme.BAR_BACKGROUND
        item.foreground = EditorTheme.TEXT_COLOR
        item.font = FontManager.JETBRAINS_MONO
        item.border = BorderFactory.createEmptyBorder(5, 10, 5, 10)
        item.addActionListener { action() }
        return item
    }

    private fun stylizeFileChooser(chooser: JFileChooser) {
        chooser.background = EditorTheme.BAR_BACKGROUND
        chooser.foreground = EditorTheme.TEXT_COLOR
        
        fun applyTheme(container: Container) {
            for (c in container.components) {
                c.background = EditorTheme.BAR_BACKGROUND
                c.foreground = EditorTheme.TEXT_COLOR
                c.font = FontManager.JETBRAINS_MONO
                if (c is JComponent && c !is JViewport) {
                    c.border = BorderFactory.createEmptyBorder()
                }
                if (c is Container) {
                    applyTheme(c)
                }
            }
        }
        applyTheme(chooser)
    }

    fun openFile() {
        val chooser = JFileChooser()
        stylizeFileChooser(chooser)
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            val file = chooser.selectedFile
            val lines = FileManager.load(file)
            editor.setLines(lines, file)
        }
    }

    fun saveFile() {
        if (editor.currentFile == null) {
            saveFileAs()
        } else {
            editor.save()
        }
    }

    fun saveFileAs() {
        val chooser = JFileChooser()
        stylizeFileChooser(chooser)
        if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            val file = chooser.selectedFile
            editor.saveAs(file)
        }
    }
}
