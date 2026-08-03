package org.kite.app.ui

import org.kite.app.core.Editor
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class TabBar(private val editor: Editor) : JPanel() {

    private val tabLabel = JLabel("Untitled")

    init {
        layout = FlowLayout(FlowLayout.LEFT, 0, 0)
        background = EditorTheme.BAR_BACKGROUND
        preferredSize = Dimension(0, 35)

        val tabContainer = JPanel(FlowLayout(FlowLayout.LEFT, 15, 5))
        tabContainer.background = EditorTheme.TAB_ACTIVE_COLOR
        
        tabLabel.foreground = EditorTheme.TEXT_COLOR
        tabLabel.font = FontManager.JETBRAINS_MONO
        
        tabLabel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    closeCurrentTab()
                }
            }
        })

        tabContainer.add(tabLabel)
        add(tabContainer)
    }

    fun updateFileName(name: String) {
        tabLabel.text = name
        revalidate()
        repaint()
    }

    fun closeCurrentTab() {
        editor.setLines(mutableListOf(""), null)
    }
}
