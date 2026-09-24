package br.com.gerenciadoreventos.view;

import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.view.components.DashboardCards;
import br.com.gerenciadoreventos.view.components.DashboardCenter;
import br.com.gerenciadoreventos.view.components.DashboardHeader;
import br.com.gerenciadoreventos.view.components.RecentEventsTable;
import br.com.gerenciadoreventos.view.components.Sidebar;
import br.com.gerenciadoreventos.view.pages.*;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private static final Color FUNDO =
            new Color(246, 248, 252);

    private final Usuario usuarioLogado;

    private Sidebar sidebar;

    private CardLayout cardLayout;

    private JPanel painelPaginas;


    // =====================================================
    // CONSTRUTOR
    // =====================================================

    public Dashboard(Usuario usuarioLogado) {

        this.usuarioLogado = usuarioLogado;

        configurarJanela();

        inicializarInterface();
    }


    // =====================================================
    // CONFIGURAÇÃO DA JANELA
    // =====================================================

    private void configurarJanela() {

        setTitle("Gerenciamento de eventos");

        setSize(
                1200,
                750
        );

        setMinimumSize(
                new Dimension(
                        1000,
                        650
                )
        );

        setDefaultCloseOperation(
                EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);
    }


    // =====================================================
    // INTERFACE
    // =====================================================

    private void inicializarInterface() {

        JPanel principal =
                new JPanel(
                        new BorderLayout()
                );

        principal.setBackground(FUNDO);


        // =================================================
        // SIDEBAR
        // =================================================

        sidebar =
                new Sidebar(
                        this,
                        usuarioLogado,
                        "dashboard"
                );

        principal.add(
                sidebar,
                BorderLayout.WEST
        );


        // =================================================
        // CARD LAYOUT
        // =================================================

        cardLayout =
                new CardLayout();

        painelPaginas =
                new JPanel(
                        cardLayout
                );

        painelPaginas.setBackground(FUNDO);


        // =================================================
        // PÁGINA: DASHBOARD
        // =================================================

        painelPaginas.add(
                criarConteudo(),
                "dashboard"
        );


        // =================================================
        // PÁGINA: EVENTOS
        // =================================================

        painelPaginas.add(
                new EventosPage(usuarioLogado),
                "eventos"
        );

        // =================================================
        // PÁGINA: ALUNO
        // =================================================
        painelPaginas.add(
                new AlunoPage(),
                "alunos"
        );

        // =================================================
        // PÁGINA: Professores
        // =================================================
        painelPaginas.add(
                new ProfessorPage(),
                "professores"
        );
        //==================================================
        // PÁGINA: Agentes Externos
        //==================================================
        painelPaginas.add(
                new AgenteExternoPage(),
                "agentesExternos"
        );
        // =================================================
        // PÁGINA: Inscricoes
        // =================================================
        painelPaginas.add(
                new InscricoesPage(),
                "inscricoes"
        );
        // =================================================
        // PÁGINA: Comissoes
        // =================================================
        painelPaginas.add(
                new ComissoesPage(),
                "comissoes"
        );

        // =================================================
        // PÁGINA: Relatórios
        // (contém, dentro dela, a aba de Importar
        // Presença via CSV)
        // =================================================
        painelPaginas.add(
                new ResumoRelatoriosPage(),
                "resumo"
        );

        // =================================================
        // ADICIONAR PÁGINAS AO PRINCIPAL
        // =================================================

        principal.add(
                painelPaginas,
                BorderLayout.CENTER
        );


        add(principal);
    }


    // =====================================================
    // CONTEÚDO DO DASHBOARD
    // =====================================================

    private JPanel criarConteudo() {

        JPanel painel =
                new JPanel(
                        new BorderLayout()
                );

        painel.setBackground(FUNDO);

        painel.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        25,
                        25,
                        25
                )
        );


        // Cabeçalho

        painel.add(
                new DashboardHeader(
                        usuarioLogado
                ),
                BorderLayout.NORTH
        );


        // Conteúdo

        painel.add(
                criarDashboard(),
                BorderLayout.CENTER
        );


        return painel;
    }


    // =====================================================
    // COMPONENTES DO DASHBOARD
    // =====================================================

    private JPanel criarDashboard() {

        JPanel painel =
                new JPanel();

        painel.setLayout(
                new BoxLayout(
                        painel,
                        BoxLayout.Y_AXIS
                )
        );

        painel.setBackground(FUNDO);


        // Cards

        painel.add(
                new DashboardCards()
        );


        painel.add(
                Box.createVerticalStrut(18)
        );


        // Gráfico / informações

        painel.add(
                new DashboardCenter()
        );


        painel.add(
                Box.createVerticalStrut(18)
        );


        // Eventos recentes

        painel.add(
                new RecentEventsTable()
        );


        return painel;
    }


    // =====================================================
    // TROCAR DE PÁGINA
    // =====================================================
    private boolean paginaExiste(String pagina) {

        return pagina.equals("dashboard")
                || pagina.equals("eventos")
                || pagina.equals("alunos")
                || pagina.equals("professores")
                || pagina.equals("agentesExternos")
                || pagina.equals("inscricoes")
                || pagina.equals("comissoes")
                || pagina.equals("resumo");

    }
    public void mostrarPagina(String pagina) {

        // Verifica se a página existe
        if (!paginaExiste(pagina)) {
            return;
        }

        // Mostra a página
        cardLayout.show(
                painelPaginas,
                pagina
        );

        // Atualiza o botão azul da Sidebar
        sidebar.atualizarPaginaAtiva(
                pagina
        );
    }


    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Usuario usuario =
                    new Usuario();

            usuario.setId(1);

            usuario.setNome(
                    "Administrador"
            );

            usuario.setEmail(
                    "admin@etec.com.br"
            );

            usuario.setTipoUsuario(
                    "ADMINISTRADOR"
            );

            usuario.setAtivo(true);


            Dashboard dashboard =
                    new Dashboard(usuario);

            dashboard.setVisible(true);
        });
    }
}