package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.theme.ThemeManager;
import br.com.gerenciadoreventos.model.Comissao;
import br.com.gerenciadoreventos.service.ComissaoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ComissoesPage extends JPanel {
    private static final Color FUNDO = new Color(246, 248, 252);

    private static final Color AZUL = new Color(37, 99, 235);

    private static final Color TEXTO = new Color(15, 23, 42);

    private static final Color CINZA = new Color(100, 116, 139);

    private static final Color BORDA = new Color(226, 232, 240);

    private final ComissaoService service;

    private JTable tabela;

    private DefaultTableModel modelo;

    private JTextField campoPesquisa;

    private Timer timerStatusAutomatico;

    private List<Comissao> comissoes = new ArrayList<>();

    // CONSTRUTOR

    public ComissoesPage() {
        service = new ComissaoService();
        setLayout(new BorderLayout());
        setBackground(FUNDO);
        setBorder( BorderFactory.createEmptyBorder( 25, 25, 25, 25 ) );
        inicializar();
        carregarDados();

        // Enquanto o sistema estiver aberto, verifica periodicamente se algum evento terminou e atualiza o status das comissões.
        timerStatusAutomatico = new Timer(30_000, e -> {
            if (isShowing()) {
                carregarDados();
            }
        });
        timerStatusAutomatico.setRepeats(true);
        timerStatusAutomatico.start();
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0
                    && isShowing()) {
                carregarDados();
            }
        });
    }

    // INTERFACE

    private void inicializar() {
        add( criarCabecalho(), BorderLayout.NORTH );
        add( criarTabela(), BorderLayout.CENTER );
    }

    // CABEÇALHO

    private JPanel criarCabecalho() {
        JPanel painel = new JPanel( new BorderLayout( 15, 15 ) );
        painel.setOpaque(false);
        JPanel titulo = new JPanel();
        titulo.setLayout( new BoxLayout( titulo, BoxLayout.Y_AXIS ) );
        titulo.setOpaque(false);
        JLabel lblTitulo = new JLabel("Comissões");
        lblTitulo.setFont( new Font( "Segoe UI", Font.BOLD, 25 ) );
        lblTitulo.setForeground(ThemeManager.getTexto());
        JLabel lblSubtitulo = new JLabel( "Gerencie as comissões responsáveis pelos eventos." );
        lblSubtitulo.setFont( new Font( "Segoe UI", Font.PLAIN, 13 ) );
        lblSubtitulo.setForeground(ThemeManager.getTextoSecundario());
        titulo.add(lblTitulo);
        titulo.add( Box.createVerticalStrut(5) );
        titulo.add(lblSubtitulo);

        // LADO DIREITO

        JPanel direita = new JPanel( new FlowLayout( FlowLayout.RIGHT, 10, 0 ) );
        direita.setOpaque(false);
        campoPesquisa = new JTextField(18);
        campoPesquisa.setPreferredSize( new Dimension( 190, 38 ) );
        campoPesquisa.setFont( new Font( "Segoe UI", Font.PLAIN, 13 ) );
        campoPesquisa.setBorder(
                BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( BORDA ), BorderFactory.createEmptyBorder( 0, 10, 0, 10 ) ) );
        campoPesquisa.putClientProperty( "JTextField.placeholderText", "Pesquisar..." );
        campoPesquisa.addActionListener(
                e -> filtrar()
        );
        JButton pesquisar = criarBotao( "Pesquisar", AZUL );
        pesquisar.addActionListener(
                e -> filtrar()
        );
        JButton nova = criarBotao( "+ Nova Comissão", new Color( 22, 163, 74 ) );
        nova.addActionListener(
                e -> abrirFormulario(null)
        );
        direita.add(campoPesquisa);
        direita.add(pesquisar);
        direita.add(nova);
        painel.add( titulo, BorderLayout.CENTER );
        painel.add( direita, BorderLayout.EAST );
        return painel;
    }

    // TABELA

    private JPanel criarTabela() {
        JPanel painel = new JPanel( new BorderLayout() );
        painel.setOpaque(false);
        painel.setBorder( BorderFactory.createEmptyBorder( 25, 0, 0, 0 ) );
        String[] colunas = {
                "ID",
                "Evento",
                "Comissão",
                "Descrição",
                "Alunos",
                "Atividades",
                "Status",
                "Ações"
        };
        modelo =
                new DefaultTableModel(
                        colunas,
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
        tabela = new JTable(modelo);
        tabela.setRowHeight(48);
        tabela.setFont( new Font( "Segoe UI", Font.PLAIN, 13 ) );
        tabela.setForeground(ThemeManager.getTexto());
        tabela.setSelectionBackground( new Color( 239, 246, 255 ) );
        tabela.setSelectionForeground(TEXTO);
        tabela.setGridColor(BORDA);
        tabela.setShowVerticalLines(false);
        tabela.setIntercellSpacing( new Dimension( 0, 1 ) );
        tabela.getTableHeader().setFont( new Font( "Segoe UI", Font.BOLD, 12 ) );
        tabela.getTableHeader().setForeground(ThemeManager.getTextoSecundario());
        tabela.getTableHeader().setBackground( new Color( 248, 250, 252 ) );
        tabela.getTableHeader().setPreferredSize( new Dimension( 0, 42 ) );
        tabela.getColumnModel() .getColumn(0) .setPreferredWidth(45);
        tabela.getColumnModel() .getColumn(1) .setPreferredWidth(180);
        tabela.getColumnModel() .getColumn(2) .setPreferredWidth(170);
        tabela.getColumnModel() .getColumn(3) .setPreferredWidth(220);
        tabela.getColumnModel() .getColumn(4) .setPreferredWidth(60);
        tabela.getColumnModel() .getColumn(5) .setPreferredWidth(80);
        tabela.getColumnModel() .getColumn(6) .setPreferredWidth(100);
        tabela.getColumnModel() .getColumn(7) .setPreferredWidth(170);
        configurarAcoesTabela();
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder( BorderFactory.createLineBorder( BORDA ) );
        scroll.getViewport().setBackground(ThemeManager.getPainel());
        painel.add( scroll, BorderLayout.CENTER );
        return painel;
    }

    // AÇÕES DA TABELA

    private void configurarAcoesTabela() {
        tabela.getColumnModel() .getColumn(7) .setCellRenderer(new AcoesRenderer());
        tabela.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int linha = tabela.rowAtPoint(e.getPoint());
                int coluna = tabela.columnAtPoint(e.getPoint());
                tabela.setCursor( linha >= 0 && coluna == 7 ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor() );
            }
        });
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabela.rowAtPoint(e.getPoint());
                int coluna = tabela.columnAtPoint(e.getPoint());
                if (linha < 0 || coluna != 7) {
                    return;
                }
                Rectangle celula = tabela.getCellRect(linha, coluna, true);
                int xDentroDaCelula = e.getX() - celula.x;

                // Metade esquerda = Editar / metade direita = Excluir.
                if (xDentroDaCelula < celula.width / 2) {
                    editarComissaoDaLinha(linha);
                } else {
                    excluirComissaoDaLinha(linha);
                }
            }
        });
    }

    private void editarComissaoDaLinha(int linha) {
        Long id = obterIdComissaoDaLinha(linha);
        if (id == null) {
            return;
        }
        try {
            Comissao comissao = service.buscarPorId(id);
            if (comissao == null) {
                JOptionPane.showMessageDialog( this, "A comissão selecionada não foi encontrada.", "Comissões", JOptionPane.WARNING_MESSAGE );
                carregarDados();
                return;
            }
            abrirFormulario(comissao);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog( this, "Erro ao abrir a comissão para edição:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE );
        }
    }

    private void excluirComissaoDaLinha(int linha) {
        Long id = obterIdComissaoDaLinha(linha);
        if (id == null) {
            return;
        }
        String nome = String.valueOf(tabela.getValueAt(linha, 2));
        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir a comissão \"" + nome + "\"?\n\n"
                        + "Os vínculos dessa comissão com alunos, atividades e "
                        + "responsabilidades também serão removidos.", "Excluir comissão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.excluir(id);
            carregarDados();
            JOptionPane.showMessageDialog( this, "Comissão excluída com sucesso.", "Comissões", JOptionPane.INFORMATION_MESSAGE );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog( this, "Não foi possível excluir a comissão:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE );
        }
    }

    private Long obterIdComissaoDaLinha(int linha) {
        if (linha < 0 || linha >= tabela.getRowCount()) {
            return null;
        }
        Object valor = tabela.getValueAt(linha, 0);
        if (valor instanceof Number numero) {
            return numero.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(valor));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static class AcoesRenderer extends JPanel implements TableCellRenderer {
        private final JLabel editar;
        private final JLabel excluir;
        private AcoesRenderer() {
            setLayout(new GridLayout(1, 2, 6, 0));
            setBorder(BorderFactory.createEmptyBorder(7, 6, 7, 6));
            setOpaque(true);
            editar = criarAcao("Editar", new Color(37, 99, 235));
            excluir = criarAcao("Excluir", new Color(220, 38, 38));
            add(editar);
            add(excluir);
        }
        private static JLabel criarAcao(String texto, Color cor) {
            JLabel label = new JLabel(texto, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setForeground(cor);
            label.setOpaque(false);
            label.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(cor), BorderFactory.createEmptyBorder(4, 5, 4, 5) ) );
            return label;
        }
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            Color fundo = isSelected ? table.getSelectionBackground() : table.getBackground();
            setBackground(fundo);
            return this;
        }
    }

    // CARREGAR DADOS

    private void carregarDados() {
        try {
            comissoes = service.listar();
            atualizarTabela( comissoes );
        } catch (Exception e) {
            JOptionPane.showMessageDialog( this, "Erro ao carregar comissões:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE );
        }
    }

    // ATUALIZAR TABELA

    private void atualizarTabela(
            List<Comissao> lista
    ) {
        modelo.setRowCount(0);
        for (Comissao c : lista) {
            modelo.addRow(
                    new Object[]{
                            c.getIdComissao(),
                            c.getEvento(),
                            c.getNome(),
                            c.getDescricao(),
                            c.getTotalAlunos(),
                            c.getTotalAtividades(),
                            c.isAtivo()
                                    ? "ATIVA"
                                    : "INATIVA",
                            "Editar | Excluir"
                    }
            );
        }
    }

    // PESQUISA

    private void filtrar() {
        String texto = campoPesquisa .getText() .trim() .toLowerCase();
        if (texto.isEmpty()) {
            atualizarTabela( comissoes );
            return;
        }
        List<Comissao> filtradas = new ArrayList<>();
        for (Comissao c : comissoes) {
            boolean encontrou =
                    (c.getNome() != null
                            && c.getNome()
                            .toLowerCase()
                            .contains(texto))
                            ||
                            (c.getEvento() != null
                                    && c.getEvento()
                                    .toLowerCase()
                                    .contains(texto))
                            ||
                            (c.getDescricao() != null && c.getDescricao() .toLowerCase() .contains(texto));
            if (encontrou) {
                filtradas.add(c);
            }
        }
        atualizarTabela(filtradas);
    }

    // FORMULÁRIO

    private void abrirFormulario(
            Comissao comissao
    ) {
        JTextField campoEvento = new JTextField();
        JTextField campoNome = new JTextField();
        JTextArea campoDescricao = new JTextArea( 4, 25 );
        campoDescricao.setLineWrap(true);
        campoDescricao.setWrapStyleWord(true);
        if (comissao != null) {
            campoEvento.setText( comissao.getIdEvento() != null ? String.valueOf( comissao.getIdEvento() ) : "" );
            campoNome.setText( comissao.getNome() );
            campoDescricao.setText( comissao.getDescricao() != null ? comissao.getDescricao() : "" );
        }
        JPanel painel = new JPanel( new GridBagLayout() );
        painel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 10 ) );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets( 6, 6, 6, 6 );
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add( new JLabel("ID do Evento:"), gbc );
        gbc.gridy++;
        painel.add( campoEvento, gbc );
        gbc.gridy++;
        painel.add( new JLabel("Nome da Comissão:"), gbc );
        gbc.gridy++;
        painel.add( campoNome, gbc );
        gbc.gridy++;
        painel.add( new JLabel("Descrição:"), gbc );
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        painel.add( new JScrollPane( campoDescricao ), gbc );
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        JLabel statusAutomatico = new JLabel( "Status automático: ativa até o término do evento." );
        statusAutomatico.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusAutomatico.setForeground(ThemeManager.getTextoSecundario());
        painel.add( statusAutomatico, gbc );
        String titulo = comissao == null ? "Nova Comissão" : "Editar Comissão";
        int resultado = JOptionPane.showConfirmDialog( this, painel, titulo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE );
        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            String idEventoTexto = campoEvento .getText() .trim();
            if (idEventoTexto.isEmpty()) {
                throw new IllegalArgumentException( "Informe o ID do evento." );
            }
            Long idEvento = Long.parseLong( idEventoTexto );
            Comissao dados = comissao == null ? new Comissao() : comissao;
            dados.setIdEvento(idEvento);
            dados.setNome( campoNome .getText() .trim() );
            dados.setDescricao( campoDescricao .getText() .trim() );
            if (comissao == null) {
                service.cadastrar(dados);
                JOptionPane.showMessageDialog( this, "Comissão cadastrada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE );
            } else {
                service.alterar(dados);
                JOptionPane.showMessageDialog( this, "Comissão alterada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE );
            }
            carregarDados();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog( this, "O ID do evento deve ser numérico.", "Erro", JOptionPane.ERROR_MESSAGE );
        } catch (Exception e) {
            JOptionPane.showMessageDialog( this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE );
        }
    }

    // BOTÃO

    private JButton criarBotao(
            String texto,
            Color cor
    ) {
        JButton botao = new JButton(texto);
        botao.setForeground(Color.WHITE);
        botao.setBackground(cor);
        botao.setFont( new Font( "Segoe UI", Font.BOLD, 12 ) );
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        botao.setPreferredSize( new Dimension( 145, 38 ) );
        return botao;
    }
}
