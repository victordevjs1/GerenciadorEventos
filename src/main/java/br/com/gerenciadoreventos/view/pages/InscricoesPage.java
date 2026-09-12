package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.dao.AlunoDAO;
import br.com.gerenciadoreventos.dao.EventoDAO;
import br.com.gerenciadoreventos.dao.InscricaoDAO;
import br.com.gerenciadoreventos.dao.InscricaoDAO.Inscricao;
import br.com.gerenciadoreventos.model.Aluno;
import br.com.gerenciadoreventos.model.Evento;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class InscricoesPage extends JPanel {

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

    private final InscricaoDAO inscricaoDAO =
            new InscricaoDAO();

    private final EventoDAO eventoDAO =
            new EventoDAO();

    private final AlunoDAO alunoDAO =
            new AlunoDAO();

    private JPanel painelInscricoes;

    private JComboBox<Evento> comboEvento;

    private JComboBox<String> comboStatus;

    private JTextField campoBusca;

    public InscricoesPage() {

        inicializarInterface();

        carregarEventos();

        carregarInscricoes();
    }

    // =====================================================
    // INTERFACE
    // =====================================================

    private void inicializarInterface() {

        setLayout(new BorderLayout());

        setBackground(FUNDO);

        add(
                criarConteudo(),
                BorderLayout.CENTER
        );
    }

    private JPanel criarConteudo() {

        JPanel painel =
                new JPanel(new BorderLayout());

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
                criarListaInscricoes(),
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
                new JPanel(new BorderLayout());

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
                new JLabel("Inscrições");

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
                        "Gerencie os participantes dos eventos"
                );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        subtitulo.setForeground(CINZA_TEXTO);

        textos.add(titulo);

        textos.add(
                Box.createVerticalStrut(4)
        );

        textos.add(subtitulo);

        linhaSuperior.add(
                textos,
                BorderLayout.WEST
        );

        JButton novaInscricao =
                criarBotaoPrincipal(
                        "+ Nova Inscrição"
                );

        novaInscricao.addActionListener(
                e -> abrirNovaInscricao()
        );

        linhaSuperior.add(
                novaInscricao,
                BorderLayout.EAST
        );

        cabecalho.add(linhaSuperior);

        cabecalho.add(
                Box.createVerticalStrut(22)
        );

        criarFiltros(cabecalho);

        return cabecalho;
    }

    // =====================================================
    // FILTROS
    // =====================================================

    private void criarFiltros(JPanel cabecalho) {

        JPanel filtros =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        filtros.setBackground(FUNDO);

        campoBusca =
                new JTextField();

        campoBusca.putClientProperty(
                "JTextField.placeholderText",
                "Pesquisar por nome ou RM..."
        );

        estilizarCampo(campoBusca);

        campoBusca.setPreferredSize(
                new Dimension(0, 42)
        );

        campoBusca.getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e
                            ) {
                                carregarInscricoes();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e
                            ) {
                                carregarInscricoes();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e
                            ) {
                                carregarInscricoes();
                            }
                        }
                );

        filtros.add(
                campoBusca,
                BorderLayout.CENTER
        );

        comboEvento =
                new JComboBox<>();

        comboEvento.setPreferredSize(
                new Dimension(
                        260,
                        42
                )
        );

        estilizarCombo(comboEvento);

        comboEvento.setRenderer(
                criarRendererEvento()
        );

        comboEvento.addActionListener(
                e -> carregarInscricoes()
        );

        filtros.add(
                comboEvento,
                BorderLayout.EAST
        );

        cabecalho.add(filtros);

        cabecalho.add(
                Box.createVerticalStrut(12)
        );

        JPanel statusPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        statusPanel.setBackground(FUNDO);

        JLabel labelStatus =
                new JLabel("Status:");

        labelStatus.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        labelStatus.setForeground(TEXTO);

        statusPanel.add(labelStatus);

        statusPanel.add(
                Box.createHorizontalStrut(8)
        );

        comboStatus =
                new JComboBox<>();

        comboStatus.addItem("TODOS");
        comboStatus.addItem("INSCRITO");
        comboStatus.addItem("CANCELADO");

        comboStatus.setPreferredSize(
                new Dimension(
                        160,
                        38
                )
        );

        estilizarCombo(comboStatus);

        comboStatus.addActionListener(
                e -> carregarInscricoes()
        );

        statusPanel.add(comboStatus);

        cabecalho.add(statusPanel);

        cabecalho.add(
                Box.createVerticalStrut(18)
        );
    }

    private DefaultListCellRenderer criarRendererEvento() {

        return new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {

                super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );

                if (value instanceof Evento) {

                    Evento evento =
                            (Evento) value;

                    setText(evento.getNome());
                }

                return this;
            }
        };
    }

    // =====================================================
    // EVENTOS
    // =====================================================

    private void carregarEventos() {

        if (comboEvento == null) {
            return;
        }

        comboEvento.removeAllItems();

        List<Evento> eventos =
                eventoDAO.listarEventos();

        for (Evento evento : eventos) {

            comboEvento.addItem(evento);
        }
    }

    // =====================================================
    // LISTA DE INSCRIÇÕES
    // =====================================================

    private JScrollPane criarListaInscricoes() {

        painelInscricoes =
                new JPanel();

        painelInscricoes.setLayout(
                new BoxLayout(
                        painelInscricoes,
                        BoxLayout.Y_AXIS
                )
        );

        painelInscricoes.setBackground(FUNDO);

        painelInscricoes.setBorder(
                new EmptyBorder(
                        5,
                        0,
                        20,
                        0
                )
        );

        JScrollPane scroll =
                new JScrollPane(
                        painelInscricoes
                );

        scroll.setBorder(null);

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(16);

        return scroll;
    }

    private void carregarInscricoes() {

        if (painelInscricoes == null) {
            return;
        }

        painelInscricoes.removeAll();

        Long idEvento = null;

        if (comboEvento != null) {

            Evento evento =
                    (Evento) comboEvento
                            .getSelectedItem();

            if (evento != null) {

                idEvento =
                        evento.getId();
            }
        }

        String busca = "";

        if (campoBusca != null) {

            busca =
                    campoBusca
                            .getText()
                            .trim();
        }

        String status = "TODOS";

        if (comboStatus != null
                && comboStatus.getSelectedItem() != null) {

            status =
                    comboStatus
                            .getSelectedItem()
                            .toString();
        }

        List<Inscricao> inscricoes =
                inscricaoDAO.listarInscricoes(
                        idEvento,
                        busca,
                        status
                );

        if (inscricoes.isEmpty()) {

            JLabel vazio =
                    new JLabel(
                            "Nenhuma inscrição encontrada."
                    );

            vazio.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            15
                    )
            );

            vazio.setForeground(CINZA_TEXTO);

            vazio.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            painelInscricoes.add(
                    Box.createVerticalStrut(30)
            );

            painelInscricoes.add(vazio);

        } else {

            for (Inscricao inscricao : inscricoes) {

                painelInscricoes.add(
                        criarCardInscricao(
                                inscricao
                        )
                );

                painelInscricoes.add(
                        Box.createVerticalStrut(12)
                );
            }
        }

        painelInscricoes.revalidate();

        painelInscricoes.repaint();
    }

    // =====================================================
    // CARD
    // =====================================================

    private JPanel criarCardInscricao(
            Inscricao inscricao
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
                        120
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

        JPanel informacoes =
                new JPanel();

        informacoes.setLayout(
                new BoxLayout(
                        informacoes,
                        BoxLayout.Y_AXIS
                )
        );

        informacoes.setBackground(Color.WHITE);

        JLabel nome =
                new JLabel(
                        inscricao.getNomeAluno()
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
                Box.createVerticalStrut(7)
        );

        JLabel dados =
                new JLabel(
                        "RM: "
                                + valor(
                                inscricao.getRm()
                        )
                                + "   •   Turma: "
                                + valor(
                                inscricao.getTurma()
                        )
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

        JLabel evento =
                new JLabel(
                        "Evento: "
                                + valor(
                                inscricao.getNomeEvento()
                        )
                );

        evento.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        evento.setForeground(CINZA_TEXTO);

        informacoes.add(evento);

        if (
                inscricao.getCurso() != null
                        && !inscricao.getCurso().isBlank()
        ) {

            informacoes.add(
                    Box.createVerticalStrut(4)
            );

            JLabel curso =
                    new JLabel(
                            "Curso: "
                                    + inscricao.getCurso()
                    );

            curso.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            12
                    )
            );

            curso.setForeground(CINZA_TEXTO);

            informacoes.add(curso);
        }

        card.add(
                informacoes,
                BorderLayout.CENTER
        );

        JPanel lateral =
                new JPanel();

        lateral.setLayout(
                new BoxLayout(
                        lateral,
                        BoxLayout.Y_AXIS
                )
        );

        lateral.setBackground(Color.WHITE);

        JLabel status =
                new JLabel(
                        valor(
                                inscricao.getStatus()
                        )
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

        if (
                "CANCELADO".equalsIgnoreCase(
                        inscricao.getStatus()
                )
        ) {

            status.setForeground(
                    new Color(
                            153,
                            27,
                            27
                    )
            );

            status.setBackground(
                    new Color(
                            254,
                            226,
                            226
                    )
            );

        } else {

            status.setForeground(
                    new Color(
                            21,
                            128,
                            61
                    )
            );

            status.setBackground(
                    new Color(
                            220,
                            252,
                            231
                    )
            );
        }

        lateral.add(status);

        lateral.add(
                Box.createVerticalStrut(10)
        );

        if (
                !"CANCELADO".equalsIgnoreCase(
                        inscricao.getStatus()
                )
        ) {

            JButton cancelar =
                    criarBotaoRemover();

            cancelar.addActionListener(
                    e -> cancelarInscricao(
                            inscricao
                    )
            );

            lateral.add(cancelar);
        }

        card.add(
                lateral,
                BorderLayout.EAST
        );

        return card;
    }

    // =====================================================
    // NOVA INSCRIÇÃO
    // =====================================================

    private void abrirNovaInscricao() {

        Evento eventoInicial =
                (Evento) comboEvento
                        .getSelectedItem();

        JDialog dialog =
                new JDialog(
                        SwingUtilities
                                .getWindowAncestor(this),
                        "Nova Inscrição",
                        Dialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setSize(
                650,
                650
        );

        dialog.setLocationRelativeTo(this);

        dialog.setResizable(false);

        JPanel principal =
                new JPanel(
                        new BorderLayout(
                                0,
                                15
                        )
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
                        "Nova inscrição"
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
                        "Escolha o evento e adicione vários alunos."
                );

        descricao.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        descricao.setForeground(CINZA_TEXTO);

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
                new JPanel();

        formulario.setLayout(
                new BoxLayout(
                        formulario,
                        BoxLayout.Y_AXIS
                )
        );

        formulario.setBackground(Color.WHITE);

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

        // =================================================
        // EVENTO
        // =================================================

        JLabel labelEvento =
                new JLabel("Evento");

        labelEvento.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        labelEvento.setForeground(TEXTO);

        formulario.add(labelEvento);

        formulario.add(
                Box.createVerticalStrut(6)
        );

        JComboBox<Evento> eventoCombo =
                new JComboBox<>();

        eventoCombo.setRenderer(
                criarRendererEvento()
        );

        List<Evento> eventos =
                eventoDAO.listarEventos();

        for (Evento evento : eventos) {

            eventoCombo.addItem(evento);
        }

        if (eventoInicial != null) {

            eventoCombo.setSelectedItem(
                    eventoInicial
            );
        }

        estilizarCombo(eventoCombo);

        eventoCombo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40
                )
        );

        formulario.add(eventoCombo);

        formulario.add(
                Box.createVerticalStrut(18)
        );

        // =================================================
        // CAMPO DE BUSCA DO ALUNO
        // =================================================

        JLabel labelAluno =
                new JLabel(
                        "Adicionar alunos"
                );

        labelAluno.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        labelAluno.setForeground(TEXTO);

        formulario.add(labelAluno);

        formulario.add(
                Box.createVerticalStrut(6)
        );

        JTextField campoAluno =
                new JTextField();

        campoAluno.putClientProperty(
                "JTextField.placeholderText",
                "Digite nome ou RM..."
        );

        estilizarCampo(campoAluno);

        campoAluno.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40
                )
        );

        formulario.add(campoAluno);

        formulario.add(
                Box.createVerticalStrut(8)
        );

        // =================================================
        // RESULTADOS
        // =================================================

        DefaultListModel<Aluno> modeloAlunos =
                new DefaultListModel<>();

        JList<Aluno> listaAlunos =
                new JList<>(
                        modeloAlunos
                );

        listaAlunos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        listaAlunos.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        listaAlunos.setCellRenderer(
                criarRendererAluno()
        );

        JScrollPane scrollAlunos =
                new JScrollPane(
                        listaAlunos
                );

        scrollAlunos.setPreferredSize(
                new Dimension(
                        0,
                        100
                )
        );

        scrollAlunos.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        100
                )
        );

        formulario.add(scrollAlunos);

        formulario.add(
                Box.createVerticalStrut(8)
        );

        // =================================================
        // LISTA DOS SELECIONADOS
        // =================================================

        JLabel labelSelecionados =
                new JLabel(
                        "Alunos selecionados"
                );

        labelSelecionados.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        labelSelecionados.setForeground(TEXTO);

        formulario.add(labelSelecionados);

        formulario.add(
                Box.createVerticalStrut(6)
        );

        JPanel alunosSelecionadosPanel =
                new JPanel();

        alunosSelecionadosPanel.setLayout(
                new BoxLayout(
                        alunosSelecionadosPanel,
                        BoxLayout.Y_AXIS
                )
        );

        alunosSelecionadosPanel.setBackground(
                Color.WHITE
        );

        JScrollPane scrollSelecionados =
                new JScrollPane(
                        alunosSelecionadosPanel
                );

        scrollSelecionados.setPreferredSize(
                new Dimension(
                        0,
                        120
                )
        );

        scrollSelecionados.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        120
                )
        );

        formulario.add(scrollSelecionados);

        formulario.add(
                Box.createVerticalStrut(8)
        );

        // =================================================
        // ALUNOS SELECIONADOS
        // =================================================

        List<Aluno> alunosSelecionados =
                new ArrayList<>();

        /*
         * IMPORTANTE:
         *
         * Não usamos:
         *
         * Runnable atualizarSelecionados =
         *     () -> {
         *         ...
         *         atualizarSelecionados.run();
         *     };
         *
         * porque isso gera:
         *
         * variable atualizarSelecionados
         * might not have been initialized
         *
         * Em vez disso, usamos um método separado.
         */

        Runnable[] atualizar =
                new Runnable[1];

        atualizar[0] =
                () -> atualizarListaSelecionados(
                        alunosSelecionadosPanel,
                        alunosSelecionados,
                        atualizar[0]
                );

        // =================================================
        // PESQUISA
        // =================================================

        campoAluno.getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            private void pesquisar() {

                                String texto =
                                        campoAluno
                                                .getText()
                                                .trim();

                                modeloAlunos.clear();

                                if (texto.isEmpty()) {
                                    return;
                                }

                                List<Aluno> alunos =
                                        inscricaoDAO.buscarAlunos(
                                                texto
                                        );

                                for (Aluno aluno : alunos) {

                                    if (
                                            !contemAluno(
                                                    alunosSelecionados,
                                                    aluno
                                            )
                                    ) {

                                        modeloAlunos.addElement(
                                                aluno
                                        );
                                    }
                                }
                            }

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e
                            ) {
                                pesquisar();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e
                            ) {
                                pesquisar();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e
                            ) {
                                pesquisar();
                            }
                        }
                );

        // =================================================
        // DUPLO CLIQUE
        // =================================================

        listaAlunos.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e
                    ) {

                        if (e.getClickCount() != 2) {
                            return;
                        }

                        Aluno aluno =
                                listaAlunos
                                        .getSelectedValue();

                        adicionarAlunoSelecionado(
                                aluno,
                                alunosSelecionados,
                                campoAluno,
                                modeloAlunos,
                                atualizar[0]
                        );
                    }
                }
        );

        // =================================================
        // BOTÃO ADICIONAR
        // =================================================

        JButton adicionarAluno =
                criarBotaoSecundario(
                        "Adicionar aluno"
                );

        adicionarAluno.addActionListener(
                e -> {

                    Aluno aluno =
                            listaAlunos
                                    .getSelectedValue();

                    adicionarAlunoSelecionado(
                            aluno,
                            alunosSelecionados,
                            campoAluno,
                            modeloAlunos,
                            atualizar[0]
                    );
                }
        );

        formulario.add(adicionarAluno);

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

        JButton inscrever =
                criarBotaoPrincipal(
                        "Inscrever alunos"
                );

        inscrever.addActionListener(
                e -> {

                    Evento evento =
                            (Evento) eventoCombo
                                    .getSelectedItem();

                    if (evento == null) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Selecione um evento.",
                                "Evento obrigatório",
                                JOptionPane.WARNING_MESSAGE
                        );

                        return;
                    }

                    if (
                            alunosSelecionados.isEmpty()
                    ) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Adicione pelo menos um aluno.",
                                "Aluno obrigatório",
                                JOptionPane.WARNING_MESSAGE
                        );

                        return;
                    }

                    int sucesso = 0;
                    int jaInscritos = 0;
                    int erros = 0;

                    for (
                            Aluno aluno :
                            alunosSelecionados
                    ) {

                        if (
                                inscricaoDAO.alunoJaInscrito(
                                        aluno.getId(),
                                        evento.getId()
                                )
                        ) {

                            jaInscritos++;

                            continue;
                        }

                        boolean resultado =
                                inscricaoDAO.cadastrarInscricao(
                                        aluno.getId(),
                                        evento.getId(),
                                        null
                                );

                        if (resultado) {

                            sucesso++;

                        } else {

                            erros++;
                        }
                    }

                    String mensagem =
                            "Processamento concluído.\n\n"
                                    + "Inscritos com sucesso: "
                                    + sucesso
                                    + "\n"
                                    + "Já estavam inscritos: "
                                    + jaInscritos
                                    + "\n"
                                    + "Erros: "
                                    + erros;

                    JOptionPane.showMessageDialog(
                            dialog,
                            mensagem,
                            "Inscrições",
                            sucesso > 0
                                    ? JOptionPane.INFORMATION_MESSAGE
                                    : JOptionPane.WARNING_MESSAGE
                    );

                    dialog.dispose();

                    carregarInscricoes();
                }
        );

        botoes.add(cancelar);

        botoes.add(inscrever);

        principal.add(
                botoes,
                BorderLayout.SOUTH
        );

        dialog.setContentPane(principal);

        dialog.setVisible(true);
    }

    // =====================================================
    // ADICIONAR ALUNO
    // =====================================================

    private void adicionarAlunoSelecionado(
            Aluno aluno,
            List<Aluno> alunosSelecionados,
            JTextField campoAluno,
            DefaultListModel<Aluno> modeloAlunos,
            Runnable atualizarSelecionados
    ) {

        if (aluno == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Pesquise e selecione um aluno.",
                    "Aluno",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (
                contemAluno(
                        alunosSelecionados,
                        aluno
                )
        ) {

            return;
        }

        alunosSelecionados.add(aluno);

        atualizarSelecionados.run();

        campoAluno.setText("");

        modeloAlunos.clear();
    }

    // =====================================================
    // ATUALIZAR ALUNOS SELECIONADOS
    // =====================================================

    private void atualizarListaSelecionados(
            JPanel painel,
            List<Aluno> alunosSelecionados,
            Runnable atualizarSelecionados
    ) {

        painel.removeAll();

        for (Aluno aluno : alunosSelecionados) {

            JPanel linha =
                    criarAlunoSelecionado(
                            aluno,
                            alunosSelecionados,
                            atualizarSelecionados
                    );

            painel.add(linha);

            painel.add(
                    Box.createVerticalStrut(5)
            );
        }

        painel.revalidate();

        painel.repaint();
    }

    // =====================================================
    // VERIFICAR ALUNO
    // =====================================================

    private boolean contemAluno(
            List<Aluno> alunos,
            Aluno aluno
    ) {

        for (Aluno item : alunos) {

            if (
                    item.getId()
                            == aluno.getId()
            ) {

                return true;
            }
        }

        return false;
    }

    // =====================================================
    // RENDERER ALUNO
    // =====================================================

    private DefaultListCellRenderer criarRendererAluno() {

        return new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {

                super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );

                if (value instanceof Aluno) {

                    Aluno aluno =
                            (Aluno) value;

                    setText(
                            aluno.getNome()
                                    + " - RM "
                                    + aluno.getRm()
                    );
                }

                return this;
            }
        };
    }

    // =====================================================
    // ALUNO SELECIONADO
    // =====================================================

    private JPanel criarAlunoSelecionado(
            Aluno aluno,
            List<Aluno> alunosSelecionados,
            Runnable atualizarSelecionados
    ) {

        JPanel linha =
                new JPanel(
                        new BorderLayout(
                                10,
                                0
                        )
                );

        linha.setBackground(
                new Color(
                        248,
                        250,
                        252
                )
        );

        linha.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        226,
                                        232,
                                        240
                                )
                        ),
                        new EmptyBorder(
                                7,
                                10,
                                7,
                                10
                        )
                )
        );

        linha.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        JLabel nome =
                new JLabel(
                        aluno.getNome()
                                + " - RM "
                                + aluno.getRm()
                );

        nome.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        nome.setForeground(TEXTO);

        linha.add(
                nome,
                BorderLayout.CENTER
        );

        JButton remover =
                new JButton("X");

        remover.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        remover.setForeground(
                new Color(
                        185,
                        28,
                        28
                )
        );

        remover.setBackground(Color.WHITE);

        remover.setFocusPainted(false);

        remover.setBorder(
                BorderFactory.createLineBorder(
                        new Color(
                                252,
                                165,
                                165
                        )
                )
        );

        remover.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        remover.addActionListener(
                e -> {

                    alunosSelecionados.remove(
                            aluno
                    );

                    atualizarSelecionados.run();
                }
        );

        linha.add(
                remover,
                BorderLayout.EAST
        );

        return linha;
    }

    // =====================================================
    // CANCELAR INSCRIÇÃO
    // =====================================================

    private void cancelarInscricao(
            Inscricao inscricao
    ) {

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja cancelar a inscrição de\n"
                                + inscricao.getNomeAluno()
                                + "?",
                        "Confirmar cancelamento",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (
                resposta
                        != JOptionPane.YES_OPTION
        ) {

            return;
        }

        boolean sucesso =
                inscricaoDAO.cancelarInscricao(
                        inscricao.getIdInscricao()
                );

        if (sucesso) {

            JOptionPane.showMessageDialog(
                    this,
                    "Inscrição cancelada com sucesso.",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            carregarInscricoes();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível cancelar a inscrição.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // UTIL
    // =====================================================

    private String valor(String texto) {

        if (
                texto == null
                        || texto.isBlank()
        ) {

            return "-";
        }

        return texto;
    }

    // =====================================================
    // CAMPO
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
    }

    // =====================================================
    // COMBO
    // =====================================================

    private void estilizarCombo(
            JComboBox<?> combo
    ) {

        combo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        combo.setBackground(Color.WHITE);

        combo.setForeground(TEXTO);
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
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e
                    ) {

                        botao.setBackground(
                                AZUL_HOVER
                        );
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e
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
                new JButton("Cancelar");

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
}
