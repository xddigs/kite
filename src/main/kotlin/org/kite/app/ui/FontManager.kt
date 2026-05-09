package org.kite.app.ui

import java.awt.Font

object FontManager {

    val JETBRAINS_MONO: Font by lazy {
        val stream = FontManager::class.java
            .getResourceAsStream("/fonts/JetBrainsMonoNL-Regular.ttf")

        Font.createFont(Font.TRUETYPE_FONT, stream)
            .deriveFont(15f)
    }
}