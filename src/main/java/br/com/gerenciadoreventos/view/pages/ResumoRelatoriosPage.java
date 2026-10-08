package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.model.AlunoStatus;
import br.com.gerenciadoreventos.model.EventoDesempenho;
import br.com.gerenciadoreventos.model.EventoOpcao;
import br.com.gerenciadoreventos.model.PontoComparecimento;
import br.com.gerenciadoreventos.model.ResumoGeral;
import br.com.gerenciadoreventos.service.PresencaImportService;
import br.com.gerenciadoreventos.service.RelatorioDesempenhoService;
import br.com.gerenciadoreventos.service.RelatorioFrequenciaService;
import br.com.gerenciadoreventos.theme.ThemeManager;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ResumoRelatoriosPage extends JPanel {

    // =====================================================
    // CORES
    // =====================================================

    private static final Color FUNDO =
            new Color(246, 248, 252);

    private static final Color CARD =
            Color.WHITE;

    private static final Color TEXTO =
            new Color(15, 23, 42);

    private static final Color CINZA =
            new Color(100, 116, 139);

    private static final Color AZUL =
            new Color(37, 99, 235);

    private static final Color AZUL_CLARO =
            new Color(239, 246, 255);

    private static final Color VERDE =
            new Color(22, 163, 74);

    private static final Color VERDE_CLARO =
            new Color(240, 253, 244);

    private static final Color LARANJA =
            new Color(234, 88, 12);

    private static final Color LARANJA_CLARO =
            new Color(255, 247, 237);

    private static final Color BORDA =
            new Color(226, 232, 240);


    // =====================================================
    // MESES
    // =====================================================

    private static final String[] MESES = {
            "Janeiro",
            "Fevereiro",
            "Março",
            "Abril",
            "Maio",
            "Junho",
            "Julho",
            "Agosto",
            "Setembro",
            "Outubro",
            "Novembro",
            "Dezembro"
    };


    // =====================================================
    // FORMATAÇÃO
    // =====================================================

    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");


    // =====================================================
    // SERVICES
    // =====================================================

    private final RelatorioDesempenhoService desempenhoService =
            new RelatorioDesempenhoService();

    private final RelatorioFrequenciaService frequenciaService =
            new RelatorioFrequenciaService();

    private final PresencaImportService presencaService =
            new PresencaImportService();


    // =====================================================
    // CARD LAYOUT
    // =====================================================

    private final CardLayout cards =
            new CardLayout();

    private final JPanel conteudo =
            new JPanel(cards);


    // =====================================================
    // COMPONENTES DINÂMICOS
    // =====================================================

    private JPanel cardsResumo;

    private JPanel cardsEvento;

    private JPanel grafico;

    private JPanel statusAlunos;

    private JLabel tituloEvento;

    private EventoOpcao eventoAtual;



    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public ResumoRelatoriosPage() {

        setLayout(new BorderLayout());
        setBackground(FUNDO);

        conteudo.setBackground(ThemeManager.getFundo());

        conteudo.add(
                criarLista(),
                "lista"
        );

        conteudo.add(
                criarDetalhe(),
                "detalhe"
        );

        conteudo.add(
                new ImportarPresencaCsvPage(),
                "importarPresenca"
        );

        add(
                conteudo,
                BorderLayout.CENTER
        );

        cards.show(
                conteudo,
                "lista"
        );
    }


    // =====================================================
    // LISTA PRINCIPAL
    // =====================================================

    private JPanel criarLista() {

        JPanel painel =
                new JPanel(new BorderLayout(0, 24));

        painel.setBackground(ThemeManager.getFundo());

        painel.setBorder(
                new EmptyBorder(
                        28,
                        30,
                        30,
                        30
                )
        );


        // -------------------------------------------------
        // CABEÇALHO
        // -------------------------------------------------

        painel.add(
                criarCabecalhoPrincipal(),
                BorderLayout.NORTH
        );


        // -------------------------------------------------
        // CONTEÚDO
        // -------------------------------------------------

        JPanel centro =
                new JPanel();

        centro.setLayout(
                new BoxLayout(
                        centro,
                        BoxLayout.Y_AXIS
                )
        );

        centro.setBackground(ThemeManager.getFundo());


        // -------------------------------------------------
        // CARDS
        // -------------------------------------------------

        cardsResumo =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                18,
                                0
                        )
                );

        cardsResumo.setBackground(ThemeManager.getFundo());

        atualizarResumo();

        centro.add(cardsResumo);

        centro.add(
                Box.createVerticalStrut(24)
        );


        // -------------------------------------------------
        // SEÇÃO DE RELATÓRIOS
        // -------------------------------------------------

        centro.add(
                criarSecaoRelatorios()
        );


        painel.add(
                centro,
                BorderLayout.CENTER
        );

        return painel;
    }


    // =====================================================
    // CABEÇALHO PRINCIPAL
    // =====================================================

    private JPanel criarCabecalhoPrincipal() {

        JPanel painel =
                new JPanel(
                        new BorderLayout()
                );

        painel.setBackground(ThemeManager.getFundo());


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
                new JLabel(
                        "Relatórios"
                );

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
                        "Acompanhe as informações de frequência, inscrições e desempenho dos eventos."
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
                Box.createVerticalStrut(5)
        );

        textos.add(subtitulo);


        painel.add(
                textos,
                BorderLayout.WEST
        );


        JLabel periodo =
                new JLabel(
                        "VISÃO GERAL"
                );

        periodo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        periodo.setForeground(AZUL);

        periodo.setOpaque(true);

        periodo.setBackground(
                AZUL_CLARO
        );

        periodo.setBorder(
                new EmptyBorder(
                        8,
                        12,
                        8,
                        12
                )
        );


        painel.add(
                periodo,
                BorderLayout.EAST
        );

        return painel;
    }


    // =====================================================
    // RESUMO
    // =====================================================

    private void atualizarResumo() {

        ResumoGeral resumo =
                desempenhoService.buscarResumoGeral();


        cardsResumo.removeAll();


        cardsResumo.add(
                criarCardResumo(
                        "Total de Eventos",
                        String.valueOf(
                                resumo.getTotalEventos()
                        ),
                        "eventos cadastrados",
                        AZUL,
                        AZUL_CLARO
                )
        );


        cardsResumo.add(
                criarCardResumo(
                        "Total de Inscrições",
                        String.valueOf(
                                resumo.getTotalInscricoes()
                        ),
                        "inscrições ativas",
                        VERDE,
                        VERDE_CLARO
                )
        );


        cardsResumo.add(
                criarCardResumo(
                        "Taxa de Comparecimento",
                        String.format(
                                "%.1f%%",
                                resumo.getTaxaComparecimento()
                        ),
                        "presença registrada",
                        LARANJA,
                        LARANJA_CLARO
                )
        );


        cardsResumo.revalidate();
        cardsResumo.repaint();
        SwingUtilities.invokeLater(() -> ThemeManager.aplicarTema(ResumoRelatoriosPage.this));
    }


    // =====================================================
    // CARD DE RESUMO
    // =====================================================

    private JPanel criarCardResumo(
            String titulo,
            String valor,
            String descricao,
            Color cor,
            Color fundoCor
    ) {

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setBackground(ThemeManager.getPainel());

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDA
                        ),
                        new EmptyBorder(
                                18,
                                20,
                                18,
                                20
                        )
                )
        );


        JPanel esquerda =
                new JPanel();

        esquerda.setLayout(
                new BoxLayout(
                        esquerda,
                        BoxLayout.Y_AXIS
                )
        );

        esquerda.setBackground(ThemeManager.getPainel());


        JLabel tituloLabel =
                new JLabel(titulo);

        tituloLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        tituloLabel.setForeground(ThemeManager.getTextoSecundario());


        JLabel numero =
                new JLabel(valor);

        numero.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        29
                )
        );

        numero.setForeground(ThemeManager.getTexto());


        JLabel desc =
                new JLabel(descricao);

        desc.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        desc.setForeground(ThemeManager.getTextoSecundario());


        esquerda.add(tituloLabel);

        esquerda.add(
                Box.createVerticalStrut(6)
        );

        esquerda.add(numero);

        esquerda.add(
                Box.createVerticalStrut(2)
        );

        esquerda.add(desc);


        JPanel indicador =
                new JPanel(
                        new GridBagLayout()
                );

        indicador.setPreferredSize(
                new Dimension(
                        45,
                        45
                )
        );

        indicador.setMaximumSize(
                new Dimension(
                        45,
                        45
                )
        );

        indicador.setBackground(
                fundoCor
        );

        JLabel ponto =
                new JLabel("●");

        ponto.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        ponto.setForeground(cor);

        indicador.add(ponto);


        card.add(
                esquerda,
                BorderLayout.CENTER
        );

        card.add(
                indicador,
                BorderLayout.EAST
        );


        return card;
    }


    // =====================================================
    // SEÇÃO DE RELATÓRIOS
    // =====================================================

    private JPanel criarSecaoRelatorios() {

        JPanel caixa =
                new JPanel();

        caixa.setLayout(
                new BoxLayout(
                        caixa,
                        BoxLayout.Y_AXIS
                )
        );

        caixa.setBackground(ThemeManager.getPainel());

        caixa.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDA
                        ),
                        new EmptyBorder(
                                0,
                                0,
                                5,
                                0
                        )
                )
        );


        // -------------------------------------------------
        // CABEÇALHO DA SEÇÃO
        // -------------------------------------------------

        JPanel cabecalho =
                new JPanel(
                        new BorderLayout()
                );

        cabecalho.setBackground(ThemeManager.getPainel());

        cabecalho.setBorder(
                new EmptyBorder(
                        18,
                        20,
                        18,
                        20
                )
        );


        JPanel textos =
                new JPanel();

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        textos.setBackground(ThemeManager.getPainel());


        JLabel titulo =
                new JLabel(
                        "Relatórios disponíveis"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );

        titulo.setForeground(ThemeManager.getTexto());


        JLabel subtitulo =
                new JLabel(
                        "Escolha uma das opções abaixo."
                );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        subtitulo.setForeground(ThemeManager.getTextoSecundario());


        textos.add(titulo);

        textos.add(
                Box.createVerticalStrut(3)
        );

        textos.add(subtitulo);


        cabecalho.add(
                textos,
                BorderLayout.WEST
        );


        caixa.add(cabecalho);

        caixa.add(separador());


        caixa.add(
                criarLinhaRelatorio(
                        "Importar Presenças",
                        "Importe as presenças através de um arquivo CSV.",
                        "Abrir importador",
                        AZUL,
                        this::abrirImportadorPresenca
                )
        );

        caixa.add(separador());

        // -------------------------------------------------
        // INSCRIÇÕES
        // -------------------------------------------------

        caixa.add(
                criarLinhaRelatorio(
                        "Inscrições por Evento",
                        "Lista dos alunos inscritos em um evento.",
                        "Exportar PDF",
                        VERDE,
                        this::exportarInscricoes
                )
        );

        caixa.add(separador());


        // -------------------------------------------------
        // DESEMPENHO
        // -------------------------------------------------

        caixa.add(
                linhaDesempenho()
        );


        return caixa;
    }


    // =====================================================
    // LINHA DE RELATÓRIO
    // =====================================================

    private JPanel criarLinhaRelatorio(
            String titulo,
            String descricao,
            String textoBotao,
            Color cor,
            Runnable acao
    ) {

        JPanel painel =
                new JPanel(
                        new BorderLayout(
                                20,
                                0
                        )
                );

        painel.setBackground(ThemeManager.getPainel());

        painel.setBorder(
                new EmptyBorder(
                        18,
                        20,
                        18,
                        20
                )
        );


        JPanel textos =
                new JPanel();

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        textos.setBackground(ThemeManager.getPainel());


        JLabel nome =
                new JLabel(titulo);

        nome.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        nome.setForeground(ThemeManager.getTexto());


        JLabel desc =
                new JLabel(descricao);

        desc.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        desc.setForeground(ThemeManager.getTextoSecundario());


        textos.add(nome);

        textos.add(
                Box.createVerticalStrut(4)
        );

        textos.add(desc);


        JButton botao =
                criarBotaoAcao(
                        textoBotao,
                        cor
                );

        botao.addActionListener(
                e -> acao.run()
        );


        painel.add(
                textos,
                BorderLayout.CENTER
        );

        painel.add(
                botao,
                BorderLayout.EAST
        );


        return painel;
    }


    // =====================================================
    // DESEMPENHO POR EVENTO
    // =====================================================

    private JPanel linhaDesempenho() {

        JPanel painel =
                new JPanel(
                        new BorderLayout(
                                20,
                                0
                        )
                );

        painel.setBackground(ThemeManager.getPainel());

        painel.setBorder(
                new EmptyBorder(
                        18,
                        20,
                        18,
                        20
                )
        );


        JPanel textos =
                new JPanel();

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        textos.setBackground(ThemeManager.getPainel());


        JLabel nome =
                new JLabel(
                        "Desempenho por Evento"
                );

        nome.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        nome.setForeground(ThemeManager.getTexto());


        JLabel descricao =
                new JLabel(
                        "Visualize presença, ausência e situação dos alunos."
                );

        descricao.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        descricao.setForeground(ThemeManager.getTextoSecundario());


        textos.add(nome);

        textos.add(
                Box.createVerticalStrut(4)
        );

        textos.add(descricao);


        JPanel direita =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        direita.setBackground(ThemeManager.getPainel());


        JComboBox<EventoOpcao> eventos =
                new JComboBox<>();

        eventos.setPreferredSize(
                new Dimension(
                        250,
                        36
                )
        );

        eventos.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );


        for (
                EventoOpcao evento :
                presencaService.listarEventos()
        ) {

            eventos.addItem(evento);
        }


        JButton ver =
                criarBotaoAcao(
                        "Ver desempenho",
                        AZUL
                );


        ver.addActionListener(e -> {

            EventoOpcao evento =
                    (EventoOpcao)
                            eventos.getSelectedItem();


            if (evento == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Nenhum evento cadastrado.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }


            abrirEvento(evento);
        });


        direita.add(eventos);
        direita.add(ver);


        painel.add(
                textos,
                BorderLayout.CENTER
        );

        painel.add(
                direita,
                BorderLayout.EAST
        );


        return painel;
    }


    // =====================================================
    // EXPORTAR PRESENÇAS
    // =====================================================

    private void exportarPresencas() {

        LocalDate hoje =
                LocalDate.now();


        JComboBox<String> mes =
                new JComboBox<>(MESES);

        mes.setSelectedIndex(
                hoje.getMonthValue() - 1
        );


        JSpinner ano =
                new JSpinner(
                        new SpinnerNumberModel(
                                hoje.getYear(),
                                2000,
                                2100,
                                1
                        )
                );


        ano.setEditor(
                new JSpinner.NumberEditor(
                        ano,
                        "#"
                )
        );


        JPanel painel =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                10,
                                10
                        )
                );

        painel.add(
                new JLabel("Mês:")
        );

        painel.add(mes);

        painel.add(
                new JLabel("Ano:")
        );

        painel.add(ano);


        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        painel,
                        "Relatório de Presenças",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );


        if (
                resposta !=
                        JOptionPane.OK_OPTION
        ) {

            return;
        }


        int mesSelecionado =
                mes.getSelectedIndex() + 1;

        int anoSelecionado =
                (int) ano.getValue();


        List<?> dados =
                frequenciaService
                        .gerarRelatorioPorCursoSerie(
                                anoSelecionado,
                                mesSelecionado
                        );


        if (dados.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Não existem registros de presença para "
                            + MESES[
                            mesSelecionado - 1
                            ]
                            + "/"
                            + anoSelecionado
                            + ".",
                    "Sem dados",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        JFileChooser chooser =
                new JFileChooser();

        chooser.setDialogTitle(
                "Salvar relatório de presenças"
        );


        chooser.setSelectedFile(
                new File(
                        "relatorio_presencas_"
                                + MESES[
                                mesSelecionado - 1
                                ]
                                .toLowerCase()
                                + "_"
                                + anoSelecionado
                                + ".pdf"
                )
        );


        if (
                chooser.showSaveDialog(this)
                        != JFileChooser.APPROVE_OPTION
        ) {

            return;
        }


        File arquivo =
                garantirPdf(
                        chooser.getSelectedFile()
                );


        try {

            frequenciaService
                    .exportarRelatorioMensal(
                            arquivo,
                            "Escola",
                            anoSelecionado,
                            mesSelecionado
                    );


            sucesso(arquivo);

        } catch (IOException ex) {

            erro(ex);
        }
    }


    // =====================================================
    // EXPORTAR INSCRIÇÕES
    // =====================================================

    private void exportarInscricoes() {

        List<EventoOpcao> eventos =
                presencaService.listarEventos();


        if (eventos.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Não existem eventos cadastrados.",
                    "Sem eventos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        JComboBox<EventoOpcao> combo =
                new JComboBox<>();


        for (
                EventoOpcao evento :
                eventos
        ) {

            combo.addItem(evento);
        }


        combo.setPreferredSize(
                new Dimension(
                        350,
                        36
                )
        );


        JPanel painel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );


        painel.add(
                new JLabel(
                        "Selecione o evento:"
                ),
                BorderLayout.NORTH
        );


        painel.add(
                combo,
                BorderLayout.CENTER
        );


        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        painel,
                        "Exportar Inscrições",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );


        if (
                resposta !=
                        JOptionPane.OK_OPTION
        ) {

            return;
        }


        EventoOpcao evento =
                (EventoOpcao)
                        combo.getSelectedItem();


        if (evento == null) {
            return;
        }


        JFileChooser chooser =
                new JFileChooser();


        chooser.setDialogTitle(
                "Salvar relatório de inscrições"
        );


        String nomeArquivo =
                "inscricoes_"
                        + limparNomeArquivo(
                        evento.toString()
                )
                        + ".pdf";


        chooser.setSelectedFile(
                new File(nomeArquivo)
        );


        if (
                chooser.showSaveDialog(this)
                        != JFileChooser.APPROVE_OPTION
        ) {

            return;
        }


        File arquivo =
                garantirPdf(
                        chooser.getSelectedFile()
                );


        try {

            desempenhoService
                    .exportarInscricoesEvento(
                            arquivo,
                            evento
                    );


            sucesso(arquivo);

        } catch (IOException ex) {

            erro(ex);
        }
    }

// =====================================================
// ABRIR IMPORTADOR DE PRESENÇA
// =====================================================

    private void abrirImportadorPresenca() {

        cards.show(
                conteudo,
                "importarPresenca"
        );
    }
// =====================================================
// MOSTRAR LISTA DE RELATÓRIOS
// =====================================================

    public void mostrarListaRelatorios() {

        atualizarResumo();

        cards.show(
                conteudo,
                "lista"
        );
    }

    // =====================================================
    // DETALHE DO EVENTO
    // =====================================================

    private void abrirEvento(
            EventoOpcao evento
    ) {
        eventoAtual = evento;

        EventoDesempenho desempenho =
                desempenhoService
                        .buscarDesempenhoEvento(
                                evento.getId()
                        );


        tituloEvento.setText(
                "Desempenho: "
                        + evento
        );


        cardsEvento.removeAll();


        cardsEvento.add(
                criarCardResumo(
                        "Total de Inscritos",
                        String.valueOf(
                                desempenho
                                        .getTotalInscritos()
                        ),
                        "inscrições ativas",
                        AZUL,
                        AZUL_CLARO
                )
        );


        cardsEvento.add(
                criarCardResumo(
                        "Presenças Confirmadas",
                        String.valueOf(
                                desempenho
                                        .getPresencasConfirmadas()
                        ),
                        "alunos com presença",
                        VERDE,
                        VERDE_CLARO
                )
        );


        cardsEvento.add(
                criarCardResumo(
                        "Ausentes",
                        String.valueOf(
                                desempenho
                                        .getAusentes()
                        ),
                        "sem presença confirmada",
                        LARANJA,
                        LARANJA_CLARO
                )
        );


        atualizarGrafico(evento);

        atualizarAlunos(evento);


        cardsEvento.revalidate();
        cardsEvento.repaint();
        SwingUtilities.invokeLater(() -> ThemeManager.aplicarTema(ResumoRelatoriosPage.this));


        cards.show(
                conteudo,
                "detalhe"
        );
    }


    // =====================================================
    // PÁGINA DE DETALHE
    // =====================================================

    private JPanel criarDetalhe() {

        JPanel painel =
                new JPanel(
                        new BorderLayout(
                                0,
                                22
                        )
                );

        painel.setBackground(ThemeManager.getFundo());

        painel.setBorder(
                new EmptyBorder(
                        28,
                        30,
                        30,
                        30
                )
        );


        // -------------------------------------------------
        // TOPO
        // -------------------------------------------------

        JPanel topo =
                new JPanel();

        topo.setLayout(
                new BoxLayout(
                        topo,
                        BoxLayout.Y_AXIS
                )
        );

        topo.setBackground(ThemeManager.getFundo());


        JButton voltar =
                new JButton(
                        "← Voltar para relatórios"
                );

        voltar.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        voltar.setForeground(AZUL);

        voltar.setBorderPainted(false);

        voltar.setContentAreaFilled(false);

        voltar.setFocusPainted(false);

        voltar.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );


        voltar.addActionListener(e -> {

            atualizarResumo();

            cards.show(
                    conteudo,
                    "lista"
            );
        });


        tituloEvento =
                new JLabel(
                        "Desempenho"
                );

        tituloEvento.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        25
                )
        );

        tituloEvento.setForeground(ThemeManager.getTexto());


        JButton exportar =
                criarBotaoAcao(
                        "Exportar PDF",
                        AZUL
                );

        exportar.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        exportar.addActionListener(e -> {

            if (
                    tituloEvento
                            .getText()
                            .equals("Desempenho")
            ) {

                return;
            }


            exportarEventoAtual();
        });


        topo.add(voltar);

        topo.add(
                Box.createVerticalStrut(8)
        );

        topo.add(tituloEvento);

        topo.add(
                Box.createVerticalStrut(12)
        );

        topo.add(exportar);


        // -------------------------------------------------
        // CENTRO
        // -------------------------------------------------

        JPanel centro =
                new JPanel();

        centro.setLayout(
                new BoxLayout(
                        centro,
                        BoxLayout.Y_AXIS
                )
        );

        centro.setBackground(ThemeManager.getFundo());


        cardsEvento =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                18,
                                0
                        )
                );

        cardsEvento.setBackground(ThemeManager.getFundo());


        grafico =
                caixa(
                        "Gráfico de Comparecimento"
                );


        grafico.setPreferredSize(
                new Dimension(
                        0,
                        270
                )
        );


        statusAlunos =
                caixa(
                        "Status dos Alunos"
                );


        centro.add(cardsEvento);

        centro.add(
                Box.createVerticalStrut(20)
        );

        centro.add(grafico);

        centro.add(
                Box.createVerticalStrut(20)
        );

        centro.add(statusAlunos);


        painel.add(
                topo,
                BorderLayout.NORTH
        );

        painel.add(
                new JScrollPane(
                        centro
                ),
                BorderLayout.CENTER
        );


        return painel;
    }


    // =====================================================
    // CAIXA
    // =====================================================

    private JPanel caixa(
            String titulo
    ) {

        JPanel painel =
                new JPanel(
                        new BorderLayout()
                );

        painel.setBackground(ThemeManager.getPainel());

        painel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDA
                        ),
                        new EmptyBorder(
                                18,
                                20,
                                18,
                                20
                        )
                )
        );


        JLabel label =
                new JLabel(titulo);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        label.setForeground(ThemeManager.getTexto());


        painel.add(
                label,
                BorderLayout.NORTH
        );


        return painel;
    }


    // =====================================================
    // GRÁFICO
    // =====================================================

    private void atualizarGrafico(
            EventoOpcao evento
    ) {

        List<PontoComparecimento> pontos =
                desempenhoService
                        .buscarComparecimentoPorData(
                                evento.getId()
                        );


        int[] valores =
                new int[pontos.size()];

        String[] datas =
                new String[pontos.size()];


        for (
                int i = 0;
                i < pontos.size();
                i++
        ) {

            valores[i] =
                    pontos.get(i).getTotal();


            datas[i] =
                    pontos.get(i)
                            .getData()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "dd/MM"
                                    )
                            );
        }


        grafico.removeAll();


        grafico.add(
                new GraficoBarras(
                        valores,
                        datas
                ),
                BorderLayout.CENTER
        );


        grafico.revalidate();

        grafico.repaint();
        SwingUtilities.invokeLater(() -> ThemeManager.aplicarTema(ResumoRelatoriosPage.this));
    }


    // =====================================================
    // ALUNOS
    // =====================================================

    private void atualizarAlunos(
            EventoOpcao evento
    ) {

        List<AlunoStatus> alunos =
                desempenhoService
                        .listarStatusAlunos(
                                evento.getId()
                        );


        statusAlunos.removeAll();


        JPanel lista =
                new JPanel();


        lista.setLayout(
                new BoxLayout(
                        lista,
                        BoxLayout.Y_AXIS
                )
        );

        lista.setBackground(ThemeManager.getPainel());


        for (
                AlunoStatus aluno :
                alunos
        ) {

            JPanel linha =
                    new JPanel(
                            new BorderLayout()
                    );

            linha.setBackground(ThemeManager.getPainel());

            linha.setBorder(
                    new EmptyBorder(
                            7,
                            0,
                            7,
                            0
                    )
            );


            JLabel nome =
                    new JLabel(
                            aluno.getNome()
                    );

            nome.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            13
                    )
            );

            nome.setForeground(ThemeManager.getTexto());


            JLabel status =
                    new JLabel(
                            aluno.getStatus()
                    );

            status.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            12
                    )
            );


            if (
                    "Confirmado".equals(
                            aluno.getStatus()
                    )
            ) {

                status.setForeground(VERDE);

            } else if (
                    "Ausente".equals(
                            aluno.getStatus()
                    )
            ) {

                status.setForeground(
                        new Color(
                                220,
                                38,
                                38
                        )
                );

            } else {

                status.setForeground(
                        LARANJA
                );
            }


            linha.add(
                    nome,
                    BorderLayout.CENTER
            );

            linha.add(
                    status,
                    BorderLayout.EAST
            );


            lista.add(linha);
        }


        if (alunos.isEmpty()) {

            lista.add(
                    new JLabel(
                            "Nenhum aluno inscrito neste evento."
                    )
            );
        }


        JScrollPane scroll =
                new JScrollPane(lista);

        scroll.setBorder(null);


        statusAlunos.add(
                scroll,
                BorderLayout.CENTER
        );


        statusAlunos.revalidate();

        statusAlunos.repaint();
        SwingUtilities.invokeLater(() -> ThemeManager.aplicarTema(ResumoRelatoriosPage.this));
    }


    // =====================================================
// EXPORTAR DESEMPENHO DO EVENTO
// =====================================================

    private void exportarEventoAtual() {

        if (eventoAtual == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Nenhum evento está selecionado.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        JFileChooser chooser =
                new JFileChooser();

        chooser.setDialogTitle(
                "Salvar relatório de desempenho"
        );

        String nomeArquivo =
                "desempenho_"
                        + limparNomeArquivo(
                        eventoAtual.toString()
                )
                        + ".pdf";

        chooser.setSelectedFile(
                new File(nomeArquivo)
        );

        if (
                chooser.showSaveDialog(this)
                        != JFileChooser.APPROVE_OPTION
        ) {

            return;
        }

        File arquivo =
                garantirPdf(
                        chooser.getSelectedFile()
                );

        try {

            desempenhoService
                    .exportarDesempenhoEvento(
                            arquivo,
                            eventoAtual
                    );

            sucesso(arquivo);

        } catch (IOException ex) {

            erro(ex);
        }
    }



    // =====================================================
    // BOTÃO
    // =====================================================

    private JButton criarBotaoAcao(
            String texto,
            Color cor
    ) {

        JButton botao =
                new JButton(texto);

        botao.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        botao.setForeground(cor);

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
                                cor
                        ),
                        new EmptyBorder(
                                8,
                                14,
                                8,
                                14
                        )
                )
        );


        return botao;
    }


    // =====================================================
    // SEPARADOR
    // =====================================================

    private JPanel separador() {

        JPanel painel =
                new JPanel();

        painel.setBackground(BORDA);

        painel.setPreferredSize(
                new Dimension(
                        1,
                        1
                )
        );

        painel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        1
                )
        );

        return painel;
    }


    // =====================================================
    // GARANTIR PDF
    // =====================================================

    private File garantirPdf(
            File arquivo
    ) {

        if (
                arquivo.getName()
                        .toLowerCase()
                        .endsWith(".pdf")
        ) {

            return arquivo;
        }


        return new File(
                arquivo.getParentFile(),
                arquivo.getName()
                        + ".pdf"
        );
    }


    // =====================================================
    // LIMPAR NOME DO ARQUIVO
    // =====================================================

    private String limparNomeArquivo(
            String nome
    ) {

        if (nome == null || nome.isBlank()) {
            return "evento";
        }


        return nome
                .replaceAll(
                        "[\\\\/:*?\"<>|]",
                        "_"
                )
                .replaceAll(
                        "\\s+",
                        "_"
                );
    }


    // =====================================================
    // SUCESSO
    // =====================================================

    private void sucesso(
            File arquivo
    ) {

        JOptionPane.showMessageDialog(
                this,
                "PDF gerado com sucesso!\n\n"
                        + arquivo.getAbsolutePath(),
                "Exportação concluída",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    // =====================================================
    // ERRO
    // =====================================================

    private void erro(
            Exception ex
    ) {

        JOptionPane.showMessageDialog(
                this,
                "Não foi possível gerar o PDF:\n"
                        + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }


    // =====================================================
    // GRÁFICO DE BARRAS
    // =====================================================

    private static class GraficoBarras
            extends JPanel {

        private final int[] valores;

        private final String[] rotulos;


        GraficoBarras(
                int[] valores,
                String[] rotulos
        ) {

            this.valores = valores;

            this.rotulos = rotulos;

            setBackground(ThemeManager.getPainel());
        }


        @Override
        protected void paintComponent(
                Graphics g
        ) {

            super.paintComponent(g);


            if (
                    valores == null
                            || valores.length == 0
            ) {

                Graphics2D vazio =
                        (Graphics2D) g.create();

                vazio.setColor(ThemeManager.getTextoSecundario());

                vazio.setFont(
                        new Font(
                                "Segoe UI",
                                Font.PLAIN,
                                13
                        )
                );

                vazio.drawString(
                        "Nenhum registro de presença encontrado.",
                        20,
                        35
                );

                vazio.dispose();

                return;
            }


            Graphics2D g2 =
                    (Graphics2D) g.create();


            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            int maior = 1;


            for (
                    int valor :
                    valores
            ) {

                maior =
                        Math.max(
                                maior,
                                valor
                        );
            }


            int espacamento = 14;

            int margemEsquerda = 25;

            int margemDireita = 25;

            int margemInferior = 30;

            int margemSuperior = 20;


            int larguraDisponivel =
                    getWidth()
                            - margemEsquerda
                            - margemDireita
                            - espacamento
                            * (
                            valores.length - 1
                    );


            int larguraBarra =
                    Math.max(
                            10,
                            larguraDisponivel
                                    / valores.length
                    );


            int alturaDisponivel =
                    getHeight()
                            - margemSuperior
                            - margemInferior;


            int x =
                    margemEsquerda;


            for (
                    int i = 0;
                    i < valores.length;
                    i++
            ) {

                int altura =
                        (int) (
                                (
                                        (double)
                                                valores[i]
                                                / maior
                                )
                                        * (
                                        alturaDisponivel
                                )
                        );


                int y =
                        getHeight()
                                - margemInferior
                                - altura;


                g2.setColor(AZUL);


                g2.fillRoundRect(
                        x,
                        y,
                        larguraBarra,
                        altura,
                        8,
                        8
                );


                // Valor
                g2.setColor(ThemeManager.getTexto());

                g2.setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                10
                        )
                );


                String valor =
                        String.valueOf(
                                valores[i]
                        );


                FontMetrics fm =
                        g2.getFontMetrics();


                int larguraTexto =
                        fm.stringWidth(
                                valor
                        );


                g2.drawString(
                        valor,
                        x
                                + (
                                larguraBarra
                                        - larguraTexto
                        ) / 2,
                        Math.max(
                                12,
                                y - 5
                        )
                );


                // Data
                g2.setColor(ThemeManager.getTextoSecundario());

                g2.setFont(
                        new Font(
                                "Segoe UI",
                                Font.PLAIN,
                                10
                        )
                );


                g2.drawString(
                        rotulos[i],
                        x,
                        getHeight()
                                - 8
                );


                x +=
                        larguraBarra
                                + espacamento;
            }


            g2.dispose();
        }
    }
}
