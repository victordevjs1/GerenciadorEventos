package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.model.Aluno;
import br.com.gerenciadoreventos.model.AtividadeOpcao;
import br.com.gerenciadoreventos.model.EventoOpcao;
import br.com.gerenciadoreventos.service.PresencaImportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ImportarPresencaCsvPage extends JPanel {

    // =====================================================
    // CORES
    // =====================================================

    private static final Color FUNDO =
            new Color(246, 248, 252);

    private static final Color AZUL =
            new Color(37, 99, 235);

    private static final Color AZUL_CLARO =
            new Color(239, 246, 255);

    private static final Color TEXTO =
            new Color(15, 23, 42);

    private static final Color CINZA_TEXTO =
            new Color(100, 116, 139);

    private static final Color BORDA =
            new Color(226, 232, 240);

    private static final Color VERDE =
            new Color(22, 163, 74);

    private static final Color VERMELHO =
            new Color(220, 38, 38);

    // =====================================================
    // COMPONENTES
    // =====================================================

    private JToggleButton toggleModoEvento;

    private JToggleButton toggleModoAtividade;

    private boolean modoAtividade = false;

    private JLabel labelContexto;

    private JComboBox<Object> comboContexto;

    private JPanel painelData;

    private JTextField campoData;

    private JLabel labelArquivo;

    private DefaultTableModel modeloTabela;

    private JTable tabela;

    private JButton botaoImportar;

    private JLabel labelResumo;

    /*
     * [0] = RM
     * [1] = Nome vindo do CSV
     * [2] = Status (PRESENTE/AUSENTE), opcional
     */
    private final List<String[]> linhasCsv =
            new ArrayList<>();

    private final PresencaImportService presencaService =
            new PresencaImportService();

    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public ImportarPresencaCsvPage() {

        setLayout(new BorderLayout());

        setBackground(FUNDO);

        criarInterface();

        carregarContexto();
    }

    // =====================================================
    // INTERFACE
    // =====================================================

    private void criarInterface() {

        JPanel principal =
                new JPanel(
                        new BorderLayout(0, 15)
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

        principal.add(
                criarCabecalho(),
                BorderLayout.NORTH
        );

        principal.add(
                criarPreview(),
                BorderLayout.CENTER
        );

        principal.add(
                criarRodape(),
                BorderLayout.SOUTH
        );

        add(
                principal,
                BorderLayout.CENTER
        );
    }

    // =====================================================
    // CABEÇALHO
    // =====================================================

    private JPanel criarCabecalho() {

        JPanel painel =
                new JPanel();

        painel.setLayout(
                new BoxLayout(
                        painel,
                        BoxLayout.Y_AXIS
                )
        );

        painel.setBackground(FUNDO);
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

        voltar.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        voltar.addActionListener(
                e -> voltarParaRelatorios()
        );

        painel.add(
                voltar
        );

        painel.add(
                Box.createVerticalStrut(8)
        );

        JLabel titulo =
                new JLabel("Importar Presença");

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        25
                )
        );

        titulo.setForeground(TEXTO);

        titulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel subtitulo =
                new JLabel(
                        "Importe as respostas do Google Forms ou Google Sheets através de um arquivo CSV."
                );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        subtitulo.setForeground(
                CINZA_TEXTO
        );

        subtitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        painel.add(titulo);

        painel.add(
                Box.createVerticalStrut(4)
        );

        painel.add(subtitulo);

        painel.add(
                Box.createVerticalStrut(18)
        );

        JPanel configuracao =
                new JPanel();

        configuracao.setLayout(
                new BoxLayout(
                        configuracao,
                        BoxLayout.Y_AXIS
                )
        );

        configuracao.setBackground(
                Color.WHITE
        );

        configuracao.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDA
                        ),
                        new EmptyBorder(
                                16,
                                18,
                                16,
                                18
                        )
                )
        );

        configuracao.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        // =================================================
        // TIPO DE IMPORTAÇÃO
        // =================================================

        JLabel labelModo =
                new JLabel(
                        "Tipo de importação"
                );

        labelModo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        labelModo.setForeground(TEXTO);

        configuracao.add(labelModo);

        configuracao.add(
                Box.createVerticalStrut(7)
        );

        JPanel linhaModo =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        linhaModo.setBackground(
                Color.WHITE
        );

        toggleModoEvento =
                criarToggleModo(
                        "Presença em Evento"
                );

        toggleModoAtividade =
                criarToggleModo(
                        "Participação em Atividade"
                );

        ButtonGroup grupo =
                new ButtonGroup();

        grupo.add(toggleModoEvento);
        grupo.add(toggleModoAtividade);

        toggleModoEvento.setSelected(true);

        aplicarEstiloToggle(
                toggleModoEvento,
                true
        );

        aplicarEstiloToggle(
                toggleModoAtividade,
                false
        );

        toggleModoEvento.addActionListener(e -> {

            modoAtividade = false;

            aplicarEstiloToggle(
                    toggleModoEvento,
                    true
            );

            aplicarEstiloToggle(
                    toggleModoAtividade,
                    false
            );

            alternarModo();
        });

        toggleModoAtividade.addActionListener(e -> {

            modoAtividade = true;

            aplicarEstiloToggle(
                    toggleModoEvento,
                    false
            );

            aplicarEstiloToggle(
                    toggleModoAtividade,
                    true
            );

            alternarModo();
        });

        linhaModo.add(
                toggleModoEvento
        );

        linhaModo.add(
                Box.createHorizontalStrut(8)
        );

        linhaModo.add(
                toggleModoAtividade
        );

        configuracao.add(linhaModo);

        configuracao.add(
                Box.createVerticalStrut(15)
        );

        // =================================================
        // CONTEXTO
        // =================================================

        JPanel linhaContexto =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                15,
                                0
                        )
                );

        linhaContexto.setBackground(
                Color.WHITE
        );

        JPanel blocoContexto =
                criarBlocoContexto();

        linhaContexto.add(
                blocoContexto
        );

        painelData =
                criarBlocoData();

        linhaContexto.add(
                painelData
        );

        configuracao.add(
                linhaContexto
        );

        configuracao.add(
                Box.createVerticalStrut(15)
        );

        // =================================================
        // ARQUIVO
        // =================================================

        JPanel linhaArquivo =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        linhaArquivo.setBackground(
                Color.WHITE
        );

        JButton escolher =
                criarBotaoPrincipal(
                        "Escolher arquivo CSV"
                );

        escolher.addActionListener(
                e -> escolherArquivo()
        );

        labelArquivo =
                new JLabel(
                        "Nenhum arquivo selecionado."
                );

        labelArquivo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        labelArquivo.setForeground(
                CINZA_TEXTO
        );

        linhaArquivo.add(escolher);

        linhaArquivo.add(labelArquivo);

        configuracao.add(linhaArquivo);

        painel.add(configuracao);

        return painel;
    }

    // =====================================================
    // BLOCO CONTEXTO
    // =====================================================

    private JPanel criarBlocoContexto() {

        JPanel painel =
                new JPanel();

        painel.setLayout(
                new BoxLayout(
                        painel,
                        BoxLayout.Y_AXIS
                )
        );

        painel.setBackground(
                Color.WHITE
        );

        labelContexto =
                new JLabel("Evento");

        labelContexto.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        labelContexto.setForeground(TEXTO);

        comboContexto =
                new JComboBox<>();

        comboContexto.setPreferredSize(
                new Dimension(
                        360,
                        38
                )
        );

        comboContexto.setMaximumSize(
                new Dimension(
                        360,
                        38
                )
        );

        comboContexto.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        painel.add(labelContexto);

        painel.add(
                Box.createVerticalStrut(5)
        );

        painel.add(comboContexto);

        return painel;
    }

    // =====================================================
    // BLOCO DATA
    // =====================================================

    private JPanel criarBlocoData() {

        JPanel painel =
                new JPanel();

        painel.setLayout(
                new BoxLayout(
                        painel,
                        BoxLayout.Y_AXIS
                )
        );

        painel.setBackground(
                Color.WHITE
        );

        JLabel label =
                new JLabel(
                        "Data da presença (AAAA-MM-DD)"
                );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        label.setForeground(TEXTO);

        campoData =
                new JTextField(
                        LocalDate.now().toString()
                );

        campoData.setPreferredSize(
                new Dimension(
                        180,
                        38
                )
        );

        campoData.setMaximumSize(
                new Dimension(
                        180,
                        38
                )
        );

        campoData.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        campoData.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDA
                        ),
                        new EmptyBorder(
                                7,
                                10,
                                7,
                                10
                        )
                )
        );

        painel.add(label);

        painel.add(
                Box.createVerticalStrut(5)
        );

        painel.add(campoData);

        return painel;
    }

    // =====================================================
    // TOGGLE
    // =====================================================

    private JToggleButton criarToggleModo(
            String texto
    ) {

        JToggleButton toggle =
                new JToggleButton(texto);

        toggle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        toggle.setFocusPainted(false);

        toggle.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        toggle.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDA
                        ),
                        new EmptyBorder(
                                8,
                                14,
                                8,
                                14
                        )
                )
        );

        return toggle;
    }

    private void aplicarEstiloToggle(
            JToggleButton toggle,
            boolean ativo
    ) {

        toggle.setBackground(
                ativo
                        ? AZUL
                        : Color.WHITE
        );

        toggle.setForeground(
                ativo
                        ? Color.WHITE
                        : TEXTO
        );
    }

    // =====================================================
    // ALTERNAR MODO
    // =====================================================

    private void alternarModo() {

        labelContexto.setText(
                modoAtividade
                        ? "Atividade"
                        : "Evento"
        );

        painelData.setVisible(
                !modoAtividade
        );

        carregarContexto();

        linhasCsv.clear();

        modeloTabela.setRowCount(0);

        labelArquivo.setText(
                "Nenhum arquivo selecionado."
        );

        labelResumo.setText(" ");

        botaoImportar.setEnabled(false);

        revalidate();
        repaint();
    }

    // =====================================================
    // PREVIEW
    // =====================================================

    private JScrollPane criarPreview() {

        JPanel caixa =
                new JPanel(
                        new BorderLayout()
                );

        caixa.setBackground(Color.WHITE);

        caixa.setBorder(
                BorderFactory.createLineBorder(
                        BORDA
                )
        );

        modeloTabela =
                new DefaultTableModel(
                        new Object[]{
                                "RM",
                                "Nome (CSV)",
                                "Status",
                                "Situação"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        tabela =
                new JTable(modeloTabela);

        tabela.setRowHeight(32);

        tabela.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        tabela.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                13
                        )
                );

        tabela.getTableHeader()
                .setBackground(
                        new Color(
                                249,
                                250,
                                252
                        )
                );

        tabela.getTableHeader()
                .setForeground(TEXTO);

        tabela.setShowGrid(true);

        tabela.setGridColor(BORDA);

        tabela.setSelectionBackground(
                AZUL_CLARO
        );

        tabela.setSelectionForeground(
                TEXTO
        );

        tabela.getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        new SituacaoRenderer()
                );

        JScrollPane scroll =
                new JScrollPane(tabela);

        scroll.setBorder(null);

        caixa.add(
                scroll,
                BorderLayout.CENTER
        );

        return scroll;
    }

    // =====================================================
    // RODAPÉ
    // =====================================================

    private JPanel criarRodape() {

        JPanel painel =
                new JPanel(
                        new BorderLayout()
                );

        painel.setBackground(FUNDO);

        painel.setBorder(
                new EmptyBorder(
                        5,
                        0,
                        0,
                        0
                )
        );

        labelResumo =
                new JLabel(" ");

        labelResumo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        labelResumo.setForeground(
                CINZA_TEXTO
        );

        botaoImportar =
                criarBotaoPrincipal(
                        "Importar Presenças"
                );

        botaoImportar.setEnabled(false);

        botaoImportar.addActionListener(
                e -> importarPresencas()
        );

        painel.add(
                labelResumo,
                BorderLayout.WEST
        );

        painel.add(
                botaoImportar,
                BorderLayout.EAST
        );

        return painel;
    }

    // =====================================================
    // CARREGAR CONTEXTO
    // =====================================================

    private void carregarContexto() {

        comboContexto.removeAllItems();

        if (modoAtividade) {

            List<AtividadeOpcao> atividades =
                    presencaService.listarAtividades();

            for (
                    AtividadeOpcao atividade
                    : atividades
            ) {

                comboContexto.addItem(
                        atividade
                );
            }

        } else {

            List<EventoOpcao> eventos =
                    presencaService.listarEventos();

            for (
                    EventoOpcao evento
                    : eventos
            ) {

                comboContexto.addItem(
                        evento
                );
            }
        }
    }

    // =====================================================
    // ESCOLHER CSV
    // =====================================================

    private void escolherArquivo() {

        JFileChooser seletor =
                new JFileChooser();

        seletor.setDialogTitle(
                "Selecione o CSV exportado do Google Sheets"
        );

        if (
                seletor.showOpenDialog(this)
                        != JFileChooser.APPROVE_OPTION
        ) {
            return;
        }

        File arquivo =
                seletor.getSelectedFile();

        if (!arquivo.getName()
                .toLowerCase()
                .endsWith(".csv")) {

            mostrarAviso(
                    "Selecione um arquivo com extensão .csv."
            );

            return;
        }

        labelArquivo.setText(
                arquivo.getName()
        );

        lerCsv(arquivo);
    }

    // =====================================================
    // LER CSV
    // =====================================================

    private void lerCsv(
            File arquivo
    ) {

        linhasCsv.clear();

        modeloTabela.setRowCount(0);

        try (
                BufferedReader leitor =
                        new BufferedReader(
                                new FileReader(
                                        arquivo,
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            String cabecalho =
                    leitor.readLine();

            if (cabecalho == null) {

                mostrarErro(
                        "O arquivo está vazio."
                );

                return;
            }

            cabecalho = removerBOM(cabecalho);
            char delimitador = detectarDelimitador(cabecalho);
            String[] colunas = dividirLinhaCsv(cabecalho, delimitador);

            int indiceRm = -1;

            int indiceNome = -1;
            int indiceStatus = -1;

            for (
                    int i = 0;
                    i < colunas.length;
                    i++
            ) {

                String coluna =
                        limparCampo(
                                colunas[i]
                        ).toLowerCase();

                if (
                        coluna.equals("rm")
                                || coluna.equals("r.m.")
                ) {

                    indiceRm = i;
                }

                if (coluna.contains("nome")) {
                    indiceNome = i;
                }

                if (coluna.equals("status") || coluna.contains("presenca") || coluna.contains("presença")) {
                    indiceStatus = i;
                }
            }

            if (indiceRm == -1) {

                mostrarAviso(
                        "Não encontrei uma coluna chamada \"RM\" no CSV.\n\n"
                                + "O cabeçalho precisa conter uma coluna chamada RM."
                );

                return;
            }

            String linha;

            while (
                    (linha = leitor.readLine())
                            != null
            ) {

                if (linha.isBlank()) {
                    continue;
                }

                String[] valores =
                        dividirLinhaCsv(linha, delimitador);

                String rm =
                        indiceRm < valores.length
                                ? limparCampo(
                                valores[indiceRm]
                        )
                                : "";

                String nomeCsv =
                        (indiceNome != -1 && indiceNome < valores.length)
                                ? limparCampo(valores[indiceNome]) : "";

                String statusCsv =
                        (indiceStatus != -1 && indiceStatus < valores.length)
                                ? normalizarStatus(limparCampo(valores[indiceStatus])) : "PRESENTE";

                if (rm.isBlank()) {
                    continue;
                }

                linhasCsv.add(
                        new String[]{
                                rm,
                                nomeCsv,
                                statusCsv
                        }
                );
            }

        } catch (IOException e) {

            mostrarErro(
                    "Não foi possível ler o arquivo:\n"
                            + e.getMessage()
            );

            return;
        }

        validarLinhas();
    }

    // =====================================================
    // REMOVER BOM
    // =====================================================

    private String removerBOM(
            String texto
    ) {

        if (
                texto != null
                        && !texto.isEmpty()
                        && texto.charAt(0) == '\uFEFF'
        ) {

            return texto.substring(1);
        }

        return texto;
    }

    // =====================================================
    // LIMPAR CAMPO
    // =====================================================

    private String limparCampo(
            String valor
    ) {

        if (valor == null) {
            return "";
        }

        valor =
                valor.trim();

        if (
                valor.length() >= 2
                        && valor.startsWith("\"")
                        && valor.endsWith("\"")
        ) {

            valor =
                    valor.substring(
                            1,
                            valor.length() - 1
                    );
        }

        return valor
                .replace(
                        "\"\"",
                        "\""
                )
                .trim();
    }

    // =====================================================
    // DIVIDIR CSV
    // =====================================================

    private String[] dividirLinhaCsv(
            String linha, char delimitador
    ) {

        List<String> campos =
                new ArrayList<>();

        StringBuilder atual =
                new StringBuilder();

        boolean dentroAspas = false;

        for (
                int i = 0;
                i < linha.length();
                i++
        ) {

            char c =
                    linha.charAt(i);

            if (c == '"') {

                if (
                        dentroAspas
                                && i + 1 < linha.length()
                                && linha.charAt(i + 1) == '"'
                ) {

                    atual.append('"');

                    i++;

                } else {

                    dentroAspas =
                            !dentroAspas;
                }

            } else if (
                    c == delimitador
                            && !dentroAspas
            ) {

                campos.add(
                        atual.toString()
                );

                atual.setLength(0);

            } else {

                atual.append(c);
            }
        }

        campos.add(
                atual.toString()
        );

        return campos.toArray(
                new String[0]
        );
    }

    private char detectarDelimitador(String linha) {
        int virgulas = contarDelimitador(linha, ',');
        int pontoEVirgulas = contarDelimitador(linha, ';');
        int tabs = contarDelimitador(linha, '\t');
        if (tabs >= virgulas && tabs >= pontoEVirgulas && tabs > 0) return '\t';
        return pontoEVirgulas > virgulas ? ';' : ',';
    }

    private int contarDelimitador(String linha, char delimitador) {
        int total = 0; boolean aspas = false;
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"') aspas = !aspas;
            else if (c == delimitador && !aspas) total++;
        }
        return total;
    }

    private String normalizarStatus(String status) {
        if (status == null || status.isBlank()) return "PRESENTE";
        String s = status.trim().toUpperCase();
        if (s.equals("AUSENTE") || s.equals("AUSENCIA") || s.equals("AUSÊNCIA") ||
                s.equals("FALTA") || s.equals("FALTANTE") || s.equals("NÃO") || s.equals("NAO") || s.equals("0")) {
            return "AUSENTE";
        }
        if (s.equals("PRESENTE") || s.equals("PRESENÇA") || s.equals("PRESENCA") ||
                s.equals("SIM") || s.equals("1") || s.equals("OK")) {
            return "PRESENTE";
        }
        return "PRESENTE";
    }

    // =====================================================
    // VALIDAR
    // =====================================================

    private void validarLinhas() {

        modeloTabela.setRowCount(0);

        int encontrados = 0;

        int naoEncontrados = 0;

        for (
                String[] linha
                : linhasCsv
        ) {

            String rm =
                    linha[0];

            String nomeCsv =
                    linha[1];

            Aluno aluno =
                    presencaService.buscarAlunoPorRm(
                            rm
                    );

            String situacao;

            if (aluno != null) {

                situacao =
                        "Encontrado: "
                                + aluno.getNome();

                encontrados++;

            } else {

                situacao =
                        "RM não encontrado no cadastro";

                naoEncontrados++;
            }

            modeloTabela.addRow(
                    new Object[]{
                            rm,
                            nomeCsv,
                            linha[2],
                            situacao
                    }
            );
        }

        labelResumo.setText(
                linhasCsv.size()
                        + " linha(s) lida(s)  •  "
                        + encontrados
                        + " encontrado(s)  •  "
                        + naoEncontrados
                        + " não encontrado(s)"
        );

        botaoImportar.setEnabled(
                encontrados > 0
        );
    }

    // =====================================================
    // IMPORTAR
    // =====================================================

    private void importarPresencas() {

        if (linhasCsv.isEmpty()) {

            mostrarAviso(
                    "Nenhum registro foi carregado."
            );

            return;
        }

        if (modoAtividade) {

            importarParticipacaoAtividade();

        } else {

            importarPresencaEvento();
        }
    }

    // =====================================================
    // IMPORTAR EVENTO
    // =====================================================

    private void importarPresencaEvento() {

        EventoOpcao evento =
                (EventoOpcao)
                        comboContexto.getSelectedItem();

        if (evento == null) {

            mostrarAviso(
                    "Selecione um evento."
            );

            return;
        }

        LocalDate data;

        try {

            data =
                    LocalDate.parse(
                            campoData
                                    .getText()
                                    .trim()
                    );

        } catch (Exception ex) {

            mostrarAviso(
                    "Data inválida.\n\n"
                            + "Use o formato AAAA-MM-DD."
            );

            return;
        }

        // =================================================
        // PERÍODO
        // =================================================

        if (
                evento.getDataInicio() != null
                        && evento.getDataFim() != null
        ) {

            boolean foraDoPeriodo =
                    data.isBefore(
                            evento.getDataInicio()
                    )
                            || data.isAfter(
                            evento.getDataFim()
                    );

            if (foraDoPeriodo) {

                int continuar =
                        JOptionPane.showConfirmDialog(
                                this,
                                "A data informada está fora do período do evento.\n\n"
                                        + "Período: "
                                        + evento.getDataInicio()
                                        + " até "
                                        + evento.getDataFim()
                                        + "\n\n"
                                        + "Deseja continuar?",
                                "Data fora do período",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );

                if (
                        continuar
                                != JOptionPane.YES_OPTION
                ) {

                    return;
                }
            }
        }

        // =================================================
        // CONFIRMAÇÃO
        // =================================================

        int confirmacao =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja importar "
                                + linhasCsv.size()
                                + " linha(s) para o evento?\n\n"
                                + evento
                                + "\n"
                                + "Data: "
                                + data,
                        "Confirmar importação",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (
                confirmacao
                        != JOptionPane.YES_OPTION
        ) {
            return;
        }

        int importados = 0;

        int falhas = 0;

        for (
                String[] linha
                : linhasCsv
        ) {

            Aluno aluno =
                    presencaService.buscarAlunoPorRm(
                            linha[0]
                    );

            if (aluno == null) {

                falhas++;

                continue;
            }

            boolean sucesso =
                    presencaService.registrarPresencaEvento(
                            aluno.getId(),
                            evento.getId(),
                            data,
                            linha.length > 2 ? linha[2] : "PRESENTE"
                    );

            if (sucesso) {

                importados++;

            } else {

                falhas++;
            }
        }

        mostrarResultado(
                importados,
                falhas
        );
    }

    // =====================================================
    // IMPORTAR ATIVIDADE
    // =====================================================

    private void importarParticipacaoAtividade() {

        AtividadeOpcao atividade =
                (AtividadeOpcao)
                        comboContexto.getSelectedItem();

        if (atividade == null) {

            mostrarAviso(
                    "Selecione uma atividade."
            );

            return;
        }

        int confirmacao =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja importar "
                                + linhasCsv.size()
                                + " linha(s) para a atividade?\n\n"
                                + atividade,
                        "Confirmar importação",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (
                confirmacao
                        != JOptionPane.YES_OPTION
        ) {
            return;
        }

        int importados = 0;

        int falhas = 0;

        for (
                String[] linha
                : linhasCsv
        ) {

            Aluno aluno =
                    presencaService.buscarAlunoPorRm(
                            linha[0]
                    );

            if (aluno == null) {

                falhas++;

                continue;
            }

            boolean sucesso =
                    presencaService.registrarParticipacaoAtividade(
                            aluno.getId(),
                            atividade.getId(),
                            linha.length > 2 ? linha[2] : "PRESENTE"
                    );

            if (sucesso) {

                importados++;

            } else {

                falhas++;
            }
        }

        mostrarResultado(
                importados,
                falhas
        );
    }

    // =====================================================
    // RESULTADO
    // =====================================================

    private void mostrarResultado(
            int importados,
            int falhas
    ) {

        JOptionPane.showMessageDialog(
                this,
                "Importação concluída!\n\n"
                        + importados
                        + " registro(s) gravado(s)\n"
                        + falhas
                        + " falha(s) / RM(s) não encontrado(s)",
                "Importação concluída",
                JOptionPane.INFORMATION_MESSAGE
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
                        13
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
                        10,
                        16,
                        10,
                        16
                )
        );

        return botao;
    }

    // =====================================================
    // AVISO
    // =====================================================

    private void mostrarAviso(
            String mensagem
    ) {

        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Aviso",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // =====================================================
    // ERRO
    // =====================================================

    private void mostrarErro(
            String mensagem
    ) {

        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
// =====================================================
// VOLTAR PARA RELATÓRIOS
// =====================================================

    private void voltarParaRelatorios() {

        Container pai = getParent();

        while (pai != null) {

            if (pai instanceof ResumoRelatoriosPage) {

                ResumoRelatoriosPage pagina =
                        (ResumoRelatoriosPage) pai;

                pagina.mostrarListaRelatorios();

                return;
            }

            pai = pai.getParent();
        }
    }

    // =====================================================
    // RENDERER
    // =====================================================

    private static class SituacaoRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            Component componente =
                    super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            setBorder(
                    new EmptyBorder(
                            0,
                            8,
                            0,
                            8
                    )
            );

            if (!isSelected) {

                String texto =
                        value == null
                                ? ""
                                : value.toString();

                if (
                        texto.startsWith(
                                "Encontrado:"
                        )
                ) {

                    setForeground(VERDE);

                } else {

                    setForeground(VERMELHO);
                }
            }

            return componente;
        }
    }
}
