package br.com.gerenciadoreventos.view.components;

import br.com.gerenciadoreventos.service.DashboardService;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class DashboardChart extends JPanel {

    private final DashboardService service = new DashboardService();

    public DashboardChart() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titulo = new JLabel("Eventos por mês");

        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(new Color(40, 40, 40));

        add(titulo, BorderLayout.NORTH);

        // Busca os dados do banco
        Map<Integer, Integer> eventosPorMes =
                service.obterEventosPorMes();

        // Cria o gráfico com os dados
        add(
                new Grafico(eventosPorMes),
                BorderLayout.CENTER
        );
    }

    // =========================================================
    // GRÁFICO
    // =========================================================

    private static class Grafico extends JPanel {

        private final Map<Integer, Integer> eventosPorMes;

        private final String[] meses = {
                "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                "Jul", "Ago", "Set", "Out", "Nov", "Dez"
        };

        public Grafico(Map<Integer, Integer> eventosPorMes) {

            this.eventosPorMes = eventosPorMes;

            setBackground(Color.WHITE);

            setPreferredSize(new Dimension(700, 250));
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int largura = getWidth();
            int altura = getHeight();

            // Margens do gráfico
            int margemEsquerda = 40;
            int margemDireita = 20;
            int margemSuperior = 20;
            int margemInferior = 40;

            int larguraGrafico =
                    largura - margemEsquerda - margemDireita;

            int alturaGrafico =
                    altura - margemSuperior - margemInferior;

            // =================================================
            // DESCOBRIR MAIOR VALOR
            // =================================================

            int maiorValor = 0;

            for (int mes = 1; mes <= 12; mes++) {

                int quantidade =
                        eventosPorMes.getOrDefault(mes, 0);

                if (quantidade > maiorValor) {
                    maiorValor = quantidade;
                }
            }

            // Evita divisão por zero
            if (maiorValor == 0) {
                maiorValor = 1;
            }

            // =================================================
            // LINHAS HORIZONTAIS
            // =================================================

            g2.setColor(new Color(230, 230, 230));
            g2.setStroke(new BasicStroke(1));

            int quantidadeLinhas = 5;

            for (int i = 0; i <= quantidadeLinhas; i++) {

                int y =
                        margemSuperior
                                + alturaGrafico
                                - (i * alturaGrafico / quantidadeLinhas);

                g2.drawLine(
                        margemEsquerda,
                        y,
                        largura - margemDireita,
                        y
                );
            }

            // =================================================
            // EIXO INFERIOR
            // =================================================

            g2.setColor(new Color(180, 180, 180));

            int baseY =
                    margemSuperior + alturaGrafico;

            g2.drawLine(
                    margemEsquerda,
                    baseY,
                    largura - margemDireita,
                    baseY
            );

            // =================================================
            // BARRAS
            // =================================================

            int quantidadeMeses = 12;

            int espacamento =
                    larguraGrafico / quantidadeMeses;

            int larguraBarra =
                    Math.min(35, espacamento - 15);

            for (int i = 0; i < quantidadeMeses; i++) {

                int mes = i + 1;

                int quantidade =
                        eventosPorMes.getOrDefault(mes, 0);

                // Calcula altura proporcional da barra
                int alturaBarra =
                        (quantidade * alturaGrafico)
                                / maiorValor;

                int x =
                        margemEsquerda
                                + i * espacamento
                                + (espacamento - larguraBarra) / 2;

                int y =
                        baseY - alturaBarra;

                // Barra
                g2.setColor(new Color(122, 162, 247));

                g2.fillRoundRect(
                        x,
                        y,
                        larguraBarra,
                        alturaBarra,
                        8,
                        8
                );

                // =================================================
                // QUANTIDADE ACIMA DA BARRA
                // =================================================

                if (quantidade > 0) {

                    g2.setColor(new Color(70, 70, 70));

                    g2.setFont(
                            new Font(
                                    "Arial",
                                    Font.BOLD,
                                    11
                            )
                    );

                    String texto =
                            String.valueOf(quantidade);

                    FontMetrics fm =
                            g2.getFontMetrics();

                    int textoX =
                            x
                                    + (larguraBarra
                                    - fm.stringWidth(texto)) / 2;

                    int textoY =
                            y - 5;

                    g2.drawString(
                            texto,
                            textoX,
                            textoY
                    );
                }

                // =================================================
                // NOME DO MÊS
                // =================================================

                g2.setColor(new Color(100, 100, 100));

                g2.setFont(
                        new Font(
                                "Arial",
                                Font.PLAIN,
                                11
                        )
                );

                String nomeMes = meses[i];

                FontMetrics fm =
                        g2.getFontMetrics();

                int textoMesX =
                        x
                                + (larguraBarra
                                - fm.stringWidth(nomeMes)) / 2;

                g2.drawString(
                        nomeMes,
                        textoMesX,
                        baseY + 20
                );
            }

            g2.dispose();
        }
    }

    // =========================================================
    // BORDA ARREDONDADA
    // =========================================================

    private static class RoundedBorder
            implements javax.swing.border.Border {

        private final int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(
                    radius,
                    radius,
                    radius,
                    radius
            );
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
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

            g2.setColor(new Color(225, 225, 225));

            g2.drawRoundRect(
                    x,
                    y,
                    width - 1,
                    height - 1,
                    radius,
                    radius
            );

            g2.dispose();
        }
    }
}