package org.kite.app.core;

import org.kite.app.ui.EditorTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Editor extends JComponent implements
        KeyListener, MouseListener,
        MouseMotionListener, MouseWheelListener {

    private final List<String> lines = new ArrayList<>();
    private int caretRow = 0;
    private int caretCol = 0;
    private int scrollY = 0;
    private static final int LINE_HEIGHT = 18;
    private static final int PADDING = 24;

    public Editor() {
        setFocusable(true);
        setBackground(EditorTheme.BACKGROUND_COLOR);
        setForeground(EditorTheme.TEXT_COLOR);

        lines.add("");

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setFont(new Font("Monospaced", Font.PLAIN, 16));

        int y = PADDING - scrollY;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            g2.setColor(EditorTheme.TEXT_COLOR);
            g2.drawString(line, PADDING, y + LINE_HEIGHT);

            if (i == caretRow) {
                int caretX = PADDING + g2.getFontMetrics().stringWidth(
                        line.substring(0, Math.min(caretCol, line.length())));

                g2.setColor(EditorTheme.CURSOR_COLOR);
                g2.drawLine(caretX, y, caretX, y + LINE_HEIGHT);
            }

            y += LINE_HEIGHT;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();

        if (c == KeyEvent.VK_BACK_SPACE) {
            backspace();
            repaint();
            return;
        }

        if (c == KeyEvent.VK_ENTER) {
            newLine();
            repaint();
            return;
        }

        insertChar(c);
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> moveLeft();
            case KeyEvent.VK_RIGHT -> moveRight();
            case KeyEvent.VK_UP -> moveUp();
            case KeyEvent.VK_DOWN -> moveDown();
        }
        repaint();
    }

    private void moveLeft() {
        if (caretCol > 0) caretCol--;
        else if (caretRow > 0) {
            caretRow--;
            caretCol = lines.get(caretRow).length();
        }
    }

    private void moveRight() {
        if (caretCol < lines.get(caretRow).length()) caretCol++;
        else if (caretRow < lines.size() - 1) {
            caretRow++;
            caretCol = 0;
        }
    }

    private void moveUp() {
        if (caretRow > 0) {
            caretRow--;
            caretCol = Math.min(caretCol, lines.get(caretRow).length());
        }
    }

    private void moveDown() {
        if (caretRow < lines.size() - 1) {
            caretRow++;
            caretCol = Math.min(caretCol, lines.get(caretRow).length());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void insertChar(char c) {
        String line = lines.get(caretRow);

        String updated =
                line.substring(0, caretCol) +
                        c +
                        line.substring(caretCol);

        lines.set(caretRow, updated);
        caretCol++;
    }

    private void backspace() {
        if (caretCol == 0 && caretRow == 0) return;

        String line = lines.get(caretRow);

        if (caretCol > 0) {
            String updated =
                    line.substring(0, caretCol - 1) +
                            line.substring(caretCol);

            lines.set(caretRow, updated);
            caretCol--;
        } else {
            String prev = lines.get(caretRow - 1);
            caretCol = prev.length();
            lines.set(caretRow - 1, prev + line);
            lines.remove(caretRow);
            caretRow--;
        }
    }

    private void newLine() {
        String line = lines.get(caretRow);

        String left = line.substring(0, caretCol);
        String right = line.substring(caretCol);

        lines.set(caretRow, left);
        lines.add(caretRow + 1, right);

        caretRow++;
        caretCol = 0;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scrollY += e.getWheelRotation() * LINE_HEIGHT;
        scrollY = Math.max(0, scrollY);
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}