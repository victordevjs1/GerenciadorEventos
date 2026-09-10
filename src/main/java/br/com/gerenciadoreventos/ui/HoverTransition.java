package br.com.gerenciadoreventos.ui;
import javax.swing.*;
import java.awt.*;

public class HoverTransition {

    public static void aplicar(JButton botao,
                               Color bgNormal, Color bgHover,
                               Color fgNormal, Color fgHover,
                               int duracaoMs) {
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.setBackground(bgNormal);
        botao.setForeground(fgNormal);

        Timer[] timerAtual = new Timer[1];

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                animar(botao, botao.getBackground(), bgHover,
                        botao.getForeground(), fgHover,
                        duracaoMs, timerAtual);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                animar(botao, botao.getBackground(), bgNormal,
                        botao.getForeground(), fgNormal,
                        duracaoMs, timerAtual);
            }
        });
    }

    private static void animar(JButton botao,
                               Color bgDe, Color bgPara,
                               Color fgDe, Color fgPara,
                               int duracaoMs, Timer[] timerAtual) {
        if (timerAtual[0] != null && timerAtual[0].isRunning()) {
            timerAtual[0].stop();
        }

        int passos = 20;
        int delay = Math.max(duracaoMs / passos, 1);
        int[] passoAtual = {0};

        timerAtual[0] = new Timer(delay, null);
        timerAtual[0].addActionListener(ev -> {
            passoAtual[0]++;
            float progresso = (float) passoAtual[0] / passos;

            if (progresso >= 1f) {
                botao.setBackground(bgPara);
                botao.setForeground(fgPara);
                timerAtual[0].stop();
            } else {
                botao.setBackground(interpolar(bgDe, bgPara, progresso));
                botao.setForeground(interpolar(fgDe, fgPara, progresso));
            }
            botao.repaint();
        });
        timerAtual[0].start();
    }

    private static Color interpolar(Color de, Color para, float progresso) {
        int r = (int) (de.getRed()   + (para.getRed()   - de.getRed())   * progresso);
        int g = (int) (de.getGreen() + (para.getGreen() - de.getGreen()) * progresso);
        int b = (int) (de.getBlue()  + (para.getBlue()  - de.getBlue())  * progresso);
        return new Color(r, g, b);
    }
}