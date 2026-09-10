package br.com.gerenciadoreventos.view;

import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.service.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Login extends JFrame {

    private UsuarioService usuarioService = new UsuarioService();

    private JTextField campoUsuario;
    private JPasswordField campoSenha;
    private JButton botaoEntrar;
    private JLabel mensagemErro;

    // Cores
    private final Color FUNDO = new Color(248, 249, 251);
    private final Color AZUL = new Color(65, 105, 225);
    private final Color TEXTO = new Color(35, 38, 45);
    private final Color CINZA = new Color(120, 125, 135);

    public Login() {

        setTitle("Login - Gerenciador de Eventos");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        criarInterface();
    }

    private void criarInterface() {

        JPanel principal = new JPanel(new GridLayout(1, 2));

        principal.setBackground(FUNDO);


        // =====================================
        // PAINEL DE APRESENTAÇÃO
        // =====================================

        JPanel painelApresentacao = new JPanel();

        painelApresentacao.setBackground(AZUL);

        painelApresentacao.setLayout(
                new BoxLayout(
                        painelApresentacao,
                        BoxLayout.Y_AXIS
                )
        );

        painelApresentacao.setBorder(
                new EmptyBorder(
                        80, 50, 80, 50
                )
        );


        JLabel tituloSistema =
                new JLabel(
                        "<html>Gerencie seus eventos escolares <br>" +
                                "de forma simples e organizada."
                );

        tituloSistema.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        25
                )
        );

        tituloSistema.setForeground(Color.WHITE);

        tituloSistema.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        JLabel descricao =
                new JLabel(
                        "<html>Crie eventos, acompanhe<br>" +
                                "atividades e distribua<br>" +
                                "responsabilidades.</html>"
                );

        descricao.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        descricao.setForeground(
                new Color(220, 225, 245)
        );

        descricao.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        painelApresentacao.add(tituloSistema);

        painelApresentacao.add(
                Box.createVerticalStrut(5)
        );

        painelApresentacao.add(
                Box.createVerticalStrut(35)
        );

        painelApresentacao.add(descricao);


        // =====================================
        // PAINEL DE LOGIN
        // =====================================

        JPanel painelLogin = new JPanel();

        painelLogin.setBackground(Color.WHITE);

        painelLogin.setLayout(
                new BoxLayout(
                        painelLogin,
                        BoxLayout.Y_AXIS
                )
        );

        painelLogin.setBorder(
                new EmptyBorder(
                        70, 65, 70, 65
                )
        );


        JLabel titulo =
                new JLabel("Bem-vindo!");

        titulo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        titulo.setForeground(TEXTO);

        titulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        JLabel descricaoLogin =
                new JLabel(
                        "Entre para acessar o sistema"
                );

        descricaoLogin.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        descricaoLogin.setForeground(CINZA);

        descricaoLogin.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        painelLogin.add(titulo);

        painelLogin.add(
                Box.createVerticalStrut(8)
        );

        painelLogin.add(descricaoLogin);

        painelLogin.add(
                Box.createVerticalStrut(35)
        );


        // =====================================
        // USUÁRIO
        // =====================================

        JLabel labelUsuario =
                new JLabel("E-mail");


        labelUsuario.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        labelUsuario.setForeground(TEXTO);

        labelUsuario.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        campoUsuario =
                new JTextField();

        estilizarCampo(campoUsuario);

        campoUsuario.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        painelLogin.add(labelUsuario);

        painelLogin.add(
                Box.createVerticalStrut(8)
        );

        painelLogin.add(campoUsuario);

        painelLogin.add(
                Box.createVerticalStrut(20)
        );


        // =====================================
        // SENHA
        // =====================================

        JLabel labelSenha =
                new JLabel("Senha");

        labelSenha.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        labelSenha.setForeground(TEXTO);

        labelSenha.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        campoSenha =
                new JPasswordField();

        estilizarCampo(campoSenha);

        campoSenha.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        painelLogin.add(labelSenha);

        painelLogin.add(
                Box.createVerticalStrut(8)
        );

        painelLogin.add(campoSenha);

        painelLogin.add(
                Box.createVerticalStrut(30)
        );


        // =====================================
        // BOTÃO ENTRAR
        // =====================================

        botaoEntrar =
                new JButton("Entrar");

        botaoEntrar.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        botaoEntrar.setForeground(Color.WHITE);

        botaoEntrar.setBackground(AZUL);

        botaoEntrar.setBorder(
                new EmptyBorder(
                        13, 20, 13, 20
                )
        );

        botaoEntrar.setFocusPainted(false);

        botaoEntrar.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );


        // =====================================
        // MENSAGEM DE ERRO
        // =====================================

        mensagemErro =
                new JLabel(" ");

        mensagemErro.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        mensagemErro.setForeground(
                new Color(220, 60, 60)
        );


        // =====================================
        // LINK ESQUECI A SENHA
        // =====================================

        JLabel esqueceuSenha =
                new JLabel(
                        "<html><u>Esqueceu sua senha?</u></html>"
                );

        esqueceuSenha.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        esqueceuSenha.setForeground(AZUL);

        esqueceuSenha.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );


        // =====================================
        // PAINEL DAS AÇÕES
        // =====================================

        JPanel painelAcoes =
                new JPanel();

        painelAcoes.setLayout(
                new BoxLayout(
                        painelAcoes,
                        BoxLayout.Y_AXIS
                )
        );

        painelAcoes.setBackground(Color.WHITE);

        painelAcoes.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        botaoEntrar.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        mensagemErro.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        esqueceuSenha.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );


        painelAcoes.add(botaoEntrar);

        painelAcoes.add(
                Box.createVerticalStrut(8)
        );

        painelAcoes.add(mensagemErro);

        painelAcoes.add(
                Box.createVerticalStrut(8)
        );

        painelAcoes.add(esqueceuSenha);


        painelLogin.add(painelAcoes);


        // =====================================
        // EVENTO DO LOGIN
        // =====================================

        botaoEntrar.addActionListener(
                e -> realizarLogin()
        );


        // =====================================
        // ENTER PARA LOGAR
        // =====================================

        campoSenha.addActionListener(
                e -> realizarLogin()
        );


        // =====================================
        // JUNTAR OS DOIS PAINÉIS
        // =====================================

        // Login fica à esquerda
        principal.add(painelLogin);

        // Apresentação azul fica à direita
        principal.add(painelApresentacao);

        add(principal);
    }


    // =====================================
    // ESTILIZAR CAMPOS
    // =====================================

    private void estilizarCampo(
            JTextField campo
    ) {

        campo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        45
                )
        );

        campo.setPreferredSize(
                new Dimension(
                        300,
                        45
                )
        );

        campo.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createLineBorder(
                                new Color(220, 222, 226)
                        ),

                        new EmptyBorder(
                                10, 12, 10, 12
                        )
                )
        );

        campo.setBackground(
                new Color(250, 250, 251)
        );
    }


    // =====================================
    // LOGIN
    // =====================================

    private void realizarLogin() {

        String email =
                campoUsuario.getText().trim();

        String senha =
                new String(
                        campoSenha.getPassword()
                );

        Usuario usuarioEncontrado =
                usuarioService.autenticar(
                        email,
                        senha
                );

        if (usuarioEncontrado != null) {

            Dashboard dashboard =
                    new Dashboard();

            dashboard.setVisible(true);

            dispose();

        } else {

            mensagemErro.setText(
                    "E-mail ou senha incorretos!"
            );
        }
    }



    // =====================================
    // MAIN
    // =====================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Login login =
                    new Login();

            login.setVisible(true);

        });
    }
}