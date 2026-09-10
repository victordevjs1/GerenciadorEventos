package br.com.gerenciadoreventos.view.components;

import br.com.gerenciadoreventos.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardHeader extends JPanel {

    private static final Color FUNDO =
            new Color(246, 248, 252);

    private static final Color AZUL =
            new Color(37, 99, 235);

    private static final Color TEXTO =
            new Color(15, 23, 42);

    private static final Color SECUNDARIO =
            new Color(100, 116, 139);

    private static final Font NORMAL =
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    13
            );

    private static final Font MEDIUM =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    14
            );

    private static final Font TITULO =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    25
            );

    public DashboardHeader(Usuario usuario) {

        setLayout(
                new BorderLayout()
        );

        setBackground(FUNDO);

        setBorder(
                new EmptyBorder(
                        0,
                        0,
                        20,
                        0
                )
        );

        add(
                criarTextos(),
                BorderLayout.WEST
        );

        add(
                criarUsuario(usuario),
                BorderLayout.EAST
        );
    }

    private JPanel criarTextos() {

        JPanel painel = new JPanel();

        painel.setLayout(
                new BoxLayout(
                        painel,
                        BoxLayout.Y_AXIS
                )
        );

        painel.setBackground(FUNDO);

        JLabel titulo =
                new JLabel("Dashboard");

        titulo.setFont(TITULO);
        titulo.setForeground(TEXTO);

        JLabel descricao =
                new JLabel(
                        "Visão geral dos eventos escolares"
                );

        descricao.setFont(NORMAL);
        descricao.setForeground(SECUNDARIO);

        painel.add(titulo);

        painel.add(
                Box.createVerticalStrut(3)
        );

        painel.add(descricao);

        return painel;
    }

    private JPanel criarUsuario(
            Usuario usuario
    ) {

        JPanel painel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        painel.setBackground(FUNDO);

        String nome =
                obterNome(usuario);

        String tipo =
                obterTipo(usuario);

        JLabel nomeLabel =
                new JLabel(nome);

        nomeLabel.setFont(MEDIUM);
        nomeLabel.setForeground(TEXTO);

        JLabel tipoLabel =
                new JLabel(tipo);

        tipoLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        tipoLabel.setForeground(SECUNDARIO);

        JPanel info = new JPanel();

        info.setLayout(
                new BoxLayout(
                        info,
                        BoxLayout.Y_AXIS
                )
        );

        info.setBackground(FUNDO);

        info.add(nomeLabel);
        info.add(tipoLabel);

        JLabel avatar =
                new JLabel(
                        obterInicial(nome)
                );

        avatar.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        avatar.setVerticalAlignment(
                SwingConstants.CENTER
        );

        avatar.setPreferredSize(
                new Dimension(38, 38)
        );

        avatar.setOpaque(true);
        avatar.setBackground(AZUL);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(MEDIUM);

        painel.add(info);
        painel.add(avatar);

        return painel;
    }

    private String obterNome(
            Usuario usuario
    ) {

        if (
                usuario == null
                        ||
                        usuario.getNome() == null
                        ||
                        usuario.getNome().isBlank()
        ) {
            return "Usuário";
        }

        return usuario.getNome();
    }

    private String obterTipo(
            Usuario usuario
    ) {

        if (usuario == null) {
            return "Sistema";
        }

        String tipo =
                usuario.getTipoUsuario();

        if (
                tipo == null
                        ||
                        tipo.isBlank()
        ) {
            return "Usuário";
        }

        return switch (
                tipo.toUpperCase()
                ) {

            case "ADMINISTRADOR" ->
                    "Administrador";

            case "DIRETOR" ->
                    "Diretor";

            case "COORDENADOR" ->
                    "Coordenador";

            case "COLABORADOR" ->
                    "Colaborador";

            case "ALUNO" ->
                    "Aluno";

            default ->
                    tipo;
        };
    }

    private String obterInicial(
            String nome
    ) {

        if (
                nome == null
                        ||
                        nome.isBlank()
        ) {
            return "?";
        }

        return nome
                .trim()
                .substring(0, 1)
                .toUpperCase();
    }
}