package br.com.gerenciadoreventos.view;

import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.service.UsuarioService;
import br.com.gerenciadoreventos.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login extends JFrame {
    private static final Color FUNDO = new Color(248, 249, 251);
    private static final Color AZUL = new Color(65, 105, 225);
    private static final Color TEXTO = new Color(35, 38, 45);
    private static final Color CINZA = new Color(120, 125, 135);
    private static final Color VERMELHO = new Color(220, 60, 60);

    private final UsuarioService usuarioService = new UsuarioService();
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JLabel mensagemErro;

    public Login() {
        ThemeManager.instalarAtualizacaoAutomatica();
        configurarJanela();
        criarInterface();
        SwingUtilities.invokeLater(() -> ThemeManager.aplicarTema(this));
    }

    private void configurarJanela() {
        setTitle("Gerenciamento de eventos");
        var recursoIcone = getClass().getResource("/images/icon.png");
        if (recursoIcone != null) {
            setIconImage(new ImageIcon(recursoIcone).getImage());
        }
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void criarInterface() {
        JPanel principal = new JPanel(new GridLayout(1, 2));
        principal.setBackground(FUNDO);
        principal.add(criarPainelLogin());
        principal.add(criarPainelApresentacao());
        setContentPane(principal);
    }

    private JPanel criarPainelApresentacao() {
        JPanel painel = new JPanel();
        painel.setBackground(AZUL);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(new EmptyBorder(80, 50, 80, 50));
        JLabel titulo = criarLabel( "<html>Gerencie seus eventos escolares<br>de forma simples e organizada.</html>", 25, Font.BOLD, Color.WHITE );
        JLabel descricao = criarLabel(
                "<html>Crie eventos, acompanhe<br>atividades e distribua<br>responsabilidades.</html>", 14, Font.PLAIN, new Color(220, 225, 245) );
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(35));
        painel.add(descricao);
        return painel;
    }

    private JPanel criarPainelLogin() {
        JPanel painel = new JPanel();
        painel.setBackground(Color.WHITE);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(new EmptyBorder(70, 65, 70, 65));
        JLabel titulo = criarLabel("Bem-vindo!", 28, Font.BOLD, TEXTO);
        JLabel descricao = criarLabel("Entre para acessar o sistema", 14, Font.PLAIN, CINZA);
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(8));
        painel.add(descricao);
        painel.add(Box.createVerticalStrut(35));
        campoEmail = new JTextField();
        campoSenha = new JPasswordField();
        adicionarCampo(painel, "E-mail", campoEmail);
        painel.add(Box.createVerticalStrut(20));
        adicionarCampo(painel, "Senha", campoSenha);
        painel.add(Box.createVerticalStrut(30));
        painel.add(criarPainelAcoes());
        campoEmail.addActionListener(e -> campoSenha.requestFocusInWindow());
        campoSenha.addActionListener(e -> realizarLogin());
        return painel;
    }

    private JPanel criarPainelAcoes() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton entrar = new JButton("Entrar");
        entrar.setFont(new Font("Arial", Font.BOLD, 14));
        entrar.setForeground(Color.WHITE);
        entrar.setBackground(AZUL);
        entrar.setOpaque(true);
        entrar.setContentAreaFilled(true);
        entrar.setBorderPainted(false);
        entrar.setFocusPainted(false);
        entrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        entrar.setBorder(new EmptyBorder(13, 20, 13, 20));
        entrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        entrar.addActionListener(e -> realizarLogin());
        mensagemErro = criarLabel(" ", 12, Font.PLAIN, VERMELHO);
        mensagemErro.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel esqueceuSenha = criarLabel("<html><u>Esqueceu sua senha?</u></html>", 13, Font.PLAIN, AZUL);
        esqueceuSenha.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        esqueceuSenha.setAlignmentX(Component.CENTER_ALIGNMENT);
        esqueceuSenha.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirRecuperacaoSenha();
            }
        });
        painel.add(entrar);
        painel.add(Box.createVerticalStrut(8));
        painel.add(mensagemErro);
        painel.add(Box.createVerticalStrut(8));
        painel.add(esqueceuSenha);
        return painel;
    }

    private void adicionarCampo(JPanel painel, String texto, JTextField campo) {
        JLabel label = criarLabel(texto, 13, Font.BOLD, TEXTO);
        estilizarCampo(campo);
        painel.add(label);
        painel.add(Box.createVerticalStrut(8));
        painel.add(campo);
    }

    private JLabel criarLabel(String texto, int tamanho, int estilo, Color cor) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", estilo, tamanho));
        label.setForeground(cor);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void estilizarCampo(JTextField campo) {
        Dimension tamanho = new Dimension(300, 45);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        campo.setPreferredSize(tamanho);
        campo.setMinimumSize(tamanho);
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 222, 226)), new EmptyBorder(10, 12, 10, 12) ));
        campo.setBackground(new Color(250, 250, 251));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void realizarLogin() {
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());
        if (email.isBlank()) {
            exibirErro("Informe o e-mail.", campoEmail);
            return;
        }
        if (senha.isBlank()) {
            exibirErro("Informe a senha.", campoSenha);
            return;
        }
        Usuario usuario = usuarioService.autenticar(email, senha);
        if (usuario == null) {
            campoSenha.setText("");
            exibirErro("E-mail ou senha incorretos!", campoSenha);
            return;
        }
        new Dashboard(usuario).setVisible(true);
        dispose();
    }

    private void exibirErro(String texto, JComponent foco) {
        mensagemErro.setForeground(VERMELHO);
        mensagemErro.setText(texto);
        foco.requestFocusInWindow();
    }

    private void abrirRecuperacaoSenha() {
        RecuperacaoSenhaDialog dialog = new RecuperacaoSenhaDialog(this, campoEmail.getText());
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ThemeManager.instalarAtualizacaoAutomatica();
            new Login().setVisible(true);
        });
    }
}
