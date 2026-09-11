package br.com.gerenciadoreventos.view;

import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.view.components.DashboardCards;
import br.com.gerenciadoreventos.view.components.DashboardCenter;
import br.com.gerenciadoreventos.view.components.DashboardHeader;
import br.com.gerenciadoreventos.view.components.RecentEventsTable;
import br.com.gerenciadoreventos.view.components.Sidebar;

import br.com.gerenciadoreventos.view.pages.EventosPage;


import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private static final Color FUNDO =
            new Color(246, 248, 252);

    private final Usuario usuarioLogado;

    private CardLayout cardLayout;
    private JPanel painelPaginas;

    public Dashboard(Usuario usuarioLogado) {

        this.usuarioLogado = usuarioLogado;

        configurarJanela();
        inicializarInterface();
    }

    private void configurarJanela() {


        setTitle("Gerenciamento de eventos");

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


        // =========================
        // SIDEBAR
        // =========================

        principal.add(
                new Sidebar(
                        this,
                        usuarioLogado,
                        "dashboard"
                ),
                BorderLayout.WEST
        );


        // =========================
        // PÁGINAS
        // =========================

        cardLayout = new CardLayout();

        painelPaginas =
                new JPanel(cardLayout);

        painelPaginas.setBackground(FUNDO);


        // Dashboard
        painelPaginas.add(
                criarConteudo(),
                "dashboard"
        );


        // Eventos
        painelPaginas.add(
                new JPanel(),
                "eventos"
        );




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
                new DashboardHeader(usuarioLogado),
                BorderLayout.NORTH
        );


        painel.add(
                criarDashboard(),
                BorderLayout.CENTER
        );


        return painel;
    }


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


        painel.add(
                new DashboardCards()
        );


        painel.add(
                Box.createVerticalStrut(18)
        );


        painel.add(
                new DashboardCenter()
        );


        painel.add(
                Box.createVerticalStrut(18)
        );


        painel.add(
                new RecentEventsTable()
        );


        return painel;
    }


    // =====================================================
    // TROCAR DE PÁGINA
    // =====================================================

    public void mostrarPagina(String pagina) {

        if (pagina.equals("eventos")) {

            painelPaginas.remove(
                    painelPaginas.getComponent(
                            1
                    )
            );

            painelPaginas.add(
                    new EventosPage(usuarioLogado),
                    "eventos"
            );

            painelPaginas.revalidate();
            painelPaginas.repaint();
        }

        cardLayout.show(
                painelPaginas,
                pagina
        );
    }


    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Usuario usuario = new Usuario();

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