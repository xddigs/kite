package org.kite.app.ui

import org.kite.app.core.Editor
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class TabBar(private val panel: Panel) : JPanel() {

    private val tabLabels = mutableListOf<JLabel>()
    private var activeTabIndex = 0
    private val tabContainer = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))

    init {
        layout = BorderLayout()
        background = EditorTheme.BAR_BACKGROUND
        preferredSize = Dimension(0, 35)

        tabContainer.background = EditorTheme.BAR_BACKGROUND
        add(tabContainer, BorderLayout.WEST)

        val addButton = JLabel("+")
        addButton.foreground = EditorTheme.TEXT_COLOR
        addButton.font = FontManager.JETBRAINS_MONO.deriveFont(Font.BOLD, 18f)
        addButton.border = BorderFactory.createEmptyBorder(0, 10, 0, 10)
        addButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                panel.addNewTab()
            }
        })
        add(addButton, BorderLayout.CENTER)

        addTab("Untitled")
    }

    fun addTab(name: String) {
        val label = object : JLabel(name) {
            var xOffset = 20
            var opacity = 0f

            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                
                val index = tabLabels.indexOf(this)
                if (index == activeTabIndex) {
                    g2.color = EditorTheme.TAB_ACTIVE_COLOR
                } else {
                    g2.color = EditorTheme.BAR_BACKGROUND
                }
                
                g2.fillRoundRect(xOffset, 5, width - 10 - xOffset, height - 5, 15, 15)
                
                g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity)
                super.paintComponent(g)
            }
        }
        
        label.foreground = EditorTheme.TEXT_COLOR
        label.font = FontManager.JETBRAINS_MONO
        label.preferredSize = Dimension(120, 35)
        label.horizontalAlignment = SwingConstants.CENTER
        label.border = BorderFactory.createEmptyBorder(5, 5, 0, 5)

        label.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val index = tabLabels.indexOf(label)
                if (SwingUtilities.isRightMouseButton(e)) {
                    panel.closeTab(index)
                    tabLabels.removeAt(index)
                    tabContainer.remove(label)
                    if (activeTabIndex >= tabLabels.size) {
                        activeTabIndex = tabLabels.size - 1
                    }
                    revalidate()
                    repaint()
                } else {
                    activeTabIndex = index
                    panel.switchToTab(index)
                    repaint()
                }
            }
        })

        tabLabels.add(label)
        tabContainer.add(label)
        
        val timer = Timer(10) { e ->
            var finished = true
            if (label.xOffset > 0) {
                label.xOffset -= 2
                finished = false
            }
            if (label.opacity < 1f) {
                label.opacity += 0.1f
                if (label.opacity > 1f) label.opacity = 1f
                finished = false
            }
            label.repaint()
            if (finished) (e.source as Timer).stop()
        }
        timer.start()

        activeTabIndex = tabLabels.size - 1
        revalidate()
        repaint()
    }

    fun updateTabName(index: Int, name: String) {
        if (index in tabLabels.indices) {
            tabLabels[index].text = name
            repaint()
        }
    }

    fun closeCurrentTab() {
        panel.closeTab(activeTabIndex)
        val label = tabLabels.removeAt(activeTabIndex)
        tabContainer.remove(label)
        if (activeTabIndex >= tabLabels.size) {
            activeTabIndex = tabLabels.size - 1
        }
        revalidate()
        repaint()
    }
}
