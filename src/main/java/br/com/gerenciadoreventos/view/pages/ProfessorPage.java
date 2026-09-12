package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.model.Professor;
import br.com.gerenciadoreventos.service.ProfessorService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ProfessorPage extends JPanel {

    // =====================================================
    // CORES
    // =====================================================

    private static final Color FUNDO =
            new Color(246, 248, 252);

    private static final Color AZUL =
            new Color(37, 99, 235);

    private static final Color AZUL_HOVER =
            new Color(29, 78, 216);

    private static final Color TEXTO =
            new Color(15, 23, 42);

    private static final Color CINZA_TEXTO =
            new Color(100, 116, 139);

    private static final Color BORDA =
            new Color(203, 213, 225);


    private JPanel painelProfessores;

    private JTextField campoBusca;

    private JRadioButton filtroAtivos;

    private JRadioButton filtroDesativados;

    private final ProfessorService professorService;


    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public ProfessorPage() {

        professorService =
                new ProfessorService();

        inicializarInterface();

        carregarProfessores();
    }


    // =====================================================
    // INTERFACE
    // =====================================================

    private void inicializarInterface() {

        setLayout(
                new BorderLayout()
        );

        setBackground(FUNDO);

        add(
                criarConteudo(),
                BorderLayout.CENTER
        );
    }


    // =====================================================
    // CONTEÚDO
    // =====================================================

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
                criarListaProfessores(),
                BorderLayout.CENTER
        );

        return painel;
    }


    // =====================================================
    // CABEÇALHO
    // =====================================================

    private JPanel criarCabecalho() {

        JPanel cabecalho =
                new JPanel();

        cabecalho.setLayout(
                new BoxLayout(
                        cabecalho,
                        BoxLayout.Y_AXIS
                )
        );

        cabecalho.setBackground(FUNDO);


        // =================================================
        // LINHA SUPERIOR
        // =================================================

        JPanel linhaSuperior =
                new JPanel(
                        new BorderLayout()
                );

        linhaSuperior.setBackground(FUNDO);


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
                new JLabel("Professores");

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        titulo.setForeground(TEXTO);


        JLabel subtitulo =
                new JLabel(
                        "Gerencie os professores da escola"
                );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        subtitulo.setForeground(
                CINZA_TEXTO
        );


        textos.add(titulo);

        textos.add(
                Box.createVerticalStrut(4)
        );

        textos.add(subtitulo);


        linhaSuperior.add(
                textos,
                BorderLayout.WEST
        );


        JButton novoProfessor =
                criarBotaoPrincipal(
                        "+ Novo Professor"
                );

        novoProfessor.addActionListener(
                e -> abrirNovoProfessor()
        );


        linhaSuperior.add(
                novoProfessor,
                BorderLayout.EAST
        );


        cabecalho.add(
                linhaSuperior
        );


        cabecalho.add(
                Box.createVerticalStrut(22)
        );


        // =================================================
        // BUSCA
        // =================================================

        campoBusca =
                new JTextField();

        campoBusca.putClientProperty(
                "JTextField.placeholderText",
                "Pesquisar por nome..."
        );

        estilizarCampo(campoBusca);

        campoBusca.setPreferredSize(
                new Dimension(
                        0,
                        42
                )
        );


        campoBusca.getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                carregarProfessores();
                            }

                            @Override
                            public void removeUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                carregarProfessores();
                            }

                            @Override
                            public void changedUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                carregarProfessores();
                            }
                        }
                );


        cabecalho.add(campoBusca);


        cabecalho.add(
                Box.createVerticalStrut(15)
        );


        // =================================================
        // FILTROS
        // =================================================

        JPanel filtros =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                0
                        )
                );

        filtros.setBackground(FUNDO);


        filtroAtivos =
                new JRadioButton(
                        "Ativos"
                );

        filtroDesativados =
                new JRadioButton(
                        "Desativados"
                );


        filtroAtivos.setSelected(true);


        ButtonGroup grupo =
                new ButtonGroup();

        grupo.add(filtroAtivos);

        grupo.add(filtroDesativados);


        estilizarFiltro(
                filtroAtivos
        );

        estilizarFiltro(
                filtroDesativados
        );


        filtroAtivos.addActionListener(
                e -> carregarProfessores()
        );

        filtroDesativados.addActionListener(
                e -> carregarProfessores()
        );


        filtros.add(filtroAtivos);

        filtros.add(filtroDesativados);


        cabecalho.add(filtros);


        cabecalho.add(
                Box.createVerticalStrut(15)
        );


        return cabecalho;
    }


    // =====================================================
    // LISTA
    // =====================================================

    private JScrollPane criarListaProfessores() {

        painelProfessores =
                new JPanel();

        painelProfessores.setLayout(
                new BoxLayout(
                        painelProfessores,
                        BoxLayout.Y_AXIS
                )
        );

        painelProfessores.setBackground(FUNDO);

        painelProfessores.setBorder(
                new EmptyBorder(
                        5,
                        0,
                        20,
                        0
                )
        );


        JScrollPane scroll =
                new JScrollPane(
                        painelProfessores
                );

        scroll.setBorder(null);

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(16);


        return scroll;
    }


    // =====================================================
    // CARREGAR
    // =====================================================

    private void carregarProfessores() {

        if (painelProfessores == null) {
            return;
        }


        painelProfessores.removeAll();


        boolean ativos =
                filtroAtivos == null
                        || filtroAtivos.isSelected();


        List<Professor> professores =
                professorService
                        .listarProfessores(ativos);


        String busca =
                campoBusca == null
                        ? ""
                        : campoBusca
                        .getText()
                        .trim()
                        .toLowerCase();


        int encontrados = 0;


        for (Professor professor : professores) {

            String nome =
                    professor.getNome() == null
                            ? ""
                            : professor
                            .getNome()
                            .toLowerCase();


            if (!nome.contains(busca)) {
                continue;
            }


            painelProfessores.add(
                    criarCardProfessor(
                            professor
                    )
            );


            painelProfessores.add(
                    Box.createVerticalStrut(15)
            );


            encontrados++;
        }


        if (encontrados == 0) {

            JLabel vazio =
                    new JLabel(
                            ativos
                                    ? "Nenhum professor ativo encontrado."
                                    : "Nenhum professor desativado encontrado."
                    );

            vazio.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            15
                    )
            );

            vazio.setForeground(
                    CINZA_TEXTO
            );

            vazio.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );


            painelProfessores.add(
                    Box.createVerticalStrut(30)
            );

            painelProfessores.add(vazio);
        }


        painelProfessores.revalidate();

        painelProfessores.repaint();
    }


    // =====================================================
    // CARD
    // =====================================================

    private JPanel criarCardProfessor(
            Professor professor
    ) {

        JPanel card =
                new JPanel(
                        new BorderLayout(
                                20,
                                0
                        )
                );

        card.setBackground(Color.WHITE);

        card.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        140
                )
        );


        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        226,
                                        232,
                                        240
                                )
                        ),
                        new EmptyBorder(
                                18,
                                22,
                                18,
                                22
                        )
                )
        );


        // =================================================
        // INFORMAÇÕES
        // =================================================

        JPanel informacoes =
                new JPanel();

        informacoes.setLayout(
                new BoxLayout(
                        informacoes,
                        BoxLayout.Y_AXIS
                )
        );

        informacoes.setBackground(
                Color.WHITE
        );


        JLabel nome =
                new JLabel(
                        valor(
                                professor.getNome()
                        )
                );

        nome.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        nome.setForeground(TEXTO);


        informacoes.add(nome);


        informacoes.add(
                Box.createVerticalStrut(8)
        );


        JLabel area =
                new JLabel(
                        "Área de atuação: "
                                + valor(
                                professor
                                        .getAreaAtuacao()
                        )
                );

        area.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        area.setForeground(
                new Color(
                        71,
                        85,
                        105
                )
        );


        informacoes.add(area);


        informacoes.add(
                Box.createVerticalStrut(6)
        );


        JLabel contato =
                new JLabel(
                        "E-mail: "
                                + valor(
                                professor.getEmail()
                        )
                                + "   •   Telefone: "
                                + valor(
                                professor.getTelefone()
                        )
                );

        contato.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        contato.setForeground(
                new Color(
                        71,
                        85,
                        105
                )
        );


        informacoes.add(contato);


        card.add(
                informacoes,
                BorderLayout.CENTER
        );


        // =================================================
        // LATERAL
        // =================================================

        JPanel lateral =
                new JPanel();

        lateral.setLayout(
                new BoxLayout(
                        lateral,
                        BoxLayout.Y_AXIS
                )
        );

        lateral.setBackground(
                Color.WHITE
        );


        JLabel status =
                new JLabel(
                        professor.isAtivo()
                                ? "ATIVO"
                                : "INATIVO"
                );

        status.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        status.setOpaque(true);


        status.setBorder(
                new EmptyBorder(
                        6,
                        10,
                        6,
                        10
                )
        );


        if (professor.isAtivo()) {

            status.setForeground(
                    new Color(
                            22,
                            101,
                            52
                    )
            );

            status.setBackground(
                    new Color(
                            220,
                            252,
                            231
                    )
            );

        } else {

            status.setForeground(
                    new Color(
                            127,
                            29,
                            29
                    )
            );

            status.setBackground(
                    new Color(
                            254,
                            226,
                            226
                    )
            );
        }


        lateral.add(status);


        lateral.add(
                Box.createVerticalStrut(10)
        );


        JButton acao;


        if (professor.isAtivo()) {

            acao =
                    criarBotaoRemover();

            acao.setText(
                    "Desativar"
            );


            acao.addActionListener(
                    e -> desativarProfessor(
                            professor
                    )
            );

        } else {

            acao =
                    criarBotaoAtivar();


            acao.addActionListener(
                    e -> ativarProfessor(
                            professor
                    )
            );
        }


        lateral.add(acao);


        card.add(
                lateral,
                BorderLayout.EAST
        );


        return card;
    }


    // =====================================================
    // DESATIVAR
    // =====================================================

    private void desativarProfessor(
            Professor professor
    ) {

        int confirmacao =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente desativar o professor\n"
                                + professor.getNome()
                                + "?",
                        "Confirmar desativação",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );


        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }


        boolean sucesso =
                professorService
                        .desativarProfessor(
                                professor.getId()
                        );


        if (sucesso) {

            JOptionPane.showMessageDialog(
                    this,
                    "Professor desativado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );


            carregarProfessores();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível desativar o professor.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // ATIVAR
    // =====================================================

    private void ativarProfessor(
            Professor professor
    ) {

        int confirmacao =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja reativar o professor\n"
                                + professor.getNome()
                                + "?",
                        "Confirmar ativação",
                        JOptionPane.YES_NO_OPTION
                );


        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }


        boolean sucesso =
                professorService
                        .ativarProfessor(
                                professor.getId()
                        );


        if (sucesso) {

            JOptionPane.showMessageDialog(
                    this,
                    "Professor ativado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );


            carregarProfessores();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível ativar o professor.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // NOVO PROFESSOR
    // =====================================================

    private void abrirNovoProfessor() {

        JDialog dialog =
                new JDialog(
                        SwingUtilities
                                .getWindowAncestor(this),
                        "Novo Professor",
                        Dialog.ModalityType.APPLICATION_MODAL
                );


        dialog.setSize(
                600,
                500
        );

        dialog.setLocationRelativeTo(this);

        dialog.setResizable(false);


        JPanel principal =
                new JPanel(
                        new BorderLayout()
                );

        principal.setBackground(FUNDO);

        principal.setBorder(
                new EmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );


        // =================================================
        // CABEÇALHO
        // =================================================

        JPanel cabecalho =
                new JPanel();

        cabecalho.setLayout(
                new BoxLayout(
                        cabecalho,
                        BoxLayout.Y_AXIS
                )
        );

        cabecalho.setBackground(FUNDO);


        JLabel titulo =
                new JLabel(
                        "Cadastrar professor"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24
                )
        );

        titulo.setForeground(TEXTO);


        JLabel descricao =
                new JLabel(
                        "Preencha as informações do professor."
                );

        descricao.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        descricao.setForeground(
                CINZA_TEXTO
        );


        cabecalho.add(titulo);

        cabecalho.add(
                Box.createVerticalStrut(5)
        );

        cabecalho.add(descricao);


        principal.add(
                cabecalho,
                BorderLayout.NORTH
        );


        // =================================================
        // FORMULÁRIO
        // =================================================

        JPanel formulario =
                new JPanel(
                        new GridBagLayout()
                );

        formulario.setBackground(
                Color.WHITE
        );

        formulario.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        226,
                                        232,
                                        240
                                )
                        ),
                        new EmptyBorder(
                                20,
                                20,
                                20,
                                20
                        )
                )
        );


        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.insets =
                new Insets(
                        7,
                        7,
                        7,
                        7
                );

        gbc.weightx = 1.0;


        // =================================================
        // NOME
        // =================================================

        JTextField campoNome =
                new JTextField();

        estilizarCampo(campoNome);


        adicionarCampo(
                formulario,
                gbc,
                0,
                "Nome completo",
                campoNome,
                0
        );


        // =================================================
        // EMAIL
        // =================================================

        JTextField campoEmail =
                new JTextField();

        campoEmail.putClientProperty(
                "JTextField.placeholderText",
                "professor@etec.com.br"
        );

        estilizarCampo(campoEmail);


        adicionarCampo(
                formulario,
                gbc,
                0,
                "E-mail",
                campoEmail,
                1
        );


        // =================================================
        // TELEFONE
        // =================================================

        JTextField campoTelefone =
                new JTextField();

        campoTelefone.putClientProperty(
                "JTextField.placeholderText",
                "(11) 99999-9999"
        );

        estilizarCampo(campoTelefone);


        adicionarCampo(
                formulario,
                gbc,
                1,
                "Telefone",
                campoTelefone,
                0
        );


        // =================================================
        // ÁREA
        // =================================================

        JTextField campoArea =
                new JTextField();

        campoArea.putClientProperty(
                "JTextField.placeholderText",
                "Ex.: Desenvolvimento de Sistemas"
        );

        estilizarCampo(campoArea);


        adicionarCampo(
                formulario,
                gbc,
                1,
                "Área de atuação",
                campoArea,
                1
        );


        principal.add(
                formulario,
                BorderLayout.CENTER
        );


        // =================================================
        // BOTÕES
        // =================================================

        JPanel botoes =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        botoes.setBackground(FUNDO);


        JButton cancelar =
                criarBotaoSecundario(
                        "Cancelar"
                );


        cancelar.addActionListener(
                e -> dialog.dispose()
        );


        JButton salvar =
                criarBotaoPrincipal(
                        "Cadastrar professor"
                );


        salvar.addActionListener(
                e -> {

                    String nome =
                            campoNome
                                    .getText()
                                    .trim();


                    if (nome.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Informe o nome do professor.",
                                "Campo obrigatório",
                                JOptionPane.WARNING_MESSAGE
                        );

                        campoNome.requestFocus();

                        return;
                    }


                    Professor professor =
                            new Professor();


                    professor.setNome(nome);

                    professor.setEmail(
                            campoEmail
                                    .getText()
                                    .trim()
                    );

                    professor.setTelefone(
                            campoTelefone
                                    .getText()
                                    .trim()
                    );

                    professor.setAreaAtuacao(
                            campoArea
                                    .getText()
                                    .trim()
                    );


                    boolean sucesso =
                            professorService
                                    .cadastrarProfessor(
                                            professor
                                    );


                    if (!sucesso) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Não foi possível cadastrar o professor.",
                                "Erro",
                                JOptionPane.ERROR_MESSAGE
                        );

                        return;
                    }


                    JOptionPane.showMessageDialog(
                            dialog,
                            "Professor cadastrado com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );


                    dialog.dispose();

                    carregarProfessores();
                });


        botoes.add(cancelar);

        botoes.add(salvar);


        principal.add(
                botoes,
                BorderLayout.SOUTH
        );


        dialog.setContentPane(principal);

        dialog.setVisible(true);
    }


    // =====================================================
    // ADICIONAR CAMPO
    // =====================================================

    private void adicionarCampo(
            JPanel painel,
            GridBagConstraints gbc,
            int linha,
            String titulo,
            Component campo,
            int coluna
    ) {

        gbc.gridx = coluna;

        gbc.gridy = linha;

        gbc.gridwidth = 1;

        gbc.weightx = 1.0;

        gbc.weighty = 0;

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.anchor =
                GridBagConstraints.NORTHWEST;


        JPanel container =
                new JPanel();


        container.setLayout(
                new BoxLayout(
                        container,
                        BoxLayout.Y_AXIS
                )
        );


        container.setBackground(
                Color.WHITE
        );


        JLabel label =
                new JLabel(titulo);


        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );


        label.setForeground(TEXTO);


        container.add(label);


        container.add(
                Box.createVerticalStrut(5)
        );


        campo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40
                )
        );


        container.add(campo);


        painel.add(
                container,
                gbc
        );
    }


    // =====================================================
    // ESTILIZAR CAMPO
    // =====================================================

    private void estilizarCampo(
            JTextField campo
    ) {

        campo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );


        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDA
                        ),
                        new EmptyBorder(
                                8,
                                10,
                                8,
                                10
                        )
                )
        );


        campo.setPreferredSize(
                new Dimension(
                        0,
                        40
                )
        );


        campo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40
                )
        );
    }


    // =====================================================
    // FILTRO
    // =====================================================

    private void estilizarFiltro(
            JRadioButton radio
    ) {

        radio.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        radio.setForeground(TEXTO);

        radio.setBackground(FUNDO);

        radio.setFocusPainted(false);

        radio.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );
    }


    // =====================================================
    // BOTÃO PRINCIPAL
    // =====================================================

    private JButton criarBotaoPrincipal(
            String texto
    ) {

        JButton botao =
                new JButton(texto);


        botao.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );


        botao.setForeground(Color.WHITE);

        botao.setBackground(AZUL);

        botao.setFocusPainted(false);

        botao.setBorderPainted(false);

        botao.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );


        botao.setBorder(
                new EmptyBorder(
                        11,
                        18,
                        11,
                        18
                )
        );


        botao.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        botao.setBackground(
                                AZUL_HOVER
                        );
                    }


                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        botao.setBackground(
                                AZUL
                        );
                    }
                }
        );


        return botao;
    }


    // =====================================================
    // BOTÃO SECUNDÁRIO
    // =====================================================

    private JButton criarBotaoSecundario(
            String texto
    ) {

        JButton botao =
                new JButton(texto);


        botao.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );


        botao.setForeground(TEXTO);

        botao.setBackground(Color.WHITE);

        botao.setFocusPainted(false);


        botao.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDA
                        ),
                        new EmptyBorder(
                                9,
                                15,
                                9,
                                15
                        )
                )
        );


        botao.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );


        return botao;
    }


    // =====================================================
    // BOTÃO REMOVER
    // =====================================================

    private JButton criarBotaoRemover() {

        JButton botao =
                new JButton("Desativar");


        botao.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );


        botao.setForeground(
                new Color(
                        185,
                        28,
                        28
                )
        );


        botao.setBackground(Color.WHITE);

        botao.setFocusPainted(false);

        botao.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );


        botao.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        252,
                                        165,
                                        165
                                )
                        ),
                        new EmptyBorder(
                                6,
                                10,
                                6,
                                10
                        )
                )
        );


        return botao;
    }


    // =====================================================
    // BOTÃO ATIVAR
    // =====================================================

    private JButton criarBotaoAtivar() {

        JButton botao =
                new JButton("Ativar");


        botao.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );


        botao.setForeground(
                new Color(
                        22,
                        101,
                        52
                )
        );


        botao.setBackground(Color.WHITE);

        botao.setFocusPainted(false);

        botao.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );


        botao.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        134,
                                        239,
                                        172
                                )
                        ),
                        new EmptyBorder(
                                6,
                                10,
                                6,
                                10
                        )
                )
        );


        return botao;
    }


    // =====================================================
    // VALOR
    // =====================================================

    private String valor(
            String texto
    ) {

        if (texto == null
                || texto.isBlank()) {

            return "Não informado";
        }

        return texto;
    }
}
