package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.model.AgenteExterno;
import br.com.gerenciadoreventos.service.AgenteExternoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AgenteExternoPage extends JPanel {

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

    private static final Color VERDE =
            new Color(22, 163, 74);

    private static final Color VERMELHO =
            new Color(220, 38, 38);

    // =====================================================
    // ATRIBUTOS
    // =====================================================

    private final AgenteExternoService service;

    private JPanel painelAgentes;
    private JTextField campoBusca;

    private JButton botaoAtivos;
    private JButton botaoInativos;

    private boolean mostrandoAtivos = true;

    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public AgenteExternoPage() {

        service = new AgenteExternoService();

        inicializarInterface();
        carregarAgentes();
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
                BorderFactory.createEmptyBorder(
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
                criarLista(),
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
                criarLabel(
                        "Agentes Externos",
                        28,
                        Font.BOLD,
                        TEXTO
                );

        JLabel subtitulo =
                criarLabel(
                        "Gerencie palestrantes, convidados e especialistas",
                        14,
                        Font.PLAIN,
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

        JButton novo =
                criarBotaoPrincipal(
                        "+ Novo Agente"
                );

        novo.addActionListener(
                e -> abrirEditor(null)
        );

        linhaSuperior.add(
                novo,
                BorderLayout.EAST
        );

        cabecalho.add(linhaSuperior);

        cabecalho.add(
                Box.createVerticalStrut(22)
        );

        // =================================================
        // ABAS
        // =================================================

        JPanel abas =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        abas.setBackground(FUNDO);

        botaoAtivos =
                criarBotaoAba(
                        "Ativos",
                        true
                );

        botaoInativos =
                criarBotaoAba(
                        "Inativos",
                        false
                );

        botaoAtivos.addActionListener(
                e -> selecionarAba(true)
        );

        botaoInativos.addActionListener(
                e -> selecionarAba(false)
        );

        abas.add(botaoAtivos);
        abas.add(botaoInativos);

        cabecalho.add(abas);

        cabecalho.add(
                Box.createVerticalStrut(20)
        );

        // =================================================
        // BUSCA
        // =================================================

        campoBusca =
                new JTextField();

        campoBusca.putClientProperty(
                "JTextField.placeholderText",
                "Pesquisar por nome, empresa, cargo..."
        );

        estilizarCampo(campoBusca);

        campoBusca.setPreferredSize(
                new Dimension(
                        0,
                        42
                )
        );

        campoBusca.addActionListener(
                e -> carregarAgentes()
        );

        campoBusca.getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                carregarAgentes();
                            }

                            @Override
                            public void removeUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                carregarAgentes();
                            }

                            @Override
                            public void changedUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                carregarAgentes();
                            }
                        }
                );

        cabecalho.add(campoBusca);

        cabecalho.add(
                Box.createVerticalStrut(20)
        );

        return cabecalho;
    }

    // =====================================================
    // LISTA
    // =====================================================

    private JScrollPane criarLista() {

        painelAgentes =
                new JPanel();

        painelAgentes.setLayout(
                new BoxLayout(
                        painelAgentes,
                        BoxLayout.Y_AXIS
                )
        );

        painelAgentes.setBackground(FUNDO);

        painelAgentes.setBorder(
                new EmptyBorder(
                        5,
                        0,
                        20,
                        0
                )
        );

        JScrollPane scroll =
                new JScrollPane(
                        painelAgentes
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

    private void carregarAgentes() {

        painelAgentes.removeAll();

        String busca =
                campoBusca == null
                        ? ""
                        : campoBusca.getText();

        List<AgenteExterno> agentes;

        if (busca.trim().isEmpty()) {

            agentes =
                    mostrandoAtivos
                            ? service.listarAtivos()
                            : service.listarInativos();

        } else {

            agentes =
                    service.pesquisar(
                            busca,
                            mostrandoAtivos
                    );
        }

        if (agentes.isEmpty()) {

            painelAgentes.add(
                    criarMensagemVazia()
            );

        } else {

            for (AgenteExterno agente : agentes) {

                painelAgentes.add(
                        criarCard(agente)
                );

                painelAgentes.add(
                        Box.createVerticalStrut(15)
                );
            }
        }

        painelAgentes.revalidate();
        painelAgentes.repaint();
    }

    // =====================================================
    // CARD
    // =====================================================

    private JPanel criarCard(
            AgenteExterno agente
    ) {

        JPanel card =
                new JPanel(
                        new BorderLayout(20, 0)
                );

        card.setBackground(Color.WHITE);

        card.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        180
                )
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(226, 232, 240)
                        ),
                        new EmptyBorder(
                                20,
                                22,
                                20,
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

        informacoes.setBackground(Color.WHITE);

        JLabel nome =
                criarLabel(
                        agente.getNome(),
                        19,
                        Font.BOLD,
                        TEXTO
                );

        informacoes.add(nome);

        informacoes.add(
                Box.createVerticalStrut(10)
        );

        String empresa =
                agente.getEmpresa();

        String cargo =
                agente.getCargo();

        if (empresa != null && !empresa.isBlank()) {

            String texto =
                    cargo != null && !cargo.isBlank()
                            ? cargo + " • " + empresa
                            : empresa;

            informacoes.add(
                    criarLabelInformacao(texto)
            );

            informacoes.add(
                    Box.createVerticalStrut(6)
            );

        } else if (cargo != null && !cargo.isBlank()) {

            informacoes.add(
                    criarLabelInformacao(cargo)
            );

            informacoes.add(
                    Box.createVerticalStrut(6)
            );
        }

        if (agente.getEspecialidade() != null
                && !agente.getEspecialidade().isBlank()) {

            informacoes.add(
                    criarLabelInformacao(
                            "Especialidade: "
                                    + agente.getEspecialidade()
                    )
            );

            informacoes.add(
                    Box.createVerticalStrut(6)
            );
        }

        if (agente.getEmail() != null
                && !agente.getEmail().isBlank()) {

            informacoes.add(
                    criarLabelInformacao(
                            "E-mail: "
                                    + agente.getEmail()
                    )
            );

            informacoes.add(
                    Box.createVerticalStrut(6)
            );
        }

        if (agente.getTelefone() != null
                && !agente.getTelefone().isBlank()) {

            informacoes.add(
                    criarLabelInformacao(
                            "Telefone: "
                                    + agente.getTelefone()
                    )
            );
        }

        card.add(
                informacoes,
                BorderLayout.CENTER
        );

        // =================================================
        // BOTÕES
        // =================================================

        JPanel painelBotoes =
                new JPanel();

        painelBotoes.setLayout(
                new BoxLayout(
                        painelBotoes,
                        BoxLayout.Y_AXIS
                )
        );

        painelBotoes.setBackground(Color.WHITE);

        JButton editar =
                criarBotaoSecundario(
                        "Editar"
                );

        editar.addActionListener(
                e -> abrirEditor(
                        agente
                )
        );

        JButton status;

        if (agente.isAtivo()) {

            status =
                    criarBotaoPerigo(
                            "Remover"
                    );

            status.addActionListener(
                    e -> desativar(agente)
            );

        } else {

            status =
                    criarBotaoAtivar(
                            "Ativar"
                    );

            status.addActionListener(
                    e -> ativar(agente)
            );
        }

        painelBotoes.add(editar);

        painelBotoes.add(
                Box.createVerticalStrut(8)
        );

        painelBotoes.add(status);

        card.add(
                painelBotoes,
                BorderLayout.EAST
        );

        return card;
    }

    // =====================================================
    // EDITOR
    // =====================================================

    private void abrirEditor(
            AgenteExterno agente
    ) {

        boolean novo =
                agente == null;

        JDialog dialog =
                new JDialog(
                        SwingUtilities.getWindowAncestor(this),
                        novo
                                ? "Novo Agente Externo"
                                : "Editar Agente Externo",
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
                        new BorderLayout()
                );

        principal.setBackground(FUNDO);

        principal.setBorder(
                new EmptyBorder(
                        25,
                        25,
                        20,
                        25
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
                criarLabel(
                        novo
                                ? "Cadastrar agente externo"
                                : "Editar agente externo",
                        24,
                        Font.BOLD,
                        TEXTO
                );

        JLabel subtitulo =
                criarLabel(
                        "Preencha as informações do agente.",
                        14,
                        Font.PLAIN,
                        CINZA_TEXTO
                );

        cabecalho.add(titulo);

        cabecalho.add(
                Box.createVerticalStrut(5)
        );

        cabecalho.add(subtitulo);

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

        formulario.setBackground(Color.WHITE);

        formulario.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(226, 232, 240)
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

        gbc.weightx = 1;

        gbc.insets =
                new Insets(
                        7,
                        7,
                        7,
                        7
                );

        JTextField nome =
                criarCampo();

        JTextField email =
                criarCampo();

        JTextField telefone =
                criarCampo();

        JTextField empresa =
                criarCampo();

        JTextField cargo =
                criarCampo();

        JTextField especialidade =
                criarCampo();

        JTextArea observacao =
                new JTextArea(4, 20);

        observacao.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        observacao.setLineWrap(true);
        observacao.setWrapStyleWord(true);

        observacao.setMargin(
                new Insets(
                        8,
                        10,
                        8,
                        10
                )
        );

        if (!novo) {

            nome.setText(
                    valor(agente.getNome())
            );

            email.setText(
                    valor(agente.getEmail())
            );

            telefone.setText(
                    valor(agente.getTelefone())
            );

            empresa.setText(
                    valor(agente.getEmpresa())
            );

            cargo.setText(
                    valor(agente.getCargo())
            );

            especialidade.setText(
                    valor(agente.getEspecialidade())
            );

            observacao.setText(
                    valor(agente.getObservacao())
            );
        }

        int linha = 0;

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Nome *",
                nome
        );

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "E-mail",
                email
        );

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Telefone",
                telefone
        );

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Empresa",
                empresa
        );

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Cargo",
                cargo
        );

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Especialidade",
                especialidade
        );

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Observação",
                new JScrollPane(observacao)
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
                        novo
                                ? "Cadastrar"
                                : "Salvar alterações"
                );

        salvar.addActionListener(
                e -> {

                    String nomeTexto =
                            nome.getText().trim();

                    if (nomeTexto.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Informe o nome do agente externo.",
                                "Campo obrigatório",
                                JOptionPane.WARNING_MESSAGE
                        );

                        nome.requestFocus();

                        return;
                    }

                    AgenteExterno agenteSalvar;

                    if (novo) {

                        agenteSalvar =
                                new AgenteExterno();

                    } else {

                        agenteSalvar =
                                agente;
                    }

                    agenteSalvar.setNome(nomeTexto);

                    agenteSalvar.setEmail(
                            email.getText()
                    );

                    agenteSalvar.setTelefone(
                            telefone.getText()
                    );

                    agenteSalvar.setEmpresa(
                            empresa.getText()
                    );

                    agenteSalvar.setCargo(
                            cargo.getText()
                    );

                    agenteSalvar.setEspecialidade(
                            especialidade.getText()
                    );

                    agenteSalvar.setObservacao(
                            observacao.getText()
                    );

                    boolean sucesso;

                    if (novo) {

                        sucesso =
                                service.cadastrar(
                                        agenteSalvar
                                );

                    } else {

                        sucesso =
                                service.atualizar(
                                        agenteSalvar
                                );
                    }

                    if (!sucesso) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Não foi possível salvar o agente externo.",
                                "Erro",
                                JOptionPane.ERROR_MESSAGE
                        );

                        return;
                    }

                    JOptionPane.showMessageDialog(
                            dialog,
                            novo
                                    ? "Agente cadastrado com sucesso!"
                                    : "Agente atualizado com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    dialog.dispose();

                    carregarAgentes();
                }
        );

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
    // STATUS
    // =====================================================

    private void desativar(
            AgenteExterno agente
    ) {

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja remover "
                                + agente.getNome()
                                + " da lista de agentes ativos?",
                        "Confirmar remoção",
                        JOptionPane.YES_NO_OPTION
                );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        if (service.desativar(agente.getId())) {

            carregarAgentes();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível remover o agente.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void ativar(
            AgenteExterno agente
    ) {

        if (service.ativar(agente.getId())) {

            carregarAgentes();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível ativar o agente.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // ABAS
    // =====================================================

    private void selecionarAba(
            boolean ativos
    ) {

        mostrandoAtivos = ativos;

        botaoAtivos.setBackground(
                ativos
                        ? AZUL
                        : Color.WHITE
        );

        botaoAtivos.setForeground(
                ativos
                        ? Color.WHITE
                        : TEXTO
        );

        botaoInativos.setBackground(
                ativos
                        ? Color.WHITE
                        : AZUL
        );

        botaoInativos.setForeground(
                ativos
                        ? TEXTO
                        : Color.WHITE
        );

        campoBusca.setText("");

        carregarAgentes();
    }

    // =====================================================
    // COMPONENTES
    // =====================================================

    private JLabel criarLabel(
            String texto,
            int tamanho,
            int estilo,
            Color cor
    ) {

        JLabel label =
                new JLabel(texto);

        label.setFont(
                new Font(
                        "Segoe UI",
                        estilo,
                        tamanho
                )
        );

        label.setForeground(cor);

        return label;
    }

    private JLabel criarLabelInformacao(
            String texto
    ) {

        return criarLabel(
                texto,
                14,
                Font.PLAIN,
                new Color(71, 85, 105)
        );
    }

    private JTextField criarCampo() {

        JTextField campo =
                new JTextField();

        estilizarCampo(campo);

        return campo;
    }

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
    }

    private void adicionarCampo(
            JPanel painel,
            GridBagConstraints gbc,
            int linha,
            String titulo,
            Component campo
    ) {

        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.weightx = 1;

        JPanel container =
                new JPanel();

        container.setLayout(
                new BoxLayout(
                        container,
                        BoxLayout.Y_AXIS
                )
        );

        container.setBackground(Color.WHITE);

        JLabel label =
                criarLabel(
                        titulo,
                        12,
                        Font.BOLD,
                        TEXTO
                );

        container.add(label);

        container.add(
                Box.createVerticalStrut(5)
        );

        container.add(campo);

        painel.add(
                container,
                gbc
        );
    }

    // =====================================================
    // BOTÕES
    // =====================================================

    private JButton criarBotaoAba(
            String texto,
            boolean ativo
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

        botao.setBackground(
                ativo
                        ? AZUL
                        : Color.WHITE
        );

        botao.setForeground(
                ativo
                        ? Color.WHITE
                        : TEXTO
        );

        botao.setOpaque(true);

        botao.setContentAreaFilled(true);

        botao.setFocusPainted(false);

        botao.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDA
                        ),
                        new EmptyBorder(
                                10,
                                25,
                                10,
                                25
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

    private JButton criarBotaoPerigo(
            String texto
    ) {

        JButton botao =
                criarBotaoSecundario(texto);

        botao.setForeground(VERMELHO);

        botao.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(252, 165, 165)
                        ),
                        new EmptyBorder(
                                9,
                                15,
                                9,
                                15
                        )
                )
        );

        return botao;
    }

    private JButton criarBotaoAtivar(
            String texto
    ) {

        JButton botao =
                criarBotaoSecundario(texto);

        botao.setForeground(VERDE);

        botao.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(134, 239, 172)
                        ),
                        new EmptyBorder(
                                9,
                                15,
                                9,
                                15
                        )
                )
        );

        return botao;
    }

    // =====================================================
    // ESTADO VAZIO
    // =====================================================

    private JPanel criarMensagemVazia() {

        JPanel painel =
                new JPanel(
                        new GridBagLayout()
                );

        painel.setBackground(Color.WHITE);

        painel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        150
                )
        );

        painel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(226, 232, 240)
                )
        );

        String texto =
                mostrandoAtivos
                        ? "Nenhum agente externo ativo encontrado."
                        : "Nenhum agente externo inativo encontrado.";

        JLabel label =
                criarLabel(
                        texto,
                        14,
                        Font.PLAIN,
                        CINZA_TEXTO
                );

        painel.add(label);

        return painel;
    }

    private String valor(String texto) {

        return texto == null
                ? ""
                : texto;
    }
}