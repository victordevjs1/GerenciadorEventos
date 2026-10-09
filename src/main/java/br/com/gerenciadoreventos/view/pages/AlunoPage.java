package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.theme.ThemeManager;
import br.com.gerenciadoreventos.model.Aluno;
import br.com.gerenciadoreventos.model.AlunoCsvImportacao;
import br.com.gerenciadoreventos.dao.CursoDAO;
import br.com.gerenciadoreventos.dao.SerieDAO;
import br.com.gerenciadoreventos.service.AlunoService;
import br.com.gerenciadoreventos.service.AlunoCsvImportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AlunoPage extends JPanel {

    // CORES

    private static final Color FUNDO = new Color(246, 248, 252);

    private static final Color AZUL = new Color(37, 99, 235);

    private static final Color AZUL_HOVER = new Color(29, 78, 216);

    private static final Color TEXTO = new Color(15, 23, 42);

    private static final Color CINZA_TEXTO = new Color(100, 116, 139);

    private static final Color BORDA = new Color(203, 213, 225);

    // Cor do selo de presença (identidade visual própria, diferente do selo de status ATIVO/INATIVO)
    private static final Color ROXO_PRESENCA_TEXTO = new Color(91, 33, 182);

    private static final Color ROXO_PRESENCA_FUNDO = new Color(237, 233, 254);

    // COMPONENTES

    private JPanel painelAlunos;

    private JTextField campoBusca;

    private JButton botaoAtivados;

    private JButton botaoDesativados;

    // SERVICE

    private final AlunoService alunoService;
    private final CursoDAO cursoDAO = new CursoDAO();
    private final SerieDAO serieDAO = new SerieDAO();

    // FILTRO ATUAL

    // true  = mostrando alunos ativados false = mostrando alunos desativados
    private boolean mostrandoAtivos = true;

    // CONSTRUTOR

    public AlunoPage() {
        alunoService = new AlunoService();
        inicializarInterface();
        carregarAlunos();
    }

    // INTERFACE

    private void inicializarInterface() {
        setLayout( new BorderLayout() );
        setBackground(FUNDO);
        add( criarConteudo(), BorderLayout.CENTER );
    }

    // CONTEÚDO

    private JPanel criarConteudo() {
        JPanel painel = new JPanel( new BorderLayout() );
        painel.setBackground(ThemeManager.getFundo());
        painel.setBorder( new EmptyBorder( 25, 25, 25, 25 ) );
        painel.add( criarCabecalho(), BorderLayout.NORTH );
        painel.add( criarListaAlunos(), BorderLayout.CENTER );
        return painel;
    }

    // CABEÇALHO

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout( new BoxLayout( cabecalho, BoxLayout.Y_AXIS ) );
        cabecalho.setBackground(ThemeManager.getFundo());

        // LINHA SUPERIOR

        JPanel linhaSuperior = new JPanel( new BorderLayout() );
        linhaSuperior.setBackground(ThemeManager.getFundo());

        // TÍTULOS

        JPanel textos = new JPanel();
        textos.setLayout( new BoxLayout( textos, BoxLayout.Y_AXIS ) );
        textos.setBackground(ThemeManager.getFundo());
        JLabel titulo = new JLabel("Alunos");
        titulo.setFont( new Font( "Segoe UI", Font.BOLD, 28 ) );
        titulo.setForeground(ThemeManager.getTexto());
        JLabel subtitulo = new JLabel( "Gerencie os alunos da escola" );
        subtitulo.setFont( new Font( "Segoe UI", Font.PLAIN, 14 ) );
        subtitulo.setForeground(ThemeManager.getTextoSecundario());
        textos.add(titulo);
        textos.add( Box.createVerticalStrut(4) );
        textos.add(subtitulo);
        linhaSuperior.add( textos, BorderLayout.WEST );

        // NOVO ALUNO

        JButton novoAluno = criarBotaoPrincipal( "+ Novo Aluno" );
        novoAluno.addActionListener(
                e -> abrirNovoAluno() );
        JButton importarCsv = criarBotaoSecundario( "Importar CSV" );
        importarCsv.addActionListener(
                e -> abrirImportadorCsv() );
        JPanel acoesCabecalho = new JPanel( new FlowLayout( FlowLayout.RIGHT, 10, 0 ) );
        acoesCabecalho.setBackground(ThemeManager.getFundo());
        acoesCabecalho.add(importarCsv);
        acoesCabecalho.add(novoAluno);
        linhaSuperior.add( acoesCabecalho, BorderLayout.EAST );
        cabecalho.add( linhaSuperior );
        cabecalho.add( Box.createVerticalStrut(22) );

        // FILTROS

        JPanel painelFiltros = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
        painelFiltros.setBackground(ThemeManager.getFundo());
        botaoAtivados = criarBotaoFiltro( "Ativos" );
        botaoDesativados = criarBotaoFiltro( "Inativos" );
        painelFiltros.add( botaoAtivados );
        painelFiltros.add( Box.createHorizontalStrut(8) );
        painelFiltros.add( botaoDesativados );

        // CLIQUE ATIVADOS

        botaoAtivados.addActionListener(
                e -> {
                    mostrandoAtivos = true;
                    atualizarBotoesFiltro();
                    carregarAlunos();
                }
        );

        // CLIQUE DESATIVADOS

        botaoDesativados.addActionListener(
                e -> {
                    mostrandoAtivos = false;
                    atualizarBotoesFiltro();
                    carregarAlunos();
                }
        );
        atualizarBotoesFiltro();
        cabecalho.add( painelFiltros );
        cabecalho.add( Box.createVerticalStrut(15) );

        // PESQUISA

        campoBusca = new JTextField();
        campoBusca.putClientProperty( "JTextField.placeholderText", "Pesquisar por nome ou RM..." );
        estilizarCampo( campoBusca );
        campoBusca.setPreferredSize( new Dimension( 0, 42 ) );
        campoBusca.getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(
                                    DocumentEvent e
                            ) {
                                filtrar( campoBusca.getText() );
                            }
                            @Override
                            public void removeUpdate(
                                    DocumentEvent e
                            ) {
                                filtrar( campoBusca.getText() );
                            }
                            @Override
                            public void changedUpdate(
                                    DocumentEvent e
                            ) {
                                filtrar( campoBusca.getText() );
                            }
                        }
                );
        cabecalho.add( campoBusca );
        cabecalho.add( Box.createVerticalStrut(20) );
        return cabecalho;
    }

    // BOTÕES DO FILTRO

    private JButton criarBotaoFiltro(
            String texto
    ) {
        JButton botao = new JButton(texto);
        botao.setFont( new Font( "Segoe UI", Font.BOLD, 13 ) );
        botao.setFocusPainted(false);
        botao.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        botao.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( BORDA ), new EmptyBorder( 9, 18, 9, 18 ) ) );
        return botao;
    }

    // ATUALIZAR FILTROS

    private void atualizarBotoesFiltro() {
        if (mostrandoAtivos) {

            // ATIVADOS SELECIONADO

            botaoAtivados.setBackground( AZUL );
            botaoAtivados.setForeground( Color.WHITE );

            // DESATIVADOS

            botaoDesativados.setBackground(ThemeManager.getPainel());
            botaoDesativados.setForeground(ThemeManager.getTexto());
        } else {

            // ATIVADOS

            botaoAtivados.setBackground(ThemeManager.getPainel());
            botaoAtivados.setForeground(ThemeManager.getTexto());

            // DESATIVADOS SELECIONADO

            botaoDesativados.setBackground( AZUL );
            botaoDesativados.setForeground( Color.WHITE );
        }
    }

    // LISTA

    private JScrollPane criarListaAlunos() {
        painelAlunos = new JPanel();
        painelAlunos.setLayout( new BoxLayout( painelAlunos, BoxLayout.Y_AXIS ) );
        painelAlunos.setBackground(ThemeManager.getFundo());
        painelAlunos.setBorder( new EmptyBorder( 5, 0, 20, 0 ) );
        JScrollPane scroll = new JScrollPane( painelAlunos );
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        scroll.getVerticalScrollBar() .setUnitIncrement(16);
        return scroll;
    }

    // CARREGAR ALUNOS

    private void carregarAlunos() {

        // FORMATURA AUTOMÁTICA Antes de exibir a lista, verifica se algum aluno ativo já ultrapassou o ano de conclusão previsto (ex.: estava no 3º ano e o ano virou) e o desativa.

        alunoService.desativarAlunosFormados();
        painelAlunos.removeAll();
        List<Aluno> alunos = alunoService.listarAlunosPorStatus( mostrandoAtivos );
        if (alunos.isEmpty()) {
            JLabel vazio = new JLabel( mostrandoAtivos ? "Nenhum aluno ativado." : "Nenhum aluno desativado." );
            vazio.setFont( new Font( "Segoe UI", Font.PLAIN, 15 ) );
            vazio.setForeground(ThemeManager.getTextoSecundario());
            vazio.setAlignmentX( Component.CENTER_ALIGNMENT );
            painelAlunos.add( Box.createVerticalStrut(30) );
            painelAlunos.add( vazio );
        } else {
            for (Aluno aluno : alunos) {
                painelAlunos.add( criarCardAluno(aluno) );
                painelAlunos.add( Box.createVerticalStrut(15) );
            }
        }
        painelAlunos.revalidate();
        painelAlunos.repaint();
        SwingUtilities.invokeLater(() -> ThemeManager.aplicarTema(AlunoPage.this));
    }

    // FILTRO DE PESQUISA

    private void filtrar(
            String texto
    ) {
        painelAlunos.removeAll();
        String busca = texto .trim() .toLowerCase();
        List<Aluno> alunos = alunoService.listarAlunosPorStatus( mostrandoAtivos );
        int encontrados = 0;
        for (Aluno aluno : alunos) {
            String nome = aluno.getNome() == null ? "" : aluno.getNome() .toLowerCase();
            String rm = aluno.getRm() == null ? "" : aluno.getRm() .toLowerCase();
            boolean corresponde = nome.contains(busca) || rm.contains(busca);
            if (corresponde) {
                painelAlunos.add( criarCardAluno(aluno) );
                painelAlunos.add( Box.createVerticalStrut(15) );
                encontrados++;
            }
        }
        if (encontrados == 0) {
            JLabel vazio = new JLabel( mostrandoAtivos ? "Nenhum aluno ativado encontrado." : "Nenhum aluno desativado encontrado." );
            vazio.setFont( new Font( "Segoe UI", Font.PLAIN, 15 ) );
            vazio.setForeground(ThemeManager.getTextoSecundario());
            vazio.setAlignmentX( Component.CENTER_ALIGNMENT );
            painelAlunos.add( Box.createVerticalStrut(30) );
            painelAlunos.add( vazio );
        }
        painelAlunos.revalidate();
        painelAlunos.repaint();
        SwingUtilities.invokeLater(() -> ThemeManager.aplicarTema(AlunoPage.this));
    }

    // CARD DO ALUNO

    private JPanel criarCardAluno(
            Aluno aluno
    ) {
        JPanel card = new JPanel( new BorderLayout( 20, 0 ) );
        card.setBackground(ThemeManager.getPainel());
        card.setMaximumSize( new Dimension( Integer.MAX_VALUE, 150 ) );
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder( new Color( 226, 232, 240 ) ), new EmptyBorder( 18, 22, 18, 22 ) ) );

        // INFORMAÇÕES

        JPanel informacoes = new JPanel();
        informacoes.setLayout( new BoxLayout( informacoes, BoxLayout.Y_AXIS ) );
        informacoes.setBackground(ThemeManager.getPainel());
        JLabel nome = new JLabel( valor( aluno.getNome() ) );
        nome.setFont( new Font( "Segoe UI", Font.BOLD, 18 ) );
        nome.setForeground(ThemeManager.getTexto());
        informacoes.add(nome);
        informacoes.add( Box.createVerticalStrut(8) );
        JLabel dados =
                new JLabel(
                        "RM: "
                                + valor(
                                aluno.getRm() ) + "   •   Curso: " + valor(aluno.getCurso()) + "   •   Série: " + textoSerie(aluno.getSerie()) );
        dados.setFont( new Font( "Segoe UI", Font.PLAIN, 14 ) );
        dados.setForeground( new Color( 71, 85, 105 ) );
        informacoes.add( dados );
        informacoes.add( Box.createVerticalStrut(6) );
        JLabel curso = new JLabel( "Curso: " + valor( aluno.getCurso() ) );
        curso.setFont( new Font( "Segoe UI", Font.PLAIN, 14 ) );
        curso.setForeground( new Color( 71, 85, 105 ) );
        informacoes.add( curso );
        informacoes.add( Box.createVerticalStrut(6) );
        JLabel serieInfo =
                new JLabel(
                        "Série: "
                                + textoSerie(aluno.getSerie())
                                + "   •   Conclusão prevista: "
                                + ( aluno.getAnoConclusao() != null ? aluno.getAnoConclusao().toString() : "Não informado" ) );
        serieInfo.setFont( new Font( "Segoe UI", Font.PLAIN, 14 ) );
        serieInfo.setForeground( new Color( 71, 85, 105 ) );
        informacoes.add( serieInfo );

        // LINHA DE PRESENÇA (segue a regra do diagrama: presença é registrada por ALUNO via InscricaoEvento / ParticipacaoAtividade, agregada pelo Relatorio)

        informacoes.add( Box.createVerticalStrut(10) );
        informacoes.add( criarLinhaPresenca(aluno) );
        card.add( informacoes, BorderLayout.CENTER );

        // LATERAL

        JPanel lateral = new JPanel();
        lateral.setLayout( new BoxLayout( lateral, BoxLayout.Y_AXIS ) );
        lateral.setBackground(ThemeManager.getPainel());
        JLabel status = new JLabel( aluno.isAtivo() ? "ATIVO" : "INATIVO" );
        status.setFont( new Font( "Segoe UI", Font.BOLD, 12 ) );
        status.setOpaque(true);
        status.setBorder( new EmptyBorder( 6, 10, 6, 10 ) );
        if (aluno.isAtivo()) {
            status.setForeground( new Color( 22, 101, 52 ) );
            status.setBackground( new Color( 220, 252, 231 ) );
        } else {
            status.setForeground( new Color( 127, 29, 29 ) );
            status.setBackground( new Color( 254, 226, 226 ) );
        }
        status.setAlignmentX( Component.RIGHT_ALIGNMENT );
        lateral.add( status );

        // VER PRESENÇA

        lateral.add( Box.createVerticalStrut(10) );
        JButton verPresenca = criarBotaoSecundarioPequeno( "Ver Presença" );
        verPresenca.setAlignmentX( Component.RIGHT_ALIGNMENT );
        verPresenca.addActionListener(
                e -> abrirHistoricoPresenca(aluno) );
        lateral.add( verPresenca );

        // REMOVER

        if (aluno.isAtivo()) {
            lateral.add( Box.createVerticalStrut(10) );
            JButton remover = criarBotaoRemover();
            remover.setAlignmentX( Component.RIGHT_ALIGNMENT );
            remover.addActionListener(
                    e -> removerAluno(aluno) );
            lateral.add( remover );
        }
        card.add( lateral, BorderLayout.EAST );
        return card;
    }

    // LINHA DE PRESENÇA (selo dentro do card)

    private JPanel criarLinhaPresenca(
            Aluno aluno
    ) {
        JPanel linha = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
        linha.setBackground(ThemeManager.getPainel());

        // TODO: quando o RelatorioService existir, trocar por: double percentual = relatorioService.calcularPercentualPresencaAluno( aluno, eventoAtualOuFiltro ); e formatar o texto com esse valor real.

        String textoPresenca = "Presença: —%";
        JLabel selo = new JLabel( textoPresenca );
        selo.setFont( new Font( "Segoe UI", Font.BOLD, 12 ) );
        selo.setOpaque(true);
        selo.setForeground( ROXO_PRESENCA_TEXTO );
        selo.setBackground( ROXO_PRESENCA_FUNDO );
        selo.setBorder( new EmptyBorder( 5, 10, 5, 10 ) );
        linha.add( selo );
        return linha;
    }

    // HISTÓRICO DE PRESENÇA (diálogo)

    private void abrirHistoricoPresenca(
            Aluno aluno
    ) {
        JDialog dialog =
                new JDialog(
                        SwingUtilities .getWindowAncestor(this), "Presença de " + valor(aluno.getNome()), Dialog.ModalityType.APPLICATION_MODAL );
        dialog.setSize( 520, 480 );
        dialog.setLocationRelativeTo( this );
        JPanel principal = new JPanel( new BorderLayout() );
        principal.setBackground(ThemeManager.getFundo());
        principal.setBorder( new EmptyBorder( 25, 25, 25, 25 ) );
        JLabel titulo = new JLabel( "Histórico de presença" );
        titulo.setFont( new Font( "Segoe UI", Font.BOLD, 20 ) );
        titulo.setForeground(ThemeManager.getTexto());
        principal.add( titulo, BorderLayout.NORTH );

        // LISTA (placeholder até existir RelatorioService)

        // TODO: substituir este placeholder por: List<InscricaoEvento> inscricoes = relatorioService.listarPresencaAluno(aluno); e por: List<ParticipacaoAtividade> participacoes = relatorioService.listarPresencaAtividadesAluno(aluno); Cada linha deve mostrar: evento/atividade, data e o status de presença (PRESENTE, AUSENTE, JUSTIFICADO), exatamente os enums do diagrama (StatusPresenca).

        JPanel corpo = new JPanel();
        corpo.setLayout( new BoxLayout( corpo, BoxLayout.Y_AXIS ) );
        corpo.setBackground(ThemeManager.getPainel());
        corpo.setBorder(
                BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( new Color(226, 232, 240) ), new EmptyBorder(20, 20, 20, 20) ) );
        JLabel aviso =
                new JLabel(
                        "<html>Ainda não há relatório de presença "
                                + "conectado.<br><br>Assim que o "
                                + "<b>RelatorioService</b> (baseado em "
                                + "InscricaoEvento e ParticipacaoAtividade) "
                                + "estiver pronto, este painel vai listar "
                                + "aqui os eventos e atividades do aluno " + "com o status PRESENTE / AUSENTE / " + "JUSTIFICADO.</html>" );
        aviso.setFont( new Font( "Segoe UI", Font.PLAIN, 14 ) );
        aviso.setForeground(ThemeManager.getTextoSecundario());
        corpo.add( aviso );
        JScrollPane scroll = new JScrollPane( corpo );
        scroll.setBorder(null);
        principal.add( scroll, BorderLayout.CENTER );

        // FECHAR

        JPanel botoes = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        botoes.setBackground(ThemeManager.getFundo());
        JButton fechar = criarBotaoSecundario( "Fechar" );
        fechar.addActionListener(
                e -> dialog.dispose() );
        botoes.add( fechar );
        principal.add( botoes, BorderLayout.SOUTH );
        dialog.setContentPane( principal );
        ThemeManager.aplicarTema(dialog);
        dialog.setVisible(true);
    }

    // REMOVER ALUNO

    private void removerAluno(
            Aluno aluno
    ) {
        int confirmacao =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente remover o aluno\n"
                                + aluno.getNome() + "?", "Confirmar remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
        if (
                confirmacao
                        != JOptionPane.YES_OPTION
        ) {
            return;
        }
        try {
            boolean sucesso = alunoService.desativarAluno( aluno.getId() );
            if (sucesso) {
                JOptionPane.showMessageDialog( this, "Aluno removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE );
                carregarAlunos();
            } else {
                JOptionPane.showMessageDialog( this, "Não foi possível remover o aluno.", "Erro", JOptionPane.ERROR_MESSAGE );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog( this, "Ocorreu um erro ao remover o aluno.", "Erro", JOptionPane.ERROR_MESSAGE );
        }
    }

    // NOVO ALUNO

    private void abrirNovoAluno() {
        JDialog dialog = new JDialog( SwingUtilities .getWindowAncestor(this), "Novo Aluno", Dialog.ModalityType.APPLICATION_MODAL );
        dialog.setSize( 650, 700 );
        dialog.setLocationRelativeTo( this );
        dialog.setResizable(false);
        JPanel principal = new JPanel( new BorderLayout() );
        principal.setBackground(ThemeManager.getFundo());
        principal.setBorder( new EmptyBorder( 25, 30, 25, 30 ) );

        // CABEÇALHO

        JPanel cabecalho = new JPanel();
        cabecalho.setLayout( new BoxLayout( cabecalho, BoxLayout.Y_AXIS ) );
        cabecalho.setBackground(ThemeManager.getFundo());
        JLabel titulo = new JLabel( "Cadastrar aluno" );
        titulo.setFont( new Font( "Segoe UI", Font.BOLD, 24 ) );
        titulo.setForeground(ThemeManager.getTexto());
        JLabel descricao = new JLabel( "Preencha as informações do aluno." );
        descricao.setFont( new Font( "Segoe UI", Font.PLAIN, 14 ) );
        descricao.setForeground(ThemeManager.getTextoSecundario());
        cabecalho.add( titulo );
        cabecalho.add( Box.createVerticalStrut(5) );
        cabecalho.add( descricao );
        principal.add( cabecalho, BorderLayout.NORTH );

        // FORMULÁRIO

        JPanel formulario = new JPanel( new GridBagLayout() );
        formulario.setBackground(ThemeManager.getPainel());
        formulario.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder( new Color( 226, 232, 240 ) ), new EmptyBorder( 20, 20, 20, 20 ) ) );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets( 7, 7, 7, 7 );
        gbc.weightx = 1.0;

        // RM

        JTextField campoRm = new JTextField();
        campoRm.putClientProperty( "JTextField.placeholderText", "Ex.: 12345" );
        estilizarCampo( campoRm );
        adicionarCampo( formulario, gbc, 0, "RM", campoRm, 0 );

        // NOME

        JTextField campoNome = new JTextField();
        estilizarCampo( campoNome );
        adicionarCampo( formulario, gbc, 0, "Nome completo", campoNome, 1 );

        // DATA

        JFormattedTextField campoData = criarCampoData();
        adicionarCampo( formulario, gbc, 1, "Data de nascimento", campoData, 0 );

        // CURSO

        JComboBox<String> campoCurso = new JComboBox<>();
        campoCurso.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoCurso.setPreferredSize(new Dimension(240, 38));
        for (String curso : cursoDAO.listarNomesAtivos()) {
            campoCurso.addItem(curso);
        }
        adicionarCampo( formulario, gbc, 1, "Curso", campoCurso, 1 );

        // EMAIL

        JTextField campoEmail = new JTextField();
        estilizarCampo( campoEmail );
        adicionarCampo( formulario, gbc, 2, "E-mail", campoEmail, 1 );

        // TELEFONE

        JTextField campoTelefone = new JTextField();
        campoTelefone.putClientProperty( "JTextField.placeholderText", "(11) 99999-9999" );
        estilizarCampo( campoTelefone );
        adicionarCampo( formulario, gbc, 2, "Telefone", campoTelefone, 0 );

        // SÉRIE — carregada do banco conforme o curso

        JComboBox<String> campoSerie = new JComboBox<>();
        campoSerie.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoSerie.setPreferredSize(new Dimension(240, 38));
        Runnable atualizarSeries = () -> {
            String cursoSelecionado = (String) campoCurso.getSelectedItem();
            campoSerie.removeAllItems();
            if (cursoSelecionado != null) {
                for (String serie : serieDAO.listarNomesAtivosPorCurso(cursoSelecionado)) {
                    campoSerie.addItem(serie);
                }
            }
        };
        campoCurso.addActionListener(e -> atualizarSeries.run());
        atualizarSeries.run();
        adicionarCampo( formulario, gbc, 3, "Série", campoSerie, 1 );
        principal.add( formulario, BorderLayout.CENTER );

        // BOTÕES

        JPanel botoes = new JPanel( new FlowLayout( FlowLayout.RIGHT, 10, 0 ) );
        botoes.setBackground(ThemeManager.getFundo());
        JButton cancelar = criarBotaoSecundario( "Cancelar" );
        cancelar.addActionListener(
                e -> dialog.dispose() );
        JButton salvar = criarBotaoPrincipal( "Cadastrar aluno" );
        salvar.addActionListener(
                e -> {
                    String rm = campoRm .getText() .trim();
                    String nome = campoNome .getText() .trim();

                    // VALIDA RM

                    if (rm.isEmpty()) {
                        JOptionPane.showMessageDialog( dialog, "Informe o RM do aluno.", "Campo obrigatório", JOptionPane.WARNING_MESSAGE );
                        campoRm.requestFocus();
                        return;
                    }

                    // VALIDA NOME

                    if (nome.isEmpty()) {
                        JOptionPane.showMessageDialog( dialog, "Informe o nome do aluno.", "Campo obrigatório", JOptionPane.WARNING_MESSAGE );
                        campoNome.requestFocus();
                        return;
                    }

                    // VALIDA SÉRIE

                    String cursoSelecionado = (String) campoCurso.getSelectedItem();
                    String serieSelecionadaNome = (String) campoSerie.getSelectedItem();
                    if (cursoSelecionado == null || cursoSelecionado.isBlank()) {
                        JOptionPane.showMessageDialog(dialog, "Selecione o curso do aluno.", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Integer serieSelecionada = serieDAO.buscarNumeroPorCursoENome(cursoSelecionado, serieSelecionadaNome);
                    if (serieSelecionada == null) {
                        JOptionPane.showMessageDialog(dialog, "Selecione uma série válida para o curso escolhido.", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    try {
                        Aluno aluno = new Aluno();

                        // SÉRIE E ANO DE CONCLUSÃO

                        aluno.setSerie( serieSelecionada );
                        aluno.setAnoConclusao( alunoService.calcularAnoConclusao( serieSelecionada ) );
                        aluno.setRm( rm );
                        aluno.setNome( nome );

                        // DATA

                        String data = campoData .getText() .replace( "_", "" ) .trim();
                        if (!data.isEmpty()) {
                            LocalDate dataConvertida = LocalDate.parse( data, DateTimeFormatter .ofPattern( "dd/MM/yyyy" ) );
                            aluno.setDataNascimento( dataConvertida.toString() );
                        }

                        // CURSO

                        aluno.setCurso(cursoSelecionado);

                        // EMAIL

                        aluno.setEmail( campoEmail .getText() .trim() );

                        // TELEFONE

                        aluno.setTelefone( campoTelefone .getText() .trim() );

                        // BANCO

                        boolean sucesso = alunoService .cadastrarAluno( aluno );
                        if (!sucesso) {
                            JOptionPane.showMessageDialog( dialog, "Não foi possível cadastrar o aluno.", "Erro", JOptionPane.ERROR_MESSAGE );
                            return;
                        }
                        JOptionPane.showMessageDialog( dialog, "Aluno cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE );
                        dialog.dispose();
                        carregarAlunos();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(
                                dialog, "Verifique a data de nascimento.\n\n" + "Formato: dd/MM/yyyy", "Data inválida", JOptionPane.WARNING_MESSAGE );
                    }
                });
        botoes.add( cancelar );
        botoes.add( salvar );
        principal.add( botoes, BorderLayout.SOUTH );
        dialog.setContentPane( principal );
        ThemeManager.aplicarTema(dialog);
        dialog.setVisible(true);
    }

    // IMPORTAÇÃO DE ALUNOS POR CSV

    private void abrirImportadorCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecionar arquivo CSV de alunos");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(true);
        int retorno = chooser.showOpenDialog(this);
        if (retorno != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File arquivo = chooser.getSelectedFile();
        if (arquivo == null || !arquivo.getName().toLowerCase().endsWith(".csv")) {
            JOptionPane.showMessageDialog( this, "Selecione um arquivo no formato .csv.", "Arquivo inválido", JOptionPane.WARNING_MESSAGE );
            return;
        }
        try {
            AlunoCsvImportService importService = new AlunoCsvImportService();
            List<AlunoCsvImportacao> linhas = importService.analisar(arquivo);
            if (linhas.isEmpty()) {
                JOptionPane.showMessageDialog( this, "O CSV não possui alunos para importar.", "Arquivo vazio", JOptionPane.INFORMATION_MESSAGE );
                return;
            }
            abrirPreviewImportacaoCsv(arquivo, linhas, importService);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage() != null
                            ? ex.getMessage() : "Não foi possível ler o arquivo CSV.", "Erro ao importar CSV", JOptionPane.ERROR_MESSAGE );
        }
    }

    private void abrirPreviewImportacaoCsv(
            File arquivo,
            List<AlunoCsvImportacao> linhas,
            AlunoCsvImportService importService
    ) {
        JDialog dialog = new JDialog( SwingUtilities.getWindowAncestor(this), "Importar alunos por CSV", Dialog.ModalityType.APPLICATION_MODAL );
        dialog.setSize(980, 650);
        dialog.setMinimumSize(new Dimension(850, 560));
        dialog.setLocationRelativeTo(this);
        JPanel principal = new JPanel(new BorderLayout(0, 18));
        principal.setBackground(ThemeManager.getFundo());
        principal.setBorder(new EmptyBorder(22, 24, 22, 24));
        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setBackground(ThemeManager.getFundo());
        JLabel titulo = new JLabel("Pré-visualização da importação");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(ThemeManager.getTexto());
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel arquivoLabel = new JLabel("Arquivo: " + arquivo.getName());
        arquivoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        arquivoLabel.setForeground(ThemeManager.getTextoSecundario());
        arquivoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel dica = new JLabel( "Colunas obrigatórias: RM, Nome, Curso e Série. " + "Data de Nascimento, E-mail e Telefone são opcionais." );
        dica.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dica.setForeground(ThemeManager.getTextoSecundario());
        dica.setAlignmentX(Component.LEFT_ALIGNMENT);
        topo.add(titulo);
        topo.add(Box.createVerticalStrut(5));
        topo.add(arquivoLabel);
        topo.add(Box.createVerticalStrut(5));
        topo.add(dica);
        principal.add(topo, BorderLayout.NORTH);
        String[] colunas = {
                "Linha",
                "RM",
                "Nome",
                "Curso",
                "Série",
                "Situação"
        };
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        int validos = 0;
        int invalidos = 0;
        for (AlunoCsvImportacao linha : linhas) {
            Aluno aluno = linha.getAluno();
            if (linha.isValido()) validos++;
            else invalidos++;
            modelo.addRow(new Object[]{
                    linha.getLinha(),
                    aluno.getRm(),
                    aluno.getNome(),
                    aluno.getCurso(),
                    aluno.getSerie() != null ? aluno.getSerie() + "ª" : "-",
                    linha.getSituacao()
            });
        }
        JTable tabela = new JTable(modelo);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(30);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final Color VERMELHO_TEXTO = new Color(185, 28, 28);
        final Color VERDE_TEXTO = new Color(22, 101, 52);
        DefaultTableCellRenderer rendererSituacao = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                Component c = super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
                if (!isSelected) {
                    boolean valido = linhas.get(row).isValido();
                    c.setForeground(valido ? VERDE_TEXTO : VERMELHO_TEXTO);
                }
                return c;
            }
        };
        tabela.getColumnModel().getColumn(5).setCellRenderer(rendererSituacao);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(85);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(210);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA));
        principal.add(scroll, BorderLayout.CENTER);
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(ThemeManager.getFundo());
        JLabel resumo = new JLabel( linhas.size() + " linha(s) • " + validos + " pronta(s) para importar • " + invalidos + " ignorada(s)" );
        resumo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resumo.setForeground(ThemeManager.getTextoSecundario());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setBackground(ThemeManager.getFundo());
        JButton cancelar = criarBotaoSecundario("Cancelar");
        cancelar.addActionListener(e -> dialog.dispose());
        JButton importar = criarBotaoPrincipal("Importar alunos");
        importar.setEnabled(validos > 0);
        final int totalValidos = validos;
        importar.addActionListener(e -> {
            int confirmacao = JOptionPane.showConfirmDialog(
                    dialog,
                    "Serão cadastrados " + totalValidos + " aluno(s).\n\nDeseja continuar?",
                    "Confirmar importação", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
            if (confirmacao != JOptionPane.YES_OPTION) {
                return;
            }
            importar.setEnabled(false);
            importar.setText("Importando...");
            try {
                int importados = importService.importar(linhas);
                JOptionPane.showMessageDialog(
                        dialog,
                        importados + " aluno(s) cadastrado(s) com sucesso."
                                + (importados < totalValidos
                                ? "\n\nAlguns registros não puderam ser gravados."
                                : ""),
                        "Importação concluída", importados == totalValidos ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE );
                dialog.dispose();
                mostrandoAtivos = true;
                atualizarBotoesFiltro();
                carregarAlunos();
            } catch (Exception ex) {
                ex.printStackTrace();
                importar.setEnabled(true);
                importar.setText("Importar alunos");
                JOptionPane.showMessageDialog(
                        dialog, "Ocorreu um erro durante a importação.\n\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE );
            }
        });
        botoes.add(cancelar);
        botoes.add(importar);
        rodape.add(resumo, BorderLayout.WEST);
        rodape.add(botoes, BorderLayout.EAST);
        principal.add(rodape, BorderLayout.SOUTH);
        dialog.setContentPane(principal);
        ThemeManager.aplicarTema(dialog);
        dialog.setVisible(true);
    }

    // CAMPO DATA

    private JFormattedTextField criarCampoData() {
        try {
            MaskFormatter mascara = new MaskFormatter( "##/##/####" );
            mascara.setPlaceholderCharacter( '_' );
            JFormattedTextField campo = new JFormattedTextField( mascara );
            campo.setFont( new Font( "Segoe UI", Font.PLAIN, 14 ) );
            campo.setPreferredSize( new Dimension( 0, 40 ) );
            campo.setMaximumSize( new Dimension( Integer.MAX_VALUE, 40 ) );
            campo.setToolTipText( "Formato: dd/mm/aaaa" );
            campo.setFocusLostBehavior( JFormattedTextField.PERSIST );
            campo.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( BORDA ), new EmptyBorder( 8, 10, 8, 10 ) ) );
            return campo;
        } catch (ParseException e) {
            throw new RuntimeException( "Erro ao criar campo de data.", e );
        }
    }

    // ADICIONAR CAMPO

    private void adicionarCampo(
            JPanel painel,
            GridBagConstraints gbc,
            int linha,
            String titulo,
            Component campo,
            int coluna
    ) {
        gbc.gridx = coluna;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JPanel container = new JPanel();
        container.setLayout( new BoxLayout( container, BoxLayout.Y_AXIS ) );
        container.setBackground(ThemeManager.getPainel());
        JLabel label = new JLabel( titulo );
        label.setFont( new Font( "Segoe UI", Font.BOLD, 12 ) );
        label.setForeground(ThemeManager.getTexto());
        container.add( label );
        container.add( Box.createVerticalStrut(5) );
        campo.setMaximumSize( new Dimension( Integer.MAX_VALUE, 40 ) );
        container.add( campo );
        painel.add( container, gbc );
    }

    // ESTILIZAR CAMPO

    private void estilizarCampo(
            JTextField campo
    ) {
        campo.setFont( new Font( "Segoe UI", Font.PLAIN, 14 ) );
        campo.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( BORDA ), new EmptyBorder( 8, 10, 8, 10 ) ) );
        campo.setPreferredSize( new Dimension( 0, 40 ) );
        campo.setMaximumSize( new Dimension( Integer.MAX_VALUE, 40 ) );
    }

    // BOTÃO PRINCIPAL

    private JButton criarBotaoPrincipal(
            String texto
    ) {
        JButton botao = new JButton(texto);
        botao.setFont( new Font( "Segoe UI", Font.BOLD, 14 ) );
        botao.setForeground( Color.WHITE );
        botao.setBackground( AZUL );
        botao.setFocusPainted( false );
        botao.setBorderPainted( false );
        botao.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        botao.setBorder( new EmptyBorder( 11, 18, 11, 18 ) );
        botao.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {
                        botao.setBackground( AZUL_HOVER );
                    }
                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {
                        botao.setBackground( AZUL );
                    }
                }
        );
        return botao;
    }

    // BOTÃO SECUNDÁRIO

    private JButton criarBotaoSecundario(
            String texto
    ) {
        JButton botao = new JButton(texto);
        botao.setFont( new Font( "Segoe UI", Font.BOLD, 13 ) );
        botao.setForeground(ThemeManager.getTexto());
        botao.setBackground(ThemeManager.getPainel());
        botao.setFocusPainted( false );
        botao.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( BORDA ), new EmptyBorder( 9, 15, 9, 15 ) ) );
        botao.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        return botao;
    }

    // BOTÃO SECUNDÁRIO PEQUENO (usado no card, ex.: Ver Presença)

    private JButton criarBotaoSecundarioPequeno(
            String texto
    ) {
        JButton botao = new JButton(texto);
        botao.setFont( new Font( "Segoe UI", Font.BOLD, 11 ) );
        botao.setForeground( AZUL );
        botao.setBackground(ThemeManager.getPainel());
        botao.setFocusPainted( false );
        botao.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        botao.setBorder(
                BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( new Color(191, 219, 254) ), new EmptyBorder( 5, 10, 5, 10 ) ) );
        return botao;
    }

    // BOTÃO REMOVER

    private JButton criarBotaoRemover() {
        JButton botao = new JButton( "Remover" );
        botao.setFont( new Font( "Segoe UI", Font.BOLD, 12 ) );
        botao.setForeground( new Color( 185, 28, 28 ) );
        botao.setBackground(ThemeManager.getPainel());
        botao.setFocusPainted( false );
        botao.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        botao.setBorder(
                BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( new Color( 252, 165, 165 ) ), new EmptyBorder( 6, 10, 6, 10 ) ) );
        return botao;
    }

    // TEXTO DA SÉRIE

    private String textoSerie(
            Integer serie
    ) {
        if (serie == null) {
            return "Não informado";
        }
        return switch (serie) {
            case 1 -> "1º Ano";
            case 2 -> "2º Ano";
            case 3 -> "3º Ano";
            default -> "Não informado";
        };
    }

    // VALOR

    private String valor(
            String texto
    ) {
        if (
                texto == null
                        || texto.isBlank()
        ) {
            return "Não informado";
        }
        return texto;
    }
}
