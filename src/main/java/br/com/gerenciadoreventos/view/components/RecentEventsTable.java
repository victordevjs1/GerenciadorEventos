package br.com.gerenciadoreventos.view.components;

import br.com.gerenciadoreventos.model.Evento;
import br.com.gerenciadoreventos.service.DashboardService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    private final DashboardService service =
            new DashboardService();

    private final DateTimeFormatter formatoData =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

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


    // =========================================================
    // TÍTULO
    // =========================================================

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


    // =========================================================
    // TABELA
    // =========================================================

    private JScrollPane criarTabela() {

        String[] colunas = {
                "Evento",
                "Data",
                "Inscritos",
                "Status"
        };


        // =====================================================
        // BUSCAR EVENTOS DO BANCO
        // =====================================================

        List<Evento> eventos =
                service.obterEventosRecentes(5);


        Object[][] dados =
                new Object[eventos.size()][4];


        for (int i = 0; i < eventos.size(); i++) {

            Evento evento = eventos.get(i);

            // Evento
            dados[i][0] =
                    evento.getNome();


            // Data
            if (evento.getDataInicio() != null) {

                dados[i][1] =
                        evento.getDataInicio()
                                .format(formatoData);

            } else {

                dados[i][1] =
                        "-";
            }


            // Inscritos
            dados[i][2] =
                    service.contarInscritosPorEvento(
                            evento.getId()
                    );


            // Status
            dados[i][3] =
                    formatarStatus(
                            evento.getStatus()
                    );
        }


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

        tabela.setSelectionForeground(
                TEXTO
        );


        tabela.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                11
                        )
                );


        tabela.getTableHeader()
                .setForeground(
                        SECUNDARIO
                );


        tabela.getTableHeader()
                .setBackground(
                        BRANCO
                );


        return new JScrollPane(tabela);
    }


    // =========================================================
    // FORMATA STATUS
    // =========================================================

    private String formatarStatus(String status) {

        if (status == null) {
            return "-";
        }

        return switch (status) {

            case "PLANEJADO" ->
                    "Planejado";

            case "ABERTO" ->
                    "Aberto";

            case "EM_ANDAMENTO" ->
                    "Em andamento";

            case "ENCERRADO" ->
                    "Encerrado";

            case "CANCELADO" ->
                    "Cancelado";

            default ->
                    status;
        };
    }


    // =========================================================
    // BORDA
    // =========================================================

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
