package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.dao.InscricaoDAO;
import br.com.gerenciadoreventos.model.Aluno;
import br.com.gerenciadoreventos.model.Evento;
import br.com.gerenciadoreventos.model.InscricaoCsvImportacao;
import br.com.gerenciadoreventos.service.InscricaoCsvImportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportarInscricoesCsvDialog extends JDialog {

    private static final Color FUNDO = new Color(246, 248, 252);
    private static final Color AZUL = new Color(37, 99, 235);
    private static final Color TEXTO = new Color(15, 23, 42);
    private static final Color CINZA_TEXTO = new Color(100, 116, 139);
    private static final Color BORDA = new Color(203, 213, 225);

    private final InscricaoDAO inscricaoDAO = new InscricaoDAO();
    private final InscricaoCsvImportService importService = new InscricaoCsvImportService();
    private final Runnable aoConcluir;

    private final JComboBox<Evento> comboEvento = new JComboBox<>();
    private final JLabel labelArquivo = new JLabel("Nenhum arquivo selecionado");
    private final JLabel labelResumo = new JLabel("Selecione um evento e um CSV para visualizar as inscrições.");
    private final DefaultTableModel modeloTabela;
    private final JButton botaoImportar = criarBotaoPrincipal("Importar inscrições");

    private File arquivoSelecionado;
    private List<InscricaoCsvImportacao> linhas = new ArrayList<>();

    public ImportarInscricoesCsvDialog(
            Window owner,
            Evento eventoInicial,
            Runnable aoConcluir
    ) {
        super(owner, "Importar inscrições por CSV", ModalityType.APPLICATION_MODAL);
        this.aoConcluir = aoConcluir;

        modeloTabela = new DefaultTableModel(
                new Object[]{"Linha", "RM", "Nome", "Curso", "Série", "Situação"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        configurarJanela();
        montarInterface();
        carregarEventos(eventoInicial);
    }

    private void configurarJanela() {
        setSize(980, 650);
        setMinimumSize(new Dimension(850, 560));
        setLocationRelativeTo(getOwner());
    }

    private void montarInterface() {
        JPanel principal = new JPanel(new BorderLayout(0, 18));
        principal.setBackground(FUNDO);
        principal.setBorder(new EmptyBorder(24, 28, 24, 28));

        principal.add(criarCabecalho(), BorderLayout.NORTH);
        principal.add(criarCentro(), BorderLayout.CENTER);
        principal.add(criarRodape(), BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setBackground(FUNDO);

        JLabel titulo = new JLabel("Importar inscrições por CSV");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(TEXTO);

        JLabel descricao = new JLabel(
                "Selecione o evento e importe as respostas do formulário de inscrição."
        );
        descricao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descricao.setForeground(CINZA_TEXTO);

        cabecalho.add(titulo);
        cabecalho.add(Box.createVerticalStrut(4));
        cabecalho.add(descricao);
        cabecalho.add(Box.createVerticalStrut(18));

        JPanel selecao = new JPanel(new GridBagLayout());
        selecao.setBackground(Color.WHITE);
        selecao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(16, 16, 16, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel labelEvento = new JLabel("Evento");
        labelEvento.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelEvento.setForeground(TEXTO);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        selecao.add(labelEvento, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        selecao.add(new JLabel("Arquivo CSV"), gbc);

        comboEvento.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboEvento.setPreferredSize(new Dimension(420, 40));
        comboEvento.addActionListener(e -> reanalisarSePossivel());

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        selecao.add(comboEvento, gbc);

        JButton selecionarCsv = criarBotaoSecundario("Selecionar CSV");
        selecionarCsv.addActionListener(e -> escolherArquivo());

        JPanel arquivoPanel = new JPanel(new BorderLayout(10, 0));
        arquivoPanel.setBackground(Color.WHITE);
        labelArquivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelArquivo.setForeground(CINZA_TEXTO);
        arquivoPanel.add(selecionarCsv, BorderLayout.WEST);
        arquivoPanel.add(labelArquivo, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 0, 8, 0);
        selecao.add(arquivoPanel, gbc);

        cabecalho.add(selecao);
        return cabecalho;
    }

    private JPanel criarCentro() {
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FUNDO);

        JTable tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(30);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        final Color VERDE = new Color(22, 101, 52);
        final Color VERMELHO = new Color(185, 28, 28);

        tabela.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );
                if (!isSelected && row < linhas.size()) {
                    c.setForeground(linhas.get(row).isValido() ? VERDE : VERMELHO);
                }
                return c;
            }
        });

        tabela.getColumnModel().getColumn(0).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(260);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA));
        centro.add(scroll, BorderLayout.CENTER);
        return centro;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(FUNDO);

        labelResumo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelResumo.setForeground(CINZA_TEXTO);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setBackground(FUNDO);

        JButton cancelar = criarBotaoSecundario("Cancelar");
        cancelar.addActionListener(e -> dispose());

        botaoImportar.setEnabled(false);
        botaoImportar.addActionListener(e -> importar());

        botoes.add(cancelar);
        botoes.add(botaoImportar);

        rodape.add(labelResumo, BorderLayout.WEST);
        rodape.add(botoes, BorderLayout.EAST);
        return rodape;
    }

    private void carregarEventos(Evento eventoInicial) {
        comboEvento.removeAllItems();

        for (Evento evento : inscricaoDAO.listarEventos()) {
            comboEvento.addItem(evento);
            if (eventoInicial != null && evento.getId() == eventoInicial.getId()) {
                comboEvento.setSelectedItem(evento);
            }
        }

        comboEvento.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );
                if (value instanceof Evento evento) {
                    label.setText(evento.getNome());
                }
                return label;
            }
        });
    }

    private void escolherArquivo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecionar CSV de inscrições");
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivo CSV (*.csv)", "csv"));

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        arquivoSelecionado = chooser.getSelectedFile();
        labelArquivo.setText(arquivoSelecionado.getName());
        analisarArquivo();
    }

    private void reanalisarSePossivel() {
        if (arquivoSelecionado != null) {
            analisarArquivo();
        }
    }

    private void analisarArquivo() {
        Evento evento = (Evento) comboEvento.getSelectedItem();
        if (evento == null || arquivoSelecionado == null) {
            return;
        }

        try {
            linhas = importService.analisar(arquivoSelecionado, evento);
            atualizarTabela();
        } catch (Exception ex) {
            ex.printStackTrace();
            linhas = new ArrayList<>();
            modeloTabela.setRowCount(0);
            botaoImportar.setEnabled(false);
            labelResumo.setText("Não foi possível analisar o arquivo.");

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "CSV inválido",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        int validos = 0;
        int invalidos = 0;

        for (InscricaoCsvImportacao linha : linhas) {
            Aluno aluno = linha.getAluno();
            if (linha.isValido()) validos++;
            else invalidos++;

            modeloTabela.addRow(new Object[]{
                    linha.getLinha(),
                    linha.getRmInformado(),
                    aluno != null ? aluno.getNome() : linha.getNomeCsv(),
                    aluno != null && aluno.getCurso() != null ? aluno.getCurso() : "-",
                    aluno != null && aluno.getSerie() != null ? aluno.getSerie() + "ª" : "-",
                    linha.getSituacao()
            });
        }

        labelResumo.setText(
                linhas.size() + " linha(s) • "
                        + validos + " pronta(s) para inscrever • "
                        + invalidos + " ignorada(s)"
        );
        botaoImportar.setEnabled(validos > 0);
    }

    private void importar() {
        Evento evento = (Evento) comboEvento.getSelectedItem();
        if (evento == null) {
            JOptionPane.showMessageDialog(this, "Selecione um evento.");
            return;
        }

        long validos = linhas.stream().filter(InscricaoCsvImportacao::isValido).count();
        if (validos == 0) {
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(
                this,
                "Serão realizadas " + validos + " inscrição(ões) em:\n\n"
                        + evento.getNome()
                        + "\n\nDeseja continuar?",
                "Confirmar importação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        botaoImportar.setEnabled(false);
        botaoImportar.setText("Importando...");

        try {
            int importados = importService.importar(linhas, evento);

            JOptionPane.showMessageDialog(
                    this,
                    importados + " inscrição(ões) realizada(s) com sucesso."
                            + (importados < validos
                            ? "\n\nAlgumas inscrições não puderam ser gravadas."
                            : ""),
                    "Importação concluída",
                    importados == validos
                            ? JOptionPane.INFORMATION_MESSAGE
                            : JOptionPane.WARNING_MESSAGE
            );

            if (aoConcluir != null) {
                aoConcluir.run();
            }
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            botaoImportar.setEnabled(true);
            botaoImportar.setText("Importar inscrições");

            JOptionPane.showMessageDialog(
                    this,
                    "Erro durante a importação:\n\n" + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JButton criarBotaoPrincipal(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setForeground(Color.WHITE);
        botao.setBackground(AZUL);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(10, 16, 10, 16));
        return botao;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setForeground(TEXTO);
        botao.setBackground(Color.WHITE);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                new EmptyBorder(9, 15, 9, 15)
        ));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return botao;
    }
}
