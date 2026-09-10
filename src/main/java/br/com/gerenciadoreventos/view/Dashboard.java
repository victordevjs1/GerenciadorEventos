package br.com.gerenciadoreventos.view;

import br.com.gerenciadoreventos.model.Usuario;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Dashboard extends JFrame {

    // =========================================================
    // USUÁRIO LOGADO
    // =========================================================

    private Usuario usuarioLogado;


    // =========================================================
    // CORES
    // =========================================================

    private static final Color FUNDO =
            new Color(246, 248, 252);

    private static final Color SIDEBAR =
            new Color(15, 23, 42);

    private static final Color SIDEBAR_HOVER =
            new Color(30, 41, 59);

    private static final Color AZUL =
            new Color(37, 99, 235);

    private static final Color AZUL_CLARO =
            new Color(239, 246, 255);

    private static final Color TEXTO =
            new Color(15, 23, 42);

    private static final Color SECUNDARIO =
            new Color(100, 116, 139);

    private static final Color BORDA =
            new Color(226, 232, 240);

    private static final Color BRANCO =
            Color.WHITE;


    // =========================================================
    // FONTES
    // =========================================================

    private static final Font NORMAL =
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    13
            );

    private static final Font MEDIUM =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    14
            );

    private static final Font TITULO =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    25
            );


    // =========================================================
    // CONSTRUTOR
    // =========================================================

    public Dashboard(Usuario usuarioLogado) {

        this.usuarioLogado = usuarioLogado;

        setTitle("Gerenciamento de eventos");

        setSize(1200, 750);

        setMinimumSize(
                new Dimension(
                        1000,
                        650
                )
        );

        setDefaultCloseOperation(
                EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        add(criarPrincipal());
    }


    // =========================================================
    // ESTRUTURA PRINCIPAL
    // =========================================================

    private JPanel criarPrincipal() {

        JPanel painel =
                new JPanel(
                        new BorderLayout()
                );

        painel.setBackground(FUNDO);

        painel.add(
                criarSidebar(),
                BorderLayout.WEST
        );

        painel.add(
                criarConteudo(),
                BorderLayout.CENTER
        );

        return painel;
    }


    // =========================================================
    // SIDEBAR
    // =========================================================

    private JPanel criarSidebar() {

        JPanel sidebar =
                new JPanel(
                        new BorderLayout()
                );

        sidebar.setPreferredSize(
                new Dimension(
                        240,
                        0
                )
        );

        sidebar.setBackground(SIDEBAR);

        sidebar.add(
                criarLogo(),
                BorderLayout.NORTH
        );

        sidebar.add(
                criarMenu(),
                BorderLayout.CENTER
        );

        sidebar.add(
                criarSair(),
                BorderLayout.SOUTH
        );

        return sidebar;
    }


    // =========================================================
    // LOGO
    // =========================================================

    private JPanel criarLogo() {

        JPanel painel =
                new JPanel();

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
                        Color.WHITE,
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
                new Color(
                        148,
                        163,
                        184
                )
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


    // =========================================================
    // MENU
    // =========================================================

    private JPanel criarMenu() {

        JPanel menu =
                new JPanel();

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


        adicionarSecao(
                menu,
                "PRINCIPAL",
                item(
                        FontAwesomeSolid.TH_LARGE,
                        "Dashboard",
                        true
                )
        );


        menu.add(
                Box.createVerticalStrut(18)
        );


        adicionarSecao(
                menu,
                "GESTÃO",

                item(
                        FontAwesomeSolid.CALENDAR_ALT,
                        "Eventos",
                        false
                ),

                item(
                        FontAwesomeSolid.USER_GRADUATE,
                        "Alunos",
                        false
                ),

                item(
                        FontAwesomeSolid.CHECK,
                        "Inscrições",
                        false
                ),

                item(
                        FontAwesomeSolid.TASKS,
                        "Atividades",
                        false
                )
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


    // =========================================================
    // SEÇÃO DO MENU
    // =========================================================

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
                new Color(
                        100,
                        116,
                        139
                )
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


    // =========================================================
    // ITEM DO MENU
    // =========================================================

    private JButton item(
            Ikon icone,
            String texto,
            boolean ativo
    ) {

        JButton botao =
                new JButton();


        botao.setPreferredSize(
                new Dimension(
                        210,
                        44
                )
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
                        ? Color.WHITE
                        : new Color(
                        226,
                        232,
                        240
                );


        Color corIcone =
                ativo
                        ? Color.WHITE
                        : new Color(
                        148,
                        163,
                        184
                );


        JLabel label =
                labelIcone(
                        texto,
                        icone,
                        corTexto,
                        NORMAL
                );


        label.setIconTextGap(10);


        FontIcon fontIcon =
                FontIcon.of(icone);


        fontIcon.setIconSize(17);

        fontIcon.setIconColor(
                corIcone
        );


        label.setIcon(fontIcon);


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


    // =========================================================
    // BOTÃO SAIR
    // =========================================================

    private JPanel criarSair() {

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

            dispose();
        });


        painel.add(
                sair
        );


        return painel;
    }


    // =========================================================
    // CONTEÚDO
    // =========================================================

    private JPanel criarConteudo() {

        JPanel painel =
                new JPanel(
                        new BorderLayout()
                );

        painel.setBackground(FUNDO);

        painel.setBorder(
                new EmptyBorder(
                        25,
                        25,
                        25,
                        25
                )
        );


        painel.add(
                criarCabecalho(),
                BorderLayout.NORTH
        );


        painel.add(
                criarDashboard(),
                BorderLayout.CENTER
        );


        return painel;
    }


    // =========================================================
    // CABEÇALHO
    // =========================================================

    private JPanel criarCabecalho() {

        JPanel painel =
                new JPanel(
                        new BorderLayout()
                );

        painel.setBackground(FUNDO);

        painel.setBorder(
                new EmptyBorder(
                        0,
                        0,
                        20,
                        0
                )
        );


        // -----------------------------------------------------
        // TÍTULO
        // -----------------------------------------------------

        JPanel textos =
                new JPanel();

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        textos.setBackground(FUNDO);


        JLabel titulo =
                new JLabel(
                        "Dashboard"
                );

        titulo.setFont(TITULO);

        titulo.setForeground(TEXTO);


        JLabel descricao =
                new JLabel(
                        "Visão geral dos eventos escolares"
                );

        descricao.setFont(NORMAL);

        descricao.setForeground(
                SECUNDARIO
        );


        textos.add(titulo);

        textos.add(
                Box.createVerticalStrut(3)
        );

        textos.add(descricao);


        painel.add(
                textos,
                BorderLayout.WEST
        );


        // -----------------------------------------------------
        // USUÁRIO
        // -----------------------------------------------------

        JPanel usuario =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        usuario.setBackground(FUNDO);


        String nomeUsuario =
                obterNomeUsuario();


        String tipoUsuario =
                obterTipoUsuario();


        JLabel nome =
                new JLabel(
                        nomeUsuario
                );

        nome.setFont(MEDIUM);

        nome.setForeground(TEXTO);


        JLabel tipo =
                new JLabel(
                        tipoUsuario
                );

        tipo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        tipo.setForeground(
                SECUNDARIO
        );


        JPanel info =
                new JPanel();


        info.setLayout(
                new BoxLayout(
                        info,
                        BoxLayout.Y_AXIS
                )
        );


        info.setBackground(FUNDO);


        info.add(nome);

        info.add(tipo);


        // -----------------------------------------------------
        // AVATAR
        // -----------------------------------------------------

        String inicial =
                obterInicial(
                        nomeUsuario
                );


        JLabel avatar =
                new JLabel(
                        inicial
                );


        avatar.setHorizontalAlignment(
                SwingConstants.CENTER
        );


        avatar.setVerticalAlignment(
                SwingConstants.CENTER
        );


        avatar.setPreferredSize(
                new Dimension(
                        38,
                        38
                )
        );


        avatar.setOpaque(true);

        avatar.setBackground(AZUL);

        avatar.setForeground(
                Color.WHITE
        );


        avatar.setFont(MEDIUM);


        usuario.add(info);

        usuario.add(avatar);


        painel.add(
                usuario,
                BorderLayout.EAST
        );


        return painel;
    }


    // =========================================================
    // NOME DO USUÁRIO
    // =========================================================

    private String obterNomeUsuario() {

        if (usuarioLogado == null) {

            return "Usuário";
        }


        if (
                usuarioLogado.getNome() == null
                        ||
                        usuarioLogado.getNome().isBlank()
        ) {

            return "Usuário";
        }


        return usuarioLogado.getNome();
    }


    // =========================================================
    // TIPO DO USUÁRIO
    // =========================================================

    private String obterTipoUsuario() {

        if (usuarioLogado == null) {

            return "Sistema";
        }


        return formatarTipoUsuario(
                usuarioLogado.getTipoUsuario()
        );
    }


    // =========================================================
    // FORMATAR TIPO
    // =========================================================

    private String formatarTipoUsuario(
            String tipo
    ) {

        if (
                tipo == null
                        ||
                        tipo.isBlank()
        ) {

            return "Usuário";
        }


        switch (
                tipo.toUpperCase()
        ) {

            case "ADMINISTRADOR":

                return "Administrador";


            case "COORDENADOR":

                return "Coordenador";


            case "PROFESSOR":

                return "Professor";


            case "ALUNO":

                return "Aluno";


            default:

                return tipo;
        }
    }


    // =========================================================
    // INICIAL DO AVATAR
    // =========================================================

    private String obterInicial(
            String nome
    ) {

        if (
                nome == null
                        ||
                        nome.isBlank()
        ) {

            return "?";
        }


        return nome
                .trim()
                .substring(0, 1)
                .toUpperCase();
    }


    // =========================================================
    // DASHBOARD
    // =========================================================

    private JPanel criarDashboard() {

        JPanel painel =
                new JPanel();


        painel.setLayout(
                new BoxLayout(
                        painel,
                        BoxLayout.Y_AXIS
                )
        );


        painel.setBackground(FUNDO);


        painel.add(
                criarCards()
        );


        painel.add(
                Box.createVerticalStrut(18)
        );


        painel.add(
                criarCentro()
        );


        painel.add(
                Box.createVerticalStrut(18)
        );


        painel.add(
                criarTabela()
        );


        return painel;
    }


    // =========================================================
    // CARDS
    // =========================================================

    private JPanel criarCards() {

        JPanel cards =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                15,
                                0
                        )
                );


        cards.setBackground(FUNDO);


        cards.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        110
                )
        );


        cards.add(
                card(
                        "Eventos",
                        "12",
                        FontAwesomeSolid.CALENDAR_ALT,
                        AZUL_CLARO,
                        AZUL
                )
        );


        cards.add(
                card(
                        "Inscritos",
                        "438",
                        FontAwesomeSolid.USER_GRADUATE,
                        new Color(
                                240,
                                253,
                                250
                        ),
                        new Color(
                                16,
                                185,
                                129
                        )
                )
        );


        cards.add(
                card(
                        "Abertos",
                        "03",
                        FontAwesomeSolid.CHECK_CIRCLE,
                        new Color(
                                245,
                                243,
                                255
                        ),
                        new Color(
                                124,
                                58,
                                237
                        )
                )
        );


        cards.add(
                card(
                        "Hoje",
                        "01",
                        FontAwesomeSolid.CALENDAR_CHECK,
                        new Color(
                                255,
                                247,
                                237
                        ),
                        new Color(
                                234,
                                88,
                                12
                        )
                )
        );


        return cards;
    }


    // =========================================================
    // CARD
    // =========================================================

    private JPanel card(
            String titulo,
            String valor,
            Ikon icone,
            Color fundoIcone,
            Color corIcone
    ) {

        JPanel card =
                painelBranco();


        JPanel conteudo =
                new JPanel(
                        new BorderLayout()
                );


        conteudo.setBackground(
                BRANCO
        );


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
                new Dimension(
                        42,
                        42
                )
        );


        iconePanel.setBackground(
                fundoIcone
        );


        FontIcon icon =
                FontIcon.of(icone);


        icon.setIconSize(19);

        icon.setIconColor(
                corIcone
        );


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


        textos.setBackground(
                BRANCO
        );


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
                SECUNDARIO
        );


        JLabel lblValor =
                new JLabel(valor);


        lblValor.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24
                )
        );


        lblValor.setForeground(
                TEXTO
        );


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


    // =========================================================
    // CENTRO
    // =========================================================

    private JPanel criarCentro() {

        JPanel centro =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                18,
                                0
                        )
                );


        centro.setBackground(FUNDO);


        centro.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        220
                )
        );


        centro.add(
                criarGrafico()
        );


        centro.add(
                criarProximosEventos()
        );


        return centro;
    }


    // =========================================================
    // GRÁFICO
    // =========================================================

    private JPanel criarGrafico() {

        JPanel painel =
                painelBranco();


        painel.setLayout(
                new BorderLayout()
        );


        painel.add(
                tituloPainel(
                        "Eventos no mês"
                ),
                BorderLayout.NORTH
        );


        painel.add(
                new Grafico(),
                BorderLayout.CENTER
        );


        return painel;
    }


    // =========================================================
    // GRÁFICO
    // =========================================================

    private static class Grafico
            extends JPanel {

        Grafico() {

            setBackground(
                    BRANCO
            );


            setBorder(
                    new EmptyBorder(
                            5,
                            20,
                            10,
                            20
                    )
            );
        }


        @Override
        protected void paintComponent(
                Graphics g
        ) {

            super.paintComponent(g);


            Graphics2D g2 =
                    (Graphics2D) g;


            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            int base =
                    getHeight() - 30;


            int esquerda =
                    25;


            g2.setColor(
                    new Color(
                            30,
                            41,
                            59
                    )
            );


            g2.drawLine(
                    esquerda,
                    15,
                    esquerda,
                    base
            );


            g2.drawLine(
                    esquerda,
                    base,
                    getWidth() - 10,
                    base
            );


            int[] valores = {
                    12,
                    18,
                    10,
                    32
            };


            String[] meses = {
                    "Jan",
                    "Fev",
                    "Mar",
                    "Abr"
            };


            int largura = 28;

            int espacamento = 42;


            for (
                    int i = 0;
                    i < valores.length;
                    i++
            ) {

                int altura =
                        valores[i] * 4;


                int x =
                        esquerda
                                + 18
                                + i * espacamento;


                int y =
                        base - altura;


                g2.setColor(
                        i == 3
                                ? AZUL
                                : new Color(
                                148,
                                163,
                                184
                        )
                );


                g2.fillRect(
                        x,
                        y,
                        largura,
                        altura
                );


                g2.setColor(
                        SECUNDARIO
                );


                g2.setFont(
                        new Font(
                                "Segoe UI",
                                Font.PLAIN,
                                10
                        )
                );


                g2.drawString(
                        meses[i],
                        x + 6,
                        base + 15
                );
            }
        }
    }


    // =========================================================
    // PRÓXIMOS EVENTOS
    // =========================================================

    private JPanel criarProximosEventos() {

        JPanel painel =
                painelBranco();


        painel.setLayout(
                new BorderLayout()
        );


        painel.add(
                tituloPainel(
                        "Próximos Eventos"
                ),
                BorderLayout.NORTH
        );


        JPanel lista =
                new JPanel();


        lista.setLayout(
                new BoxLayout(
                        lista,
                        BoxLayout.Y_AXIS
                )
        );


        lista.setBackground(
                BRANCO
        );


        lista.setBorder(
                new EmptyBorder(
                        0,
                        15,
                        15,
                        15
                )
        );


        lista.add(
                evento(
                        "Olimpíadas",
                        "15/09",
                        "7:00 - 14:00"
                )
        );


        lista.add(
                Box.createVerticalStrut(12)
        );


        lista.add(
                evento(
                        "Semana de TI",
                        "26/09",
                        "7:30 - 18:20"
                )
        );


        painel.add(
                lista,
                BorderLayout.CENTER
        );


        return painel;
    }


    // =========================================================
    // EVENTO
    // =========================================================

    private JPanel evento(
            String nome,
            String data,
            String horario
    ) {

        JPanel painel =
                new JPanel();


        painel.setLayout(
                new BoxLayout(
                        painel,
                        BoxLayout.Y_AXIS
                )
        );


        painel.setBackground(
                BRANCO
        );


        JLabel titulo =
                new JLabel(nome);


        titulo.setFont(MEDIUM);


        titulo.setForeground(
                TEXTO
        );


        JLabel info =
                new JLabel(
                        data
                                + "  •  "
                                + horario
                );


        info.setFont(NORMAL);


        info.setForeground(
                SECUNDARIO
        );


        painel.add(titulo);


        painel.add(
                Box.createVerticalStrut(4)
        );


        painel.add(info);


        return painel;
    }


    // =========================================================
    // TABELA
    // =========================================================

    private JPanel criarTabela() {

        JPanel painel =
                painelBranco();


        painel.setLayout(
                new BorderLayout()
        );


        painel.add(
                tituloPainel(
                        "Eventos Recentes"
                ),
                BorderLayout.NORTH
        );


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

        tabela.setFont(NORMAL);

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


        painel.add(
                new JScrollPane(tabela),
                BorderLayout.CENTER
        );


        return painel;
    }


    // =========================================================
    // PAINEL BRANCO
    // =========================================================

    private JPanel painelBranco() {

        JPanel painel =
                new JPanel();


        painel.setBackground(
                BRANCO
        );


        painel.setBorder(
                new RoundedBorder(
                        14,
                        BORDA
                )
        );


        return painel;
    }


    // =========================================================
    // TÍTULO DOS PAINÉIS
    // =========================================================

    private JLabel tituloPainel(
            String texto
    ) {

        JLabel label =
                new JLabel(texto);


        label.setFont(MEDIUM);


        label.setForeground(
                TEXTO
        );


        label.setBorder(
                new EmptyBorder(
                        15,
                        18,
                        10,
                        18
                )
        );


        return label;
    }


    // =========================================================
    // LABEL COM ÍCONE
    // =========================================================

    private JLabel labelIcone(
            String texto,
            Ikon icone,
            Color cor,
            Font fonte
    ) {

        FontIcon icon =
                FontIcon.of(icone);


        icon.setIconSize(18);


        icon.setIconColor(
                cor
        );


        JLabel label =
                new JLabel(
                        texto,
                        icon,
                        JLabel.LEFT
                );


        label.setFont(fonte);


        label.setForeground(
                cor
        );


        return label;
    }


    // =========================================================
    // BORDA ARREDONDADA
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


    // =========================================================
    // MAIN - APENAS PARA TESTAR O DASHBOARD
    // =========================================================

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(() -> {

            Usuario usuario =
                    new Usuario();


            usuario.setId(1);

            usuario.setNome(
                    "Administrador"
            );

            usuario.setEmail(
                    "admin@etec.com.br"
            );

            usuario.setTipoUsuario(
                    "ADMINISTRADOR"
            );

            usuario.setAtivo(true);


            Dashboard dashboard =
                    new Dashboard(
                            usuario
                    );


            dashboard.setVisible(true);
        });
    }
}
