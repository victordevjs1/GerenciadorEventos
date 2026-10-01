package br.com.gerenciadoreventos.view.components;

import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.view.Dashboard;
import br.com.gerenciadoreventos.view.Login;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

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

    private static final Color TEXTO =
            new Color(226, 232, 240);

    private static final Color ICONE =
            new Color(148, 163, 184);

    private static final Font NORMAL =
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    13
            );

    // =====================================================
    // BOTÕES
    // =====================================================

    private JButton botaoDashboard;
    private JButton botaoEventos;
    private JButton botaoAlunos;
    private JButton botaoInscricoes;
    private JButton botaoResumo;
    private JButton botaoConfiguracoes;
    private JButton botaoComissoes;
    private JButton botaoProfessores;
    private JButton botaoAgentesExternos;

    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public Sidebar(
            Dashboard dashboard,
            Usuario usuarioLogado,
            String paginaAtiva
    ) {

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
                        dashboard,
                        usuarioLogado,
                        paginaAtiva
                ),
                BorderLayout.CENTER
        );

        add(
                criarSair(dashboard),
                BorderLayout.SOUTH
        );
    }

    // =====================================================
    // LOGO
    // =====================================================

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

    // =====================================================
    // MENU
    // =====================================================

    private JPanel criarMenu(
            Dashboard dashboard,
            Usuario usuarioLogado,
            String paginaAtiva
    ) {

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

        // =================================================
        // DASHBOARD
        // =================================================

        botaoDashboard =
                item(
                        FontAwesomeSolid.TH_LARGE,
                        "Dashboard",
                        paginaAtiva.equals("dashboard")
                );

        botaoDashboard.addActionListener(e ->
                dashboard.mostrarPagina("dashboard")
        );

        adicionarSecao(
                menu,
                "PRINCIPAL",
                botaoDashboard
        );

        menu.add(
                Box.createVerticalStrut(18)
        );

        // =================================================
        // EVENTOS
        // =================================================

        botaoEventos =
                item(
                        FontAwesomeSolid.CALENDAR_ALT,
                        "Eventos",
                        paginaAtiva.equals("eventos")
                );

        botaoEventos.addActionListener(e ->
                dashboard.mostrarPagina("eventos")
        );

        // =================================================
        // ALUNOS
        // =================================================

        botaoAlunos =
                item(
                        FontAwesomeSolid.USER_GRADUATE,
                        "Alunos",
                        paginaAtiva.equals("alunos")
                );

        botaoAlunos.addActionListener(e ->
                dashboard.mostrarPagina("alunos")
        );

        // =================================================
        // INSCRIÇÕES
        // =================================================

        botaoInscricoes =
                item(
                        FontAwesomeSolid.CHECK,
                        "Inscrições",
                        paginaAtiva.equals("inscricoes")
                );

        botaoInscricoes.addActionListener(e ->
                dashboard.mostrarPagina("inscricoes")
        );





        // =================================================
        // PROFESSORES
        // =================================================

        botaoProfessores =
                item(
                        FontAwesomeSolid.USER_TIE,
                        "Professores",
                        paginaAtiva.equals("professores")
                );

        botaoProfessores.addActionListener(e ->
                dashboard.mostrarPagina("professores")
        );

        // =================================================
        // COMISSÕES
        // =================================================

        botaoComissoes =
                item(
                        FontAwesomeSolid.USERS,
                        "Comissões",
                        paginaAtiva.equals("comissoes")
                );

        botaoComissoes.addActionListener(e ->
                dashboard.mostrarPagina("comissoes")
        );

        // =================================================
        // AGENTES EXTERNOS
        // =================================================

        botaoAgentesExternos =
                item(
                        FontAwesomeSolid.USER_TIE,
                        "Agentes Externos",
                        paginaAtiva.equals("agentesExternos")
                );

        botaoAgentesExternos.addActionListener(e ->
                dashboard.mostrarPagina("agentesExternos")
        );

        adicionarSecao(
                menu,
                "GESTÃO",
                botaoEventos,
                botaoAlunos,
                botaoProfessores,
                botaoInscricoes,
                botaoComissoes,
                botaoAgentesExternos

        );

        menu.add(
                Box.createVerticalStrut(18)
        );

        // =================================================
        // RELATÓRIOS
        // (a importação de presença via CSV vive dentro
        // desta página, como uma aba)
        // =================================================


        botaoResumo =
                item(
                        FontAwesomeSolid.CHART_BAR,
                        "Resumo",
                        paginaAtiva.equals("resumo")
                );

        botaoResumo.addActionListener(e ->
                dashboard.mostrarPagina("resumo")
        );
        adicionarSecao(
                menu,
                "RELATÓRIOS",

                botaoResumo
        );

        menu.add(
                Box.createVerticalStrut(18)
        );

        // =================================================
        // CONFIGURAÇÕES
        // =================================================

        botaoConfiguracoes =
                item(
                        FontAwesomeSolid.COG,
                        "Configurações",
                        paginaAtiva.equals("configuracoes")
                );

        botaoConfiguracoes.addActionListener(e ->
                dashboard.mostrarPagina("configuracoes")
        );

        adicionarSecao(
                menu,
                "CONFIGURAÇÃO",
                botaoConfiguracoes
        );

        return menu;
    }

    // =====================================================
    // SEÇÃO
    // =====================================================

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

    // =====================================================
    // BOTÃO DO MENU
    // =====================================================

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

        // Guarda se está ativo
        botao.putClientProperty(
                "ativo",
                ativo
        );

        // =================================================
        // CORES
        // =================================================

        Color corTexto =
                ativo
                        ? BRANCO
                        : TEXTO;

        Color corIcone =
                ativo
                        ? BRANCO
                        : ICONE;

        // =================================================
        // LABEL
        // =================================================

        JLabel label =
                labelIcone(
                        texto,
                        icone,
                        corTexto,
                        NORMAL
                );

        label.setIconTextGap(10);

        // Cria o ícone que será usado no JLabel
        FontIcon icon =
                FontIcon.of(icone);

        icon.setIconSize(17);
        icon.setIconColor(corIcone);

        label.setIcon(icon);

        // Guarda referências para podermos atualizar depois
        botao.putClientProperty(
                "label",
                label
        );

        botao.putClientProperty(
                "icone",
                icon
        );

        botao.add(label);

        // =================================================
        // FUNDO
        // =================================================

        botao.setBackground(
                ativo
                        ? AZUL
                        : SIDEBAR
        );

        return botao;

    }

    // =====================================================
    // ATUALIZAR PÁGINA ATIVA
    // =====================================================

    public void atualizarPaginaAtiva(
            String pagina
    ) {

        atualizarBotao(
                botaoDashboard,
                pagina.equals("dashboard")
        );

        atualizarBotao(
                botaoEventos,
                pagina.equals("eventos")
        );

        atualizarBotao(
                botaoAlunos,
                pagina.equals("alunos")
        );

        atualizarBotao(
                botaoProfessores,
                pagina.equals("professores")
        );

        atualizarBotao(
                botaoInscricoes,
                pagina.equals("inscricoes")
        );

        atualizarBotao(
                botaoComissoes,
                pagina.equals("comissoes")
        );

        atualizarBotao(
                botaoAgentesExternos,
                pagina.equals("agentesExternos")
        );

        atualizarBotao(
                botaoResumo,
                pagina.equals("resumo")
        );

        atualizarBotao(
                botaoConfiguracoes,
                pagina.equals("configuracoes")
        );
    }


    // =====================================================
    // ATUALIZAR UM BOTÃO
    // =====================================================

    private void atualizarBotao(
            JButton botao,
            boolean ativo
    ) {

        if (botao == null) {
            return;
        }

        botao.putClientProperty(
                "ativo",
                ativo
        );

        // Fundo
        botao.setBackground(
                ativo
                        ? AZUL
                        : SIDEBAR
        );

        // Texto
        JLabel label =
                (JLabel) botao.getClientProperty(
                        "label"
                );

        if (label != null) {

            label.setForeground(
                    ativo
                            ? BRANCO
                            : TEXTO
            );
        }

        // Ícone
        FontIcon icon =
                (FontIcon) botao.getClientProperty(
                        "icone"
                );

        if (icon != null) {

            icon.setIconColor(
                    ativo
                            ? BRANCO
                            : ICONE
            );
        }
    }

    // =====================================================
    // SAIR
    // =====================================================

    private JPanel criarSair(
            Dashboard dashboard
    ) {

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

    // =====================================================
    // LABEL COM ÍCONE
    // =====================================================

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