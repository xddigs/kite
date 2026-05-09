package org.kite.app.ui;

import javax.swing.*;
import java.awt.*;

public class SwingFrame extends JFrame {
    private static final String TITLE = "Kite";

    public SwingFrame() {
        super(TITLE);
        SwingPanel swingPanel = new SwingPanel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(swingPanel, BorderLayout.CENTER);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
