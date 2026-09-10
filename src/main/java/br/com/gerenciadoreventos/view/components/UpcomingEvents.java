package br.com.gerenciadoreventos.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UpcomingEvents extends JPanel {

    private static final Color BRANCO =
            Color.WHITE;

    private static final Color TEXTO =
            new Color(15, 23, 42);

    private static final Color SECUNDARIO =
            new Color(100, 116, 139);

    private static final Color BORDA =
            new Color(226, 232, 240);

    public UpcomingEvents() {

        setLayout(
                new BorderLayout()
        );

        setBackground(BRANCO);

        setBorder(
                new RoundedBorder(
                        14,
                        BORDA
                )
        );

        add(
                criarTitulo(),
                BorderLayout.NORTH
        );

        add(
                criarLista(),
                BorderLayout.CENTER
        );
    }

    private JLabel criarTitulo() {

        JLabel titulo =
                new JLabel(
                        "Próximos Eventos"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        titulo.setForeground(TEXTO);

        titulo.setBorder(
                new EmptyBorder(
                        15,
                        18,
                        10,
                        18
                )
        );

        return titulo;
    }

    private JPanel criarLista() {

        JPanel lista = new JPanel();

        lista.setLayout(
                new BoxLayout(
                        lista,
                        BoxLayout.Y_AXIS
                )
        );

        lista.setBackground(BRANCO);

        lista.setBorder(
                new EmptyBorder(
                        0,
                        15,
                        15,
                        15
                )
        );

        lista.add(
                criarEvento(
                        "Olimpíadas",
                        "15/09",
                        "7:00 - 14:00"
                )
        );

        lista.add(
                Box.createVerticalStrut(12)
        );

        lista.add(
                criarEvento(
                        "Semana de TI",
                        "26/09",
                        "7:30 - 18:20"
                )
        );

        return lista;
    }

    private JPanel criarEvento(
            String nome,
            String data,
            String horario
    ) {

        JPanel painel = new JPanel();

        painel.setLayout(
                new BoxLayout(
                        painel,
                        BoxLayout.Y_AXIS
                )
        );

        painel.setBackground(BRANCO);

        JLabel titulo =
                new JLabel(nome);

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        titulo.setForeground(TEXTO);

        JLabel info =
                new JLabel(
                        data
                                + "  •  "
                                + horario
                );

        info.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        info.setForeground(SECUNDARIO);

        painel.add(titulo);

        painel.add(
                Box.createVerticalStrut(4)
        );

        painel.add(info);

        return painel;
    }

    private static class RoundedBorder
            extends javax.swing.border.AbstractBorder {

        private final int raio;
        private final Color cor;

        RoundedBorder(
                int raio,
                Color cor
        ) {
            this.raio = raio;
            this.cor = cor;
        }

        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x,
                int y,
                int width,
                int height
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(cor);

            g2.drawRoundRect(
                    x,
                    y,
                    width - 1,
                    height - 1,
                    raio,
                    raio
            );

            g2.dispose();
        }
    }
}