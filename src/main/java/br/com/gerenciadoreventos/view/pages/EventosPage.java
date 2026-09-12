package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.model.Evento;
import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.service.EventoService;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EventosPage extends JPanel {

    // =====================================================
    // CORES
    // =====================================================

    private static final Color FUNDO = new Color(246, 248, 252);
    private static final Color AZUL = new Color(37, 99, 235);
    private static final Color AZUL_HOVER = new Color(29, 78, 216);
    private static final Color TEXTO = new Color(15, 23, 42);
    private static final Color CINZA_TEXTO = new Color(100, 116, 139);
    private static final Color BORDA = new Color(203, 213, 225);
    private static final Color VERDE = new Color(22, 163, 74);
    private static final Color VERMELHO = new Color(220, 38, 38);

    // =====================================================
    // ATRIBUTOS
    // =====================================================

    private final Usuario usuarioLogado;
    private final EventoService eventoService;

    private JPanel painelEventos;
    private JTextField campoBusca;
    private JComboBox<String> filtroStatus;

    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public EventosPage(Usuario usuarioLogado) {

        this.usuarioLogado = usuarioLogado;
        this.eventoService = new EventoService();

        inicializarInterface();
        carregarEventos();
    }

    // =====================================================
    // INTERFACE
    // =====================================================

    private void inicializarInterface() {

        setLayout(new BorderLayout());
        setBackground(FUNDO);

        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {

        JPanel painel = new JPanel(new BorderLayout());

        painel.setBackground(FUNDO);
        painel.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        25,
                        25,
                        25
                )
        );

        painel.add(criarCabecalho(), BorderLayout.NORTH);
        painel.add(criarListaEventos(), BorderLayout.CENTER);

        return painel;
    }

    // =====================================================
    // CABEÇALHO
    // =====================================================

    private JPanel criarCabecalho() {

        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(
                new BoxLayout(
                        cabecalho,
                        BoxLayout.Y_AXIS
                )
        );
        cabecalho.setBackground(FUNDO);

        cabecalho.add(criarLinhaSuperior());
        cabecalho.add(Box.createVerticalStrut(22));
        cabecalho.add(criarFiltros());
        cabecalho.add(Box.createVerticalStrut(20));

        return cabecalho;
    }

    private JPanel criarLinhaSuperior() {

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
                        "Eventos",
                        28,
                        Font.BOLD,
                        TEXTO
                );

        JLabel subtitulo =
                criarLabel(
                        "Gerencie os eventos da escola",
                        14,
                        Font.PLAIN,
                        CINZA_TEXTO
                );

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JButton novoEvento =
                criarBotaoPrincipal("+ Novo Evento");

        novoEvento.addActionListener(e ->
                abrirNovoEvento()
        );

        linhaSuperior.add(
                textos,
                BorderLayout.WEST
        );

        linhaSuperior.add(
                novoEvento,
                BorderLayout.EAST
        );

        return linhaSuperior;
    }

    private JPanel criarFiltros() {

        JPanel filtros =
                new JPanel(
                        new BorderLayout(10, 0)
                );

        filtros.setBackground(FUNDO);

        campoBusca = new JTextField();

        campoBusca.putClientProperty(
                "JTextField.placeholderText",
                "Pesquisar evento..."
        );

        estilizarCampo(campoBusca);

        campoBusca.setPreferredSize(
                new Dimension(0, 42)
        );

        campoBusca.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        aplicarFiltros();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        aplicarFiltros();
                    }

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        aplicarFiltros();
                    }
                }
        );

        filtroStatus =
                new JComboBox<>(
                        new String[]{
                                "Todos os eventos",
                                "Planejados",
                                "Abertos",
                                "Em andamento",
                                "Encerrados",
                                "Cancelados"
                        }
                );

        estilizarComboBox(filtroStatus);

        filtroStatus.setPreferredSize(
                new Dimension(190, 42)
        );

        filtroStatus.addActionListener(e ->
                aplicarFiltros()
        );

        filtros.add(
                campoBusca,
                BorderLayout.CENTER
        );

        filtros.add(
                filtroStatus,
                BorderLayout.EAST
        );

        return filtros;
    }

    // =====================================================
    // LISTA
    // =====================================================

    private JScrollPane criarListaEventos() {

        painelEventos = new JPanel();

        painelEventos.setLayout(
                new BoxLayout(
                        painelEventos,
                        BoxLayout.Y_AXIS
                )
        );

        painelEventos.setBackground(FUNDO);

        painelEventos.setBorder(
                new EmptyBorder(
                        5,
                        0,
                        20,
                        0
                )
        );

        JScrollPane scroll =
                new JScrollPane(painelEventos);

        scroll.setBorder(null);

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(16);

        return scroll;
    }

    // =====================================================
    // CARREGAR / FILTRAR EVENTOS
    // =====================================================

    private void carregarEventos() {

        aplicarFiltros();
    }

    private void aplicarFiltros() {

        if (painelEventos == null) {
            return;
        }

        painelEventos.removeAll();

        List<Evento> eventos =
                eventoService.listarEventos();

        String textoBusca =
                campoBusca != null
                        ? campoBusca.getText().trim()
                        : "";

        String statusSelecionado =
                filtroStatus != null
                        ? filtroStatus.getSelectedItem().toString()
                        : "Todos os eventos";

        int eventosExibidos = 0;

        for (Evento evento : eventos) {

            if (!correspondeBusca(evento, textoBusca)) {
                continue;
            }

            if (!correspondeStatus(evento, statusSelecionado)) {
                continue;
            }

            painelEventos.add(
                    criarCardEvento(evento)
            );

            painelEventos.add(
                    Box.createVerticalStrut(15)
            );

            eventosExibidos++;
        }

        if (eventosExibidos == 0) {
            painelEventos.add(
                    criarMensagemSemEventos()
            );
        }

        painelEventos.revalidate();
        painelEventos.repaint();
    }

    private boolean correspondeBusca(
            Evento evento,
            String textoBusca
    ) {

        if (textoBusca.isEmpty()) {
            return true;
        }

        String nomeEvento =
                evento.getNome() != null
                        ? evento.getNome()
                        : "";

        return nomeEvento
                .toLowerCase()
                .contains(
                        textoBusca.toLowerCase()
                );
    }

    private boolean correspondeStatus(
            Evento evento,
            String filtro
    ) {

        if ("Todos os eventos".equals(filtro)) {
            return true;
        }

        String status = evento.getStatus();

        if (status == null) {
            return false;
        }

        return switch (filtro) {

            case "Planejados" ->
                    "PLANEJADO".equals(status);

            case "Abertos" ->
                    "ABERTO".equals(status);

            case "Em andamento" ->
                    "EM_ANDAMENTO".equals(status);

            case "Encerrados" ->
                    "ENCERRADO".equals(status);

            case "Cancelados" ->
                    "CANCELADO".equals(status);

            default ->
                    true;
        };
    }

    private JPanel criarMensagemSemEventos() {

        JPanel painel =
                new JPanel(
                        new GridBagLayout()
                );

        painel.setBackground(Color.WHITE);

        painel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(226, 232, 240)
                )
        );

        painel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        120
                )
        );

        JLabel mensagem =
                criarLabel(
                        "Nenhum evento encontrado.",
                        15,
                        Font.PLAIN,
                        CINZA_TEXTO
                );

        painel.add(mensagem);

        return painel;
    }

    // =====================================================
    // CARD
    // =====================================================

    private JPanel criarCardEvento(Evento evento) {

        JPanel card =
                new JPanel(
                        new BorderLayout(20, 0)
                );

        card.setBackground(Color.WHITE);

        card.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        175
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
                        evento.getNome(),
                        19,
                        Font.BOLD,
                        TEXTO
                );

        informacoes.add(nome);
        informacoes.add(Box.createVerticalStrut(12));

        adicionarInformacoesData(
                informacoes,
                evento
        );

        informacoes.add(
                criarLabelInformacao(
                        "Local: "
                                + obterLocal(evento)
                )
        );

        informacoes.add(
                Box.createVerticalStrut(6)
        );

        informacoes.add(
                criarLabelInformacao(
                        "Status: "
                                + obterStatus(evento)
                )
        );

        card.add(
                informacoes,
                BorderLayout.CENTER
        );

        JButton verEvento =
                criarBotaoSecundario("Ver evento");

        verEvento.addActionListener(e ->
                abrirDetalhesEvento(evento)
        );

        JPanel painelBotao =
                new JPanel(
                        new GridBagLayout()
                );

        painelBotao.setBackground(Color.WHITE);
        painelBotao.add(verEvento);

        card.add(
                painelBotao,
                BorderLayout.EAST
        );

        return card;
    }

    private void adicionarInformacoesData(
            JPanel painel,
            Evento evento
    ) {

        if (evento.getDataInicio() == null) {
            return;
        }

        String horarioTexto;

        if (evento.getDataFim() != null) {

            horarioTexto =
                    evento.getDataInicio().format(HORA)
                            + " - "
                            + evento.getDataFim().format(HORA);

        } else {

            horarioTexto =
                    evento.getDataInicio().format(HORA);
        }

        painel.add(
                criarLabelInformacao(
                        "Data: "
                                + evento.getDataInicio()
                                .format(DATA)
                )
        );

        painel.add(
                Box.createVerticalStrut(6)
        );

        painel.add(
                criarLabelInformacao(
                        "Horário: "
                                + horarioTexto
                )
        );

        painel.add(
                Box.createVerticalStrut(6)
        );
    }

    private String obterLocal(Evento evento) {

        return evento.getLocal() != null
                ? evento.getLocal()
                : "Não informado";
    }

    private String obterStatus(Evento evento) {

        return evento.getStatus() != null
                ? evento.getStatus()
                : "Não informado";
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

    // =====================================================
    // NOVO EVENTO
    // =====================================================

    private void abrirNovoEvento() {

        abrirEditorEvento(null);
    }

    // =====================================================
    // DETALHES / EDITAR EVENTO
    // =====================================================

    private void abrirDetalhesEvento(
            Evento evento
    ) {

        Evento eventoAtualizado =
                eventoService.buscarPorId(
                        evento.getId()
                );

        if (eventoAtualizado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível carregar o evento.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        abrirEditorEvento(eventoAtualizado);
    }

    // =====================================================
    // EDITOR DO EVENTO
    // =====================================================

    private void abrirEditorEvento(
            Evento evento
    ) {

        boolean novo = evento == null;

        JDialog dialog =
                new JDialog(
                        SwingUtilities.getWindowAncestor(this),
                        novo
                                ? "Novo Evento"
                                : "Evento",
                        Dialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setSize(850, 800);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel principal =
                new JPanel(
                        new BorderLayout()
                );

        principal.setBackground(FUNDO);

        principal.setBorder(
                new EmptyBorder(
                        20,
                        25,
                        20,
                        25
                )
        );

        principal.add(
                criarCabecalhoEditor(
                        evento,
                        novo
                ),
                BorderLayout.NORTH
        );

        JTabbedPane abas =
                criarAbasEditor(
                        evento,
                        novo,
                        dialog
                );

        principal.add(
                abas,
                BorderLayout.CENTER
        );

        principal.add(
                criarBotoesEditor(dialog),
                BorderLayout.SOUTH
        );

        dialog.setContentPane(principal);
        dialog.setVisible(true);
    }

    private JPanel criarCabecalhoEditor(
            Evento evento,
            boolean novo
    ) {

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
                                ? "Criar novo evento"
                                : evento.getNome(),
                        24,
                        Font.BOLD,
                        TEXTO
                );

        JLabel descricao =
                criarLabel(
                        novo
                                ? "Preencha as informações principais do evento."
                                : "Gerencie as informações e participantes do evento.",
                        14,
                        Font.PLAIN,
                        CINZA_TEXTO
                );

        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(5));
        cabecalho.add(descricao);

        return cabecalho;
    }

    private JTabbedPane criarAbasEditor(
            Evento evento,
            boolean novo,
            JDialog dialog
    ) {

        JTabbedPane abas =
                new JTabbedPane();

        abas.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        abas.addTab(
                "Dados do evento",
                criarAbaDados(
                        evento,
                        dialog,
                        novo
                )
        );

        if (!novo) {

            abas.addTab(
                    "Comissões",
                    criarAbaComissoes(evento)
            );

            abas.addTab(
                    "Professores",
                    criarAbaProfessores(evento)
            );

            abas.addTab(
                    "Alunos",
                    criarAbaAlunos(evento)
            );

            abas.addTab(
                    "Agentes externos",
                    criarAbaAgentesExternos(evento)
            );

            abas.addTab(
                    "Atividades",
                    criarAbaAtividades(evento)
            );

            abas.addTab(
                    "Responsabilidades",
                    criarAbaResponsabilidades(evento)
            );
        }

        return abas;
    }

    private JPanel criarBotoesEditor(
            JDialog dialog
    ) {

        JPanel botoes =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        botoes.setBackground(FUNDO);

        JButton fechar =
                criarBotaoSecundario("Fechar");

        fechar.addActionListener(e ->
                dialog.dispose()
        );

        botoes.add(fechar);

        return botoes;
    }

    // =====================================================
    // ABA DADOS
    // =====================================================

    private JPanel criarAbaDados(
            Evento evento,
            JDialog dialog,
            boolean novo
    ) {

        JPanel principal =
                new JPanel(
                        new BorderLayout()
                );

        principal.setBackground(FUNDO);

        principal.setBorder(
                new EmptyBorder(
                        15,
                        0,
                        10,
                        0
                )
        );

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
                criarConstraintsFormulario();

        int linha = 0;

        // =================================================
        // CAMPOS
        // =================================================

        JTextField campoNome = new JTextField();
        estilizarCampo(campoNome);

        if (!novo) {
            campoNome.setText(evento.getNome());
        }

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Nome do evento",
                campoNome,
                2
        );

        JTextArea campoDescricao =
                new JTextArea(5, 20);

        estilizarAreaTexto(campoDescricao);

        if (!novo && evento.getDescricao() != null) {
            campoDescricao.setText(
                    evento.getDescricao()
            );
        }

        JScrollPane scrollDescricao =
                new JScrollPane(campoDescricao);

        scrollDescricao.setBorder(
                BorderFactory.createLineBorder(BORDA)
        );

        scrollDescricao.setPreferredSize(
                new Dimension(0, 100)
        );

        scrollDescricao.setMinimumSize(
                new Dimension(0, 100)
        );

        scrollDescricao.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollDescricao.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Descrição",
                scrollDescricao,
                2
        );

        // =================================================
        // DATA E HORA DE INÍCIO
        // =================================================

        JFormattedTextField campoDataInicio =
                criarCampoData();

        JFormattedTextField campoHoraInicio =
                criarCampoHora();

        if (!novo && evento.getDataInicio() != null) {

            campoDataInicio.setText(
                    evento.getDataInicio()
                            .format(DATA)
            );

            campoHoraInicio.setText(
                    evento.getDataInicio()
                            .format(HORA)
            );
        }

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Data de início",
                campoDataInicio,
                1
        );

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Horário de início",
                campoHoraInicio,
                2
        );

        linha++;

        // =================================================
        // DATA E HORA DE TÉRMINO
        // =================================================

        JFormattedTextField campoDataFim =
                criarCampoData();

        JFormattedTextField campoHoraFim =
                criarCampoHora();

        if (!novo && evento.getDataFim() != null) {

            campoDataFim.setText(
                    evento.getDataFim()
                            .format(DATA)
            );

            campoHoraFim.setText(
                    evento.getDataFim()
                            .format(HORA)
            );
        }

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Data de término",
                campoDataFim,
                1
        );

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Horário de término",
                campoHoraFim,
                2
        );

        linha++;

        // =================================================
        // LOCAL
        // =================================================

        JTextField campoLocal = new JTextField();

        estilizarCampo(campoLocal);

        if (!novo && evento.getLocal() != null) {
            campoLocal.setText(
                    evento.getLocal()
            );
        }

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Local",
                campoLocal,
                2
        );

        // =================================================
        // CAPACIDADE
        // =================================================

        JTextField campoCapacidade =
                new JTextField();

        estilizarCampo(campoCapacidade);

        if (!novo) {
            campoCapacidade.setText(
                    String.valueOf(
                            evento.getCapacidade()
                    )
            );
        }

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Capacidade",
                campoCapacidade,
                1
        );

        // =================================================
        // STATUS
        // =================================================

        JComboBox<String> campoStatus =
                new JComboBox<>(
                        new String[]{
                                "PLANEJADO",
                                "ABERTO",
                                "EM_ANDAMENTO",
                                "ENCERRADO",
                                "CANCELADO"
                        }
                );

        estilizarComboBox(campoStatus);

        if (!novo && evento.getStatus() != null) {

            campoStatus.setSelectedItem(
                    evento.getStatus()
            );
        }

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Status",
                campoStatus,
                2
        );

        // =================================================
        // SALVAR
        // =================================================

        JButton salvar =
                criarBotaoPrincipal(
                        novo
                                ? "Criar evento"
                                : "Salvar alterações"
                );

        salvar.addActionListener(e ->
                salvarEvento(
                        evento,
                        novo,
                        dialog,
                        campoNome,
                        campoDescricao,
                        campoDataInicio,
                        campoHoraInicio,
                        campoDataFim,
                        campoHoraFim,
                        campoLocal,
                        campoCapacidade,
                        campoStatus
                )
        );

        JPanel painelSalvar =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        painelSalvar.setBackground(Color.WHITE);
        painelSalvar.add(salvar);

        JPanel container =
                new JPanel(
                        new BorderLayout()
                );

        container.setBackground(Color.WHITE);

        container.add(
                formulario,
                BorderLayout.CENTER
        );

        container.add(
                painelSalvar,
                BorderLayout.SOUTH
        );

        principal.add(
                container,
                BorderLayout.CENTER
        );

        return principal;
    }

    // =====================================================
    // SALVAR EVENTO
    // =====================================================

    private void salvarEvento(
            Evento evento,
            boolean novo,
            JDialog dialog,
            JTextField campoNome,
            JTextArea campoDescricao,
            JFormattedTextField campoDataInicio,
            JFormattedTextField campoHoraInicio,
            JFormattedTextField campoDataFim,
            JFormattedTextField campoHoraFim,
            JTextField campoLocal,
            JTextField campoCapacidade,
            JComboBox<String> campoStatus
    ) {

        try {

            // ---------------------------------------------
            // NOME
            // ---------------------------------------------

            if (campoNome.getText()
                    .trim()
                    .isEmpty()) {

                mostrarAviso(
                        dialog,
                        "Informe o nome do evento.",
                        "Campo obrigatório"
                );

                campoNome.requestFocus();
                return;
            }

            // ---------------------------------------------
            // DATA DE INÍCIO
            // ---------------------------------------------

            String textoDataInicio =
                    campoDataInicio.getText().trim();

            String textoHoraInicio =
                    campoHoraInicio.getText().trim();

            if (
                    textoDataInicio.isEmpty()
                            || textoHoraInicio.isEmpty()
                            || textoDataInicio.contains("_")
                            || textoHoraInicio.contains("_")
            ) {

                mostrarAviso(
                        dialog,
                        "Informe a data e o horário de início.",
                        "Campo obrigatório"
                );

                return;
            }

            LocalDate dataInicio =
                    LocalDate.parse(
                            textoDataInicio,
                            DATA
                    );

            LocalTime horaInicio =
                    LocalTime.parse(
                            textoHoraInicio,
                            HORA
                    );

            LocalDateTime dataInicioFinal =
                    LocalDateTime.of(
                            dataInicio,
                            horaInicio
                    );

            // ---------------------------------------------
            // DATA DE TÉRMINO
            // ---------------------------------------------

            LocalDateTime dataFimFinal = null;

            String textoDataFim =
                    campoDataFim.getText().trim();

            String textoHoraFim =
                    campoHoraFim.getText().trim();

            boolean informouDataFim =
                    !textoDataFim.isEmpty()
                            && !textoDataFim.contains("_");

            boolean informouHoraFim =
                    !textoHoraFim.isEmpty()
                            && !textoHoraFim.contains("_");

            if (informouDataFim != informouHoraFim) {

                mostrarAviso(
                        dialog,
                        "Informe a data e o horário de término completos.",
                        "Dados incompletos"
                );

                return;
            }

            if (informouDataFim && informouHoraFim) {

                LocalDate dataFim =
                        LocalDate.parse(
                                textoDataFim,
                                DATA
                        );

                LocalTime horaFim =
                        LocalTime.parse(
                                textoHoraFim,
                                HORA
                        );

                dataFimFinal =
                        LocalDateTime.of(
                                dataFim,
                                horaFim
                        );

                if (dataFimFinal.isBefore(
                        dataInicioFinal
                )) {

                    mostrarAviso(
                            dialog,
                            "A data de término não pode ser anterior à data de início.",
                            "Data inválida"
                    );

                    return;
                }
            }

            // ---------------------------------------------
            // CAPACIDADE
            // ---------------------------------------------

            int capacidade = 0;

            String textoCapacidade =
                    campoCapacidade.getText().trim();

            if (!textoCapacidade.isEmpty()) {

                capacidade =
                        Integer.parseInt(
                                textoCapacidade
                        );

                if (capacidade < 0) {

                    mostrarAviso(
                            dialog,
                            "A capacidade não pode ser negativa.",
                            "Valor inválido"
                    );

                    return;
                }
            }

            // ---------------------------------------------
            // OBJETO EVENTO
            // ---------------------------------------------

            Evento eventoSalvar =
                    novo
                            ? new Evento()
                            : evento;

            if (novo) {

                eventoSalvar.setIdUsuarioCriador(
                        usuarioLogado.getId()
                );
            }

            eventoSalvar.setNome(
                    campoNome.getText().trim()
            );

            eventoSalvar.setDescricao(
                    campoDescricao.getText().trim()
            );

            eventoSalvar.setDataInicio(
                    dataInicioFinal
            );

            eventoSalvar.setDataFim(
                    dataFimFinal
            );

            eventoSalvar.setLocal(
                    campoLocal.getText().trim()
            );

            eventoSalvar.setCapacidade(
                    capacidade
            );

            eventoSalvar.setStatus(
                    campoStatus
                            .getSelectedItem()
                            .toString()
            );

            // ---------------------------------------------
            // BANCO
            // ---------------------------------------------

            boolean sucesso;

            if (novo) {

                sucesso =
                        eventoService.cadastrarEvento(
                                eventoSalvar
                        );

            } else {

                sucesso =
                        eventoService.atualizarEvento(
                                eventoSalvar
                        );
            }

            if (!sucesso) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Não foi possível salvar o evento.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    dialog,
                    novo
                            ? "Evento criado com sucesso!"
                            : "Evento atualizado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dialog.dispose();

            aplicarFiltros();

        } catch (DateTimeParseException ex) {

            mostrarAviso(
                    dialog,
                    "Verifique as datas e horários.\n\n"
                            + "Data: dd/MM/yyyy\n"
                            + "Horário: HH:mm",
                    "Formato inválido"
            );

        } catch (NumberFormatException ex) {

            mostrarAviso(
                    dialog,
                    "A capacidade deve ser um número inteiro.",
                    "Valor inválido"
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    dialog,
                    "Erro ao salvar evento:\n\n"
                            + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void mostrarAviso(
            Component componente,
            String mensagem,
            String titulo
    ) {

        JOptionPane.showMessageDialog(
                componente,
                mensagem,
                titulo,
                JOptionPane.WARNING_MESSAGE
        );
    }

    // =====================================================
    // ABA COMISSÕES
    // =====================================================

    private JPanel criarAbaComissoes(Evento evento) {

        return criarAbaInformacao(
                "Comissões do evento",
                """
                Aqui ficarão as comissões deste evento.

                Exemplos:

                • Organização
                • Recepção
                • Infraestrutura
                • Divulgação
                • Cerimonial
                • Tecnologia

                Cada comissão poderá possuir vários alunos.
                """,
                "+ Nova comissão",
                e -> abrirNovaComissao(evento)
        );
    }

    private void abrirNovaComissao(Evento evento) {

        JOptionPane.showMessageDialog(
                this,
                "Aqui será aberta a tela para criar uma comissão para:\n\n"
                        + evento.getNome()
                        + "\n\n"
                        + "Depois podemos adicionar alunos e definir "
                        + "as funções de cada aluno.",
                "Nova comissão",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // ABA PROFESSORES
    // =====================================================

    private JPanel criarAbaProfessores(Evento evento) {

        return criarAbaInformacao(
                "Professores participantes",
                """
                Professores vinculados ao evento aparecerão aqui.

                Um professor pode ser marcado como responsável/principal
                pelo evento.
                """,
                "+ Adicionar professor",
                e -> JOptionPane.showMessageDialog(
                        this,
                        "Aqui será aberta a seleção dos professores cadastrados.",
                        "Adicionar professor",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );
    }

    // =====================================================
    // ABA ALUNOS
    // =====================================================

    private JPanel criarAbaAlunos(Evento evento) {

        return criarAbaInformacao(
                "Alunos inscritos",
                """
                Os alunos inscritos neste evento aparecerão aqui.

                Também será possível visualizar:

                • RM
                • Nome
                • Turma
                • Curso
                • Status da inscrição
                • Data da inscrição
                """,
                null,
                null
        );
    }

    // =====================================================
    // ABA AGENTES EXTERNOS
    // =====================================================

    private JPanel criarAbaAgentesExternos(Evento evento) {

        return criarAbaInformacao(
                "Agentes externos",
                """
                Pessoas externas à escola poderão ser vinculadas
                a este evento.

                Exemplos:

                • Palestrante
                • Convidado
                • Especialista
                • Avaliador
                • Outro

                Também será possível informar o tema da participação.
                """,
                "+ Adicionar agente externo",
                e -> JOptionPane.showMessageDialog(
                        this,
                        "Aqui será aberta a tela para cadastrar/vincular "
                                + "um palestrante ou convidado.",
                        "Agente externo",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );
    }

    // =====================================================
    // ABA ATIVIDADES
    // =====================================================

    private JPanel criarAbaAtividades(Evento evento) {

        return criarAbaInformacao(
                "Atividades",
                """
                As atividades do evento aparecerão aqui.

                Cada atividade poderá ser vinculada a uma ou mais
                comissões responsáveis.
                """,
                null,
                null
        );
    }

    // =====================================================
    // ABA RESPONSABILIDADES
    // =====================================================

    private JPanel criarAbaResponsabilidades(Evento evento) {

        return criarAbaInformacao(
                "Responsabilidades",
                """
                Responsabilidades específicas deste evento.

                Uma responsabilidade poderá ser atribuída a uma
                ou mais comissões.

                Exemplos:

                • Organizar auditório
                • Recepcionar convidados
                • Preparar equipamentos
                • Organizar certificados
                """,
                null,
                null
        );
    }

    // =====================================================
    // ABA DE INFORMAÇÃO PADRÃO
    // =====================================================

    private JPanel criarAbaInformacao(
            String titulo,
            String texto,
            String textoBotao,
            java.awt.event.ActionListener acaoBotao
    ) {

        JPanel painel =
                criarPainelAba();

        JLabel labelTitulo =
                criarLabel(
                        titulo,
                        18,
                        Font.BOLD,
                        TEXTO
                );

        painel.add(
                labelTitulo,
                BorderLayout.NORTH
        );

        JTextArea informacao =
                new JTextArea();

        informacao.setEditable(false);
        informacao.setLineWrap(true);
        informacao.setWrapStyleWord(true);

        informacao.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        informacao.setForeground(TEXTO);
        informacao.setBackground(Color.WHITE);
        informacao.setText(texto);

        informacao.setBorder(
                new EmptyBorder(
                        15,
                        10,
                        15,
                        10
                )
        );

        painel.add(
                new JScrollPane(informacao),
                BorderLayout.CENTER
        );

        if (
                textoBotao != null
                        && acaoBotao != null
        ) {

            JPanel botoes =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.RIGHT
                            )
                    );

            botoes.setBackground(FUNDO);

            JButton botao =
                    criarBotaoPrincipal(
                            textoBotao
                    );

            botao.addActionListener(
                    acaoBotao
            );

            botoes.add(botao);

            painel.add(
                    botoes,
                    BorderLayout.SOUTH
            );
        }

        return painel;
    }

    // =====================================================
    // PAINEL PADRÃO DAS ABAS
    // =====================================================

    private JPanel criarPainelAba() {

        JPanel painel =
                new JPanel(
                        new BorderLayout(10, 10)
                );

        painel.setBackground(FUNDO);

        painel.setBorder(
                new EmptyBorder(
                        15,
                        5,
                        10,
                        5
                )
        );

        return painel;
    }

    // =====================================================
    // CONSTRAINTS DO FORMULÁRIO
    // =====================================================

    private GridBagConstraints criarConstraintsFormulario() {

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

        gbc.weightx = 1;
        gbc.weighty = 0;

        return gbc;
    }

    // =====================================================
    // CAMPO DATA
    // =====================================================

    private JFormattedTextField criarCampoData() {

        try {

            MaskFormatter mascara =
                    new MaskFormatter("##/##/####");

            mascara.setPlaceholderCharacter('_');

            JFormattedTextField campo =
                    new JFormattedTextField(mascara);

            estilizarCampoFormatado(campo);

            return campo;

        } catch (ParseException e) {

            throw new RuntimeException(
                    "Erro ao criar campo de data.",
                    e
            );
        }
    }

    // =====================================================
    // CAMPO HORA
    // =====================================================

    private JFormattedTextField criarCampoHora() {

        try {

            MaskFormatter mascara =
                    new MaskFormatter("##:##");

            mascara.setPlaceholderCharacter('_');

            JFormattedTextField campo =
                    new JFormattedTextField(mascara);

            estilizarCampoFormatado(campo);

            return campo;

        } catch (ParseException e) {

            throw new RuntimeException(
                    "Erro ao criar campo de horário.",
                    e
            );
        }
    }

    private void estilizarCampoFormatado(
            JFormattedTextField campo
    ) {

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

        campo.setFocusLostBehavior(
                JFormattedTextField.PERSIST
        );

        campo.setBorder(
                criarBordaCampo()
        );
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

        gbc.gridx = coluna - 1;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0;
        gbc.fill =
                GridBagConstraints.HORIZONTAL;

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
        container.add(Box.createVerticalStrut(5));
        container.add(campo);

        painel.add(
                container,
                gbc
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
    // LABEL
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
                criarBordaCampo()
        );

        campo.setPreferredSize(
                new Dimension(
                        0,
                        40
                )
        );
    }

    private Border criarBordaCampo() {

        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        BORDA
                ),
                new EmptyBorder(
                        8,
                        10,
                        8,
                        10
                )
        );
    }

    // =====================================================
    // ESTILIZAR ÁREA DE TEXTO
    // =====================================================

    private void estilizarAreaTexto(
            JTextArea area
    ) {

        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        area.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        area.setForeground(TEXTO);
        area.setBackground(Color.WHITE);

        area.setMargin(
                new Insets(
                        8,
                        10,
                        8,
                        10
                )
        );
    }

    // =====================================================
    // COMBOBOX
    // =====================================================

    private void estilizarComboBox(
            JComboBox<String> combo
    ) {

        combo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXTO);

        combo.setBorder(
                BorderFactory.createLineBorder(
                        BORDA
                )
        );

        combo.setFocusable(false);

        combo.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component
                    getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {

                        JLabel label =
                                (JLabel)
                                        super.getListCellRendererComponent(
                                                list,
                                                value,
                                                index,
                                                isSelected,
                                                cellHasFocus
                                        );

                        label.setBorder(
                                new EmptyBorder(
                                        5,
                                        8,
                                        5,
                                        8
                                )
                        );

                        label.setFont(
                                new Font(
                                        "Segoe UI",
                                        Font.PLAIN,
                                        13
                                )
                        );

                        return label;
                    }
                }
        );
    }
}