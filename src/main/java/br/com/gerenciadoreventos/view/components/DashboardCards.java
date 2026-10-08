package br.com.gerenciadoreventos.view.components;

import br.com.gerenciadoreventos.service.DashboardService;
import br.com.gerenciadoreventos.theme.ThemeManager;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardCards extends JPanel {

    private static final Color FUNDO =
            new Color(246, 248, 252);

    private static final Color BRANCO =
            Color.WHITE;

    private static final Color TEXTO =
            new Color(15, 23, 42);

    private static final Color SECUNDARIO =
            new Color(100, 116, 139);

    private static final Color BORDA =
            new Color(226, 232, 240);

    private static final Color AZUL =
            new Color(37, 99, 235);

    private final DashboardService service =
            new DashboardService();

    public DashboardCards() {

        setLayout(
                new GridLayout(
                        1,
                        4,
                        15,
                        0
                )
        );

        setBackground(ThemeManager.getFundo());

        setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        110
                )
        );

        carregarCards();
    }

    private void carregarCards() {

        int eventos =
                service.contarEventos();

        int inscritos =
                service.contarInscritos();

        int abertos =
                service.contarEventosAbertos();

        int hoje =
                service.contarEventosHoje();

        add(
                criarCard(
                        "Eventos",
                        eventos,
                        FontAwesomeSolid.CALENDAR_ALT,
                        new Color(239, 246, 255),
                        AZUL
                )
        );

        add(
                criarCard(
                        "Inscritos",
                        inscritos,
                        FontAwesomeSolid.USER_GRADUATE,
                        new Color(240, 253, 250),
                        new Color(16, 185, 129)
                )
        );

        add(
                criarCard(
                        "Abertos",
                        abertos,
                        FontAwesomeSolid.CHECK_CIRCLE,
                        new Color(245, 243, 255),
                        new Color(124, 58, 237)
                )
        );

        add(
                criarCard(
                        "Hoje",
                        hoje,
                        FontAwesomeSolid.CALENDAR_CHECK,
                        new Color(255, 247, 237),
                        new Color(234, 88, 12)
                )
        );
    }

    private JPanel criarCard(
            String titulo,
            int valor,
            Ikon icone,
            Color fundoIcone,
            Color corIcone
    ) {

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setBackground(ThemeManager.getPainel());

        card.setBorder(
                new RoundedBorder(14)
        );

        card.setPreferredSize(
                new Dimension(0, 110)
        );

        JPanel conteudo =
                new JPanel(
                        new BorderLayout()
                );

        conteudo.setBackground(ThemeManager.getPainel());

        conteudo.setBorder(
                new EmptyBorder(
                        12,
                        15,
                        12,
                        15
                )
        );

        JPanel iconePanel =
                new JPanel(
                        new GridBagLayout()
                );

        iconePanel.setPreferredSize(
                new Dimension(42, 42)
        );

        // Sem quadrado claro atrás do ícone no modo escuro.
        iconePanel.setOpaque(false);

        FontIcon icon =
                FontIcon.of(icone);

        icon.setIconSize(19);
        icon.setIconColor(corIcone);

        iconePanel.add(
                new JLabel(icon)
        );

        JPanel textos =
                new JPanel();

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        textos.setBackground(ThemeManager.getPainel());

        textos.setBorder(
                new EmptyBorder(
                        0,
                        12,
                        0,
                        0
                )
        );

        JLabel lblTitulo =
                new JLabel(titulo);

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        lblTitulo.setForeground(
                ThemeManager.getTextoSecundario()
        );

        JLabel lblValor =
                new JLabel(
                        String.valueOf(valor)
                );

        lblValor.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24
                )
        );

        lblValor.setForeground(ThemeManager.getTexto());

        textos.add(lblTitulo);

        textos.add(
                Box.createVerticalStrut(3)
        );

        textos.add(lblValor);

        conteudo.add(
                iconePanel,
                BorderLayout.WEST
        );

        conteudo.add(
                textos,
                BorderLayout.CENTER
        );

        card.add(conteudo);

        return card;
    }

    private static class RoundedBorder
            extends javax.swing.border.AbstractBorder {

        private final int raio;

        RoundedBorder(int raio) {
            this.raio = raio;
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

            g2.setColor(
                    ThemeManager.isModoEscuro()
                            ? ThemeManager.getBorda()
                            : BORDA
            );

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

        @Override
        public Insets getBorderInsets(
                Component c
        ) {

            return new Insets(
                    1,
                    1,
                    1,
                    1
            );
        }
    }
}