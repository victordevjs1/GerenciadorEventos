package br.com.gerenciadoreventos.view;

import br.com.gerenciadoreventos.model.Evento;
import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.service.EventoService;
import br.com.gerenciadoreventos.view.components.Sidebar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Eventos extends JFrame {

    private static final Color FUNDO =
            new Color(246, 248, 252);

    private final Usuario usuarioLogado;

    private final EventoService eventoService;

    private JPanel painelEventos;

    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    public Eventos(Usuario usuarioLogado) {

        this.usuarioLogado = usuarioLogado;

        eventoService = new EventoService();

        configurarJanela();
        inicializarInterface();
        carregarEventos();
    }

    private void configurarJanela() {

        setTitle("Eventos - Gerenciador de eventos");

        setSize(1200, 750);

        setMinimumSize(
                new Dimension(1000, 650)
        );

        setDefaultCloseOperation(
                EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);
    }

    private void inicializarInterface() {

        JPanel principal =
                new JPanel(new BorderLayout());

        principal.setBackground(FUNDO);

        principal.add(
                new Sidebar(
                        this,
                        usuarioLogado,
                        "eventos"
                ),
                BorderLayout.WEST
        );

        principal.add(
                criarConteudo(),
                BorderLayout.CENTER
        );

        add(principal);
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

        JLabel titulo =
                new JLabel("Eventos");

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        titulo.setForeground(
                new Color(15, 23, 42)
        );

        JPanel linhaSuperior =
                new JPanel(
                        new BorderLayout()
                );

        linhaSuperior.setBackground(FUNDO);

        linhaSuperior.add(
                titulo,
                BorderLayout.WEST
        );

        JButton novoEvento =
                new JButton("+ Novo Evento");

        novoEvento.setFocusPainted(false);

        linhaSuperior.add(
                novoEvento,
                BorderLayout.EAST
        );

        cabecalho.add(linhaSuperior);

        cabecalho.add(
                Box.createVerticalStrut(20)
        );

        JPanel filtros =
                new JPanel(
                        new BorderLayout(10, 0)
                );

        filtros.setBackground(FUNDO);

        JTextField busca =
                new JTextField();

        busca.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(203, 213, 225)
                        ),
                        new EmptyBorder(
                                8,
                                10,
                                8,
                                10
                        )
                )
        );

        busca.setPreferredSize(
                new Dimension(0, 40)
        );

        JComboBox<String> filtro =
                new JComboBox<>(
                        new String[]{
                                "Todos",
                                "Abertos",
                                "Encerrados"
                        }
                );

        filtro.setPreferredSize(
                new Dimension(120, 40)
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

    private JPanel criarCardEvento(
            Evento evento
    ) {

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setBackground(Color.WHITE);

        card.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        170
                )
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 226, 234)
                        ),
                        new EmptyBorder(
                                18,
                                20,
                                18,
                                20
                        )
                )
        );

        JLabel nome =
                new JLabel(
                        evento.getNome()
                );

        nome.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        nome.setForeground(
                new Color(15, 23, 42)
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

        informacoes.add(nome);

        informacoes.add(
                Box.createVerticalStrut(12)
        );

        if (evento.getDataInicio() != null) {

            JLabel data =
                    new JLabel(
                            "Data: "
                                    + evento.getDataInicio()
                                    .format(DATA)
                    );

            informacoes.add(data);

            informacoes.add(
                    Box.createVerticalStrut(6)
            );

            JLabel horario =
                    new JLabel(
                            "Horário: "
                                    + evento.getDataInicio()
                                    .format(HORA)
                                    + " - "
                                    + evento.getDataFim()
                                    .format(HORA)
                    );

            informacoes.add(horario);

            informacoes.add(
                    Box.createVerticalStrut(6)
            );
        }

        JLabel local =
                new JLabel(
                        "Local: "
                                + evento.getLocal()
                );

        informacoes.add(local);

        informacoes.add(
                Box.createVerticalStrut(6)
        );

        JLabel status =
                new JLabel(
                        "Status: "
                                + evento.getStatus()
                );

        informacoes.add(status);

        card.add(
                informacoes,
                BorderLayout.CENTER
        );

        JButton verEvento =
                new JButton("Ver evento");

        verEvento.setFocusPainted(false);

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
}
