package br.com.gerenciadoreventos.ui;

import javax.swing.border.Border;
import java.awt.*;

public class RoundedBorder implements Border {
    private int raio;
    private Color corBorda;

    public RoundedBorder(int raio, Color corBorda) {
        this.raio = raio;
        this.corBorda = corBorda;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(raio / 2, raio, raio / 2, raio);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(corBorda);
        g2.drawRoundRect(x, y, width - 1, height - 1, raio, raio);
        g2.dispose();
    }
}