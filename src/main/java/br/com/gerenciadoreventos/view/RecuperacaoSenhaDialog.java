package br.com.gerenciadoreventos.view;

import br.com.gerenciadoreventos.service.RecuperacaoSenhaService;
import br.com.gerenciadoreventos.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RecuperacaoSenhaDialog extends JDialog {
    private static final String ETAPA_EMAIL = "email";
    private static final String ETAPA_CODIGO = "codigo";
    private static final String ETAPA_SENHA = "senha";

    private final RecuperacaoSenhaService service = new RecuperacaoSenhaService();
    private final CardLayout cards = new CardLayout();
    private final JPanel conteudo = new JPanel(cards);

    private JTextField campoEmail;
    private JTextField campoCodigo;
    private JPasswordField campoNovaSenha;
    private JPasswordField campoConfirmacao;
    private String emailSolicitado;
    private String codigoValidado;

    public RecuperacaoSenhaDialog(Window dono, String emailInicial) {
        super(dono, "Recuperar senha", ModalityType.APPLICATION_MODAL);
        configurarJanela();
        conteudo.add(criarEtapaEmail(emailInicial), ETAPA_EMAIL);
        conteudo.add(criarEtapaCodigo(), ETAPA_CODIGO);
        conteudo.add(criarEtapaSenha(), ETAPA_SENHA);
        setContentPane(conteudo);
        ThemeManager.aplicarTema(this);
    }

    private void configurarJanela() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(470, 360);
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    private JPanel criarEtapaEmail(String emailInicial) {
        JPanel painel = painelBase("Recuperar senha", "Informe o e-mail cadastrado para receber um código de 6 dígitos.");
        campoEmail = new JTextField(emailInicial == null ? "" : emailInicial.trim());
        adicionarCampo(painel, "E-mail", campoEmail);
        JButton enviar = botaoPrimario("Enviar código");
        enviar.addActionListener(e -> enviarCodigo(enviar));
        campoEmail.addActionListener(e -> enviar.doClick());
        painel.add(Box.createVerticalStrut(22));
        painel.add(enviar);
        return painel;
    }

    private JPanel criarEtapaCodigo() {
        JPanel painel = painelBase("Verifique seu e-mail", "Digite o código enviado. Ele expira em 10 minutos.");
        campoCodigo = new JTextField();
        campoCodigo.setHorizontalAlignment(JTextField.CENTER);
        adicionarCampo(painel, "Código", campoCodigo);
        JButton validar = botaoPrimario("Validar código");
        validar.addActionListener(e -> validarCodigo());
        campoCodigo.addActionListener(e -> validar.doClick());
        JButton reenviar = botaoLink("Reenviar código");
        reenviar.addActionListener(e -> reenviarCodigo(reenviar));
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        acoes.setOpaque(false);
        acoes.add(validar);
        acoes.add(Box.createHorizontalStrut(12));
        acoes.add(reenviar);
        painel.add(Box.createVerticalStrut(22));
        painel.add(acoes);
        return painel;
    }

    private JPanel criarEtapaSenha() {
        JPanel painel = painelBase("Crie uma nova senha", "Use pelo menos 6 caracteres.");
        campoNovaSenha = new JPasswordField();
        campoConfirmacao = new JPasswordField();
        adicionarCampo(painel, "Nova senha", campoNovaSenha);
        painel.add(Box.createVerticalStrut(12));
        adicionarCampo(painel, "Confirmar nova senha", campoConfirmacao);
        JButton salvar = botaoPrimario("Alterar senha");
        salvar.addActionListener(e -> redefinirSenha(salvar));
        campoConfirmacao.addActionListener(e -> salvar.doClick());
        painel.add(Box.createVerticalStrut(22));
        painel.add(salvar);
        return painel;
    }

    private JPanel painelBase(String titulo, String descricao) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(new EmptyBorder(28, 34, 28, 34));
        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        labelTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel labelDescricao = new JLabel("<html>" + descricao + "</html>");
        labelDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelDescricao.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(labelTitulo);
        painel.add(Box.createVerticalStrut(8));
        painel.add(labelDescricao);
        painel.add(Box.createVerticalStrut(24));
        return painel;
    }

    private void adicionarCampo(JPanel painel, String texto, JTextField campo) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)), new EmptyBorder(9, 11, 9, 11) ));
        painel.add(label);
        painel.add(Box.createVerticalStrut(6));
        painel.add(campo);
    }

    private JButton botaoPrimario(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setForeground(Color.WHITE);
        botao.setBackground(ThemeManager.AZUL);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(11, 18, 11, 18));
        return botao;
    }

    private JButton botaoLink(String texto) {
        JButton botao = new JButton(texto);
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setForeground(ThemeManager.AZUL);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private void enviarCodigo(JButton botao) {
        String email = campoEmail.getText().trim();
        executarAsync(
                botao,
                () -> service.solicitarCodigo(email),
                () -> {
                    emailSolicitado = email;
                    cards.show(conteudo, ETAPA_CODIGO);
                    campoCodigo.requestFocusInWindow();
                    mostrarInformacao("Código enviado para " + emailSolicitado + ".");
                }
        );
    }

    private void reenviarCodigo(JButton botao) {
        executarAsync(
                botao,
                () -> service.solicitarCodigo(emailSolicitado),
                () -> {
                    campoCodigo.setText("");
                    campoCodigo.requestFocusInWindow();
                    mostrarInformacao("Um novo código foi enviado.");
                }
        );
    }

    private void validarCodigo() {
        try {
            codigoValidado = campoCodigo.getText().trim();
            service.validarCodigo(emailSolicitado, codigoValidado);
            cards.show(conteudo, ETAPA_SENHA);
            campoNovaSenha.requestFocusInWindow();
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Não foi possível validar o código.");
        }
    }

    private void redefinirSenha(JButton botao) {
        String novaSenha = new String(campoNovaSenha.getPassword());
        String confirmacao = new String(campoConfirmacao.getPassword());
        executarAsync(
                botao,
                () -> service.redefinirSenha(emailSolicitado, codigoValidado, novaSenha, confirmacao),
                () -> {
                    mostrarInformacao("Senha alterada com sucesso. Você já pode entrar no sistema.");
                    dispose();
                }
        );
    }

    private void executarAsync(JButton botao, Runnable tarefa, Runnable sucesso) {
        botao.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private RuntimeException erro;
            @Override
            protected Void doInBackground() {
                try {
                    tarefa.run();
                } catch (RuntimeException e) {
                    erro = e;
                }
                return null;
            }
            @Override
            protected void done() {
                botao.setEnabled(true);
                setCursor(Cursor.getDefaultCursor());
                if (erro != null) {
                    erro.printStackTrace();
                    mostrarErro(mensagemErro(erro));
                    return;
                }
                sucesso.run();
            }
        };
        worker.execute();
    }

    private String mensagemErro(RuntimeException erro) {
        if (erro instanceof IllegalArgumentException || erro instanceof IllegalStateException) {
            return erro.getMessage();
        }
        return "Não foi possível concluir a recuperação de senha.";
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Recuperar senha", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInformacao(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Recuperar senha", JOptionPane.INFORMATION_MESSAGE);
    }
}
