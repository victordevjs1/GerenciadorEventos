package br.com.gerenciadoreventos.view.pages;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RelatoriosPage extends JPanel {

    private static final Color FUNDO = new Color(246, 248, 252);
    private static final Color TEXTO = new Color(15, 23, 42);
    private static final Color CINZA_TEXTO = new Color(100, 116, 139);


    public RelatoriosPage() {

        setLayout(new BorderLayout());
        setBackground(FUNDO);

        JPanel conteudo = new JPanel(new BorderLayout(0, 20));
        conteudo.setBackground(FUNDO);
        conteudo.setBorder(new EmptyBorder(25, 25, 25, 25));

        conteudo.add(criarCabecalho(), BorderLayout.NORTH);
        conteudo.add(criarAbas(), BorderLayout.CENTER);

        add(conteudo, BorderLayout.CENTER);
    }


    // =====================================================
    // CABEÇALHO
    // =====================================================

    private JPanel criarCabecalho() {

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(FUNDO);

        JLabel titulo = new JLabel("Relatórios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel(
                "Presenças, participações e indicadores dos eventos"
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(CINZA_TEXTO);

        painel.add(titulo);
        painel.add(Box.createVerticalStrut(4));
        painel.add(subtitulo);

        return painel;
    }


    // =====================================================
    // ABAS
    // =====================================================

    private JTabbedPane criarAbas() {

        JTabbedPane abas = new JTabbedPane();

        abas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        abas.setBackground(Color.WHITE);
        abas.setForeground(TEXTO);

        // =================================================
        // ABA 1: RESUMO
        // (cards de totais + lista de relatórios + tela de
        // desempenho por evento — ver ResumoRelatoriosPage)
        // =================================================

        abas.addTab(
                "Resumo",
                new ResumoRelatoriosPage()
        );

        // =================================================
        // ABA 2: IMPORTAR PRESENÇA (CSV)
        // =================================================

        abas.addTab(
                "Importar Presença (CSV)",
                new ImportarPresencaCsvPage()
        );

        return abas;
    }
}