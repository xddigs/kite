package org.kite.app.ui;

import org.kite.app.core.Editor;

import javax.swing.*;
import java.awt.*;

public class SwingPanel extends JPanel {

    public SwingPanel() {
        setLayout(new BorderLayout());
        setBackground(EditorTheme.BACKGROUND_COLOR);

        Editor editor = new Editor();
        add(editor, BorderLayout.CENTER);
    }
}
