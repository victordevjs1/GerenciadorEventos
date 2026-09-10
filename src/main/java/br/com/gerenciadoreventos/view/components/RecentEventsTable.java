package br.com.gerenciadoreventos.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RecentEventsTable extends JPanel {

    private static final Color BRANCO =
            Color.WHITE;

    private static final Color TEXTO =
            new Color(15, 23, 42);

    private static final Color SECUNDARIO =
            new Color(100, 116, 139);

    private static final Color BORDA =
            new Color(226, 232, 240);

    private static final Color AZUL_CLARO =
            new Color(239, 246, 255);

    public RecentEventsTable() {

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
                criarTabela(),
                BorderLayout.CENTER
        );
    }

    private JLabel criarTitulo() {

        JLabel titulo =
                new JLabel(
                        "Eventos Recentes"
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

    private JScrollPane criarTabela() {

        String[] colunas = {
                "Evento",
                "Data",
                "Inscritos",
                "Status"
        };

        Object[][] dados = {
                {
                        "Interclasse",
                        "15/09",
                        "125",
                        "Aberto"
                },
                {
                        "Semana de TI",
                        "26/09",
                        "80",
                        "Aberto"
                },
                {
                        "Feira de Projetos",
                        "20/06",
                        "300",
                        "Fechado"
                }
        };

        JTable tabela =
                new JTable(
                        dados,
                        colunas
                );

        tabela.setRowHeight(30);

        tabela.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        tabela.setForeground(TEXTO);
        tabela.setBackground(BRANCO);
        tabela.setGridColor(BORDA);

        tabela.setSelectionBackground(
                AZUL_CLARO
        );

        tabela.setSelectionForeground(TEXTO);

        tabela.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                11
                        )
                );

        tabela.getTableHeader()
                .setForeground(SECUNDARIO);

        tabela.getTableHeader()
                .setBackground(BRANCO);

        return new JScrollPane(tabela);
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