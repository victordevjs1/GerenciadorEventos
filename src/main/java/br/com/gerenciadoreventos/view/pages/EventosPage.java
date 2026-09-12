package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.model.Evento;
import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.service.EventoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EventosPage extends JPanel {

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

    private final Usuario usuarioLogado;
    private final EventoService eventoService;

    private JPanel painelEventos;

    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    public EventosPage(Usuario usuarioLogado) {

        this.usuarioLogado = usuarioLogado;

        eventoService = new EventoService();

        inicializarInterface();
        carregarEventos();
    }

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

        // -------------------------
        // TÍTULO + BOTÃO
        // -------------------------

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
        textos.add(Box.createVerticalStrut(4));
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

        // -------------------------
        // FILTROS
        // -------------------------

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
    // LISTA DE EVENTOS
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
    // CARD DO EVENTO
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

        // -------------------------
        // INFORMAÇÕES
        // -------------------------

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

            JLabel data =
                    criarLabelInformacao(
                            "Data: "
                                    + evento.getDataInicio()
                                    .format(DATA)
                    );

            JLabel horario =
                    criarLabelInformacao(
                            "Horário: " + horarioTexto
                    );

            informacoes.add(data);

            informacoes.add(
                    Box.createVerticalStrut(6)
            );

            informacoes.add(horario);

            informacoes.add(
                    Box.createVerticalStrut(6)
            );
        }

        JLabel local =
                criarLabelInformacao(
                        "Local: "
                                + (evento.getLocal() != null
                                ? evento.getLocal()
                                : "Não informado")
                );

        informacoes.add(local);

        informacoes.add(
                Box.createVerticalStrut(6)
        );

        JLabel status =
                criarLabelInformacao(
                        "Status: "
                                + evento.getStatus()
                );

        informacoes.add(status);

        card.add(
                informacoes,
                BorderLayout.CENTER
        );

        // -------------------------
        // BOTÃO
        // -------------------------

        JButton verEvento =
                criarBotaoSecundario(
                        "Ver evento"
                );

        verEvento.addActionListener(e -> {

            JOptionPane.showMessageDialog(
                    this,
                    "Tela de detalhes do evento:\n"
                            + evento.getNome(),
                    "Evento",
                    JOptionPane.INFORMATION_MESSAGE
            );

        });

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

        JDialog dialog =
                new JDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Novo Evento",
                        Dialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setSize(
                600,
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
                        30,
                        25,
                        30
                )
        );

        // =================================================
        // CABEÇALHO DO FORMULÁRIO
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
                new JLabel("Criar novo evento");

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
                        "Preencha as informações principais do evento."
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

        // NOME

        JTextField campoNome =
                new JTextField();

        estilizarCampo(campoNome);

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Nome do evento",
                campoNome,
                2
        );

        // DESCRIÇÃO

        JTextArea campoDescricao =
                new JTextArea(3, 20);

        campoDescricao.setLineWrap(true);
        campoDescricao.setWrapStyleWord(true);

        campoDescricao.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        campoDescricao.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA),
                        new EmptyBorder(
                                8,
                                10,
                                8,
                                10
                        )
                )
        );

        JScrollPane scrollDescricao =
                new JScrollPane(
                        campoDescricao
                );

        scrollDescricao.setBorder(null);

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Descrição",
                scrollDescricao,
                2
        );

        // DATA INÍCIO

        JTextField campoDataInicio =
                new JTextField();

        campoDataInicio.putClientProperty(
                "JTextField.placeholderText",
                "dd/MM/yyyy"
        );

        estilizarCampo(campoDataInicio);

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Data de início",
                campoDataInicio,
                1
        );

        JTextField campoHoraInicio =
                new JTextField();

        campoHoraInicio.putClientProperty(
                "JTextField.placeholderText",
                "HH:mm"
        );

        estilizarCampo(campoHoraInicio);

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Horário de início",
                campoHoraInicio,
                2
        );

        linha++;

        // DATA FIM

        JTextField campoDataFim =
                new JTextField();

        campoDataFim.putClientProperty(
                "JTextField.placeholderText",
                "dd/MM/yyyy"
        );

        estilizarCampo(campoDataFim);

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Data de término",
                campoDataFim,
                1
        );

        JTextField campoHoraFim =
                new JTextField();

        campoHoraFim.putClientProperty(
                "JTextField.placeholderText",
                "HH:mm"
        );

        estilizarCampo(campoHoraFim);

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Horário de término",
                campoHoraFim,
                2
        );

        linha++;

        // LOCAL

        JTextField campoLocal =
                new JTextField();

        estilizarCampo(campoLocal);

        adicionarCampo(
                formulario,
                gbc,
                linha++,
                "Local",
                campoLocal,
                2
        );

        // CAPACIDADE

        JTextField campoCapacidade =
                new JTextField();

        campoCapacidade.putClientProperty(
                "JTextField.placeholderText",
                "Ex.: 100"
        );

        estilizarCampo(campoCapacidade);

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Capacidade",
                campoCapacidade,
                1
        );

        // STATUS

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

        adicionarCampo(
                formulario,
                gbc,
                linha,
                "Status",
                campoStatus,
                2
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

        cancelar.addActionListener(e ->
                dialog.dispose()
        );

        JButton salvar =
                criarBotaoPrincipal(
                        "Criar evento"
                );

        salvar.addActionListener(e -> {

            try {

                // -----------------------------
                // VALIDAÇÕES
                // -----------------------------

                if (campoNome.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Informe o nome do evento.",
                            "Campo obrigatório",
                            JOptionPane.WARNING_MESSAGE
                    );

                    campoNome.requestFocus();
                    return;
                }

                if (campoDataInicio.getText().trim().isEmpty()
                        || campoHoraInicio.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Informe a data e o horário de início.",
                            "Campo obrigatório",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                // -----------------------------
                // DATA E HORA
                // -----------------------------

                LocalDate dataInicio =
                        LocalDate.parse(
                                campoDataInicio.getText().trim(),
                                DATA
                        );

                LocalTime horaInicio =
                        LocalTime.parse(
                                campoHoraInicio.getText().trim(),
                                HORA
                        );

                LocalDateTime dataInicioFinal =
                        LocalDateTime.of(
                                dataInicio,
                                horaInicio
                        );

                LocalDateTime dataFimFinal = null;

                if (!campoDataFim.getText().trim().isEmpty()
                        && !campoHoraFim.getText().trim().isEmpty()) {

                    LocalDate dataFim =
                            LocalDate.parse(
                                    campoDataFim.getText().trim(),
                                    DATA
                            );

                    LocalTime horaFim =
                            LocalTime.parse(
                                    campoHoraFim.getText().trim(),
                                    HORA
                            );

                    dataFimFinal =
                            LocalDateTime.of(
                                    dataFim,
                                    horaFim
                            );

                    if (dataFimFinal.isBefore(dataInicioFinal)) {

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

                if (!campoCapacidade.getText().trim().isEmpty()) {

                    capacidade =
                            Integer.parseInt(
                                    campoCapacidade
                                            .getText()
                                            .trim()
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
                // OBJETO EVENTO
                // -----------------------------

                Evento evento =
                        new Evento();

                evento.setIdUsuarioCriador(
                        usuarioLogado.getId()
                );

                evento.setNome(
                        campoNome.getText().trim()
                );

                evento.setDescricao(
                        campoDescricao
                                .getText()
                                .trim()
                );

                evento.setDataInicio(
                        dataInicioFinal
                );

                evento.setDataFim(
                        dataFimFinal
                );

                evento.setLocal(
                        campoLocal.getText().trim()
                );

                evento.setCapacidade(
                        capacidade
                );

                evento.setStatus(
                        campoStatus
                                .getSelectedItem()
                                .toString()
                );

                // --------------------------------
                // SALVAR
                // --------------------------------
                //
                // Quando o método cadastrarEvento()
                // estiver implementado no Service,
                // coloque aqui:
                //
                // eventoService.cadastrarEvento(evento);
                //
                // --------------------------------

                eventoService.cadastrarEvento(evento);

                JOptionPane.showMessageDialog(
                        dialog,
                        "Evento criado com sucesso!",
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
                        "Não foi possível criar o evento.\n\n"
                                + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
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
    // MÉTODOS AUXILIARES DO FORMULÁRIO
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

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        painel.add(
                container,
                gbc
        );
    }

    // =====================================================
    // ESTILOS
    // =====================================================

    private JButton criarBotaoPrincipal(String texto) {

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

    private JButton criarBotaoSecundario(String texto) {

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
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {

                        JLabel label =
                                (JLabel) super
                                        .getListCellRendererComponent(
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