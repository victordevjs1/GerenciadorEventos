package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.dao.AlunoDAO;
import br.com.gerenciadoreventos.dao.PresencaImportDAO;
import br.com.gerenciadoreventos.dao.PresencaImportDAO.AtividadeOpcao;
import br.com.gerenciadoreventos.dao.PresencaImportDAO.EventoOpcao;
import br.com.gerenciadoreventos.model.Aluno;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    // CORES (mesma paleta usada no resto do app)
    // =====================================================

    private static final Color FUNDO = new Color(246, 248, 252);
    private static final Color AZUL = new Color(37, 99, 235);
    private static final Color TEXTO = new Color(15, 23, 42);
    private static final Color CINZA_TEXTO = new Color(100, 116, 139);
    private static final Color BORDA = new Color(203, 213, 225);


    // =====================================================
    // COMPONENTES
    // =====================================================

    private JToggleButton toggleModoEvento;
    private JToggleButton toggleModoAtividade;
    private boolean modoAtividade = false;

    private JLabel labelContexto;
    private JComboBox<Object> comboContexto; // EventoOpcao ou AtividadeOpcao
    private JPanel painelData;
    private JTextField campoData; // formato yyyy-MM-dd, só usado no modo Evento

    private JLabel labelArquivo;
    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private JButton botaoImportar;
    private JLabel labelResumo;

    // Linhas lidas do CSV: [0]=rm, [1]=nomeCsv
    private final List<String[]> linhasCsv = new ArrayList<>();

    private final AlunoDAO alunoDAO = new AlunoDAO();
    private final PresencaImportDAO presencaDAO = new PresencaImportDAO();


    public ImportarPresencaCsvPage() {

        setLayout(new BorderLayout());
        setBackground(FUNDO);

        JPanel conteudo = new JPanel(new BorderLayout(0, 20));
        conteudo.setBackground(FUNDO);
        conteudo.setBorder(new EmptyBorder(25, 25, 25, 25));

        conteudo.add(criarCabecalho(), BorderLayout.NORTH);
        conteudo.add(criarPreview(), BorderLayout.CENTER);
        conteudo.add(criarRodape(), BorderLayout.SOUTH);

        add(conteudo, BorderLayout.CENTER);

        carregarContexto();
    }


    // =====================================================
    // CABEÇALHO: título + toggle de modo + seleção + arquivo
    // =====================================================

    private JPanel criarCabecalho() {

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(FUNDO);

        JLabel titulo = new JLabel("Importar Presença (CSV)");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel(
                "Importe as respostas de um formulário (Google Forms / Sheets exportado como CSV)"
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(CINZA_TEXTO);

        painel.add(titulo);
        painel.add(Box.createVerticalStrut(4));
        painel.add(subtitulo);
        painel.add(Box.createVerticalStrut(20));

        JPanel controles = new JPanel();
        controles.setLayout(new BoxLayout(controles, BoxLayout.Y_AXIS));
        controles.setBackground(Color.WHITE);
        controles.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240)),
                        new EmptyBorder(20, 20, 20, 20)
                )
        );

        // =================================================
        // TOGGLE DE MODO (Evento / Atividade)
        // =================================================

        JPanel linhaModo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        linhaModo.setBackground(Color.WHITE);

        toggleModoEvento = criarToggleModo("Presença em Evento");
        toggleModoAtividade = criarToggleModo("Participação em Atividade");

        ButtonGroup grupoModo = new ButtonGroup();
        grupoModo.add(toggleModoEvento);
        grupoModo.add(toggleModoAtividade);

        toggleModoEvento.setSelected(true);
        aplicarEstiloToggle(toggleModoEvento, true);
        aplicarEstiloToggle(toggleModoAtividade, false);

        toggleModoEvento.addActionListener(e -> {
            modoAtividade = false;
            aplicarEstiloToggle(toggleModoEvento, true);
            aplicarEstiloToggle(toggleModoAtividade, false);
            alternarModo();
        });

        toggleModoAtividade.addActionListener(e -> {
            modoAtividade = true;
            aplicarEstiloToggle(toggleModoEvento, false);
            aplicarEstiloToggle(toggleModoAtividade, true);
            alternarModo();
        });

        linhaModo.add(toggleModoEvento);
        linhaModo.add(toggleModoAtividade);

        controles.add(linhaModo);
        controles.add(Box.createVerticalStrut(15));

        // =================================================
        // LINHA DE SELEÇÃO (evento/atividade + data)
        // =================================================

        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        linha1.setBackground(Color.WHITE);

        JPanel blocoContexto = new JPanel();
        blocoContexto.setLayout(new BoxLayout(blocoContexto, BoxLayout.Y_AXIS));
        blocoContexto.setBackground(Color.WHITE);

        labelContexto = new JLabel("Evento");
        labelContexto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelContexto.setForeground(TEXTO);

        comboContexto = new JComboBox<>();
        comboContexto.setPreferredSize(new Dimension(320, 38));
        comboContexto.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        blocoContexto.add(labelContexto);
        blocoContexto.add(Box.createVerticalStrut(5));
        blocoContexto.add(comboContexto);

        painelData = new JPanel();
        painelData.setLayout(new BoxLayout(painelData, BoxLayout.Y_AXIS));
        painelData.setBackground(Color.WHITE);

        JLabel labelData = new JLabel("Data da presença (AAAA-MM-DD)");
        labelData.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelData.setForeground(TEXTO);

        campoData = new JTextField(LocalDate.now().toString());
        campoData.setPreferredSize(new Dimension(160, 38));
        campoData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoData.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA),
                        new EmptyBorder(8, 10, 8, 10)
                )
        );

        painelData.add(labelData);
        painelData.add(Box.createVerticalStrut(5));
        painelData.add(campoData);

        linha1.add(blocoContexto);
        linha1.add(painelData);

        controles.add(linha1);
        controles.add(Box.createVerticalStrut(15));

        // =================================================
        // ESCOLHER ARQUIVO
        // =================================================

        JPanel linha2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        linha2.setBackground(Color.WHITE);

        JButton botaoEscolher = criarBotaoPrincipal("Escolher arquivo CSV...");
        botaoEscolher.addActionListener(e -> escolherArquivo());

        labelArquivo = new JLabel("Nenhum arquivo selecionado.");
        labelArquivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelArquivo.setForeground(CINZA_TEXTO);

        linha2.add(botaoEscolher);
        linha2.add(labelArquivo);

        controles.add(linha2);

        painel.add(controles);

        return painel;
    }


    // =====================================================
    // TOGGLE DE MODO — estilo
    // =====================================================

    private JToggleButton criarToggleModo(String texto) {

        JToggleButton toggle = new JToggleButton(texto);

        toggle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        toggle.setFocusPainted(false);
        toggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggle.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA),
                        new EmptyBorder(9, 16, 9, 16)
                )
        );

        return toggle;
    }

    private void aplicarEstiloToggle(JToggleButton toggle, boolean ativo) {

        toggle.setBackground(ativo ? AZUL : Color.WHITE);
        toggle.setForeground(ativo ? Color.WHITE : TEXTO);
    }


    // =====================================================
    // ALTERNAR MODO (troca rótulo, combo e visibilidade
    // do campo de data)
    // =====================================================

    private void alternarModo() {

        labelContexto.setText(modoAtividade ? "Atividade" : "Evento");
        painelData.setVisible(!modoAtividade);

        carregarContexto();

        linhasCsv.clear();
        modeloTabela.setRowCount(0);
        labelArquivo.setText("Nenhum arquivo selecionado.");
        labelResumo.setText(" ");
        botaoImportar.setEnabled(false);
    }


    // =====================================================
    // PREVIEW (tabela com o que foi lido do CSV)
    // =====================================================

    private JScrollPane criarPreview() {

        modeloTabela = new DefaultTableModel(
                new Object[]{"RM", "Nome (do formulário)", "Situação"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(28);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240))
        );

        return scroll;
    }


    // =====================================================
    // RODAPÉ (botão importar + resumo)
    // =====================================================

    private JPanel criarRodape() {

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(FUNDO);
        painel.setBorder(new EmptyBorder(15, 0, 0, 0));

        labelResumo = new JLabel(" ");
        labelResumo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelResumo.setForeground(CINZA_TEXTO);

        botaoImportar = criarBotaoPrincipal("Importar Presenças");
        botaoImportar.setEnabled(false);
        botaoImportar.addActionListener(e -> importarPresencas());

        painel.add(labelResumo, BorderLayout.WEST);
        painel.add(botaoImportar, BorderLayout.EAST);

        return painel;
    }


    // =====================================================
    // CARREGAR EVENTOS OU ATIVIDADES NO COMBO, conforme o
    // modo selecionado
    // =====================================================

    private void carregarContexto() {

        comboContexto.removeAllItems();

        if (modoAtividade) {

            List<AtividadeOpcao> atividades = presencaDAO.listarAtividades();

            for (AtividadeOpcao atividade : atividades) {
                comboContexto.addItem(atividade);
            }

        } else {

            List<EventoOpcao> eventos = presencaDAO.listarEventos();

            for (EventoOpcao evento : eventos) {
                comboContexto.addItem(evento);
            }
        }
    }


    // =====================================================
    // ESCOLHER ARQUIVO CSV
    // =====================================================

    private void escolherArquivo() {

        JFileChooser seletor = new JFileChooser();
        seletor.setDialogTitle("Selecione o CSV exportado do Google Sheets");

        int resultado = seletor.showOpenDialog(this);

        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = seletor.getSelectedFile();
        labelArquivo.setText(arquivo.getName());

        lerCsv(arquivo);
    }


    // =====================================================
    // LER CSV
    //
    // Espera um cabeçalho contendo uma coluna "RM" e,
    // opcionalmente, uma coluna com o nome (qualquer coluna
    // cujo título contenha "nome"). A ordem das colunas do
    // Google Forms pode variar, então localizamos pelo
    // cabeçalho em vez de posição fixa.
    // =====================================================

    private void lerCsv(File arquivo) {

        linhasCsv.clear();
        modeloTabela.setRowCount(0);

        try (
                BufferedReader leitor = new BufferedReader(
                        new FileReader(arquivo, StandardCharsets.UTF_8)
                )
        ) {

            String cabecalho = leitor.readLine();

            if (cabecalho == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "O arquivo está vazio.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            String[] colunas = dividirLinhaCsv(cabecalho);

            int indiceRm = -1;
            int indiceNome = -1;

            for (int i = 0; i < colunas.length; i++) {

                String coluna = colunas[i].trim().toLowerCase();

                if (coluna.equals("rm")) {
                    indiceRm = i;
                }

                if (coluna.contains("nome")) {
                    indiceNome = i;
                }
            }

            if (indiceRm == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Não encontrei uma coluna chamada \"RM\" no CSV.\n"
                                + "Confira se o formulário tem essa pergunta "
                                + "exatamente com esse nome.",
                        "Coluna não encontrada",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            String linha;

            while ((linha = leitor.readLine()) != null) {

                if (linha.isBlank()) {
                    continue;
                }

                String[] valores = dividirLinhaCsv(linha);

                String rm = indiceRm < valores.length
                        ? valores[indiceRm].trim()
                        : "";

                String nomeCsv = (indiceNome != -1 && indiceNome < valores.length)
                        ? valores[indiceNome].trim()
                        : "";

                if (rm.isBlank()) {
                    continue;
                }

                linhasCsv.add(new String[]{rm, nomeCsv});
            }

        } catch (IOException e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível ler o arquivo.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        validarLinhas();
    }


    // =====================================================
    // DIVIDIR LINHA CSV RESPEITANDO ASPAS
    // =====================================================

    private String[] dividirLinhaCsv(String linha) {

        return linha.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }


    // =====================================================
    // VALIDAR LINHAS (checa RM contra o banco e preenche
    // a tabela de preview)
    // =====================================================

    private void validarLinhas() {

        modeloTabela.setRowCount(0);

        int encontrados = 0;
        int naoEncontrados = 0;

        for (String[] linha : linhasCsv) {

            String rm = linha[0];
            String nomeCsv = linha[1];

            Aluno aluno = alunoDAO.buscarPorRm(rm);

            String situacao;

            if (aluno != null) {

                situacao = "Encontrado: " + aluno.getNome();
                encontrados++;

            } else {

                situacao = "RM não encontrado no cadastro de alunos";
                naoEncontrados++;
            }

            modeloTabela.addRow(new Object[]{rm, nomeCsv, situacao});
        }

        labelResumo.setText(
                linhasCsv.size() + " linha(s) lida(s)  •  "
                        + encontrados + " encontrado(s)  •  "
                        + naoEncontrados + " não encontrado(s)"
        );

        botaoImportar.setEnabled(encontrados > 0);
    }


    // =====================================================
    // IMPORTAR PRESENÇAS / PARTICIPAÇÕES
    // =====================================================

    private void importarPresencas() {

        if (modoAtividade) {
            importarParticipacaoAtividade();
        } else {
            importarPresencaEvento();
        }
    }


    // =====================================================
    // IMPORTAR — MODO EVENTO
    // =====================================================

    private void importarPresencaEvento() {

        EventoOpcao evento = (EventoOpcao) comboContexto.getSelectedItem();

        if (evento == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um evento.",
                    "Campo obrigatório",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        LocalDate data;

        try {

            data = LocalDate.parse(campoData.getText().trim());

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Data inválida. Use o formato AAAA-MM-DD.",
                    "Data inválida",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (evento.dataInicio != null && evento.dataFim != null) {

            boolean foraDoPeriodo =
                    data.isBefore(evento.dataInicio)
                            || data.isAfter(evento.dataFim);

            if (foraDoPeriodo) {

                int continuar = JOptionPane.showConfirmDialog(
                        this,
                        "A data informada está fora do período do evento\n"
                                + "(" + evento.dataInicio + " a " + evento.dataFim + ").\n\n"
                                + "Deseja continuar mesmo assim?",
                        "Data fora do período",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (continuar != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }

        int importados = 0;
        int falhas = 0;

        for (String[] linha : linhasCsv) {

            Aluno aluno = alunoDAO.buscarPorRm(linha[0]);

            if (aluno == null) {
                falhas++;
                continue;
            }

            Long idInscricao = presencaDAO.obterOuCriarInscricao(
                    aluno.getId(),
                    evento.id
            );

            if (idInscricao == null) {
                falhas++;
                continue;
            }

            boolean sucesso = presencaDAO.registrarPresenca(
                    idInscricao,
                    data,
                    "PRESENTE"
            );

            if (sucesso) {
                importados++;
            } else {
                falhas++;
            }
        }

        mostrarResultado(importados, falhas);
    }


    // =====================================================
    // IMPORTAR — MODO ATIVIDADE
    //
    // Não precisa de data: a atividade já tem seu próprio
    // horário, e a ligação é direta aluno <-> atividade.
    // =====================================================

    private void importarParticipacaoAtividade() {

        AtividadeOpcao atividade = (AtividadeOpcao) comboContexto.getSelectedItem();

        if (atividade == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma atividade.",
                    "Campo obrigatório",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int importados = 0;
        int falhas = 0;

        for (String[] linha : linhasCsv) {

            Aluno aluno = alunoDAO.buscarPorRm(linha[0]);

            if (aluno == null) {
                falhas++;
                continue;
            }

            boolean sucesso = presencaDAO.registrarParticipacaoAtividade(
                    aluno.getId(),
                    atividade.id,
                    "PRESENTE"
            );

            if (sucesso) {
                importados++;
            } else {
                falhas++;
            }
        }

        mostrarResultado(importados, falhas);
    }


    // =====================================================
    // RESULTADO DA IMPORTAÇÃO
    // =====================================================

    private void mostrarResultado(int importados, int falhas) {

        JOptionPane.showMessageDialog(
                this,
                "Importação concluída!\n\n"
                        + importados + " registro(s) gravado(s)\n"
                        + falhas + " falha(s) / RM(s) não encontrado(s)",
                "Importação concluída",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    // =====================================================
    // BOTÃO PRINCIPAL (mesmo estilo do resto do app)
    // =====================================================

    private JButton criarBotaoPrincipal(String texto) {

        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setForeground(Color.WHITE);
        botao.setBackground(AZUL);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(10, 16, 10, 16));

        return botao;
    }
}