package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.model.Evento;
import br.com.gerenciadoreventos.model.EventoPublico;
import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.service.EventoService;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
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

    // =====================================================
    // DADOS
    // =====================================================

    private final Usuario usuarioLogado;
    private final EventoService eventoService;

    private JPanel painelEventos;

    private JTextField campoBusca;

    private JComboBox<String> filtroCurso;
    private JComboBox<String> filtroSerie;
    private JComboBox<String> filtroStatus;

    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public EventosPage(
            Usuario usuarioLogado
    ) {

        this.usuarioLogado =
                usuarioLogado;

        this.eventoService =
                new EventoService();

        inicializarInterface();

        carregarEventos();
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

    private JPanel criarConteudo() {

        JPanel painel =
                new JPanel(
                        new BorderLayout()
                );

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
                criarListaEventos(),
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

        cabecalho.add(
                criarLinhaSuperior()
        );

        cabecalho.add(
                Box.createVerticalStrut(20)
        );

        cabecalho.add(
                criarFiltros()
        );

        cabecalho.add(
                Box.createVerticalStrut(20)
        );

        return cabecalho;
    }

    private JPanel criarLinhaSuperior() {

        JPanel linha =
                new JPanel(
                        new BorderLayout()
                );

        linha.setBackground(FUNDO);

        JPanel textos =
                new JPanel();

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        textos.setBackground(FUNDO);

        textos.add(
                criarLabel(
                        "Eventos",
                        28,
                        Font.BOLD,
                        TEXTO
                )
        );

        textos.add(
                Box.createVerticalStrut(4)
        );

        textos.add(
                criarLabel(
                        "Gerencie os eventos da escola",
                        14,
                        Font.PLAIN,
                        CINZA_TEXTO
                )
        );

        JButton novo =
                criarBotaoPrincipal(
                        "+ Novo Evento"
                );

        novo.addActionListener(
                e -> abrirNovoEvento()
        );

        linha.add(
                textos,
                BorderLayout.WEST
        );

        linha.add(
                novo,
                BorderLayout.EAST
        );

        return linha;
    }

    // =====================================================
    // FILTROS
    // =====================================================

    private JPanel criarFiltros() {

        JPanel painel =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                10,
                                0
                        )
                );

        painel.setBackground(FUNDO);

        campoBusca =
                new JTextField();

        campoBusca.putClientProperty(
                "JTextField.placeholderText",
                "Pesquisar evento..."
        );

        estilizarCampo(campoBusca);

        painel.add(
                campoBusca
        );

        filtroCurso =
                new JComboBox<>();

        filtroCurso.addItem(
                "Todos os cursos"
        );

        for (
                String curso :
                eventoService.listarCursos()
        ) {

            filtroCurso.addItem(curso);
        }

        estilizarComboBox(
                filtroCurso
        );

        painel.add(
                filtroCurso
        );

        filtroSerie =
                new JComboBox<>(
                        new String[]{
                                "Todas as séries",
                                "1º Ano",
                                "2º Ano",
                                "3º Ano"
                        }
                );

        estilizarComboBox(
                filtroSerie
        );

        painel.add(
                filtroSerie
        );

        filtroStatus =
                new JComboBox<>(
                        new String[]{
                                "Todos os status",
                                "Planejados",
                                "Abertos",
                                "Em andamento",
                                "Encerrados",
                                "Cancelados"
                        }
                );

        estilizarComboBox(
                filtroStatus
        );

        painel.add(
                filtroStatus
        );

        campoBusca
                .getDocument()
                .addDocumentListener(
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

        filtroCurso.addActionListener(
                e -> aplicarFiltros()
        );

        filtroSerie.addActionListener(
                e -> aplicarFiltros()
        );

        filtroStatus.addActionListener(
                e -> aplicarFiltros()
        );

        return painel;
    }

    // =====================================================
    // LISTA
    // =====================================================

    private JScrollPane criarListaEventos() {

        painelEventos =
                new JPanel();

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
                new JScrollPane(
                        painelEventos
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
    // EVENTOS
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

        String busca =
                campoBusca != null
                        ? campoBusca
                        .getText()
                        .trim()
                        .toLowerCase()
                        : "";

        String curso =
                filtroCurso != null
                        ? filtroCurso
                        .getSelectedItem()
                        .toString()
                        : "Todos os cursos";

        String serie =
                filtroSerie != null
                        ? filtroSerie
                        .getSelectedItem()
                        .toString()
                        : "Todas as séries";

        String status =
                filtroStatus != null
                        ? filtroStatus
                        .getSelectedItem()
                        .toString()
                        : "Todos os status";

        int quantidade = 0;

        for (Evento evento : eventos) {

            if (!correspondeBusca(
                    evento,
                    busca
            )) {
                continue;
            }

            if (!correspondePublico(
                    evento,
                    curso,
                    serie
            )) {
                continue;
            }

            if (!correspondeStatus(
                    evento,
                    status
            )) {
                continue;
            }

            painelEventos.add(
                    criarCardEvento(
                            evento
                    )
            );

            painelEventos.add(
                    Box.createVerticalStrut(15)
            );

            quantidade++;
        }

        if (quantidade == 0) {

            painelEventos.add(
                    criarMensagemSemEventos()
            );
        }

        painelEventos.revalidate();
        painelEventos.repaint();
    }

    // =====================================================
    // FILTRO BUSCA
    // =====================================================

    private boolean correspondeBusca(
            Evento evento,
            String busca
    ) {

        if (busca.isEmpty()) {
            return true;
        }

        String nome =
                evento.getNome() != null
                        ? evento.getNome()
                        .toLowerCase()
                        : "";

        String descricao =
                evento.getDescricao() != null
                        ? evento.getDescricao()
                        .toLowerCase()
                        : "";

        return nome.contains(busca)
                || descricao.contains(busca);
    }

    // =====================================================
    // FILTRO PÚBLICO
    // =====================================================

    private boolean correspondePublico(
            Evento evento,
            String cursoFiltro,
            String serieFiltro
    ) {

        /*
         * Evento para toda a escola sempre aparece.
         */
        if (evento.isPublicoTodos()) {
            return true;
        }

        boolean filtraCurso =
                !"Todos os cursos"
                        .equals(cursoFiltro);

        boolean filtraSerie =
                !"Todas as séries"
                        .equals(serieFiltro);

        /*
         * Sem filtro específico.
         */
        if (!filtraCurso && !filtraSerie) {
            return true;
        }

        for (
                EventoPublico publico :
                evento.getPublicos()
        ) {

            boolean cursoOk =
                    !filtraCurso
                            || publico
                            .getCurso()
                            .equals(cursoFiltro);

            boolean serieOk =
                    !filtraSerie
                            || publico.isCursoInteiro()
                            || publico.getSerie().equals(serieFiltro);

            if (cursoOk && serieOk) {
                return true;
            }
        }

        return false;
    }

    // =====================================================
    // FILTRO STATUS
    // =====================================================

    private boolean correspondeStatus(
            Evento evento,
            String filtro
    ) {

        if ("Todos os status".equals(filtro)) {
            return true;
        }

        String status =
                eventoService.calcularStatus(
                        evento
                );

        return switch (filtro) {

            case "Planejados" ->
                    "PLANEJADO".equals(status);

            case "Abertos" ->
                    "ABERTO".equals(evento.getStatus());

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

    // =====================================================
    // CARD
    // =====================================================

    private JPanel criarCardEvento(
            Evento evento
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
                        205
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

        informacoes.setBackground(
                Color.WHITE
        );

        informacoes.add(
                criarLabel(
                        evento.getNome(),
                        19,
                        Font.BOLD,
                        TEXTO
                )
        );

        informacoes.add(
                Box.createVerticalStrut(10)
        );

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
                Box.createVerticalStrut(5)
        );

        informacoes.add(
                criarLabelInformacao(
                        "Público: "
                                + obterPublico(evento)
                )
        );

        informacoes.add(
                Box.createVerticalStrut(5)
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

        JButton ver =
                criarBotaoSecundario(
                        "Ver evento"
                );

        ver.addActionListener(
                e -> abrirDetalhesEvento(
                        evento
                )
        );

        JPanel botao =
                new JPanel(
                        new GridBagLayout()
                );

        botao.setBackground(
                Color.WHITE
        );

        botao.add(ver);

        card.add(
                botao,
                BorderLayout.EAST
        );

        return card;
    }

    // =====================================================
    // INFORMAÇÕES DATA
    // =====================================================

    private void adicionarInformacoesData(
            JPanel painel,
            Evento evento
    ) {

        if (evento.getDataInicio() == null) {
            return;
        }

        painel.add(
                criarLabelInformacao(
                        "Data: "
                                + evento
                                .getDataInicio()
                                .format(DATA)
                )
        );

        painel.add(
                Box.createVerticalStrut(5)
        );

        String horario =
                evento.getDataInicio()
                        .format(HORA);

        if (evento.getDataFim() != null) {

            horario +=
                    " - "
                            + evento
                            .getDataFim()
                            .format(HORA);
        }

        painel.add(
                criarLabelInformacao(
                        "Horário: "
                                + horario
                )
        );

        painel.add(
                Box.createVerticalStrut(5)
        );
    }

    // =====================================================
    // PÚBLICO
    // =====================================================

    private String obterPublico(
            Evento evento
    ) {

        if (evento.isPublicoTodos()) {
            return "Toda a escola";
        }

        List<EventoPublico> publicos =
                evento.getPublicos();

        if (
                publicos == null
                        || publicos.isEmpty()
        ) {

            return "Nenhum público definido";
        }

        if (publicos.size() == 1) {

            return publicos
                    .get(0)
                    .toString();
        }

        String primeiro =
                publicos
                        .get(0)
                        .toString();

        return primeiro
                + " + "
                + (publicos.size() - 1)
                + " outra(s)";
    }

    private String obterLocal(
            Evento evento
    ) {

        return evento.getLocal() != null
                && !evento.getLocal().isBlank()
                ? evento.getLocal()
                : "Não informado";
    }

    private String obterStatus(
            Evento evento
    ) {

        return eventoService.calcularStatus(
                evento
        );
    }

    // =====================================================
    // MENSAGEM
    // =====================================================

    private JPanel criarMensagemSemEventos() {

        JPanel painel =
                new JPanel(
                        new GridBagLayout()
                );

        painel.setBackground(
                Color.WHITE
        );

        painel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(
                                226,
                                232,
                                240
                        )
                )
        );

        painel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        120
                )
        );

        painel.add(
                criarLabel(
                        "Nenhum evento encontrado.",
                        15,
                        Font.PLAIN,
                        CINZA_TEXTO
                )
        );

        return painel;
    }

    // =====================================================
    // NOVO / DETALHES
    // =====================================================

    private void abrirNovoEvento() {

        abrirEditorEvento(null);
    }

    private void abrirDetalhesEvento(
            Evento evento
    ) {

        Evento atualizado =
                eventoService.buscarPorId(
                        evento.getId()
                );

        if (atualizado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível carregar o evento.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        abrirEditorEvento(
                atualizado
        );
    }

    // =====================================================
    // EDITOR
    // =====================================================

    private void abrirEditorEvento(
            Evento evento
    ) {

        boolean novo =
                evento == null;

        JDialog dialog =
                new JDialog(
                        SwingUtilities
                                .getWindowAncestor(
                                        this
                                ),
                        novo
                                ? "Novo Evento"
                                : "Evento",
                        Dialog.ModalityType
                                .APPLICATION_MODAL
                );

        dialog.setSize(
                900,
                820
        );

        dialog.setLocationRelativeTo(
                this
        );

        dialog.setResizable(false);

        JPanel principal =
                new JPanel(
                        new BorderLayout()
                );

        principal.setBackground(
                FUNDO
        );

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

        principal.add(
                criarAbasEditor(
                        evento,
                        novo,
                        dialog
                ),
                BorderLayout.CENTER
        );

        principal.add(
                criarBotoesEditor(
                        dialog
                ),
                BorderLayout.SOUTH
        );

        dialog.setContentPane(
                principal
        );

        dialog.setVisible(true);
    }

    private JPanel criarCabecalhoEditor(
            Evento evento,
            boolean novo
    ) {

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
                criarLabel(
                        novo
                                ? "Criar novo evento"
                                : evento.getNome(),
                        24,
                        Font.BOLD,
                        TEXTO
                )
        );

        painel.add(
                Box.createVerticalStrut(5)
        );

        painel.add(
                criarLabel(
                        novo
                                ? "Preencha as informações do evento."
                                : "Gerencie as informações e o público do evento.",
                        14,
                        Font.PLAIN,
                        CINZA_TEXTO
                )
        );

        return painel;
    }

    private JTabbedPane criarAbasEditor(
            Evento evento,
            boolean novo,
            JDialog dialog
    ) {

        JTabbedPane abas =
                new JTabbedPane();

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
                    criarAbaInformacao(
                            "Comissões do evento",
                            "Aqui ficarão as comissões deste evento.",
                            "+ Nova comissão",
                            e -> mensagem(
                                    "Nova comissão",
                                    "Aqui será criada uma comissão para o evento."
                            )
                    )
            );

            abas.addTab(
                    "Professores",
                    criarAbaInformacao(
                            "Professores participantes",
                            "Professores vinculados ao evento aparecerão aqui.",
                            "+ Adicionar professor",
                            e -> mensagem(
                                    "Adicionar professor",
                                    "Aqui será aberta a seleção dos professores."
                            )
                    )
            );

            abas.addTab(
                    "Alunos",
                    criarAbaInformacao(
                            "Alunos inscritos",
                            "Os alunos inscritos neste evento aparecerão aqui.",
                            null,
                            null
                    )
            );

            abas.addTab(
                    "Agentes externos",
                    criarAbaInformacao(
                            "Agentes externos",
                            "Palestrantes, convidados e especialistas vinculados ao evento.",
                            "+ Adicionar agente externo",
                            e -> mensagem(
                                    "Agente externo",
                                    "Aqui será aberta a seleção do agente externo."
                            )
                    )
            );

            abas.addTab(
                    "Atividades",
                    criarAbaInformacao(
                            "Atividades",
                            "As atividades do evento aparecerão aqui.",
                            null,
                            null
                    )
            );

            abas.addTab(
                    "Responsabilidades",
                    criarAbaInformacao(
                            "Responsabilidades",
                            "As responsabilidades específicas deste evento aparecerão aqui.",
                            null,
                            null
                    )
            );
        }

        return abas;
    }

    private JPanel criarBotoesEditor(
            JDialog dialog
    ) {

        JPanel painel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                5
                        )
                );

        painel.setBackground(FUNDO);

        JButton fechar =
                criarBotaoSecundario(
                        "Fechar"
                );

        fechar.addActionListener(
                e -> dialog.dispose()
        );

        JButton salvar =
                (JButton)
                        dialog
                                .getRootPane()
                                .getClientProperty(
                                        "botaoSalvarEvento"
                                );

        painel.add(fechar);

        if (salvar != null) {
            painel.add(salvar);
        }

        return painel;
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

        JPanel formulario =
                new JPanel(
                        new GridBagLayout()
                );

        formulario.setBackground(
                Color.WHITE
        );

        formulario.setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        GridBagConstraints gbc =
                criarConstraintsFormulario();

        int linha = 0;

        // -------------------------------------------------
        // NOME
        // -------------------------------------------------

        JTextField nome =
                new JTextField();

        estilizarCampo(nome);

        if (!novo) {
            nome.setText(
                    evento.getNome()
            );
        }

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Nome do evento",
                nome,
                2
        );

        // -------------------------------------------------
        // DESCRIÇÃO
        // -------------------------------------------------

        JTextArea descricao =
                new JTextArea(
                        5,
                        20
                );

        estilizarAreaTexto(
                descricao
        );

        if (
                !novo
                        && evento.getDescricao() != null
        ) {

            descricao.setText(
                    evento.getDescricao()
            );
        }

        JScrollPane scrollDescricao =
                new JScrollPane(
                        descricao
                );

        scrollDescricao.setPreferredSize(
                new Dimension(
                        0,
                        100
                )
        );

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Descrição",
                scrollDescricao,
                2
        );

        // -------------------------------------------------
        // DATA INÍCIO
        // -------------------------------------------------

        JFormattedTextField dataInicio =
                criarCampoData();

        JFormattedTextField horaInicio =
                criarCampoHora();

        if (
                !novo
                        && evento.getDataInicio() != null
        ) {

            dataInicio.setText(
                    evento.getDataInicio()
                            .format(DATA)
            );

            horaInicio.setText(
                    evento.getDataInicio()
                            .format(HORA)
            );
        }

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Data de início",
                dataInicio,
                1
        );

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Horário de início",
                horaInicio,
                2
        );

        linha++;

        // -------------------------------------------------
        // DATA FIM
        // -------------------------------------------------

        JFormattedTextField dataFim =
                criarCampoData();

        JFormattedTextField horaFim =
                criarCampoHora();

        if (
                !novo
                        && evento.getDataFim() != null
        ) {

            dataFim.setText(
                    evento.getDataFim()
                            .format(DATA)
            );

            horaFim.setText(
                    evento.getDataFim()
                            .format(HORA)
            );
        }

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Data de término",
                dataFim,
                1
        );

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Horário de término",
                horaFim,
                2
        );

        linha++;

        // -------------------------------------------------
        // LOCAL
        // -------------------------------------------------

        JTextField local =
                new JTextField();

        estilizarCampo(local);

        if (
                !novo
                        && evento.getLocal() != null
        ) {

            local.setText(
                    evento.getLocal()
            );
        }

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Local",
                local,
                2
        );

        // -------------------------------------------------
        // CAPACIDADE
        // -------------------------------------------------

        JTextField capacidade =
                new JTextField();

        estilizarCampo(capacidade);

        if (!novo) {

            capacidade.setText(
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
                capacidade,
                1
        );

        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        JComboBox<String> status =
                new JComboBox<>(
                        new String[]{
                                "PLANEJADO",
                                "ABERTO",
                                "EM_ANDAMENTO",
                                "ENCERRADO",
                                "CANCELADO"
                        }
                );

        estilizarComboBox(
                status
        );

        if (
                !novo
                        && evento.getStatus() != null
        ) {

            status.setSelectedItem(
                    evento.getStatus()
            );
        }

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Status",
                status,
                2
        );

        // -------------------------------------------------
        // PÚBLICO
        // -------------------------------------------------

        JPanel publico =
                criarSeletorPublico(
                        evento,
                        novo
                );

        gbc.gridx = 0;
        gbc.gridy = linha++;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        formulario.add(
                publico,
                gbc
        );

        // -------------------------------------------------
        // BOTÃO
        // -------------------------------------------------

        JButton salvar =
                criarBotaoPrincipal(
                        novo
                                ? "Criar evento"
                                : "Salvar alterações"
                );

        salvar.addActionListener(
                e -> salvarEvento(
                        evento,
                        novo,
                        dialog,
                        nome,
                        descricao,
                        dataInicio,
                        horaInicio,
                        dataFim,
                        horaFim,
                        local,
                        capacidade,
                        status,
                        publico
                )
        );

        dialog
                .getRootPane()
                .putClientProperty(
                        "botaoSalvarEvento",
                        salvar
                );

        JScrollPane scroll =
                new JScrollPane(
                        formulario
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        new Color(
                                226,
                                232,
                                240
                        )
                )
        );

        principal.add(
                scroll,
                BorderLayout.CENTER
        );

        return principal;
    }

    // =====================================================
    // SELETOR DE PÚBLICO
    // =====================================================

    private JPanel criarSeletorPublico(
            Evento evento,
            boolean novo
    ) {

        JPanel painel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        painel.setBackground(
                Color.WHITE
        );

        painel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDA
                        ),
                        new EmptyBorder(
                                12,
                                12,
                                12,
                                12
                        )
                )
        );

        JLabel titulo =
                criarLabel(
                        "Público do evento",
                        13,
                        Font.BOLD,
                        TEXTO
                );

        painel.add(
                titulo,
                BorderLayout.NORTH
        );

        JRadioButton todas =
                new JRadioButton(
                        "Toda a escola"
                );

        JRadioButton especificas =
                new JRadioButton(
                        "Selecionar cursos e/ou séries"
                );

        todas.setBackground(
                Color.WHITE
        );

        especificas.setBackground(
                Color.WHITE
        );

        ButtonGroup grupo =
                new ButtonGroup();

        grupo.add(todas);
        grupo.add(especificas);

        DefaultListModel<EventoPublico> modelo =
                new DefaultListModel<>();

        List<EventoPublico> disponiveis =
                eventoService
                        .listarPublicosDisponiveis();

        for (
                EventoPublico publico :
                disponiveis
        ) {

            modelo.addElement(
                    publico
            );
        }

        JList<EventoPublico> lista =
                new JList<>(
                        modelo
                );

        lista.setSelectionMode(
                ListSelectionModel
                        .MULTIPLE_INTERVAL_SELECTION
        );

        lista.setVisibleRowCount(7);

        lista.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        lista.setBackground(
                Color.WHITE
        );

        lista.setCellRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component
                    getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean selected,
                            boolean focus
                    ) {

                        JLabel label =
                                (JLabel)
                                        super.getListCellRendererComponent(
                                                list,
                                                value,
                                                index,
                                                selected,
                                                focus
                                        );

                        label.setText(
                                value.toString()
                        );

                        label.setBorder(
                                new EmptyBorder(
                                        7,
                                        8,
                                        7,
                                        8
                                )
                        );

                        return label;
                    }
                }
        );

        if (
                !novo
                        && evento != null
                        && !evento.isPublicoTodos()
        ) {

            especificas.setSelected(
                    true
            );

            for (int i = 0;
                 i < modelo.size();
                 i++) {

                EventoPublico disponivel =
                        modelo.getElementAt(i);

                for (
                        EventoPublico selecionado :
                        evento.getPublicos()
                ) {

                    if (
                            disponivel
                                    .getChave()
                                    .equals(
                                            selecionado
                                                    .getChave()
                                    )
                    ) {

                        lista.addSelectionInterval(
                                i,
                                i
                        );

                        break;
                    }
                }
            }

        } else {

            todas.setSelected(
                    true
            );
        }

        JPanel opcoes =
                new JPanel();

        opcoes.setLayout(
                new BoxLayout(
                        opcoes,
                        BoxLayout.Y_AXIS
                )
        );

        opcoes.setBackground(
                Color.WHITE
        );

        opcoes.add(todas);
        opcoes.add(especificas);

        JScrollPane scroll =
                new JScrollPane(
                        lista
                );

        scroll.setPreferredSize(
                new Dimension(
                        0,
                        150
                )
        );

        painel.add(
                opcoes,
                BorderLayout.WEST
        );

        painel.add(
                scroll,
                BorderLayout.CENTER
        );

        Runnable atualizar =
                () -> {

                    boolean usarLista =
                            especificas.isSelected();

                    lista.setEnabled(
                            usarLista
                    );

                    scroll.setEnabled(
                            usarLista
                    );

                    if (!usarLista) {
                        lista.clearSelection();
                    }
                };

        todas.addActionListener(
                e -> atualizar.run()
        );

        especificas.addActionListener(
                e -> atualizar.run()
        );

        atualizar.run();

        /*
         * Guardamos os componentes no painel.
         * Isso permite recuperar posteriormente
         * no método salvarEvento.
         */
        painel.putClientProperty(
                "radioTodas",
                todas
        );

        painel.putClientProperty(
                "radioEspecificas",
                especificas
        );

        painel.putClientProperty(
                "listaPublicos",
                lista
        );

        return painel;
    }

    // =====================================================
    // SALVAR
    // =====================================================

    private void salvarEvento(
            Evento evento,
            boolean novo,
            JDialog dialog,
            JTextField nome,
            JTextArea descricao,
            JFormattedTextField dataInicio,
            JFormattedTextField horaInicio,
            JFormattedTextField dataFim,
            JFormattedTextField horaFim,
            JTextField local,
            JTextField capacidade,
            JComboBox<String> status,
            JPanel painelPublico
    ) {

        try {

            // -------------------------------------------------
            // NOME
            // -------------------------------------------------

            if (
                    nome.getText()
                            .trim()
                            .isEmpty()
            ) {

                mostrarAviso(
                        dialog,
                        "Informe o nome do evento.",
                        "Campo obrigatório"
                );

                nome.requestFocus();

                return;
            }

            // -------------------------------------------------
            // DATA INÍCIO
            // -------------------------------------------------

            String textoDataInicio =
                    dataInicio
                            .getText()
                            .trim();

            String textoHoraInicio =
                    horaInicio
                            .getText()
                            .trim();

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

            LocalDate dataInicial =
                    LocalDate.parse(
                            textoDataInicio,
                            DATA
                    );

            LocalTime horaInicial =
                    LocalTime.parse(
                            textoHoraInicio,
                            HORA
                    );

            LocalDateTime inicio =
                    LocalDateTime.of(
                            dataInicial,
                            horaInicial
                    );

            // -------------------------------------------------
            // DATA FIM
            // -------------------------------------------------

            LocalDateTime fim =
                    null;

            String textoDataFim =
                    dataFim
                            .getText()
                            .trim();

            String textoHoraFim =
                    horaFim
                            .getText()
                            .trim();

            boolean informouDataFim =
                    !textoDataFim.isEmpty()
                            && !textoDataFim.contains("_");

            boolean informouHoraFim =
                    !textoHoraFim.isEmpty()
                            && !textoHoraFim.contains("_");

            if (
                    informouDataFim
                            != informouHoraFim
            ) {

                mostrarAviso(
                        dialog,
                        "Informe a data e o horário de término completos.",
                        "Dados incompletos"
                );

                return;
            }

            if (
                    informouDataFim
                            && informouHoraFim
            ) {

                LocalDate dataFinal =
                        LocalDate.parse(
                                textoDataFim,
                                DATA
                        );

                LocalTime horaFinal =
                        LocalTime.parse(
                                textoHoraFim,
                                HORA
                        );

                fim =
                        LocalDateTime.of(
                                dataFinal,
                                horaFinal
                        );

                if (fim.isBefore(inicio)) {

                    mostrarAviso(
                            dialog,
                            "A data de término não pode ser anterior à data de início.",
                            "Data inválida"
                    );

                    return;
                }
            }

            // -------------------------------------------------
            // CAPACIDADE
            // -------------------------------------------------

            int capacidadeValor = 0;

            String textoCapacidade =
                    capacidade
                            .getText()
                            .trim();

            if (
                    !textoCapacidade.isEmpty()
            ) {

                capacidadeValor =
                        Integer.parseInt(
                                textoCapacidade
                        );

                if (
                        capacidadeValor < 0
                ) {

                    mostrarAviso(
                            dialog,
                            "A capacidade não pode ser negativa.",
                            "Valor inválido"
                    );

                    return;
                }
            }

            // -------------------------------------------------
            // EVENTO
            // -------------------------------------------------

            Evento eventoSalvar =
                    novo
                            ? new Evento()
                            : evento;

            if (novo) {

                eventoSalvar
                        .setIdUsuarioCriador(
                                usuarioLogado
                                        .getId()
                        );
            }

            eventoSalvar.setNome(
                    nome.getText()
                            .trim()
            );

            eventoSalvar.setDescricao(
                    descricao.getText()
                            .trim()
            );

            eventoSalvar.setDataInicio(
                    inicio
            );

            eventoSalvar.setDataFim(
                    fim
            );

            eventoSalvar.setLocal(
                    local.getText()
                            .trim()
            );

            eventoSalvar.setCapacidade(
                    capacidadeValor
            );

            eventoSalvar.setStatus(
                    status.getSelectedItem()
                            .toString()
            );

            // -------------------------------------------------
            // PÚBLICO
            // -------------------------------------------------

            JRadioButton radioTodas =
                    (JRadioButton)
                            painelPublico
                                    .getClientProperty(
                                            "radioTodas"
                                    );

            @SuppressWarnings("unchecked")
            JList<EventoPublico> lista =
                    (JList<EventoPublico>)
                            painelPublico
                                    .getClientProperty(
                                            "listaPublicos"
                                    );

            boolean todas =
                    radioTodas.isSelected();

            eventoSalvar.setPublicoTodos(
                    todas
            );

            if (todas) {

                eventoSalvar.limparPublicos();

            } else {

                List<EventoPublico> selecionados =
                        lista.getSelectedValuesList();

                if (
                        selecionados == null
                                || selecionados.isEmpty()
                ) {

                    mostrarAviso(
                            dialog,
                            "Selecione pelo menos um público para o evento.",
                            "Público obrigatório"
                    );

                    return;
                }

                eventoSalvar.setPublicos(
                        selecionados
                );
            }

            // -------------------------------------------------
            // BANCO
            // -------------------------------------------------

            boolean sucesso;

            if (novo) {

                sucesso =
                        eventoService
                                .cadastrarEvento(
                                        eventoSalvar
                                );

            } else {

                sucesso =
                        eventoService
                                .atualizarEvento(
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

        } catch (
                DateTimeParseException ex
        ) {

            mostrarAviso(
                    dialog,
                    "Verifique as datas e horários.\n\n"
                            + "Data: dd/MM/yyyy\n"
                            + "Horário: HH:mm",
                    "Formato inválido"
            );

        } catch (
                NumberFormatException ex
        ) {

            mostrarAviso(
                    dialog,
                    "A capacidade deve ser um número inteiro.",
                    "Valor inválido"
            );

        } catch (
                IllegalArgumentException ex
        ) {

            mostrarAviso(
                    dialog,
                    ex.getMessage(),
                    "Dados inválidos"
            );

        } catch (
                Exception ex
        ) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    dialog,
                    "Erro ao salvar evento:\n\n"
                            + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // ABAS INFORMATIVAS
    // =====================================================

    private JPanel criarAbaInformacao(
            String titulo,
            String texto,
            String textoBotao,
            ActionListener acao
    ) {

        JPanel painel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        painel.setBackground(
                FUNDO
        );

        painel.setBorder(
                new EmptyBorder(
                        15,
                        5,
                        10,
                        5
                )
        );

        painel.add(
                criarLabel(
                        titulo,
                        18,
                        Font.BOLD,
                        TEXTO
                ),
                BorderLayout.NORTH
        );

        JTextArea area =
                new JTextArea(
                        texto
                );

        area.setEditable(
                false
        );

        area.setLineWrap(
                true
        );

        area.setWrapStyleWord(
                true
        );

        area.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        area.setForeground(
                TEXTO
        );

        area.setBackground(
                Color.WHITE
        );

        area.setBorder(
                new EmptyBorder(
                        15,
                        15,
                        15,
                        15
                )
        );

        painel.add(
                new JScrollPane(
                        area
                ),
                BorderLayout.CENTER
        );

        if (
                textoBotao != null
                        && acao != null
        ) {

            JPanel botoes =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.RIGHT
                            )
                    );

            botoes.setBackground(
                    FUNDO
            );

            JButton botao =
                    criarBotaoPrincipal(
                            textoBotao
                    );

            botao.addActionListener(
                    acao
            );

            botoes.add(botao);

            painel.add(
                    botoes,
                    BorderLayout.SOUTH
            );
        }

        return painel;
    }

    private void mensagem(
            String titulo,
            String texto
    ) {

        JOptionPane.showMessageDialog(
                this,
                texto,
                titulo,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // CAMPOS
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

            estilizarCampoFormatado(
                    campo
            );

            return campo;

        } catch (ParseException e) {

            throw new RuntimeException(
                    "Erro ao criar campo de data.",
                    e
            );
        }
    }

    private JFormattedTextField criarCampoHora() {

        try {

            MaskFormatter mascara =
                    new MaskFormatter(
                            "##:##"
                    );

            mascara.setPlaceholderCharacter(
                    '_'
            );

            JFormattedTextField campo =
                    new JFormattedTextField(
                            mascara
                    );

            estilizarCampoFormatado(
                    campo
            );

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
    // FORMULÁRIO
    // =====================================================

    private GridBagConstraints
    criarConstraintsFormulario() {

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

        return gbc;
    }

    private void adicionarCampo(
            JPanel painel,
            GridBagConstraints gbc,
            int linha,
            String titulo,
            Component campo,
            int coluna
    ) {

        gbc.gridx =
                coluna - 1;

        gbc.gridy =
                linha;

        gbc.gridwidth = 1;

        gbc.weightx =
                coluna == 1
                        ? 0.5
                        : 0.5;

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

        container.setBackground(
                Color.WHITE
        );

        container.add(
                criarLabel(
                        titulo,
                        12,
                        Font.BOLD,
                        TEXTO
                )
        );

        container.add(
                Box.createVerticalStrut(5)
        );

        container.add(
                campo
        );

        painel.add(
                container,
                gbc
        );
    }

    // =====================================================
    // ESTILO
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

        return BorderFactory
                .createCompoundBorder(
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

    private void estilizarAreaTexto(
            JTextArea area
    ) {

        area.setLineWrap(
                true
        );

        area.setWrapStyleWord(
                true
        );

        area.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        area.setForeground(
                TEXTO
        );

        area.setBackground(
                Color.WHITE
        );

        area.setMargin(
                new Insets(
                        8,
                        10,
                        8,
                        10
                )
        );
    }

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

        combo.setBackground(
                Color.WHITE
        );

        combo.setForeground(
                TEXTO
        );

        combo.setBorder(
                BorderFactory.createLineBorder(
                        BORDA
                )
        );

        combo.setFocusable(
                false
        );
    }

    // =====================================================
    // BOTÕES
    // =====================================================

    private JButton criarBotaoPrincipal(
            String texto
    ) {

        JButton botao =
                new JButton(
                        texto
                );

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

        botao.setBackground(
                AZUL
        );

        botao.setFocusPainted(
                false
        );

        botao.setBorderPainted(
                false
        );

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
                new JButton(
                        texto
                );

        botao.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        botao.setForeground(
                TEXTO
        );

        botao.setBackground(
                Color.WHITE
        );

        botao.setFocusPainted(
                false
        );

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
                new JLabel(
                        texto
                );

        label.setFont(
                new Font(
                        "Segoe UI",
                        estilo,
                        tamanho
                )
        );

        label.setForeground(
                cor
        );

        return label;
    }

    private JLabel criarLabelInformacao(
            String texto
    ) {

        return criarLabel(
                texto,
                14,
                Font.PLAIN,
                new Color(
                        71,
                        85,
                        105
                )
        );
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
}
