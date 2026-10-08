package br.com.gerenciadoreventos.view.pages;


import br.com.gerenciadoreventos.theme.ThemeManager;
import br.com.gerenciadoreventos.model.Professor;
import br.com.gerenciadoreventos.model.ProfessorCsvImportacao;
import br.com.gerenciadoreventos.service.ProfessorCsvImportService;
import br.com.gerenciadoreventos.service.ProfessorService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
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

        painel.setBackground(ThemeManager.getFundo());

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

        cabecalho.setBackground(ThemeManager.getFundo());


        // =================================================
        // LINHA SUPERIOR
        // =================================================

        JPanel linhaSuperior =
                new JPanel(
                        new BorderLayout()
                );

        linhaSuperior.setBackground(ThemeManager.getFundo());


        JPanel textos =
                new JPanel();

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        textos.setBackground(ThemeManager.getFundo());


        JLabel titulo =
                new JLabel("Professores");

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        titulo.setForeground(ThemeManager.getTexto());


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

        subtitulo.setForeground(ThemeManager.getTextoSecundario());


        textos.add(titulo);

        textos.add(
                Box.createVerticalStrut(4)
        );

        textos.add(subtitulo);


        linhaSuperior.add(
                textos,
                BorderLayout.WEST
        );


        JPanel acoesCabecalho = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        acoesCabecalho.setBackground(ThemeManager.getFundo());

        JButton importarCsv =
                criarBotaoSecundario(
                        "Importar CSV"
                );

        importarCsv.addActionListener(
                e -> abrirImportadorCsv()
        );

        JButton novoProfessor =
                criarBotaoPrincipal(
                        "+ Novo Professor"
                );

        novoProfessor.addActionListener(
                e -> abrirNovoProfessor()
        );

        acoesCabecalho.add(importarCsv);
        acoesCabecalho.add(novoProfessor);

        linhaSuperior.add(
                acoesCabecalho,
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

        filtros.setBackground(ThemeManager.getFundo());


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

        painelProfessores.setBackground(ThemeManager.getFundo());

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

            vazio.setForeground(ThemeManager.getTextoSecundario());

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
        SwingUtilities.invokeLater(() -> ThemeManager.aplicarTema(ProfessorPage.this));
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

        card.setBackground(ThemeManager.getPainel());

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

        informacoes.setBackground(ThemeManager.getPainel());


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

        nome.setForeground(ThemeManager.getTexto());


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

        lateral.setBackground(ThemeManager.getPainel());


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

        principal.setBackground(ThemeManager.getFundo());

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

        cabecalho.setBackground(ThemeManager.getFundo());


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

        titulo.setForeground(ThemeManager.getTexto());


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

        descricao.setForeground(ThemeManager.getTextoSecundario());


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

        formulario.setBackground(ThemeManager.getPainel());

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

        botoes.setBackground(ThemeManager.getFundo());


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

        ThemeManager.aplicarTema(dialog);
        dialog.setVisible(true);
    }


    // =====================================================
    // IMPORTAÇÃO DE PROFESSORES POR CSV
    // =====================================================

    private void abrirImportadorCsv() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecionar arquivo CSV de professores");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(true);

        int retorno = chooser.showOpenDialog(this);

        if (retorno != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = chooser.getSelectedFile();

        if (arquivo == null || !arquivo.getName().toLowerCase().endsWith(".csv")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um arquivo no formato .csv.",
                    "Arquivo inválido",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            ProfessorCsvImportService importService = new ProfessorCsvImportService();
            List<ProfessorCsvImportacao> linhas = importService.analisar(arquivo);

            if (linhas.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "O CSV não possui professores para importar.",
                        "Arquivo vazio",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            abrirPreviewImportacaoCsv(arquivo, linhas, importService);

        } catch (Exception ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage() != null
                            ? ex.getMessage()
                            : "Não foi possível ler o arquivo CSV.",
                    "Erro ao importar CSV",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void abrirPreviewImportacaoCsv(
            File arquivo,
            List<ProfessorCsvImportacao> linhas,
            ProfessorCsvImportService importService
    ) {

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Importar professores por CSV",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(980, 650);
        dialog.setMinimumSize(new Dimension(850, 560));
        dialog.setLocationRelativeTo(this);

        JPanel principal = new JPanel(new BorderLayout(0, 18));
        principal.setBackground(ThemeManager.getFundo());
        principal.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setBackground(ThemeManager.getFundo());

        JLabel titulo = new JLabel("Pré-visualização da importação");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(ThemeManager.getTexto());
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel arquivoLabel = new JLabel("Arquivo: " + arquivo.getName());
        arquivoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        arquivoLabel.setForeground(ThemeManager.getTextoSecundario());
        arquivoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dica = new JLabel(
                "Coluna obrigatória: Nome. E-mail, Telefone e Área de Atuação são opcionais."
        );
        dica.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dica.setForeground(ThemeManager.getTextoSecundario());
        dica.setAlignmentX(Component.LEFT_ALIGNMENT);

        topo.add(titulo);
        topo.add(Box.createVerticalStrut(5));
        topo.add(arquivoLabel);
        topo.add(Box.createVerticalStrut(5));
        topo.add(dica);

        principal.add(topo, BorderLayout.NORTH);

        String[] colunas = {
                "Linha",
                "Nome",
                "E-mail",
                "Telefone",
                "Área de atuação",
                "Situação"
        };

        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int validos = 0;
        int invalidos = 0;

        for (ProfessorCsvImportacao linha : linhas) {
            Professor professor = linha.getProfessor();

            if (linha.isValido()) validos++;
            else invalidos++;

            modelo.addRow(new Object[]{
                    linha.getLinha(),
                    professor.getNome(),
                    professor.getEmail(),
                    professor.getTelefone(),
                    professor.getAreaAtuacao(),
                    linha.getSituacao()
            });
        }

        JTable tabela = new JTable(modelo);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(30);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        final Color VERMELHO_TEXTO = new Color(185, 28, 28);
        final Color VERDE_TEXTO = new Color(22, 101, 52);

        DefaultTableCellRenderer rendererSituacao = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                if (!isSelected) {
                    boolean valido = linhas.get(row).isValido();
                    c.setForeground(valido ? VERDE_TEXTO : VERMELHO_TEXTO);
                }
                return c;
            }
        };

        tabela.getColumnModel().getColumn(5).setCellRenderer(rendererSituacao);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(190);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(210);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA));
        principal.add(scroll, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(ThemeManager.getFundo());

        JLabel resumo = new JLabel(
                linhas.size() + " linha(s) • "
                        + validos + " pronta(s) para importar • "
                        + invalidos + " ignorada(s)"
        );
        resumo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resumo.setForeground(ThemeManager.getTextoSecundario());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setBackground(ThemeManager.getFundo());

        JButton cancelar = criarBotaoSecundario("Cancelar");
        cancelar.addActionListener(e -> dialog.dispose());

        JButton importar = criarBotaoPrincipal("Importar professores");
        importar.setEnabled(validos > 0);

        final int totalValidos = validos;

        importar.addActionListener(e -> {
            int confirmacao = JOptionPane.showConfirmDialog(
                    dialog,
                    "Serão cadastrados " + totalValidos + " professor(es).\n\nDeseja continuar?",
                    "Confirmar importação",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirmacao != JOptionPane.YES_OPTION) {
                return;
            }

            importar.setEnabled(false);
            importar.setText("Importando...");

            try {
                int importados = importService.importar(linhas);

                JOptionPane.showMessageDialog(
                        dialog,
                        importados + " professor(es) cadastrado(s) com sucesso."
                                + (importados < totalValidos
                                ? "\n\nAlguns registros não puderam ser gravados."
                                : ""),
                        "Importação concluída",
                        importados == totalValidos
                                ? JOptionPane.INFORMATION_MESSAGE
                                : JOptionPane.WARNING_MESSAGE
                );

                dialog.dispose();
                filtroAtivos.setSelected(true);
                carregarProfessores();

            } catch (Exception ex) {
                ex.printStackTrace();
                importar.setEnabled(true);
                importar.setText("Importar professores");

                JOptionPane.showMessageDialog(
                        dialog,
                        "Ocorreu um erro durante a importação.\n\n" + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        botoes.add(cancelar);
        botoes.add(importar);

        rodape.add(resumo, BorderLayout.WEST);
        rodape.add(botoes, BorderLayout.EAST);

        principal.add(rodape, BorderLayout.SOUTH);

        dialog.setContentPane(principal);
        ThemeManager.aplicarTema(dialog);
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


        container.setBackground(ThemeManager.getPainel());


        JLabel label =
                new JLabel(titulo);


        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );


        label.setForeground(ThemeManager.getTexto());


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

        radio.setForeground(ThemeManager.getTexto());

        radio.setBackground(ThemeManager.getFundo());

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


        botao.setForeground(ThemeManager.getTexto());

        botao.setBackground(ThemeManager.getPainel());

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


        botao.setBackground(ThemeManager.getPainel());

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


        botao.setBackground(ThemeManager.getPainel());

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
