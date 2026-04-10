package ui;

import javax.swing.*;
import java.awt.*;

public class RddButton extends JButton {

    private boolean hovered = false;

    public RddButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setBackground(UI.BUTTON_BG);
        setForeground(UI.BUTTON_TEXT);
        setFont(UI.FONT_BUTTON);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hovered = true;
                setBackground(UI.BUTTON_BG_HOVER);
                repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hovered = false;
                setBackground(UI.BUTTON_BG);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = h;


        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, w, h, arc, arc);


        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int textX = (w - fm.stringWidth(getText())) / 2;
        int textY = (h + fm.getAscent()) / 2 - 2;
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        if (hovered) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(2));
            int w = getWidth();
            int h = getHeight();
            int arc = h;
            g2.drawRoundRect(1, 1, w-3, h-3, arc, arc);
            g2.dispose();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(180, 45);
    }
}
