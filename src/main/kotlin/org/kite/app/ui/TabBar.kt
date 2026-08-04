package org.kite.app.ui

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import kotlin.system.exitProcess

class TabBar(private val panel: Panel) : JPanel() {
    private val tabLabels = mutableListOf<JLabel>()
    private val tabExtensions = mutableListOf<String?>()
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

        addTab("Untitled", null)
    }

    fun addTab(name: String, extension: String?) {
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
                val ext = if (index in tabExtensions.indices) tabExtensions[index] else null
                drawIcon(g2, ext, xOffset + 10, height / 2 - 4)

                super.paintComponent(g)
            }

            private fun drawIcon(g2: Graphics2D, extension: String?, x: Int, y: Int) {
                val (icon, color) = when (extension?.lowercase()) {
                    "py"        -> "\uE73C" to Color(0x37, 0x76, 0xAB)
                    "java"      -> "\uE738" to Color(0xE7, 0x6F, 0x51)
                    "kt", "kts" -> "\uE634" to Color(0x7F, 0x52, 0xFF)
                    else        -> "\uF15C" to Color(0x9E, 0x9E, 0x9E)
                }

                val oldColor = g2.color
                val oldFont = g2.font
                g2.color = color
                g2.font = FontManager.JETBRAINS_MONO.deriveFont(Font.PLAIN, 20f)
                g2.drawString(icon, x, y + 12)

                g2.color = oldColor
                g2.font = oldFont
            }
        }
        
        label.foreground = EditorTheme.TEXT_COLOR
        label.font = FontManager.JETBRAINS_MONO
        label.preferredSize = Dimension(140, 35)
        label.horizontalAlignment = SwingConstants.LEFT
        label.border = BorderFactory.createEmptyBorder(5, 35, 0, 5)

        label.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val index = tabLabels.indexOf(label)
                if (SwingUtilities.isRightMouseButton(e)) {
                    closeTabAt(index)
                } else {
                    activeTabIndex = index
                    panel.switchToTab(index)
                    repaint()
                }
            }
        })

        tabLabels.add(label)
        tabExtensions.add(extension)
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

    fun updateTabName(index: Int, name: String, extension: String?) {
        if (index in tabLabels.indices) {
            tabLabels[index].text = name
            tabExtensions[index] = extension
            repaint()
        }
    }

    fun closeTabAt(index: Int) {
        if (index !in tabLabels.indices) return

        panel.closeTab(index)
        val label = tabLabels.removeAt(index)
        tabExtensions.removeAt(index)
        tabContainer.remove(label)

        if (tabLabels.isEmpty()) {
            exitProcess(0)
        }

        if (activeTabIndex >= tabLabels.size) {
            activeTabIndex = tabLabels.size - 1
        } else if (index < activeTabIndex) {
            activeTabIndex--
        }

        panel.switchToTab(activeTabIndex)
        revalidate()
        repaint()
    }

    fun closeCurrentTab() {
        closeTabAt(activeTabIndex)
    }
}
