package br.com.gerenciadoreventos.view.components;

import br.com.gerenciadoreventos.view.Login;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.view.Dashboard;
import br.com.gerenciadoreventos.view.Eventos;
import br.com.gerenciadoreventos.view.Login;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Sidebar extends JPanel {

    private static final Color SIDEBAR =
            new Color(15, 23, 42);

    private static final Color SIDEBAR_HOVER =
            new Color(30, 41, 59);

    private static final Color AZUL =
            new Color(37, 99, 235);

    private static final Color BRANCO =
            Color.WHITE;

    private static final Font NORMAL =
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    13
            );

    public Sidebar(JFrame janelaAtual,Usuario usuarioLogado,String paginaAtiva) {

        setPreferredSize(
                new Dimension(240, 0)
        );

        setBackground(SIDEBAR);

        setLayout(
                new BorderLayout()
        );

        add(
                criarLogo(),
                BorderLayout.NORTH
        );

        add(
                criarMenu(
                        janelaAtual,
                        usuarioLogado,
                        paginaAtiva
                ),
                BorderLayout.CENTER
        );

        add(
                criarSair(janelaAtual),
                BorderLayout.SOUTH
        );
    }

    private JPanel criarLogo() {

        JPanel painel = new JPanel();

        painel.setLayout(
                new BoxLayout(
                        painel,
                        BoxLayout.Y_AXIS
                )
        );

        painel.setBackground(SIDEBAR);

        painel.setBorder(
                new EmptyBorder(
                        22,
                        20,
                        25,
                        20
                )
        );

        JLabel logo =
                labelIcone(
                        "Eventos",
                        FontAwesomeSolid.CALENDAR_ALT,
                        BRANCO,
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                20
                        )
                );

        JLabel subtitulo =
                new JLabel(
                        "Gerenciador Escolar"
                );

        subtitulo.setForeground(
                new Color(148, 163, 184)
        );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        painel.add(logo);

        painel.add(
                Box.createVerticalStrut(4)
        );

        painel.add(subtitulo);

        return painel;
    }

    private JPanel criarMenu(JFrame janelaAtual,Usuario usuarioLogado,String paginaAtiva) {

        JPanel menu = new JPanel();

        menu.setLayout(
                new BoxLayout(
                        menu,
                        BoxLayout.Y_AXIS
                )
        );

        menu.setBackground(SIDEBAR);

        menu.setBorder(
                new EmptyBorder(
                        5,
                        14,
                        10,
                        14
                )
        );

        JButton dashboard = item(
                FontAwesomeSolid.TH_LARGE,
                "Dashboard",
                paginaAtiva.equals("dashboard")
        );

        dashboard.addActionListener(e -> {

            if (!paginaAtiva.equals("dashboard")) {

                new Dashboard(
                        usuarioLogado
                ).setVisible(true);

                janelaAtual.dispose();
            }
        });

        adicionarSecao(
                menu,
                "PRINCIPAL",
                dashboard
        );

        menu.add(
                Box.createVerticalStrut(18)
        );

        JButton eventos = item(
                FontAwesomeSolid.CALENDAR_ALT,
                "Eventos",
                paginaAtiva.equals("eventos")
        );

        eventos.addActionListener(e -> {

            if (!paginaAtiva.equals("eventos")) {

                new Eventos(
                        usuarioLogado
                ).setVisible(true);

                janelaAtual.dispose();
            }
        });

        JButton alunos = item(
                FontAwesomeSolid.USER_GRADUATE,
                "Alunos",
                paginaAtiva.equals("alunos")
        );

        JButton inscricoes = item(
                FontAwesomeSolid.CHECK,
                "Inscrições",
                paginaAtiva.equals("inscricoes")
        );

        JButton atividades = item(
                FontAwesomeSolid.TASKS,
                "Atividades",
                paginaAtiva.equals("atividades")
        );

        adicionarSecao(
                menu,
                "GESTÃO",
                eventos,
                alunos,
                inscricoes,
                atividades
        );

        menu.add(
                Box.createVerticalStrut(18)
        );

        adicionarSecao(
                menu,
                "RELATÓRIOS",

                item(
                        FontAwesomeSolid.CHART_BAR,
                        "Relatórios",
                        false
                )
        );

        menu.add(
                Box.createVerticalStrut(18)
        );

        adicionarSecao(
                menu,
                "CONFIGURAÇÃO",

                item(
                        FontAwesomeSolid.COG,
                        "Configurações",
                        false
                )
        );

        return menu;
    }

    private void adicionarSecao(
            JPanel menu,
            String titulo,
            JButton... itens
    ) {

        JLabel categoria =
                new JLabel(titulo);

        categoria.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        10
                )
        );

        categoria.setForeground(
                new Color(100, 116, 139)
        );

        categoria.setBorder(
                new EmptyBorder(
                        8,
                        12,
                        8,
                        0
                )
        );

        menu.add(categoria);

        for (JButton item : itens) {
            menu.add(item);
        }
    }

    private JButton item(
            Ikon icone,
            String texto,
            boolean ativo
    ) {

        JButton botao = new JButton();

        botao.setPreferredSize(
                new Dimension(210, 44)
        );

        botao.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        44
                )
        );

        botao.setBorder(
                new EmptyBorder(
                        0,
                        12,
                        0,
                        12
                )
        );

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);

        botao.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        Color corTexto =
                ativo
                        ? BRANCO
                        : new Color(226, 232, 240);

        Color corIcone =
                ativo
                        ? BRANCO
                        : new Color(148, 163, 184);

        JLabel label =
                labelIcone(
                        texto,
                        icone,
                        corTexto,
                        NORMAL
                );

        label.setIconTextGap(10);

        FontIcon icon =
                FontIcon.of(icone);

        icon.setIconSize(17);

        icon.setIconColor(corIcone);

        label.setIcon(icon);

        botao.add(label);

        botao.setBackground(
                ativo
                        ? AZUL
                        : SIDEBAR
        );

        if (!ativo) {

            botao.addMouseListener(
                    new java.awt.event.MouseAdapter() {

                        @Override
                        public void mouseEntered(
                                java.awt.event.MouseEvent e
                        ) {
                            botao.setBackground(
                                    SIDEBAR_HOVER
                            );
                        }

                        @Override
                        public void mouseExited(
                                java.awt.event.MouseEvent e
                        ) {
                            botao.setBackground(
                                    SIDEBAR
                            );
                        }
                    }
            );
        }

        return botao;
    }

    private JPanel criarSair(JFrame dashboard) {

        JPanel painel =
                new JPanel(
                        new BorderLayout()
                );

        painel.setBackground(SIDEBAR);

        painel.setBorder(
                new EmptyBorder(
                        10,
                        14,
                        20,
                        14
                )
        );

        JButton sair =
                item(
                        FontAwesomeSolid.SIGN_OUT_ALT,
                        "Sair",
                        false
                );

        sair.addActionListener(e -> {

            new Login().setVisible(true);

            dashboard.dispose();
        });

        painel.add(sair);

        return painel;
    }

    private JLabel labelIcone(
            String texto,
            Ikon icone,
            Color cor,
            Font fonte
    ) {

        FontIcon icon =
                FontIcon.of(icone);

        icon.setIconSize(18);

        icon.setIconColor(cor);

        JLabel label =
                new JLabel(
                        texto,
                        icon,
                        JLabel.LEFT
                );

        label.setFont(fonte);

        label.setForeground(cor);

        return label;
    }
}