package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.model.Evento;
import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.service.EventoService;

import javax.swing.*;
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

    private final Usuario usuarioLogado;

    private final EventoService eventoService;

    private JPanel painelEventos;

    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public EventosPage(Usuario usuarioLogado) {

        this.usuarioLogado = usuarioLogado;

        eventoService = new EventoService();

        inicializarInterface();

        carregarEventos();
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

        JPanel linhaSuperior =
                new JPanel(
                        new BorderLayout()
                );

        linhaSuperior.setBackground(FUNDO);

        JLabel titulo =
                new JLabel("Eventos");

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
                        "Gerencie os eventos da escola"
                );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        subtitulo.setForeground(CINZA_TEXTO);

        JPanel textos =
                new JPanel();

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        textos.setBackground(FUNDO);

        textos.add(titulo);

        textos.add(
                Box.createVerticalStrut(4)
        );

        textos.add(subtitulo);

        linhaSuperior.add(
                textos,
                BorderLayout.WEST
        );

        JButton novoEvento =
                criarBotaoPrincipal(
                        "+ Novo Evento"
                );

        novoEvento.addActionListener(e ->
                abrirNovoEvento()
        );

        linhaSuperior.add(
                novoEvento,
                BorderLayout.EAST
        );

        cabecalho.add(linhaSuperior);

        cabecalho.add(
                Box.createVerticalStrut(22)
        );

        // =================================================
        // FILTROS
        // =================================================

        JPanel filtros =
                new JPanel(
                        new BorderLayout(10, 0)
                );

        filtros.setBackground(FUNDO);

        JTextField busca =
                new JTextField();

        busca.putClientProperty(
                "JTextField.placeholderText",
                "Pesquisar evento..."
        );

        estilizarCampo(busca);

        busca.setPreferredSize(
                new Dimension(0, 42)
        );

        JComboBox<String> filtro =
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

        estilizarComboBox(filtro);

        filtro.setPreferredSize(
                new Dimension(190, 42)
        );

        filtros.add(
                busca,
                BorderLayout.CENTER
        );

        filtros.add(
                filtro,
                BorderLayout.EAST
        );

        cabecalho.add(filtros);

        cabecalho.add(
                Box.createVerticalStrut(20)
        );

        return cabecalho;
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

    private void carregarEventos() {

        painelEventos.removeAll();

        List<Evento> eventos =
                eventoService.listarEventos();

        for (Evento evento : eventos) {

            painelEventos.add(
                    criarCardEvento(evento)
            );

            painelEventos.add(
                    Box.createVerticalStrut(15)
            );
        }

        painelEventos.revalidate();

        painelEventos.repaint();
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
                new JLabel(
                        evento.getNome()
                );

        nome.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        19
                )
        );

        nome.setForeground(TEXTO);

        informacoes.add(nome);

        informacoes.add(
                Box.createVerticalStrut(12)
        );

        if (evento.getDataInicio() != null) {

            String horarioTexto =
                    evento.getDataFim() != null
                            ? evento.getDataInicio().format(HORA)
                            + " - "
                            + evento.getDataFim().format(HORA)
                            : evento.getDataInicio().format(HORA);

            informacoes.add(
                    criarLabelInformacao(
                            "Data: "
                                    + evento.getDataInicio()
                                    .format(DATA)
                    )
            );

            informacoes.add(
                    Box.createVerticalStrut(6)
            );

            informacoes.add(
                    criarLabelInformacao(
                            "Horário: "
                                    + horarioTexto
                    )
            );

            informacoes.add(
                    Box.createVerticalStrut(6)
            );
        }

        informacoes.add(
                criarLabelInformacao(
                        "Local: "
                                + (
                                evento.getLocal() != null
                                        ? evento.getLocal()
                                        : "Não informado"
                        )
                )
        );

        informacoes.add(
                Box.createVerticalStrut(6)
        );

        informacoes.add(
                criarLabelInformacao(
                        "Status: "
                                + evento.getStatus()
                )
        );

        card.add(
                informacoes,
                BorderLayout.CENTER
        );

        JButton verEvento =
                criarBotaoSecundario(
                        "Ver evento"
                );

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

    private JLabel criarLabelInformacao(String texto) {

        JLabel label =
                new JLabel(texto);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        label.setForeground(
                new Color(71, 85, 105)
        );

        return label;
    }

    // =====================================================
    // NOVO EVENTO
    // =====================================================

    private void abrirNovoEvento() {

        abrirEditorEvento(null);
    }

    // =====================================================
    // EDITAR EVENTO
    // =====================================================

    private void abrirDetalhesEvento(Evento evento) {

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
    // DIALOG PRINCIPAL
    // =====================================================

    private void abrirEditorEvento(Evento evento) {

        boolean novo =
                evento == null;

        JDialog dialog =
                new JDialog(
                        SwingUtilities.getWindowAncestor(this),
                        novo
                                ? "Novo Evento"
                                : "Evento",
                        Dialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setSize(
                850,
                800
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
                        20,
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
                new JLabel(
                        novo
                                ? "Criar novo evento"
                                : evento.getNome()
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
                        novo
                                ? "Preencha as informações principais do evento."
                                : "Gerencie as informações e participantes do evento."
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
        // ABAS
        // =================================================

        JTabbedPane abas =
                new JTabbedPane();

        abas.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        // Aba principal
        if (novo) {

            abas.addTab(
                    "Dados do evento",
                    criarAbaDados(
                            evento,
                            dialog,
                            true
                    )
            );

        } else {

            abas.addTab(
                    "Dados do evento",
                    criarAbaDados(
                            evento,
                            dialog,
                            false
                    )
            );

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

        principal.add(
                abas,
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

        JButton fechar =
                criarBotaoSecundario(
                        "Fechar"
                );

        fechar.addActionListener(e ->
                dialog.dispose()
        );

        botoes.add(fechar);

        principal.add(
                botoes,
                BorderLayout.SOUTH
        );

        dialog.setContentPane(principal);

        dialog.setVisible(true);
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

        int linha = 0;

        // =================================================
        // NOME
        // =================================================

        JTextField campoNome =
                new JTextField();

        estilizarCampo(campoNome);

        if (!novo) {
            campoNome.setText(
                    evento.getNome()
            );
        }

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Nome do evento",
                campoNome,
                2
        );

        // =================================================
        // DESCRIÇÃO
        // =================================================

        JTextArea campoDescricao =
                new JTextArea(5, 20);

        campoDescricao.setLineWrap(true);
        campoDescricao.setWrapStyleWord(true);

        campoDescricao.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        campoDescricao.setForeground(TEXTO);
        campoDescricao.setBackground(Color.WHITE);

        campoDescricao.setMargin(
                new Insets(
                        8,
                        10,
                        8,
                        10
                )
        );

        JScrollPane scrollDescricao =
                new JScrollPane(campoDescricao);

        scrollDescricao.setBorder(
                BorderFactory.createLineBorder(BORDA)
        );

        scrollDescricao.setPreferredSize(
                new Dimension(
                        0,
                        100
                )
        );

        scrollDescricao.setMinimumSize(
                new Dimension(
                        0,
                        100
                )
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
        // DATA INÍCIO
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
        // DATA FIM
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

        JTextField campoLocal =
                new JTextField();

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
        // BOTÃO SALVAR
        // =================================================

        JButton salvar =
                criarBotaoPrincipal(
                        novo
                                ? "Criar evento"
                                : "Salvar alterações"
                );

        salvar.addActionListener(e -> {

            try {

                // -----------------------------
                // NOME
                // -----------------------------

                if (campoNome.getText()
                        .trim()
                        .isEmpty()) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Informe o nome do evento.",
                            "Campo obrigatório",
                            JOptionPane.WARNING_MESSAGE
                    );

                    campoNome.requestFocus();

                    return;
                }

                // -----------------------------
                // DATA INÍCIO
                // -----------------------------

                String textoDataInicio =
                        campoDataInicio
                                .getText()
                                .trim();

                String textoHoraInicio =
                        campoHoraInicio
                                .getText()
                                .trim();

                if (textoDataInicio.contains("_")
                        || textoHoraInicio.contains("_")
                        || textoDataInicio.isEmpty()
                        || textoHoraInicio.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Informe a data e o horário de início.",
                            "Campo obrigatório",
                            JOptionPane.WARNING_MESSAGE
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

                // -----------------------------
                // DATA FIM
                // -----------------------------

                LocalDateTime dataFimFinal =
                        null;

                String textoDataFim =
                        campoDataFim
                                .getText()
                                .trim();

                String textoHoraFim =
                        campoHoraFim
                                .getText()
                                .trim();

                boolean informouDataFim =
                        !textoDataFim.isEmpty()
                                && !textoDataFim.contains("_");

                boolean informouHoraFim =
                        !textoHoraFim.isEmpty()
                                && !textoHoraFim.contains("_");

                if (informouDataFim != informouHoraFim) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Informe a data e o horário de término completos.",
                            "Dados incompletos",
                            JOptionPane.WARNING_MESSAGE
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

                        JOptionPane.showMessageDialog(
                                dialog,
                                "A data de término não pode ser anterior à data de início.",
                                "Data inválida",
                                JOptionPane.WARNING_MESSAGE
                        );

                        return;
                    }
                }

                // -----------------------------
                // CAPACIDADE
                // -----------------------------

                int capacidade = 0;

                String textoCapacidade =
                        campoCapacidade
                                .getText()
                                .trim();

                if (!textoCapacidade.isEmpty()) {

                    capacidade =
                            Integer.parseInt(
                                    textoCapacidade
                            );

                    if (capacidade < 0) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "A capacidade não pode ser negativa.",
                                "Valor inválido",
                                JOptionPane.WARNING_MESSAGE
                        );

                        return;
                    }
                }

                // -----------------------------
                // EVENTO
                // -----------------------------

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

                // -----------------------------
                // SALVAR
                // -----------------------------

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

                carregarEventos();

            } catch (DateTimeParseException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Verifique as datas e horários.\n\n"
                                + "Data: dd/MM/yyyy\n"
                                + "Horário: HH:mm",
                        "Formato inválido",
                        JOptionPane.WARNING_MESSAGE
                );

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "A capacidade deve ser um número inteiro.",
                        "Valor inválido",
                        JOptionPane.WARNING_MESSAGE
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
        });

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
    // ABA COMISSÕES
    // =====================================================

    private JPanel criarAbaComissoes(Evento evento) {

        JPanel painel =
                criarPainelAba();

        JLabel titulo =
                new JLabel(
                        "Comissões do evento"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        titulo.setForeground(TEXTO);

        painel.add(
                titulo,
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

        informacao.setText(
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
                """
        );

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

        JPanel botoes =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        botoes.setBackground(FUNDO);

        JButton adicionar =
                criarBotaoPrincipal(
                        "+ Nova comissão"
                );

        adicionar.addActionListener(e ->
                abrirNovaComissao(evento)
        );

        botoes.add(adicionar);

        painel.add(
                botoes,
                BorderLayout.SOUTH
        );

        return painel;
    }

    // =====================================================
    // NOVA COMISSÃO
    // =====================================================

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

        JPanel painel =
                criarPainelAba();

        JLabel titulo =
                new JLabel(
                        "Professores participantes"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        titulo.setForeground(TEXTO);

        painel.add(
                titulo,
                BorderLayout.NORTH
        );

        JTextArea informacao =
                new JTextArea();

        informacao.setEditable(false);

        informacao.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        informacao.setText(
                """
                Professores vinculados ao evento aparecerão aqui.

                Um professor pode ser marcado como responsável/principal
                pelo evento.
                """
        );

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

        JPanel botoes =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        botoes.setBackground(FUNDO);

        JButton adicionar =
                criarBotaoPrincipal(
                        "+ Adicionar professor"
                );

        adicionar.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Aqui será aberta a seleção dos professores cadastrados.",
                        "Adicionar professor",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        botoes.add(adicionar);

        painel.add(
                botoes,
                BorderLayout.SOUTH
        );

        return painel;
    }

    // =====================================================
    // ABA ALUNOS
    // =====================================================

    private JPanel criarAbaAlunos(Evento evento) {

        JPanel painel =
                criarPainelAba();

        JLabel titulo =
                new JLabel(
                        "Alunos inscritos"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        titulo.setForeground(TEXTO);

        painel.add(
                titulo,
                BorderLayout.NORTH
        );

        JTextArea informacao =
                new JTextArea();

        informacao.setEditable(false);

        informacao.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        informacao.setText(
                """
                Os alunos inscritos neste evento aparecerão aqui.

                Também será possível visualizar:

                • RM
                • Nome
                • Turma
                • Curso
                • Status da inscrição
                • Data da inscrição
                """
        );

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

        return painel;
    }

    // =====================================================
    // ABA AGENTES EXTERNOS
    // =====================================================

    private JPanel criarAbaAgentesExternos(Evento evento) {

        JPanel painel =
                criarPainelAba();

        JLabel titulo =
                new JLabel(
                        "Agentes externos"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        titulo.setForeground(TEXTO);

        painel.add(
                titulo,
                BorderLayout.NORTH
        );

        JTextArea informacao =
                new JTextArea();

        informacao.setEditable(false);

        informacao.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        informacao.setText(
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
                """
        );

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

        JPanel botoes =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        botoes.setBackground(FUNDO);

        JButton adicionar =
                criarBotaoPrincipal(
                        "+ Adicionar agente externo"
                );

        adicionar.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Aqui será aberta a tela para cadastrar/vincular "
                                + "um palestrante ou convidado.",
                        "Agente externo",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        botoes.add(adicionar);

        painel.add(
                botoes,
                BorderLayout.SOUTH
        );

        return painel;
    }

    // =====================================================
    // ABA ATIVIDADES
    // =====================================================

    private JPanel criarAbaAtividades(Evento evento) {

        JPanel painel =
                criarPainelAba();

        JLabel titulo =
                new JLabel(
                        "Atividades"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        titulo.setForeground(TEXTO);

        painel.add(
                titulo,
                BorderLayout.NORTH
        );

        JTextArea informacao =
                new JTextArea();

        informacao.setEditable(false);

        informacao.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        informacao.setText(
                """
                As atividades do evento aparecerão aqui.

                Cada atividade poderá ser vinculada a uma ou mais
                comissões responsáveis.
                """
        );

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

        return painel;
    }

    // =====================================================
    // ABA RESPONSABILIDADES
    // =====================================================

    private JPanel criarAbaResponsabilidades(Evento evento) {

        JPanel painel =
                criarPainelAba();

        JLabel titulo =
                new JLabel(
                        "Responsabilidades"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        titulo.setForeground(TEXTO);

        painel.add(
                titulo,
                BorderLayout.NORTH
        );

        JTextArea informacao =
                new JTextArea();

        informacao.setEditable(false);

        informacao.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        informacao.setText(
                """
                Responsabilidades específicas deste evento.

                Uma responsabilidade poderá ser atribuída a uma
                ou mais comissões.

                Exemplos:

                • Organizar auditório
                • Recepcionar convidados
                • Preparar equipamentos
                • Organizar certificados
                """
        );

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
    // CAMPO DATA
    // =====================================================

    private JFormattedTextField criarCampoData() {

        try {

            MaskFormatter mascara =
                    new MaskFormatter("##/##/####");

            mascara.setPlaceholderCharacter('_');

            JFormattedTextField campo =
                    new JFormattedTextField(mascara);

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
    // CAMPO HORA
    // =====================================================

    private JFormattedTextField criarCampoHora() {

        try {

            MaskFormatter mascara =
                    new MaskFormatter("##:##");

            mascara.setPlaceholderCharacter('_');

            JFormattedTextField campo =
                    new JFormattedTextField(mascara);

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
                    "Erro ao criar campo de horário.",
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

        gbc.gridx = coluna - 1;
        gbc.gridy = linha;

        gbc.gridwidth = 1;

        gbc.weightx = 0.5;

        // Importante para o campo crescer corretamente
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