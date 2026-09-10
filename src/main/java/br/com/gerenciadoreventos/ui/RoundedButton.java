package br.com.gerenciadoreventos.ui;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    private int raio;

    public RoundedButton(String texto, int raio) {
        super(texto);
        this.raio = raio;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground()); // usa a cor atual do background
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public boolean contains(int x, int y) {
        Shape forma = new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), raio, raio);
        return forma.contains(x, y);
    }
}
