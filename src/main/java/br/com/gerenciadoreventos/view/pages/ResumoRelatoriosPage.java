package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.dao.PresencaImportDAO;
import br.com.gerenciadoreventos.dao.PresencaImportDAO.EventoOpcao;
import br.com.gerenciadoreventos.dao.RelatorioDAO;
import br.com.gerenciadoreventos.dao.RelatorioDAO.AlunoStatus;
import br.com.gerenciadoreventos.dao.RelatorioDAO.EventoDesempenho;
import br.com.gerenciadoreventos.dao.RelatorioDAO.PontoComparecimento;
import br.com.gerenciadoreventos.dao.RelatorioDAO.ResumoGeral;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Aba "Resumo" da tela de Relatórios.
 *
 * Layout baseado no protótipo:
 *   - cards com totais (eventos, inscrições, taxa de comparecimento)
 *   - lista de relatórios disponíveis, cada um com sua ação
 *   - "Desempenho por Evento" abre uma tela de detalhe (cards +
 *     gráfico de comparecimento + status dos alunos)
 *
 * Os números vêm do RelatorioDAO, direto do banco
 * (tabelas evento, inscricao_evento, presenca_evento, aluno).
 */
public class ResumoRelatoriosPage extends JPanel {

    // =====================================================
    // CORES (mesma paleta usada no resto do app)
    // =====================================================

    private static final Color FUNDO = new Color(246, 248, 252);
    private static final Color AZUL = new Color(37, 99, 235);
    private static final Color VERDE = new Color(22, 163, 74);
    private static final Color TEXTO = new Color(15, 23, 42);
    private static final Color CINZA_TEXTO = new Color(100, 116, 139);
    private static final Color BORDA = new Color(226, 232, 240);

    private static final DateTimeFormatter FORMATO_DATA_CURTA =
            DateTimeFormatter.ofPattern("dd/MM");

    // =====================================================
    // DAOs
    // =====================================================

    private final RelatorioDAO relatorioDAO = new RelatorioDAO();
    private final PresencaImportDAO presencaDAO = new PresencaImportDAO();

    // =====================================================
    // NAVEGAÇÃO INTERNA (lista <-> detalhe)
    // =====================================================

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel painelConteudo = new JPanel(cardLayout);

    private static final String CARTAO_LISTA = "lista";
    private static final String CARTAO_DETALHE = "detalhe";

    // Cards da lista (totais gerais) — precisam ser atualizados
    // toda vez que a aba é reaberta, então guardamos a referência.
    private JPanel painelCardsResumo;

    private JLabel labelTituloDetalhe;
    private JPanel painelCardsDetalhe;
    private JPanel painelGrafico;
    private JPanel painelStatusAlunos;


    public ResumoRelatoriosPage() {

        setLayout(new BorderLayout());
        setBackground(FUNDO);

        painelConteudo.setBackground(FUNDO);
        painelConteudo.add(criarPainelLista(), CARTAO_LISTA);
        painelConteudo.add(criarPainelDetalhe(), CARTAO_DETALHE);

        add(painelConteudo, BorderLayout.CENTER);

        cardLayout.show(painelConteudo, CARTAO_LISTA);
    }


    // =====================================================
    // TELA 1: LISTA (cards de totais + tabela de relatórios)
    // =====================================================

    private JPanel criarPainelLista() {

        JPanel painel = new JPanel(new BorderLayout(0, 20));
        painel.setBackground(FUNDO);
        painel.setBorder(new EmptyBorder(25, 25, 25, 25));

        painelCardsResumo = new JPanel(new GridLayout(1, 3, 15, 0));
        painelCardsResumo.setBackground(FUNDO);
        atualizarCardsResumo();

        painel.add(painelCardsResumo, BorderLayout.NORTH);
        painel.add(criarListaRelatorios(), BorderLayout.CENTER);

        return painel;
    }


    // =====================================================
    // CARDS: Total de Eventos / Total de Inscrições / Taxa
    // =====================================================

    private void atualizarCardsResumo() {

        ResumoGeral resumo = relatorioDAO.buscarResumoGeral();

        painelCardsResumo.removeAll();
        painelCardsResumo.add(criarCard("Total de Eventos", String.valueOf(resumo.totalEventos)));
        painelCardsResumo.add(criarCard("Total de Inscrições", String.valueOf(resumo.totalInscricoes)));
        painelCardsResumo.add(criarCard("Taxa de Comparecimento", formatarPercentual(resumo.taxaComparecimento)));
        painelCardsResumo.revalidate();
        painelCardsResumo.repaint();
    }

    private String formatarPercentual(double valor) {
        return String.format("%.0f%%", valor);
    }

    private JPanel criarCard(String titulo, String valor) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA),
                        new EmptyBorder(18, 20, 18, 20)
                )
        );

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelTitulo.setForeground(CINZA_TEXTO);

        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(new Font("Segoe UI", Font.BOLD, 26));
        labelValor.setForeground(TEXTO);

        card.add(labelTitulo);
        card.add(Box.createVerticalStrut(6));
        card.add(labelValor);

        return card;
    }


    // =====================================================
    // LISTA DE RELATÓRIOS (nome + ação)
    // =====================================================

    private JPanel criarListaRelatorios() {

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(FUNDO);

        JPanel caixa = new JPanel();
        caixa.setLayout(new BoxLayout(caixa, BoxLayout.Y_AXIS));
        caixa.setBackground(Color.WHITE);
        caixa.setBorder(BorderFactory.createLineBorder(BORDA));

        caixa.add(criarCabecalhoTabela());
        caixa.add(criarSeparador());

        caixa.add(criarLinhaRelatorioSimples("Relatório de Presenças"));
        caixa.add(criarSeparador());

        caixa.add(criarLinhaRelatorioSimples("Relatório Financeiro"));
        caixa.add(criarSeparador());

        caixa.add(criarLinhaDesempenhoPorEvento());
        caixa.add(criarSeparador());

        caixa.add(criarLinhaRelatorioSimples("Relatório de Inscrição por Período"));

        container.add(caixa, BorderLayout.NORTH);

        return container;
    }

    private JPanel criarCabecalhoTabela() {

        JPanel linha = new JPanel(new BorderLayout());
        linha.setBackground(new Color(249, 250, 252));
        linha.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel nome = new JLabel("Nome do Relatório");
        nome.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nome.setForeground(CINZA_TEXTO);

        JLabel acoes = new JLabel("Ações");
        acoes.setFont(new Font("Segoe UI", Font.BOLD, 13));
        acoes.setForeground(CINZA_TEXTO);

        linha.add(nome, BorderLayout.WEST);
        linha.add(acoes, BorderLayout.EAST);

        return linha;
    }

    private JPanel criarSeparador() {
        JPanel separador = new JPanel();
        separador.setBackground(BORDA);
        separador.setPreferredSize(new Dimension(1, 1));
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return separador;
    }

    private JPanel criarLinhaRelatorioSimples(String nomeRelatorio) {

        JPanel linha = new JPanel(new BorderLayout());
        linha.setBackground(Color.WHITE);
        linha.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel nome = new JLabel(nomeRelatorio);
        nome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nome.setForeground(TEXTO);

        JButton botaoExportar = criarBotaoSecundario("Exportar");

        // TODO: gerar o arquivo (PDF/CSV) real a partir dos dados
        // já disponíveis no RelatorioDAO / banco.
        botaoExportar.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Exportação de \"" + nomeRelatorio + "\" ainda não está disponível.",
                        "Em breve",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        linha.add(nome, BorderLayout.WEST);
        linha.add(botaoExportar, BorderLayout.EAST);

        return linha;
    }

    private JPanel criarLinhaDesempenhoPorEvento() {

        JPanel linha = new JPanel(new BorderLayout());
        linha.setBackground(Color.WHITE);
        linha.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel nome = new JLabel("Desempenho por Evento");
        nome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nome.setForeground(TEXTO);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        direita.setBackground(Color.WHITE);

        // Mesma fonte de eventos já usada na aba "Importar Presença (CSV)"
        JComboBox<EventoOpcao> comboEventos = new JComboBox<>();
        comboEventos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboEventos.setPreferredSize(new Dimension(220, 32));

        List<EventoOpcao> eventos = presencaDAO.listarEventos();
        for (EventoOpcao evento : eventos) {
            comboEventos.addItem(evento);
        }

        JButton botaoVer = criarBotaoSecundario("Ver");
        botaoVer.addActionListener(e -> {

            EventoOpcao evento = (EventoOpcao) comboEventos.getSelectedItem();

            if (evento == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Nenhum evento cadastrado ainda.",
                        "Sem eventos",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            abrirDetalheEvento(evento);
        });

        direita.add(comboEventos);
        direita.add(botaoVer);

        linha.add(nome, BorderLayout.WEST);
        linha.add(direita, BorderLayout.EAST);

        return linha;
    }


    // =====================================================
    // TELA 2: DETALHE DE DESEMPENHO DE UM EVENTO
    // =====================================================

    private JPanel criarPainelDetalhe() {

        JPanel painel = new JPanel(new BorderLayout(0, 20));
        painel.setBackground(FUNDO);
        painel.setBorder(new EmptyBorder(25, 25, 25, 25));

        painel.add(criarCabecalhoDetalhe(), BorderLayout.NORTH);

        JPanel meio = new JPanel();
        meio.setLayout(new BoxLayout(meio, BoxLayout.Y_AXIS));
        meio.setBackground(FUNDO);

        painelCardsDetalhe = new JPanel(new GridLayout(1, 3, 15, 0));
        painelCardsDetalhe.setBackground(FUNDO);
        painelCardsDetalhe.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelGrafico = new JPanel(new BorderLayout());
        painelGrafico.setBackground(Color.WHITE);
        painelGrafico.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA),
                        new EmptyBorder(18, 20, 18, 20)
                )
        );
        painelGrafico.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelGrafico.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JLabel tituloGrafico = new JLabel("Gráfico de Comparecimento");
        tituloGrafico.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tituloGrafico.setForeground(TEXTO);
        painelGrafico.add(tituloGrafico, BorderLayout.NORTH);

        JPanel caixaStatus = criarCaixaStatusAlunos();

        meio.add(painelCardsDetalhe);
        meio.add(Box.createVerticalStrut(20));
        meio.add(painelGrafico);
        meio.add(Box.createVerticalStrut(20));
        meio.add(caixaStatus);

        painel.add(meio, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarCabecalhoDetalhe() {

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(FUNDO);

        JButton botaoVoltar = new JButton("< Voltar");
        botaoVoltar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        botaoVoltar.setForeground(AZUL);
        botaoVoltar.setBorderPainted(false);
        botaoVoltar.setContentAreaFilled(false);
        botaoVoltar.setFocusPainted(false);
        botaoVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoVoltar.setBorder(new EmptyBorder(0, 0, 8, 0));
        botaoVoltar.setAlignmentX(Component.LEFT_ALIGNMENT);
        botaoVoltar.addActionListener(e -> {
            atualizarCardsResumo();
            cardLayout.show(painelConteudo, CARTAO_LISTA);
        });

        labelTituloDetalhe = new JLabel("Desempenho: ");
        labelTituloDetalhe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        labelTituloDetalhe.setForeground(TEXTO);
        labelTituloDetalhe.setAlignmentX(Component.LEFT_ALIGNMENT);

        painel.add(botaoVoltar);
        painel.add(labelTituloDetalhe);

        return painel;
    }

    private JPanel criarCaixaStatusAlunos() {

        JPanel caixa = new JPanel();
        caixa.setLayout(new BoxLayout(caixa, BoxLayout.Y_AXIS));
        caixa.setBackground(Color.WHITE);
        caixa.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA),
                        new EmptyBorder(18, 20, 18, 20)
                )
        );
        caixa.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Status dos Alunos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(TEXTO);

        painelStatusAlunos = new JPanel();
        painelStatusAlunos.setLayout(new BoxLayout(painelStatusAlunos, BoxLayout.Y_AXIS));
        painelStatusAlunos.setBackground(Color.WHITE);

        caixa.add(titulo);
        caixa.add(Box.createVerticalStrut(10));
        caixa.add(painelStatusAlunos);

        return caixa;
    }

    private JPanel criarLinhaStatusAluno(String nome, String status) {

        JPanel linha = new JPanel(new BorderLayout());
        linha.setBackground(Color.WHITE);
        linha.setBorder(new EmptyBorder(8, 0, 8, 0));
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel labelNome = new JLabel(nome);
        labelNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelNome.setForeground(TEXTO);

        JLabel labelStatus = new JLabel(status);
        labelStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelStatus.setForeground(corStatus(status));

        linha.add(labelNome, BorderLayout.WEST);
        linha.add(labelStatus, BorderLayout.EAST);

        return linha;
    }

    private Color corStatus(String status) {

        switch (status) {
            case "Confirmado":
                return VERDE;
            case "Pendente":
                return new Color(217, 119, 6);
            case "Ausente":
                return new Color(220, 38, 38);
            default:
                return CINZA_TEXTO;
        }
    }


    // =====================================================
    // ABRIR DETALHE DE UM EVENTO ESPECÍFICO (dados reais)
    // =====================================================

    private void abrirDetalheEvento(EventoOpcao evento) {

        labelTituloDetalhe.setText("Desempenho: " + evento.toString());

        // ---- cards ----
        EventoDesempenho desempenho = relatorioDAO.buscarDesempenhoEvento(evento.id);

        painelCardsDetalhe.removeAll();
        painelCardsDetalhe.add(criarCard("Total de Inscritos", String.valueOf(desempenho.totalInscritos)));
        painelCardsDetalhe.add(criarCard("Presenças Confirmadas", String.valueOf(desempenho.presencasConfirmadas)));
        painelCardsDetalhe.add(criarCard("Ausentes", String.valueOf(desempenho.ausentes)));
        painelCardsDetalhe.revalidate();
        painelCardsDetalhe.repaint();

        // ---- gráfico de comparecimento por data ----
        List<PontoComparecimento> pontos = relatorioDAO.buscarComparecimentoPorData(evento.id);

        int[] valores = new int[pontos.size()];
        String[] rotulos = new String[pontos.size()];

        for (int i = 0; i < pontos.size(); i++) {
            valores[i] = pontos.get(i).total;
            rotulos[i] = pontos.get(i).data.format(FORMATO_DATA_CURTA);
        }

        // remove o gráfico anterior (se houver) e desenha o atual
        for (Component c : painelGrafico.getComponents()) {
            if (c instanceof BarChartPanel) {
                painelGrafico.remove(c);
            }
        }

        if (valores.length == 0) {

            JLabel semDados = new JLabel("Ainda não há presenças registradas para este evento.");
            semDados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            semDados.setForeground(CINZA_TEXTO);
            semDados.setBorder(new EmptyBorder(20, 0, 0, 0));
            painelGrafico.add(semDados, BorderLayout.CENTER);

        } else {

            BarChartPanel grafico = new BarChartPanel(valores, rotulos);
            grafico.setPreferredSize(new Dimension(100, 150));
            painelGrafico.add(grafico, BorderLayout.CENTER);
        }

        painelGrafico.revalidate();
        painelGrafico.repaint();

        // ---- status dos alunos ----
        List<AlunoStatus> alunos = relatorioDAO.listarStatusAlunos(evento.id);

        painelStatusAlunos.removeAll();

        if (alunos.isEmpty()) {

            JLabel semAlunos = new JLabel("Nenhum aluno inscrito neste evento.");
            semAlunos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            semAlunos.setForeground(CINZA_TEXTO);
            painelStatusAlunos.add(semAlunos);

        } else {

            for (int i = 0; i < alunos.size(); i++) {

                AlunoStatus status = alunos.get(i);
                painelStatusAlunos.add(criarLinhaStatusAluno(status.nome, status.status));

                if (i < alunos.size() - 1) {
                    painelStatusAlunos.add(criarSeparador());
                }
            }
        }

        painelStatusAlunos.revalidate();
        painelStatusAlunos.repaint();

        cardLayout.show(painelConteudo, CARTAO_DETALHE);
    }


    // =====================================================
    // BOTÃO SECUNDÁRIO (contorno azul, fundo branco)
    // =====================================================

    private JButton criarBotaoSecundario(String texto) {

        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setForeground(AZUL);
        botao.setBackground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AZUL),
                        new EmptyBorder(6, 14, 6, 14)
                )
        );

        return botao;
    }


    // =====================================================
    // GRÁFICO DE BARRAS SIMPLES (sem dependências externas)
    // =====================================================

    private static class BarChartPanel extends JPanel {

        private final int[] valores;
        private final String[] rotulos;

        BarChartPanel(int[] valores, String[] rotulos) {
            this.valores = valores;
            this.rotulos = rotulos;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int largura = getWidth();
            int altura = getHeight();

            if (valores.length == 0 || largura <= 0 || altura <= 0) {
                return;
            }

            int maiorValor = 0;
            for (int valor : valores) {
                maiorValor = Math.max(maiorValor, valor);
            }
            if (maiorValor == 0) {
                maiorValor = 1;
            }

            int margemInferior = 22; // espaço para o rótulo da data
            int espacoEntreBarras = 14;
            int larguraBarra = Math.max(
                    10,
                    (largura - espacoEntreBarras * (valores.length + 1)) / valores.length
            );

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(CINZA_TEXTO);

            int x = espacoEntreBarras;

            for (int i = 0; i < valores.length; i++) {

                int valor = valores[i];
                int alturaBarra = (int) (((double) valor / maiorValor) * (altura - margemInferior - 10));
                int y = altura - margemInferior - alturaBarra;

                g2.setColor(AZUL);
                g2.fill(new RoundRectangle2D.Double(x, y, larguraBarra, alturaBarra, 6, 6));

                g2.setColor(CINZA_TEXTO);
                String rotulo = rotulos[i];
                int larguraTexto = g2.getFontMetrics().stringWidth(rotulo);
                g2.drawString(
                        rotulo,
                        x + (larguraBarra - larguraTexto) / 2,
                        altura - 6
                );

                x += larguraBarra + espacoEntreBarras;
            }
        }
    }
}