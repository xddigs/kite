package org.kite.app.ui

import java.awt.Font

object FontManager {

    val JETBRAINS_MONO: Font by lazy {
        val stream = FontManager::class.java
            .getResourceAsStream("/fonts/JetBrainsMonoNLNerdFontMono-Regular.ttf")

        Font.createFont(Font.TRUETYPE_FONT, stream)
            .deriveFont(15f)
    }

    val JETBRAINS_MONO_BOLD: Font by lazy {
        val stream = FontManager::class.java
            .getResourceAsStream("/fonts/JetBrainsMonoNLNerdFontMono-Semibold.ttf")

        Font.createFont(Font.TRUETYPE_FONT, stream)
            .deriveFont(15f)
    }
}