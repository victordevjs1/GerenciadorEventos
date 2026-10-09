package br.com.gerenciadoreventos.view.pages;

import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.service.UsuarioService;
import br.com.gerenciadoreventos.theme.ThemeManager;
import br.com.gerenciadoreventos.view.Dashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ConfiguracoesPage extends JPanel {
    private final Dashboard dashboard;
    private final Usuario usuario;
    private final UsuarioService usuarioService = new UsuarioService();

    private JTextField campoNome;
    private JTextField campoEmail;
    private JPasswordField campoSenhaAtual;
    private JPasswordField campoNovaSenha;
    private JPasswordField campoConfirmarSenha;
    private JComboBox<String> comboIdioma;
    private SwitchButton switchModoEscuro;

    public ConfiguracoesPage(Dashboard dashboard, Usuario usuario) {
        this.dashboard = dashboard;
        this.usuario = usuario;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 28, 28, 28));
        setBackground(ThemeManager.getFundo());
        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JComponent criarConteudo() {
        JPanel externo = new JPanel(new BorderLayout());
        externo.setOpaque(false);
        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("Configurações");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(ThemeManager.getTexto());
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        conteudo.add(titulo);
        conteudo.add(Box.createVerticalStrut(18));
        JPanel perfil = criarCardPerfil();
        perfil.setAlignmentX(Component.LEFT_ALIGNMENT);
        conteudo.add(perfil);
        conteudo.add(Box.createVerticalStrut(18));
        JPanel preferencias = criarCardPreferencias();
        preferencias.setAlignmentX(Component.LEFT_ALIGNMENT);
        conteudo.add(preferencias);
        conteudo.add(Box.createVerticalGlue());
        JScrollPane scroll = new JScrollPane(conteudo);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        externo.add(scroll, BorderLayout.CENTER);
        return externo;
    }

    private JPanel criarCardPerfil() {
        JPanel card = criarCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();
        int linha = 0;
        JLabel titulo = tituloSecao("Configurações de Perfil");
        gbc.gridx = 0;
        gbc.gridy = linha++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(titulo, gbc);
        campoNome = new JTextField(valor(usuario != null ? usuario.getNome() : null));
        linha = adicionarCampo(card, gbc, linha, "Nome de Usuário", campoNome);
        campoEmail = new JTextField(valor(usuario != null ? usuario.getEmail() : null));
        linha = adicionarCampo(card, gbc, linha, "Email", campoEmail);
        campoSenhaAtual = new JPasswordField();
        linha = adicionarCampo(card, gbc, linha, "Senha Atual", campoSenhaAtual);
        campoNovaSenha = new JPasswordField();
        linha = adicionarCampo(card, gbc, linha, "Nova Senha", campoNovaSenha);
        campoConfirmarSenha = new JPasswordField();
        linha = adicionarCampo(card, gbc, linha, "Confirmar Nova Senha", campoConfirmarSenha);
        JButton salvar = botaoPrimario("Salvar Alterações");
        salvar.addActionListener(e -> salvarPerfil());
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 0, 0);
        card.add(salvar, gbc);
        return card;
    }

    private JPanel criarCardPreferencias() {
        JPanel card = criarCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();
        int linha = 0;
        JLabel titulo = tituloSecao("Preferências do Sistema");
        gbc.gridx = 0;
        gbc.gridy = linha++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(titulo, gbc);
        JLabel idiomaLabel = labelCampo("Idioma");
        gbc.gridx = 0;
        gbc.gridy = linha++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(idiomaLabel, gbc);
        comboIdioma = new JComboBox<>(new String[]{"Português (Brasil)"});
        estilizarCampo(comboIdioma);
        comboIdioma.setMaximumRowCount(3);
        gbc.gridx = 0;
        gbc.gridy = linha++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(comboIdioma, gbc);
        JLabel modoLabel = labelCampo("Modo Escuro");
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 14, 0);
        card.add(modoLabel, gbc);
        switchModoEscuro = new SwitchButton();
        switchModoEscuro.setSelected(ThemeManager.isModoEscuro());
        gbc.gridx = 1;
        gbc.gridy = linha++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(switchModoEscuro, gbc);
        JButton aplicar = botaoSecundario("Aplicar Preferências");
        aplicar.addActionListener(e -> aplicarPreferencias());
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 0, 0, 0);
        card.add(aplicar, gbc);
        return card;
    }

    private void salvarPerfil() {
        if (usuario == null) {
            mostrarErro("Não foi possível identificar o usuário logado.");
            return;
        }
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String senhaAtual = new String(campoSenhaAtual.getPassword());
        String novaSenha = new String(campoNovaSenha.getPassword());
        String confirmar = new String(campoConfirmarSenha.getPassword());
        try {
            usuarioService.atualizarPerfil( usuario, nome, email, senhaAtual, novaSenha, confirmar );
            campoSenhaAtual.setText("");
            campoNovaSenha.setText("");
            campoConfirmarSenha.setText("");
            dashboard.atualizarDadosUsuario();
            JOptionPane.showMessageDialog( this, "Alterações salvas com sucesso.", "Configurações", JOptionPane.INFORMATION_MESSAGE );
        } catch (IllegalArgumentException ex) {
            mostrarErro(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarErro("Não foi possível salvar as alterações.");
        }
    }

    private void aplicarPreferencias() {
        ThemeManager.setModoEscuro(switchModoEscuro.isSelected());
        dashboard.aplicarTemaGlobal();
        JOptionPane.showMessageDialog( this, "Preferências aplicadas com sucesso.", "Configurações", JOptionPane.INFORMATION_MESSAGE );
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog( this, mensagem, "Configurações", JOptionPane.ERROR_MESSAGE );
    }

    private JPanel criarCard() {
        JPanel card = new JPanel();
        card.setBackground(ThemeManager.getPainel());
        card.setBorder(BorderFactory.createCompoundBorder( new LineBorder(ThemeManager.getBorda(), 1, true), new EmptyBorder(18, 18, 18, 18) ));
        card.setMaximumSize(new Dimension(760, Integer.MAX_VALUE));
        return card;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private int adicionarCampo(
            JPanel card,
            GridBagConstraints gbc,
            int linha,
            String texto,
            JComponent campo
    ) {
        JLabel label = labelCampo(texto);
        gbc.gridx = 0;
        gbc.gridy = linha++;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(label, gbc);
        estilizarCampo(campo);
        gbc.gridx = 0;
        gbc.gridy = linha++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 14, 0);
        card.add(campo, gbc);
        return linha;
    }

    private void estilizarCampo(JComponent campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBackground(ThemeManager.getCampo());
        campo.setForeground(ThemeManager.getTexto());
        campo.setBorder(BorderFactory.createCompoundBorder( new LineBorder(ThemeManager.getBorda(), 1, true), new EmptyBorder(8, 10, 8, 10) ));
        campo.setPreferredSize(new Dimension(320, 36));
        campo.setMinimumSize(new Dimension(160, 36));
    }

    private JLabel tituloSecao(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(ThemeManager.getTexto());
        return label;
    }

    private JLabel labelCampo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(ThemeManager.getTexto());
        return label;
    }

    private JButton botaoPrimario(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setForeground(Color.WHITE);
        botao.setBackground(ThemeManager.AZUL);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(9, 14, 9, 14));
        return botao;
    }

    private JButton botaoSecundario(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setForeground(ThemeManager.getTexto());
        botao.setBackground(ThemeManager.getPainel());
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createCompoundBorder( new LineBorder(ThemeManager.getBorda(), 1, true), new EmptyBorder(7, 12, 7, 12) ));
        return botao;
    }

    private String valor(String valor) {
        return valor == null ? "" : valor;
    }

    // Toggle visual feito somente com Swing/AWT.
    private static class SwitchButton extends JToggleButton {
        SwitchButton() {
            setPreferredSize(new Dimension(40, 22));
            setMinimumSize(new Dimension(40, 22));
            setMaximumSize(new Dimension(40, 22));
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            Color trilho = isSelected() ? ThemeManager.AZUL : (ThemeManager.isModoEscuro() ? new Color(71, 85, 105) : new Color(203, 213, 225));
            g2.setColor(trilho);
            g2.fillRoundRect(0, 0, w, h, h, h);
            int diametro = h - 6;
            int x = isSelected() ? w - diametro - 3 : 3;
            g2.setColor(Color.WHITE);
            g2.fillOval(x, 3, diametro, diametro);
            g2.dispose();
        }
    }
}
