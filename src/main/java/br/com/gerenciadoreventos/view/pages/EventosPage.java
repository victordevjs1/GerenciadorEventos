package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.dao.EventoDetalhesDAO;
import br.com.gerenciadoreventos.model.AgenteExterno;
import br.com.gerenciadoreventos.model.Aluno;
import br.com.gerenciadoreventos.model.Comissao;
import br.com.gerenciadoreventos.model.Evento;
import br.com.gerenciadoreventos.model.EventoPublico;
import br.com.gerenciadoreventos.model.Professor;
import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.service.EventoService;
import br.com.gerenciadoreventos.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
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

    // CORES

    private static final Color FUNDO = new Color(246, 248, 252);

    private static final Color AZUL = new Color(37, 99, 235);

    private static final Color AZUL_HOVER = new Color(29, 78, 216);

    private static final Color TEXTO = new Color(15, 23, 42);

    private static final Color CINZA_TEXTO = new Color(100, 116, 139);

    private static final Color BORDA = new Color(203, 213, 225);

    // DADOS

    private final Usuario usuarioLogado;
    private final EventoService eventoService;
    private final EventoDetalhesDAO detalhesDAO = new EventoDetalhesDAO();

    private JPanel painelEventos;

    private JTextField campoBusca;

    private JComboBox<String> filtroCurso;
    private JComboBox<String> filtroSerie;
    private JComboBox<String> filtroStatus;

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    // CONSTRUTOR

    public EventosPage(
            Usuario usuarioLogado
    ) {
        this.usuarioLogado = usuarioLogado;
        this.eventoService = new EventoService();
        inicializarInterface();
        carregarEventos();
    }

    // INTERFACE

    private void inicializarInterface() {
        setLayout( new BorderLayout() );
        setBackground(FUNDO);
        add( criarConteudo(), BorderLayout.CENTER );
    }

    private JPanel criarConteudo() {
        JPanel painel = new JPanel( new BorderLayout() );
        painel.setBackground(ThemeManager.getFundo());
        painel.setBorder( BorderFactory.createEmptyBorder( 25, 25, 25, 25 ) );
        painel.add( criarCabecalho(), BorderLayout.NORTH );
        painel.add( criarListaEventos(), BorderLayout.CENTER );
        return painel;
    }

    // CABEÇALHO

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout( new BoxLayout( cabecalho, BoxLayout.Y_AXIS ) );
        cabecalho.setBackground(ThemeManager.getFundo());
        cabecalho.add( criarLinhaSuperior() );
        cabecalho.add( Box.createVerticalStrut(20) );
        cabecalho.add( criarFiltros() );
        cabecalho.add( Box.createVerticalStrut(20) );
        return cabecalho;
    }

    private JPanel criarLinhaSuperior() {
        JPanel linha = new JPanel( new BorderLayout() );
        linha.setBackground(ThemeManager.getFundo());
        JPanel textos = new JPanel();
        textos.setLayout( new BoxLayout( textos, BoxLayout.Y_AXIS ) );
        textos.setBackground(ThemeManager.getFundo());
        textos.add( criarLabel( "Eventos", 28, Font.BOLD, TEXTO ) );
        textos.add( Box.createVerticalStrut(4) );
        textos.add( criarLabel( "Gerencie os eventos da escola", 14, Font.PLAIN, CINZA_TEXTO ) );
        JButton novo = criarBotaoPrincipal( "+ Novo Evento" );
        novo.addActionListener(
                e -> abrirNovoEvento()
        );
        linha.add( textos, BorderLayout.WEST );
        linha.add( novo, BorderLayout.EAST );
        return linha;
    }

    // FILTROS

    private JPanel criarFiltros() {
        JPanel painel = new JPanel( new GridLayout( 1, 4, 10, 0 ) );
        painel.setBackground(ThemeManager.getFundo());
        campoBusca = new JTextField();
        campoBusca.putClientProperty( "JTextField.placeholderText", "Pesquisar evento..." );
        estilizarCampo(campoBusca);
        painel.add( campoBusca );
        filtroCurso = new JComboBox<>();
        filtroCurso.addItem( "Todos os cursos" );
        for (
                String curso :
                eventoService.listarCursos()
        ) {
            filtroCurso.addItem(curso);
        }
        estilizarComboBox( filtroCurso );
        painel.add( filtroCurso );
        filtroSerie =
                new JComboBox<>(
                        new String[]{
                                "Todas as séries",
                                "1º Ano",
                                "2º Ano",
                                "3º Ano"
                        }
                );
        estilizarComboBox( filtroSerie );
        painel.add( filtroSerie );
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
        estilizarComboBox( filtroStatus );
        painel.add( filtroStatus );
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

    // LISTA

    private JScrollPane criarListaEventos() {
        painelEventos = new JPanel();
        painelEventos.setLayout( new BoxLayout( painelEventos, BoxLayout.Y_AXIS ) );
        painelEventos.setBackground(ThemeManager.getFundo());
        painelEventos.setBorder( new EmptyBorder( 5, 0, 20, 0 ) );
        JScrollPane scroll = new JScrollPane( painelEventos );
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        scroll.getVerticalScrollBar() .setUnitIncrement(16);
        return scroll;
    }

    // EVENTOS

    private void carregarEventos() {
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        if (painelEventos == null) {
            return;
        }
        painelEventos.removeAll();
        List<Evento> eventos = eventoService.listarEventos();
        String busca = campoBusca != null ? campoBusca .getText() .trim() .toLowerCase() : "";
        String curso = filtroCurso != null ? filtroCurso .getSelectedItem() .toString() : "Todos os cursos";
        String serie = filtroSerie != null ? filtroSerie .getSelectedItem() .toString() : "Todas as séries";
        String status = filtroStatus != null ? filtroStatus .getSelectedItem() .toString() : "Todos os status";
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
            painelEventos.add( criarCardEvento( evento ) );
            painelEventos.add( Box.createVerticalStrut(15) );
            quantidade++;
        }
        if (quantidade == 0) {
            painelEventos.add( criarMensagemSemEventos() );
        }
        painelEventos.revalidate();
        painelEventos.repaint();
        SwingUtilities.invokeLater(() -> ThemeManager.aplicarTema(EventosPage.this));
    }

    // FILTRO BUSCA

    private boolean correspondeBusca(
            Evento evento,
            String busca
    ) {
        if (busca.isEmpty()) {
            return true;
        }
        String nome = evento.getNome() != null ? evento.getNome() .toLowerCase() : "";
        String descricao = evento.getDescricao() != null ? evento.getDescricao() .toLowerCase() : "";
        return nome.contains(busca) || descricao.contains(busca);
    }

    // FILTRO PÚBLICO

    private boolean correspondePublico(
            Evento evento,
            String cursoFiltro,
            String serieFiltro
    ) {

        // Evento para toda a escola sempre aparece.
        if (evento.isPublicoTodos()) {
            return true;
        }
        boolean filtraCurso = !"Todos os cursos" .equals(cursoFiltro);
        boolean filtraSerie = !"Todas as séries" .equals(serieFiltro);

        // Sem filtro específico.
        if (!filtraCurso && !filtraSerie) {
            return true;
        }
        for (
                EventoPublico publico :
                evento.getPublicos()
        ) {
            boolean cursoOk = !filtraCurso || publico .getCurso() .equals(cursoFiltro);
            boolean serieOk = !filtraSerie || publico.isCursoInteiro() || publico.getSerie().equals(serieFiltro);
            if (cursoOk && serieOk) {
                return true;
            }
        }
        return false;
    }

    // FILTRO STATUS

    private boolean correspondeStatus(
            Evento evento,
            String filtro
    ) {
        if ("Todos os status".equals(filtro)) {
            return true;
        }
        String status = eventoService.calcularStatus( evento );
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

    // CARD

    private JPanel criarCardEvento(
            Evento evento
    ) {
        JPanel card = new JPanel( new BorderLayout( 20, 0 ) );
        card.setBackground(ThemeManager.getPainel());
        card.setMaximumSize( new Dimension( Integer.MAX_VALUE, 205 ) );
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder( new Color( 226, 232, 240 ) ), new EmptyBorder( 20, 22, 20, 22 ) ) );
        JPanel informacoes = new JPanel();
        informacoes.setLayout( new BoxLayout( informacoes, BoxLayout.Y_AXIS ) );
        informacoes.setBackground(ThemeManager.getPainel());
        informacoes.add( criarLabel( evento.getNome(), 19, Font.BOLD, TEXTO ) );
        informacoes.add( Box.createVerticalStrut(10) );
        adicionarInformacoesData( informacoes, evento );
        informacoes.add( criarLabelInformacao( "Local: " + obterLocal(evento) ) );
        informacoes.add( Box.createVerticalStrut(5) );
        informacoes.add( criarLabelInformacao( "Público: " + obterPublico(evento) ) );
        informacoes.add( Box.createVerticalStrut(5) );
        informacoes.add( criarLabelInformacao( "Status: " + obterStatus(evento) ) );
        card.add( informacoes, BorderLayout.CENTER );
        JButton ver = criarBotaoSecundario( "Ver evento" );
        ver.addActionListener(
                e -> abrirDetalhesEvento(
                        evento ) );
        JPanel botao = new JPanel( new GridBagLayout() );
        botao.setBackground(ThemeManager.getPainel());
        botao.add(ver);
        card.add( botao, BorderLayout.EAST );
        return card;
    }

    // INFORMAÇÕES DATA

    private void adicionarInformacoesData(
            JPanel painel,
            Evento evento
    ) {
        if (evento.getDataInicio() == null) {
            return;
        }
        painel.add( criarLabelInformacao( "Data: " + evento .getDataInicio() .format(DATA) ) );
        painel.add( Box.createVerticalStrut(5) );
        String horario = evento.getDataInicio() .format(HORA);
        if (evento.getDataFim() != null) {
            horario += " - " + evento .getDataFim() .format(HORA);
        }
        painel.add( criarLabelInformacao( "Horário: " + horario ) );
        painel.add( Box.createVerticalStrut(5) );
    }

    // PÚBLICO

    private String obterPublico(
            Evento evento
    ) {
        if (evento.isPublicoTodos()) {
            return "Toda a escola";
        }
        List<EventoPublico> publicos = evento.getPublicos();
        if (
                publicos == null
                        || publicos.isEmpty()
        ) {
            return "Nenhum público definido";
        }
        if (publicos.size() == 1) {
            return publicos .get(0) .toString();
        }
        String primeiro = publicos .get(0) .toString();
        return primeiro + " + " + (publicos.size() - 1) + " outra(s)";
    }

    private String obterLocal(
            Evento evento
    ) {
        return evento.getLocal() != null && !evento.getLocal().isBlank() ? evento.getLocal() : "Não informado";
    }

    private String obterStatus(
            Evento evento
    ) {
        return eventoService.calcularStatus( evento );
    }

    // MENSAGEM

    private JPanel criarMensagemSemEventos() {
        JPanel painel = new JPanel( new GridBagLayout() );
        painel.setBackground(ThemeManager.getPainel());
        painel.setBorder( BorderFactory.createLineBorder( new Color( 226, 232, 240 ) ) );
        painel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 120 ) );
        painel.add( criarLabel( "Nenhum evento encontrado.", 15, Font.PLAIN, CINZA_TEXTO ) );
        return painel;
    }

    // NOVO / DETALHES

    private void abrirNovoEvento() {
        abrirEditorEvento(null);
    }

    private void abrirDetalhesEvento(
            Evento evento
    ) {
        Evento atualizado = eventoService.buscarPorId( evento.getId() );
        if (atualizado == null) {
            JOptionPane.showMessageDialog( this, "Não foi possível carregar o evento.", "Erro", JOptionPane.ERROR_MESSAGE );
            return;
        }
        abrirEditorEvento( atualizado );
    }

    // EDITOR

    private void abrirEditorEvento(
            Evento evento
    ) {
        boolean novo = evento == null;
        JDialog dialog =
                new JDialog( SwingUtilities .getWindowAncestor( this ), novo ? "Novo Evento" : "Evento", Dialog.ModalityType .APPLICATION_MODAL );
        dialog.setSize( 900, 820 );
        dialog.setLocationRelativeTo( this );
        dialog.setResizable(false);
        JPanel principal = new JPanel( new BorderLayout() );
        principal.setBackground(ThemeManager.getFundo());
        principal.setBorder( new EmptyBorder( 20, 25, 20, 25 ) );
        principal.add( criarCabecalhoEditor( evento, novo ), BorderLayout.NORTH );
        principal.add( criarAbasEditor( evento, novo, dialog ), BorderLayout.CENTER );
        principal.add( criarBotoesEditor( dialog ), BorderLayout.SOUTH );
        dialog.setContentPane( principal );
        ThemeManager.aplicarTema(dialog);
        dialog.setVisible(true);
    }

    private JPanel criarCabecalhoEditor(
            Evento evento,
            boolean novo
    ) {
        JPanel painel = new JPanel();
        painel.setLayout( new BoxLayout( painel, BoxLayout.Y_AXIS ) );
        painel.setBackground(ThemeManager.getFundo());
        painel.add( criarLabel( novo ? "Criar novo evento" : evento.getNome(), 24, Font.BOLD, TEXTO ) );
        painel.add( Box.createVerticalStrut(5) );
        painel.add(
                criarLabel(
                        novo
                                ? "Preencha as informações do evento."
                                : "Gerencie as informações e o público do evento.", 14, Font.PLAIN, CINZA_TEXTO ) );
        return painel;
    }

    private JTabbedPane criarAbasEditor(
            Evento evento,
            boolean novo,
            JDialog dialog
    ) {
        JTabbedPane abas = new JTabbedPane();
        abas.addTab( "Dados do evento", criarAbaDados(evento, dialog, novo) );
        if (!novo) {
            abas.addTab("Comissões", criarAbaComissoes(evento));
            abas.addTab("Professores", criarAbaProfessores(evento));
            abas.addTab("Alunos", criarAbaAlunos(evento));
            abas.addTab("Agentes externos", criarAbaAgentes(evento));
            abas.addTab("Atividades", criarAbaAtividades(evento));
            abas.addTab("Responsabilidades", criarAbaResponsabilidades(evento));
        }
        ThemeManager.aplicarTema(abas);
        return abas;
    }

    private String valor(String texto) { return texto == null ? "" : texto; }

    // ABAS FUNCIONAIS DO EVENTO

    private JPanel criarAbaComissoes(Evento evento) {
        String[] colunas = {"Nome", "Descrição", "Status"};
        JTable tabela = criarTabelaDetalhes(colunas);
        JPanel painel = criarPainelGerenciamento("Comissões do evento", tabela);
        JButton novo = criarBotaoPrincipal("+ Nova comissão");
        JButton editar = criarBotaoSecundario("Editar");
        JButton excluir = criarBotaoSecundario("Excluir");
        JPanel barra = barraAcoes(novo, editar, excluir);
        painel.add(barra, BorderLayout.SOUTH);
        final java.util.List<Comissao>[] dados = new java.util.List[]{java.util.List.of()};
        Runnable recarregar = () -> {
            dados[0] = detalhesDAO.listarComissoes(evento.getId());
            DefaultTableModel m = (DefaultTableModel) tabela.getModel();
            m.setRowCount(0);
            for (Comissao c : dados[0]) m.addRow(new Object[]{c.getNome(), valor(c.getDescricao()), c.isAtivo() ? "ATIVA" : "INATIVA"});
            ThemeManager.aplicarTema(painel);
        };
        novo.addActionListener(e -> { if (editarComissao(evento, null)) recarregar.run(); });
        editar.addActionListener(e -> {
            int i=tabela.getSelectedRow(); if(i<0){mensagem("Comissões","Selecione uma comissão para editar.");return;}
            if(editarComissao(evento,dados[0].get(i))) recarregar.run();
        });
        excluir.addActionListener(e -> {
            int i=tabela.getSelectedRow(); if(i<0){mensagem("Comissões","Selecione uma comissão para excluir.");return;}
            if(confirmar("Excluir comissão", "Excluir a comissão selecionada?")){detalhesDAO.excluirComissao(evento.getId(),dados[0].get(i).getIdComissao());recarregar.run();}
        });
        Timer timerStatus = new Timer(30_000, e -> {
            if (painel.isShowing()) recarregar.run();
        });
        painel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (painel.isShowing()) {
                    recarregar.run();
                    timerStatus.start();
                } else {
                    timerStatus.stop();
                }
            }
        });
        recarregar.run(); return painel;
    }

    private boolean editarComissao(Evento evento, Comissao atual) {
        JTextField nome = campoDialogo();
        JTextArea desc = areaDialogo();
        if(atual!=null){nome.setText(valor(atual.getNome()));desc.setText(valor(atual.getDescricao()));}
        JPanel p=formularioVertical();
        adicionarLinhaFormulario(p,"Nome *",nome);
        adicionarLinhaFormulario(p,"Descrição",new JScrollPane(desc));
        JLabel statusAutomatico = new JLabel("Status automático: ativa até o término do evento.");
        statusAutomatico.setForeground(ThemeManager.getTextoSecundario());
        p.add(statusAutomatico);
        ThemeManager.aplicarTema(p);
        if(JOptionPane.showConfirmDialog(this,p,atual==null?"Nova comissão":"Editar comissão",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)!=JOptionPane.OK_OPTION)return false;
        if(nome.getText().trim().isEmpty()){mensagem("Comissão","Informe o nome da comissão.");return false;}
        Comissao c=atual==null?new Comissao():atual;
        c.setIdEvento(evento.getId());
        c.setNome(nome.getText().trim());
        c.setDescricao(desc.getText().trim());
        detalhesDAO.salvarComissao(c);
        return true;
    }

    private JPanel criarAbaProfessores(Evento evento) {
        JTable tabela=criarTabelaDetalhes(new String[]{"Professor","Área de atuação","E-mail"}); JPanel painel=criarPainelGerenciamento("Professores participantes",tabela);
        JButton add=criarBotaoPrincipal("+ Adicionar professor"), remover=criarBotaoSecundario("Remover"); painel.add(barraAcoes(add,remover),BorderLayout.SOUTH);
        final java.util.List<Professor>[] dados=new java.util.List[]{java.util.List.of()};
        Runnable recarregar=()->{dados[0]=detalhesDAO.listarProfessoresVinculados(evento.getId());DefaultTableModel m=(DefaultTableModel)tabela.getModel();m.setRowCount(0);for(Professor p:dados[0])m.addRow(new Object[]{p.getNome(),valor(p.getAreaAtuacao()),valor(p.getEmail())});ThemeManager.aplicarTema(painel);};
        add.addActionListener(e->{java.util.List<Professor> op=detalhesDAO.listarProfessoresDisponiveis(evento.getId());Professor p=escolherProfessor(op);if(p!=null){int r=JOptionPane.showConfirmDialog(this,"Definir "+p.getNome()+" como professor principal?","Professor",JOptionPane.YES_NO_CANCEL_OPTION);if(r!=JOptionPane.CANCEL_OPTION&&r!=JOptionPane.CLOSED_OPTION){detalhesDAO.vincularProfessor(evento.getId(),p.getId(),r==JOptionPane.YES_OPTION);recarregar.run();}}});
        remover.addActionListener(e->{int i=tabela.getSelectedRow();if(i<0){mensagem("Professores","Selecione um professor.");return;}if(confirmar("Remover professor","Remover este professor do evento?")){detalhesDAO.removerProfessor(evento.getId(),dados[0].get(i).getId());recarregar.run();}});
        recarregar.run();return painel;
    }

    private JPanel criarAbaAlunos(Evento evento) {
        JTable tabela=criarTabelaDetalhes(new String[]{"RM","Aluno","Curso","Série"}); JPanel painel=criarPainelGerenciamento("Alunos inscritos",tabela);
        JButton add=criarBotaoPrincipal("+ Adicionar aluno"), cancelar=criarBotaoSecundario("Cancelar inscrição"); painel.add(barraAcoes(add,cancelar),BorderLayout.SOUTH);
        final java.util.List<Aluno>[] dados=new java.util.List[]{java.util.List.of()};
        Runnable recarregar=()->{dados[0]=detalhesDAO.listarAlunosInscritos(evento.getId());DefaultTableModel m=(DefaultTableModel)tabela.getModel();m.setRowCount(0);for(Aluno a:dados[0])m.addRow(new Object[]{a.getRm(),a.getNome(),valor(a.getCurso()),a.getSerie()==null?"":a.getSerie()+"º Ano"});ThemeManager.aplicarTema(painel);};
        add.addActionListener(e->{Aluno a=escolherAluno(detalhesDAO.listarAlunosDisponiveis(evento.getId()));if(a!=null){detalhesDAO.inscreverAluno(evento.getId(),a.getId());recarregar.run();}});
        cancelar.addActionListener(e->{int i=tabela.getSelectedRow();if(i<0){mensagem("Alunos","Selecione um aluno.");return;}if(confirmar("Cancelar inscrição","Cancelar a inscrição de "+dados[0].get(i).getNome()+"?")){detalhesDAO.cancelarInscricao(evento.getId(),dados[0].get(i).getId());recarregar.run();}});
        recarregar.run();return painel;
    }

    private JPanel criarAbaAgentes(Evento evento) {
        JTable tabela=criarTabelaDetalhes(new String[]{"Agente","Participação","Tema","Empresa"}); JPanel painel=criarPainelGerenciamento("Agentes externos vinculados",tabela);
        JButton add=criarBotaoPrincipal("+ Adicionar agente"), editar=criarBotaoSecundario("Editar vínculo"), remover=criarBotaoSecundario("Remover"); painel.add(barraAcoes(add,editar,remover),BorderLayout.SOUTH);
        final java.util.List<EventoDetalhesDAO.AgenteVinculo>[] dados=new java.util.List[]{java.util.List.of()};
        Runnable recarregar=()->{dados[0]=detalhesDAO.listarAgentesVinculados(evento.getId());DefaultTableModel m=(DefaultTableModel)tabela.getModel();m.setRowCount(0);for(EventoDetalhesDAO.AgenteVinculo v:dados[0])m.addRow(new Object[]{v.agente.getNome(),v.tipoParticipacao,valor(v.tema),valor(v.agente.getEmpresa())});ThemeManager.aplicarTema(painel);};
        add.addActionListener(e->{AgenteExterno a=escolherAgente(detalhesDAO.listarAgentesDisponiveis(evento.getId()));if(a!=null&&editarVinculoAgente(evento,a,null))recarregar.run();});
        editar.addActionListener(e->{int i=tabela.getSelectedRow();if(i<0){mensagem("Agentes externos","Selecione um agente.");return;}EventoDetalhesDAO.AgenteVinculo v=dados[0].get(i);if(editarVinculoAgente(evento,v.agente,v))recarregar.run();});
        remover.addActionListener(e->{int i=tabela.getSelectedRow();if(i<0){mensagem("Agentes externos","Selecione um agente.");return;}if(confirmar("Remover agente","Remover o agente externo deste evento?")){detalhesDAO.removerAgente(evento.getId(),dados[0].get(i).agente.getId());recarregar.run();}});
        recarregar.run();return painel;
    }

    private boolean editarVinculoAgente(Evento evento,AgenteExterno a,EventoDetalhesDAO.AgenteVinculo atual){
        JComboBox<String> tipo=new JComboBox<>(new String[]{"PALESTRANTE","CONVIDADO","ESPECIALISTA","AVALIADOR","OUTRO"});JTextField tema=campoDialogo();JTextArea obs=areaDialogo();if(atual!=null){tipo.setSelectedItem(atual.tipoParticipacao);tema.setText(valor(atual.tema));obs.setText(valor(atual.observacao));}
        JPanel p=formularioVertical();adicionarLinhaFormulario(p,"Agente",new JLabel(a.getNome()));adicionarLinhaFormulario(p,"Tipo de participação",tipo);adicionarLinhaFormulario(p,"Tema",tema);adicionarLinhaFormulario(p,"Observação",new JScrollPane(obs));ThemeManager.aplicarTema(p);
        if(JOptionPane.showConfirmDialog(this,p,"Vínculo do agente externo",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)!=JOptionPane.OK_OPTION)return false;
        detalhesDAO.vincularAgente(evento.getId(),a.getId(),String.valueOf(tipo.getSelectedItem()),tema.getText().trim(),obs.getText().trim());return true;
    }

    private JPanel criarAbaAtividades(Evento evento){
        JTable tabela=criarTabelaDetalhes(new String[]{"Atividade","Início","Local","Status"});JPanel painel=criarPainelGerenciamento("Atividades do evento",tabela);JButton novo=criarBotaoPrincipal("+ Nova atividade"),editar=criarBotaoSecundario("Editar"),excluir=criarBotaoSecundario("Excluir");painel.add(barraAcoes(novo,editar,excluir),BorderLayout.SOUTH);
        final java.util.List<EventoDetalhesDAO.AtividadeItem>[] dados=new java.util.List[]{java.util.List.of()};Runnable recarregar=()->{dados[0]=detalhesDAO.listarAtividades(evento.getId());DefaultTableModel m=(DefaultTableModel)tabela.getModel();m.setRowCount(0);for(EventoDetalhesDAO.AtividadeItem a:dados[0])m.addRow(new Object[]{a.nome,a.inicio==null?"":a.inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),valor(a.local),a.status});ThemeManager.aplicarTema(painel);};
        novo.addActionListener(e->{if(editarAtividade(evento,null))recarregar.run();});editar.addActionListener(e->{int i=tabela.getSelectedRow();if(i<0){mensagem("Atividades","Selecione uma atividade.");return;}if(editarAtividade(evento,dados[0].get(i)))recarregar.run();});excluir.addActionListener(e->{int i=tabela.getSelectedRow();if(i<0){mensagem("Atividades","Selecione uma atividade.");return;}if(confirmar("Excluir atividade","Excluir a atividade selecionada?")){detalhesDAO.excluirAtividade(evento.getId(),dados[0].get(i).id);recarregar.run();}});recarregar.run();return painel;
    }

    private boolean editarAtividade(Evento evento,EventoDetalhesDAO.AtividadeItem atual){
        JTextField nome=campoDialogo(),data=campoDialogo(),inicio=campoDialogo(),fim=campoDialogo(),local=campoDialogo(),cap=campoDialogo();JTextArea desc=areaDialogo();JComboBox<String> status=new JComboBox<>(new String[]{"PLANEJADA","ABERTA","EM_ANDAMENTO","CONCLUIDA","CANCELADA"});
        LocalDateTime base=atual!=null&&atual.inicio!=null?atual.inicio:evento.getDataInicio();if(base==null)base=LocalDateTime.now();data.setText(base.toLocalDate().format(DATA));inicio.setText(base.toLocalTime().format(HORA));if(atual!=null){nome.setText(valor(atual.nome));desc.setText(valor(atual.descricao));local.setText(valor(atual.local));if(atual.capacidade!=null)cap.setText(String.valueOf(atual.capacidade));status.setSelectedItem(atual.status);if(atual.fim!=null)fim.setText(atual.fim.toLocalTime().format(HORA));}
        JPanel p=formularioVertical();adicionarLinhaFormulario(p,"Nome *",nome);adicionarLinhaFormulario(p,"Descrição",new JScrollPane(desc));adicionarLinhaFormulario(p,"Data (dd/MM/yyyy)",data);adicionarLinhaFormulario(p,"Horário de início (HH:mm)",inicio);adicionarLinhaFormulario(p,"Horário de término (HH:mm)",fim);adicionarLinhaFormulario(p,"Local",local);adicionarLinhaFormulario(p,"Capacidade",cap);adicionarLinhaFormulario(p,"Status",status);ThemeManager.aplicarTema(p);
        if(JOptionPane.showConfirmDialog(this,p,atual==null?"Nova atividade":"Editar atividade",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)!=JOptionPane.OK_OPTION)return false;
        try{EventoDetalhesDAO.AtividadeItem a=atual==null?new EventoDetalhesDAO.AtividadeItem():atual;a.nome=nome.getText().trim();if(a.nome.isEmpty())throw new IllegalArgumentException("Informe o nome da atividade.");a.descricao=desc.getText().trim();LocalDate d=LocalDate.parse(data.getText().trim(),DATA);LocalTime hi=LocalTime.parse(inicio.getText().trim(),HORA);a.inicio=LocalDateTime.of(d,hi);a.fim=fim.getText().trim().isEmpty()?null:LocalDateTime.of(d,LocalTime.parse(fim.getText().trim(),HORA));a.local=local.getText().trim();a.capacidade=cap.getText().trim().isEmpty()?null:Integer.parseInt(cap.getText().trim());a.status=String.valueOf(status.getSelectedItem());detalhesDAO.salvarAtividade(evento.getId(),a);return true;}catch(Exception ex){mensagem("Atividade",ex.getMessage()==null?"Dados inválidos.":ex.getMessage());return false;}
    }

    private JPanel criarAbaResponsabilidades(Evento evento){
        JTable tabela=criarTabelaDetalhes(new String[]{"Responsabilidade","Comissão","Status","Observação"});JPanel painel=criarPainelGerenciamento("Responsabilidades do evento",tabela);JButton novo=criarBotaoPrincipal("+ Nova responsabilidade"),editar=criarBotaoSecundario("Editar"),excluir=criarBotaoSecundario("Excluir");painel.add(barraAcoes(novo,editar,excluir),BorderLayout.SOUTH);
        final java.util.List<EventoDetalhesDAO.ResponsabilidadeItem>[] dados=new java.util.List[]{java.util.List.of()};Runnable recarregar=()->{dados[0]=detalhesDAO.listarResponsabilidades(evento.getId());DefaultTableModel m=(DefaultTableModel)tabela.getModel();m.setRowCount(0);for(EventoDetalhesDAO.ResponsabilidadeItem r:dados[0])m.addRow(new Object[]{r.nome,valor(r.comissao),r.status,valor(r.observacao)});ThemeManager.aplicarTema(painel);};
        novo.addActionListener(e->{if(editarResponsabilidade(evento,null))recarregar.run();});editar.addActionListener(e->{int i=tabela.getSelectedRow();if(i<0){mensagem("Responsabilidades","Selecione uma responsabilidade.");return;}if(editarResponsabilidade(evento,dados[0].get(i)))recarregar.run();});excluir.addActionListener(e->{int i=tabela.getSelectedRow();if(i<0){mensagem("Responsabilidades","Selecione uma responsabilidade.");return;}if(confirmar("Excluir responsabilidade","Excluir a responsabilidade selecionada?")){detalhesDAO.excluirResponsabilidade(evento.getId(),dados[0].get(i).id);recarregar.run();}});recarregar.run();return painel;
    }

    private boolean editarResponsabilidade(Evento evento,EventoDetalhesDAO.ResponsabilidadeItem atual){
        JTextField nome=campoDialogo();JTextArea desc=areaDialogo(),obs=areaDialogo();JComboBox<String> status=new JComboBox<>(new String[]{"PENDENTE","EM_ANDAMENTO","CONCLUIDA","CANCELADA"});java.util.List<Comissao> comissoes=detalhesDAO.listarComissoes(evento.getId());JComboBox<Object> comissao=new JComboBox<>();comissao.addItem("Sem comissão");for(Comissao c:comissoes)comissao.addItem(c);comissao.setRenderer(new DefaultListCellRenderer(){@Override public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){JLabel x=(JLabel)super.getListCellRendererComponent(l,v,i,s,f);if(v instanceof Comissao c)x.setText(c.getNome());return x;}});
        if(atual!=null){nome.setText(valor(atual.nome));desc.setText(valor(atual.descricao));obs.setText(valor(atual.observacao));status.setSelectedItem(atual.status);if(atual.idComissao!=null)for(int i=1;i<comissao.getItemCount();i++){Comissao c=(Comissao)comissao.getItemAt(i);if(c.getIdComissao().equals(atual.idComissao)){comissao.setSelectedIndex(i);break;}}}
        JPanel p=formularioVertical();adicionarLinhaFormulario(p,"Nome *",nome);adicionarLinhaFormulario(p,"Descrição",new JScrollPane(desc));adicionarLinhaFormulario(p,"Status",status);adicionarLinhaFormulario(p,"Comissão responsável",comissao);adicionarLinhaFormulario(p,"Observação",new JScrollPane(obs));ThemeManager.aplicarTema(p);
        if(JOptionPane.showConfirmDialog(this,p,atual==null?"Nova responsabilidade":"Editar responsabilidade",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)!=JOptionPane.OK_OPTION)return false;if(nome.getText().trim().isEmpty()){mensagem("Responsabilidade","Informe o nome.");return false;}EventoDetalhesDAO.ResponsabilidadeItem r=atual==null?new EventoDetalhesDAO.ResponsabilidadeItem():atual;r.nome=nome.getText().trim();r.descricao=desc.getText().trim();r.status=String.valueOf(status.getSelectedItem());r.observacao=obs.getText().trim();Object sel=comissao.getSelectedItem();r.idComissao=sel instanceof Comissao c?c.getIdComissao():null;detalhesDAO.salvarResponsabilidade(evento.getId(),r);return true;
    }

    private JTable criarTabelaDetalhes(String[] colunas){DefaultTableModel m=new DefaultTableModel(colunas,0){@Override public boolean isCellEditable(int r,int c){return false;}};JTable t=new JTable(m);t.setRowHeight(30);t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);return t;}
    private JPanel criarPainelGerenciamento(String titulo,JTable tabela){JPanel p=new JPanel(new BorderLayout(10,12));p.setBackground(ThemeManager.getFundo());p.setBorder(new EmptyBorder(15,8,10,8));JLabel l=criarLabel(titulo,18,Font.BOLD,TEXTO);p.add(l,BorderLayout.NORTH);JScrollPane sp=new JScrollPane(tabela);sp.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorda()));p.add(sp,BorderLayout.CENTER);return p;}
    private JPanel barraAcoes(JButton...b){JPanel p=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,6));p.setBackground(ThemeManager.getFundo());for(JButton x:b)p.add(x);return p;}
    private JPanel formularioVertical(){JPanel p=new JPanel();p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));p.setBorder(new EmptyBorder(10,10,10,10));p.setBackground(ThemeManager.getPainel());return p;}
    private void adicionarLinhaFormulario(JPanel p,String rotulo,JComponent c){JLabel l=new JLabel(rotulo);l.setFont(new Font("Segoe UI",Font.BOLD,12));l.setForeground(ThemeManager.getTexto());l.setAlignmentX(Component.LEFT_ALIGNMENT);c.setAlignmentX(Component.LEFT_ALIGNMENT);c.setMaximumSize(new Dimension(Integer.MAX_VALUE,c instanceof JScrollPane?90:38));p.add(l);p.add(Box.createVerticalStrut(4));p.add(c);p.add(Box.createVerticalStrut(10));}
    private JTextField campoDialogo(){JTextField f=new JTextField();f.setPreferredSize(new Dimension(420,36));return f;}
    private JTextArea areaDialogo(){JTextArea a=new JTextArea(3,30);a.setLineWrap(true);a.setWrapStyleWord(true);return a;}
    private boolean confirmar(String titulo,String texto){return JOptionPane.showConfirmDialog(this,texto,titulo,JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION;}
    private Professor escolherProfessor(java.util.List<Professor> lista){if(lista.isEmpty()){mensagem("Professores","Não há professores disponíveis para vincular.");return null;}JComboBox<Professor> c=new JComboBox<>(lista.toArray(new Professor[0]));c.setRenderer(rendererNome());ThemeManager.aplicarTema(c);return JOptionPane.showConfirmDialog(this,c,"Selecionar professor",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION?(Professor)c.getSelectedItem():null;}
    private Aluno escolherAluno(java.util.List<Aluno> lista){if(lista.isEmpty()){mensagem("Alunos","Não há alunos disponíveis para inscrever.");return null;}JComboBox<Aluno> c=new JComboBox<>(lista.toArray(new Aluno[0]));c.setRenderer(new DefaultListCellRenderer(){@Override public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){JLabel x=(JLabel)super.getListCellRendererComponent(l,v,i,s,f);if(v instanceof Aluno a)x.setText(a.getRm()+" — "+a.getNome()+" — "+valor(a.getCurso()));return x;}});ThemeManager.aplicarTema(c);return JOptionPane.showConfirmDialog(this,c,"Selecionar aluno",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION?(Aluno)c.getSelectedItem():null;}
    private AgenteExterno escolherAgente(java.util.List<AgenteExterno> lista){if(lista.isEmpty()){mensagem("Agentes externos","Não há agentes disponíveis para vincular.");return null;}JComboBox<AgenteExterno> c=new JComboBox<>(lista.toArray(new AgenteExterno[0]));c.setRenderer(new DefaultListCellRenderer(){@Override public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){JLabel x=(JLabel)super.getListCellRendererComponent(l,v,i,s,f);if(v instanceof AgenteExterno a)x.setText(a.getNome()+(a.getEmpresa()==null||a.getEmpresa().isBlank()?"":" — "+a.getEmpresa()));return x;}});ThemeManager.aplicarTema(c);return JOptionPane.showConfirmDialog(this,c,"Selecionar agente externo",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION?(AgenteExterno)c.getSelectedItem():null;}
    private DefaultListCellRenderer rendererNome(){return new DefaultListCellRenderer(){@Override public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){JLabel x=(JLabel)super.getListCellRendererComponent(l,v,i,s,f);if(v instanceof Professor p)x.setText(p.getNome()+" — "+valor(p.getAreaAtuacao()));return x;}};}

    private JPanel criarBotoesEditor(
            JDialog dialog
    ) {
        JPanel painel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 10, 5 ) );
        painel.setBackground(ThemeManager.getFundo());
        JButton fechar = criarBotaoSecundario( "Fechar" );
        fechar.addActionListener(
                e -> dialog.dispose()
        );
        JButton salvar = (JButton) dialog .getRootPane() .getClientProperty( "botaoSalvarEvento" );
        painel.add(fechar);
        if (salvar != null) {
            painel.add(salvar);
        }
        return painel;
    }

    // ABA DADOS

    private JPanel criarAbaDados(
            Evento evento,
            JDialog dialog,
            boolean novo
    ) {
        JPanel principal = new JPanel( new BorderLayout() );
        principal.setBackground(ThemeManager.getFundo());
        JPanel formulario = new JPanel( new GridBagLayout() );
        formulario.setBackground(ThemeManager.getPainel());
        formulario.setBorder( new EmptyBorder( 20, 20, 20, 20 ) );
        GridBagConstraints gbc = criarConstraintsFormulario();
        int linha = 0;

        // NOME

        JTextField nome = new JTextField();
        estilizarCampo(nome);
        if (!novo) {
            nome.setText( evento.getNome() );
        }
        adicionarCampo( formulario, gbc, linha++, "Nome do evento", nome, 2 );

        // DESCRIÇÃO

        JTextArea descricao = new JTextArea( 5, 20 );
        estilizarAreaTexto( descricao );
        if (
                !novo
                        && evento.getDescricao() != null
        ) {
            descricao.setText( evento.getDescricao() );
        }
        JScrollPane scrollDescricao = new JScrollPane( descricao );
        scrollDescricao.setPreferredSize( new Dimension( 0, 100 ) );
        adicionarCampo( formulario, gbc, linha++, "Descrição", scrollDescricao, 2 );

        // DATA INÍCIO

        JFormattedTextField dataInicio = criarCampoData();
        JFormattedTextField horaInicio = criarCampoHora();
        if (
                !novo
                        && evento.getDataInicio() != null
        ) {
            dataInicio.setText( evento.getDataInicio() .format(DATA) );
            horaInicio.setText( evento.getDataInicio() .format(HORA) );
        }
        adicionarCampo( formulario, gbc, linha, "Data de início", dataInicio, 1 );
        adicionarCampo( formulario, gbc, linha, "Horário de início", horaInicio, 2 );
        linha++;

        // DATA FIM

        JFormattedTextField dataFim = criarCampoData();
        JFormattedTextField horaFim = criarCampoHora();
        if (
                !novo
                        && evento.getDataFim() != null
        ) {
            dataFim.setText( evento.getDataFim() .format(DATA) );
            horaFim.setText( evento.getDataFim() .format(HORA) );
        }
        adicionarCampo( formulario, gbc, linha, "Data de término", dataFim, 1 );
        adicionarCampo( formulario, gbc, linha, "Horário de término", horaFim, 2 );
        linha++;

        // LOCAL

        JTextField local = new JTextField();
        estilizarCampo(local);
        if (
                !novo
                        && evento.getLocal() != null
        ) {
            local.setText( evento.getLocal() );
        }
        adicionarCampo( formulario, gbc, linha++, "Local", local, 2 );

        // CAPACIDADE

        JTextField capacidade = new JTextField();
        estilizarCampo(capacidade);
        if (!novo) {
            capacidade.setText( String.valueOf( evento.getCapacidade() ) );
        }
        adicionarCampo( formulario, gbc, linha, "Capacidade", capacidade, 1 );

        // STATUS

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
        estilizarComboBox( status );
        if (
                !novo
                        && evento.getStatus() != null
        ) {
            status.setSelectedItem( evento.getStatus() );
        }
        adicionarCampo( formulario, gbc, linha++, "Status", status, 2 );

        // PÚBLICO

        JPanel publico = criarSeletorPublico( evento, novo );
        gbc.gridx = 0;
        gbc.gridy = linha++;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add( publico, gbc );

        // BOTÃO

        JButton salvar = criarBotaoPrincipal( novo ? "Criar evento" : "Salvar alterações" );
        salvar.addActionListener(
                e -> salvarEvento(
                        evento, novo, dialog, nome, descricao, dataInicio, horaInicio, dataFim, horaFim, local, capacidade, status, publico ) );
        dialog .getRootPane() .putClientProperty( "botaoSalvarEvento", salvar );
        JScrollPane scroll = new JScrollPane( formulario );
        scroll.setBorder( BorderFactory.createLineBorder( new Color( 226, 232, 240 ) ) );
        principal.add( scroll, BorderLayout.CENTER );
        return principal;
    }

    // SELETOR DE PÚBLICO

    private JPanel criarSeletorPublico(
            Evento evento,
            boolean novo
    ) {
        JPanel painel = new JPanel( new BorderLayout( 10, 10 ) );
        painel.setBackground(ThemeManager.getPainel());
        painel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( BORDA ), new EmptyBorder( 12, 12, 12, 12 ) ) );
        JLabel titulo = criarLabel( "Público do evento", 13, Font.BOLD, TEXTO );
        painel.add( titulo, BorderLayout.NORTH );
        JRadioButton todas = new JRadioButton( "Toda a escola" );
        JRadioButton especificas = new JRadioButton( "Selecionar cursos e/ou séries" );
        todas.setBackground(ThemeManager.getPainel());
        especificas.setBackground(ThemeManager.getPainel());
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(todas);
        grupo.add(especificas);
        DefaultListModel<EventoPublico> modelo = new DefaultListModel<>();
        List<EventoPublico> disponiveis = eventoService .listarPublicosDisponiveis();
        for (
                EventoPublico publico :
                disponiveis
        ) {
            modelo.addElement( publico );
        }
        JList<EventoPublico> lista = new JList<>( modelo );
        lista.setSelectionMode( ListSelectionModel .MULTIPLE_INTERVAL_SELECTION );
        lista.setVisibleRowCount(7);
        lista.setFont( new Font( "Segoe UI", Font.PLAIN, 13 ) );
        lista.setBackground(ThemeManager.getPainel());
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
                        JLabel label = (JLabel) super.getListCellRendererComponent( list, value, index, selected, focus );
                        label.setText( value.toString() );
                        label.setBorder( new EmptyBorder( 7, 8, 7, 8 ) );
                        return label;
                    }
                }
        );
        if (
                !novo
                        && evento != null
                        && !evento.isPublicoTodos()
        ) {
            especificas.setSelected( true );
            for (int i = 0;
                 i < modelo.size();
                 i++) {
                EventoPublico disponivel = modelo.getElementAt(i);
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
                        lista.addSelectionInterval( i, i );
                        break;
                    }
                }
            }
        } else {
            todas.setSelected( true );
        }
        JPanel opcoes = new JPanel();
        opcoes.setLayout( new BoxLayout( opcoes, BoxLayout.Y_AXIS ) );
        opcoes.setBackground(ThemeManager.getPainel());
        opcoes.add(todas);
        opcoes.add(especificas);
        JScrollPane scroll = new JScrollPane( lista );
        scroll.setPreferredSize( new Dimension( 0, 150 ) );
        painel.add( opcoes, BorderLayout.WEST );
        painel.add( scroll, BorderLayout.CENTER );
        Runnable atualizar =
                () -> {
                    boolean usarLista = especificas.isSelected();
                    lista.setEnabled( usarLista );
                    scroll.setEnabled( usarLista );
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

        // Guardamos os componentes no painel. Isso permite recuperar posteriormente no método salvarEvento.
        painel.putClientProperty( "radioTodas", todas );
        painel.putClientProperty( "radioEspecificas", especificas );
        painel.putClientProperty( "listaPublicos", lista );
        return painel;
    }

    // SALVAR

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

            // NOME

            if (
                    nome.getText()
                            .trim()
                            .isEmpty()
            ) {
                mostrarAviso( dialog, "Informe o nome do evento.", "Campo obrigatório" );
                nome.requestFocus();
                return;
            }

            // DATA INÍCIO

            String textoDataInicio = dataInicio .getText() .trim();
            String textoHoraInicio = horaInicio .getText() .trim();
            if (
                    textoDataInicio.isEmpty()
                            || textoHoraInicio.isEmpty()
                            || textoDataInicio.contains("_")
                            || textoHoraInicio.contains("_")
            ) {
                mostrarAviso( dialog, "Informe a data e o horário de início.", "Campo obrigatório" );
                return;
            }
            LocalDate dataInicial = LocalDate.parse( textoDataInicio, DATA );
            LocalTime horaInicial = LocalTime.parse( textoHoraInicio, HORA );
            LocalDateTime inicio = LocalDateTime.of( dataInicial, horaInicial );

            // DATA FIM

            LocalDateTime fim = null;
            String textoDataFim = dataFim .getText() .trim();
            String textoHoraFim = horaFim .getText() .trim();
            boolean informouDataFim = !textoDataFim.isEmpty() && !textoDataFim.contains("_");
            boolean informouHoraFim = !textoHoraFim.isEmpty() && !textoHoraFim.contains("_");
            if (
                    informouDataFim
                            != informouHoraFim
            ) {
                mostrarAviso( dialog, "Informe a data e o horário de término completos.", "Dados incompletos" );
                return;
            }
            if (
                    informouDataFim
                            && informouHoraFim
            ) {
                LocalDate dataFinal = LocalDate.parse( textoDataFim, DATA );
                LocalTime horaFinal = LocalTime.parse( textoHoraFim, HORA );
                fim = LocalDateTime.of( dataFinal, horaFinal );
                if (fim.isBefore(inicio)) {
                    mostrarAviso( dialog, "A data de término não pode ser anterior à data de início.", "Data inválida" );
                    return;
                }
            }

            // CAPACIDADE

            int capacidadeValor = 0;
            String textoCapacidade = capacidade .getText() .trim();
            if (
                    !textoCapacidade.isEmpty()
            ) {
                capacidadeValor = Integer.parseInt( textoCapacidade );
                if (
                        capacidadeValor < 0
                ) {
                    mostrarAviso( dialog, "A capacidade não pode ser negativa.", "Valor inválido" );
                    return;
                }
            }

            // EVENTO

            Evento eventoSalvar = novo ? new Evento() : evento;
            if (novo) {
                eventoSalvar .setIdUsuarioCriador( usuarioLogado .getId() );
            }
            eventoSalvar.setNome( nome.getText() .trim() );
            eventoSalvar.setDescricao( descricao.getText() .trim() );
            eventoSalvar.setDataInicio( inicio );
            eventoSalvar.setDataFim( fim );
            eventoSalvar.setLocal( local.getText() .trim() );
            eventoSalvar.setCapacidade( capacidadeValor );
            eventoSalvar.setStatus( status.getSelectedItem() .toString() );

            // PÚBLICO

            JRadioButton radioTodas = (JRadioButton) painelPublico .getClientProperty( "radioTodas" );
            @SuppressWarnings("unchecked") JList<EventoPublico> lista = (JList<EventoPublico>) painelPublico .getClientProperty( "listaPublicos" );
            boolean todas = radioTodas.isSelected();
            eventoSalvar.setPublicoTodos( todas );
            if (todas) {
                eventoSalvar.limparPublicos();
            } else {
                List<EventoPublico> selecionados = lista.getSelectedValuesList();
                if (
                        selecionados == null
                                || selecionados.isEmpty()
                ) {
                    mostrarAviso( dialog, "Selecione pelo menos um público para o evento.", "Público obrigatório" );
                    return;
                }
                eventoSalvar.setPublicos( selecionados );
            }

            // BANCO

            boolean sucesso;
            if (novo) {
                sucesso = eventoService .cadastrarEvento( eventoSalvar );
            } else {
                sucesso = eventoService .atualizarEvento( eventoSalvar );
            }
            if (!sucesso) {
                JOptionPane.showMessageDialog( dialog, "Não foi possível salvar o evento.", "Erro", JOptionPane.ERROR_MESSAGE );
                return;
            }
            JOptionPane.showMessageDialog(
                    dialog, novo ? "Evento criado com sucesso!" : "Evento atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE );
            dialog.dispose();
            aplicarFiltros();
        } catch (
                DateTimeParseException ex
        ) {
            mostrarAviso( dialog, "Verifique as datas e horários.\n\n" + "Data: dd/MM/yyyy\n" + "Horário: HH:mm", "Formato inválido" );
        } catch (
                NumberFormatException ex
        ) {
            mostrarAviso( dialog, "A capacidade deve ser um número inteiro.", "Valor inválido" );
        } catch (
                IllegalArgumentException ex
        ) {
            mostrarAviso( dialog, ex.getMessage(), "Dados inválidos" );
        } catch (
                Exception ex
        ) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog( dialog, "Erro ao salvar evento:\n\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE );
        }
    }

    // ABAS INFORMATIVAS

    private JPanel criarAbaInformacao(
            String titulo,
            String texto,
            String textoBotao,
            ActionListener acao
    ) {
        JPanel painel = new JPanel( new BorderLayout( 10, 10 ) );
        painel.setBackground(ThemeManager.getFundo());
        painel.setBorder( new EmptyBorder( 15, 5, 10, 5 ) );
        painel.add( criarLabel( titulo, 18, Font.BOLD, TEXTO ), BorderLayout.NORTH );
        JTextArea area = new JTextArea( texto );
        area.setEditable( false );
        area.setLineWrap( true );
        area.setWrapStyleWord( true );
        area.setFont( new Font( "Segoe UI", Font.PLAIN, 14 ) );
        area.setForeground(ThemeManager.getTexto());
        area.setBackground(ThemeManager.getPainel());
        area.setBorder( new EmptyBorder( 15, 15, 15, 15 ) );
        painel.add( new JScrollPane( area ), BorderLayout.CENTER );
        if (
                textoBotao != null
                        && acao != null
        ) {
            JPanel botoes = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
            botoes.setBackground(ThemeManager.getFundo());
            JButton botao = criarBotaoPrincipal( textoBotao );
            botao.addActionListener( acao );
            botoes.add(botao);
            painel.add( botoes, BorderLayout.SOUTH );
        }
        return painel;
    }

    private void mensagem(
            String titulo,
            String texto
    ) {
        JOptionPane.showMessageDialog( this, texto, titulo, JOptionPane.INFORMATION_MESSAGE );
    }

    // CAMPOS

    private JFormattedTextField criarCampoData() {
        try {
            MaskFormatter mascara = new MaskFormatter( "##/##/####" );
            mascara.setPlaceholderCharacter( '_' );
            JFormattedTextField campo = new JFormattedTextField( mascara );
            estilizarCampoFormatado( campo );
            return campo;
        } catch (ParseException e) {
            throw new RuntimeException( "Erro ao criar campo de data.", e );
        }
    }

    private JFormattedTextField criarCampoHora() {
        try {
            MaskFormatter mascara = new MaskFormatter( "##:##" );
            mascara.setPlaceholderCharacter( '_' );
            JFormattedTextField campo = new JFormattedTextField( mascara );
            estilizarCampoFormatado( campo );
            return campo;
        } catch (ParseException e) {
            throw new RuntimeException( "Erro ao criar campo de horário.", e );
        }
    }

    private void estilizarCampoFormatado(
            JFormattedTextField campo
    ) {
        campo.setFont( new Font( "Segoe UI", Font.PLAIN, 14 ) );
        campo.setPreferredSize( new Dimension( 0, 40 ) );
        campo.setFocusLostBehavior( JFormattedTextField.PERSIST );
        campo.setBorder( criarBordaCampo() );
    }

    // FORMULÁRIO

    private GridBagConstraints
    criarConstraintsFormulario() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets( 7, 7, 7, 7 );
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
        gbc.gridx = coluna - 1;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.weightx = coluna == 1 ? 0.5 : 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel container = new JPanel();
        container.setLayout( new BoxLayout( container, BoxLayout.Y_AXIS ) );
        container.setBackground(ThemeManager.getPainel());
        container.add( criarLabel( titulo, 12, Font.BOLD, TEXTO ) );
        container.add( Box.createVerticalStrut(5) );
        container.add( campo );
        painel.add( container, gbc );
    }

    // ESTILO

    private void estilizarCampo(
            JTextField campo
    ) {
        campo.setFont( new Font( "Segoe UI", Font.PLAIN, 14 ) );
        campo.setBorder( criarBordaCampo() );
        campo.setPreferredSize( new Dimension( 0, 40 ) );
    }

    private Border criarBordaCampo() {
        return BorderFactory .createCompoundBorder( BorderFactory.createLineBorder( BORDA ), new EmptyBorder( 8, 10, 8, 10 ) );
    }

    private void estilizarAreaTexto(
            JTextArea area
    ) {
        area.setLineWrap( true );
        area.setWrapStyleWord( true );
        area.setFont( new Font( "Segoe UI", Font.PLAIN, 14 ) );
        area.setForeground(ThemeManager.getTexto());
        area.setBackground(ThemeManager.getPainel());
        area.setMargin( new Insets( 8, 10, 8, 10 ) );
    }

    private void estilizarComboBox(
            JComboBox<String> combo
    ) {
        combo.setFont( new Font( "Segoe UI", Font.PLAIN, 13 ) );
        combo.setBackground(ThemeManager.getPainel());
        combo.setForeground(ThemeManager.getTexto());
        combo.setBorder( BorderFactory.createLineBorder( BORDA ) );
        combo.setFocusable( false );
    }

    // BOTÕES

    private JButton criarBotaoPrincipal(
            String texto
    ) {
        JButton botao = new JButton( texto );
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

    private JButton criarBotaoSecundario(
            String texto
    ) {
        JButton botao = new JButton( texto );
        botao.setFont( new Font( "Segoe UI", Font.BOLD, 13 ) );
        botao.setForeground(ThemeManager.getTexto());
        botao.setBackground(ThemeManager.getPainel());
        botao.setFocusPainted( false );
        botao.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( BORDA ), new EmptyBorder( 9, 15, 9, 15 ) ) );
        botao.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        return botao;
    }

    // LABEL

    private JLabel criarLabel(
            String texto,
            int tamanho,
            int estilo,
            Color cor
    ) {
        JLabel label = new JLabel( texto );
        label.setFont( new Font( "Segoe UI", estilo, tamanho ) );
        label.setForeground( cor );
        return label;
    }

    private JLabel criarLabelInformacao(
            String texto
    ) {
        return criarLabel( texto, 14, Font.PLAIN, ThemeManager.isModoEscuro() ? ThemeManager.getTexto() : new Color(71, 85, 105) );
    }

    private void mostrarAviso(
            Component componente,
            String mensagem,
            String titulo
    ) {
        JOptionPane.showMessageDialog( componente, mensagem, titulo, JOptionPane.WARNING_MESSAGE );
    }
}
