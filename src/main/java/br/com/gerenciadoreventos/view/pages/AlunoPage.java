package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.model.Aluno;
import br.com.gerenciadoreventos.service.AlunoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AlunoPage extends JPanel {

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

    private JPanel painelAlunos;

    private final AlunoService alunoService;


    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public AlunoPage() {

        alunoService = new AlunoService();

        inicializarInterface();

        carregarAlunos();
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
                criarListaAlunos(),
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
                new JLabel("Alunos");

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
                        "Gerencie os alunos da escola"
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


        JButton novoAluno =
                criarBotaoPrincipal(
                        "+ Novo Aluno"
                );

        novoAluno.addActionListener(
                e -> abrirNovoAluno()
        );


        linhaSuperior.add(
                novoAluno,
                BorderLayout.EAST
        );


        cabecalho.add(linhaSuperior);

        cabecalho.add(
                Box.createVerticalStrut(22)
        );


        // =================================================
        // PESQUISA
        // =================================================

        JTextField busca =
                new JTextField();

        busca.putClientProperty(
                "JTextField.placeholderText",
                "Pesquisar por nome ou RM..."
        );

        estilizarCampo(busca);

        busca.setPreferredSize(
                new Dimension(
                        0,
                        42
                )
        );


        busca.getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                filtrar(busca.getText());
                            }

                            @Override
                            public void removeUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                filtrar(busca.getText());
                            }

                            @Override
                            public void changedUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                filtrar(busca.getText());
                            }
                        }
                );


        cabecalho.add(busca);

        cabecalho.add(
                Box.createVerticalStrut(20)
        );


        return cabecalho;
    }


    // =====================================================
    // LISTA
    // =====================================================

    private JScrollPane criarListaAlunos() {

        painelAlunos =
                new JPanel();

        painelAlunos.setLayout(
                new BoxLayout(
                        painelAlunos,
                        BoxLayout.Y_AXIS
                )
        );

        painelAlunos.setBackground(FUNDO);

        painelAlunos.setBorder(
                new EmptyBorder(
                        5,
                        0,
                        20,
                        0
                )
        );


        JScrollPane scroll =
                new JScrollPane(
                        painelAlunos
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
    // CARREGAR ALUNOS
    // =====================================================

    private void carregarAlunos() {

        painelAlunos.removeAll();

        List<Aluno> alunos =
                alunoService.listarAlunos();


        if (alunos.isEmpty()) {

            JLabel vazio =
                    new JLabel(
                            "Nenhum aluno cadastrado."
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


            painelAlunos.add(
                    Box.createVerticalStrut(30)
            );

            painelAlunos.add(vazio);

        } else {

            for (Aluno aluno : alunos) {

                painelAlunos.add(
                        criarCardAluno(aluno)
                );

                painelAlunos.add(
                        Box.createVerticalStrut(15)
                );
            }
        }


        painelAlunos.revalidate();

        painelAlunos.repaint();
    }


    // =====================================================
    // FILTRO
    // =====================================================

    private void filtrar(String texto) {

        painelAlunos.removeAll();

        String busca =
                texto
                        .trim()
                        .toLowerCase();


        List<Aluno> alunos =
                alunoService.listarAlunos();


        int encontrados = 0;


        for (Aluno aluno : alunos) {

            String nome =
                    aluno.getNome() == null
                            ? ""
                            : aluno.getNome().toLowerCase();


            String rm =
                    aluno.getRm() == null
                            ? ""
                            : aluno.getRm().toLowerCase();


            boolean corresponde =
                    nome.contains(busca)
                            ||
                            rm.contains(busca);


            if (corresponde) {

                painelAlunos.add(
                        criarCardAluno(aluno)
                );

                painelAlunos.add(
                        Box.createVerticalStrut(15)
                );

                encontrados++;
            }
        }


        if (encontrados == 0) {

            JLabel vazio =
                    new JLabel(
                            "Nenhum aluno encontrado."
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

            painelAlunos.add(vazio);
        }


        painelAlunos.revalidate();

        painelAlunos.repaint();
    }


    // =====================================================
    // CARD DO ALUNO
    // =====================================================

    private JPanel criarCardAluno(
            Aluno aluno
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
                        125
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
                        valor(aluno.getNome())
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


        JLabel dados =
                new JLabel(
                        "RM: "
                                + valor(aluno.getRm())
                                + "   •   Turma: "
                                + valor(aluno.getTurma())
                );

        dados.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        dados.setForeground(
                new Color(
                        71,
                        85,
                        105
                )
        );

        informacoes.add(dados);

        informacoes.add(
                Box.createVerticalStrut(6)
        );


        JLabel curso =
                new JLabel(
                        "Curso: "
                                + valor(aluno.getCurso())
                );

        curso.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        curso.setForeground(
                new Color(
                        71,
                        85,
                        105
                )
        );

        informacoes.add(curso);


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
                        aluno.isAtivo()
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


        if (aluno.isAtivo()) {

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


        // =================================================
        // REMOVER
        // =================================================

        if (aluno.isAtivo()) {

            lateral.add(
                    Box.createVerticalStrut(10)
            );


            JButton remover =
                    criarBotaoRemover();


            remover.addActionListener(
                    e -> removerAluno(aluno)
            );


            lateral.add(remover);
        }


        card.add(
                lateral,
                BorderLayout.EAST
        );


        return card;
    }


    // =====================================================
    // REMOVER ALUNO
    // =====================================================

    private void removerAluno(
            Aluno aluno
    ) {

        int confirmacao =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente remover o aluno\n"
                                + aluno.getNome()
                                + "?",
                        "Confirmar remoção",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );


        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }


        try {

            boolean sucesso =
                    alunoService.desativarAluno(
                             aluno.getId()
                    );


            if (sucesso) {

                JOptionPane.showMessageDialog(
                        this,
                        "Aluno removido com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE
                );

                carregarAlunos();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Não foi possível remover o aluno.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Ocorreu um erro ao remover o aluno.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // NOVO ALUNO
    // =====================================================

    private void abrirNovoAluno() {

        JDialog dialog =
                new JDialog(
                        SwingUtilities
                                .getWindowAncestor(this),
                        "Novo Aluno",
                        Dialog.ModalityType.APPLICATION_MODAL
                );


        // AUMENTADO PARA CABER TODOS OS CAMPOS

        dialog.setSize(
                650,
                700
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
                        "Cadastrar aluno"
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
                        "Preencha as informações do aluno."
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
        // RM
        // =================================================

        JTextField campoRm =
                new JTextField();

        campoRm.putClientProperty(
                "JTextField.placeholderText",
                "Ex.: 12345"
        );

        estilizarCampo(campoRm);


        adicionarCampo(
                formulario,
                gbc,
                0,
                "RM",
                campoRm,
                0
        );


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
                1
        );


        // =================================================
        // DATA
        // =================================================

        JFormattedTextField campoData =
                criarCampoData();


        adicionarCampo(
                formulario,
                gbc,
                1,
                "Data de nascimento",
                campoData,
                0
        );


        // =================================================
        // TURMA
        // =================================================

        JTextField campoTurma =
                new JTextField();

        campoTurma.putClientProperty(
                "JTextField.placeholderText",
                "Ex.: 2º DS"
        );

        estilizarCampo(campoTurma);


        adicionarCampo(
                formulario,
                gbc,
                1,
                "Turma",
                campoTurma,
                1
        );


        // =================================================
        // CURSO
        // =================================================

        JTextField campoCurso =
                new JTextField();

        estilizarCampo(campoCurso);


        adicionarCampo(
                formulario,
                gbc,
                2,
                "Curso",
                campoCurso,
                0
        );


        // =================================================
        // EMAIL
        // =================================================

        JTextField campoEmail =
                new JTextField();

        estilizarCampo(campoEmail);


        adicionarCampo(
                formulario,
                gbc,
                2,
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
                3,
                "Telefone",
                campoTelefone,
                0
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
                        "Cadastrar aluno"
                );


        salvar.addActionListener(
                e -> {

                    String rm =
                            campoRm
                                    .getText()
                                    .trim();


                    String nome =
                            campoNome
                                    .getText()
                                    .trim();


                    // =================================================
                    // VALIDAÇÃO RM
                    // =================================================

                    if (rm.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Informe o RM do aluno.",
                                "Campo obrigatório",
                                JOptionPane.WARNING_MESSAGE
                        );

                        campoRm.requestFocus();

                        return;
                    }


                    // =================================================
                    // VALIDAÇÃO NOME
                    // =================================================

                    if (nome.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Informe o nome do aluno.",
                                "Campo obrigatório",
                                JOptionPane.WARNING_MESSAGE
                        );

                        campoNome.requestFocus();

                        return;
                    }


                    try {

                        Aluno aluno =
                                new Aluno();


                        aluno.setRm(rm);

                        aluno.setNome(nome);


                        // =================================================
                        // DATA
                        // =================================================

                        String data =
                                campoData
                                        .getText()
                                        .replace(
                                                "_",
                                                ""
                                        )
                                        .trim();


                        if (!data.isEmpty()) {

                            LocalDate dataConvertida =
                                    LocalDate.parse(
                                            data,
                                            DateTimeFormatter
                                                    .ofPattern(
                                                            "dd/MM/yyyy"
                                                    )
                                    );


                            aluno.setDataNascimento(
                                    dataConvertida.toString()
                            );
                        }


                        // =================================================
                        // TURMA
                        // =================================================

                        aluno.setTurma(
                                campoTurma
                                        .getText()
                                        .trim()
                        );


                        // =================================================
                        // CURSO
                        // =================================================

                        aluno.setCurso(
                                campoCurso
                                        .getText()
                                        .trim()
                        );


                        // =================================================
                        // EMAIL
                        // =================================================

                        aluno.setEmail(
                                campoEmail
                                        .getText()
                                        .trim()
                        );


                        // =================================================
                        // TELEFONE
                        // =================================================

                        aluno.setTelefone(
                                campoTelefone
                                        .getText()
                                        .trim()
                        );


                        // =================================================
                        // SERVICE
                        // =================================================

                        boolean sucesso =
                                alunoService
                                        .cadastrarAluno(aluno);


                        if (!sucesso) {

                            JOptionPane.showMessageDialog(
                                    dialog,
                                    "Não foi possível cadastrar o aluno.",
                                    "Erro",
                                    JOptionPane.ERROR_MESSAGE
                            );

                            return;
                        }


                        JOptionPane.showMessageDialog(
                                dialog,
                                "Aluno cadastrado com sucesso!",
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE
                        );


                        dialog.dispose();

                        carregarAlunos();


                    } catch (Exception ex) {

                        ex.printStackTrace();

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Verifique a data de nascimento.\n\n"
                                        + "Formato: dd/MM/yyyy",
                                "Data inválida",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }
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
    // CAMPO DATA
    // =====================================================

    private JFormattedTextField criarCampoData() {

        try {

            MaskFormatter mascara =
                    new MaskFormatter(
                            "##/##/####"
                    );

            mascara.setPlaceholderCharacter(
                    '_'
            );


            JFormattedTextField campo =
                    new JFormattedTextField(
                            mascara
                    );


            campo.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            14
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


            campo.setToolTipText(
                    "Formato: dd/mm/aaaa"
            );


            campo.setFocusLostBehavior(
                    JFormattedTextField.PERSIST
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


            return campo;


        } catch (ParseException e) {

            throw new RuntimeException(
                    "Erro ao criar campo de data.",
                    e
            );
        }
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

        gbc.gridx =
                coluna;

        gbc.gridy =
                linha;

        gbc.gridwidth =
                1;

        gbc.weightx =
                1.0;

        gbc.weighty =
                0;

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


        botao.setForeground(
                Color.WHITE
        );


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
                new JButton("Remover");


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


        botao.setBackground(
                Color.WHITE
        );


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
