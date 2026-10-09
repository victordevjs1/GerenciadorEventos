package br.com.gerenciadoreventos.view.components;

import br.com.gerenciadoreventos.model.Usuario;
import br.com.gerenciadoreventos.view.Dashboard;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Sidebar extends JPanel {
    private static final Color SIDEBAR = new Color(15, 23, 42);

    private static final Color SIDEBAR_HOVER = new Color(30, 41, 59);

    private static final Color AZUL = new Color(37, 99, 235);

    private static final Color BRANCO = Color.WHITE;

    private static final Color TEXTO = new Color(226, 232, 240);

    private static final Color ICONE = new Color(148, 163, 184);

    private static final Font NORMAL = new Font( "Segoe UI", Font.PLAIN, 13 );

    private static final int LOGO_MAX_LARGURA = 165;
    private static final int LOGO_MAX_ALTURA = 42;

    // BOTÕES

    private JButton botaoDashboard;
    private JButton botaoEventos;
    private JButton botaoAlunos;
    private JButton botaoInscricoes;
    private JButton botaoResumo;
    private JButton botaoConfiguracoes;
    private JButton botaoComissoes;
    private JButton botaoProfessores;
    private JButton botaoAgentesExternos;

    // CONSTRUTOR

    public Sidebar(
            Dashboard dashboard,
            Usuario usuarioLogado,
            String paginaAtiva
    ) {

        // A sidebar mantém a identidade visual escura em ambos os temas.
        putClientProperty("theme.ignore", true);
        setPreferredSize( new Dimension(240, 0) );
        setBackground(SIDEBAR);
        setLayout( new BorderLayout() );
        add( criarLogo(), BorderLayout.NORTH );
        add( criarMenu( dashboard, usuarioLogado, paginaAtiva ), BorderLayout.CENTER );
        add( criarSair(dashboard), BorderLayout.SOUTH );
    }

    // LOGO

    private JPanel criarLogo() {
        JPanel painel = new JPanel();
        painel.setLayout( new BoxLayout( painel, BoxLayout.Y_AXIS ) );
        painel.setBackground(SIDEBAR);
        painel.setBorder( new EmptyBorder( 22, 20, 25, 20 ) );
        JLabel logo = criarLabelLogo();
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitulo = new JLabel( "Gerenciador de Eventos Escolares" );
        subtitulo.setForeground(ICONE);
        subtitulo.setFont( new Font( "Segoe UI", Font.PLAIN, 11 ) );
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(logo);
        painel.add(Box.createVerticalStrut(6));
        painel.add(subtitulo);
        return painel;
    }

    private JLabel criarLabelLogo() {
        try {
            URL caminhoLogo = localizarLogo();
            if (caminhoLogo != null) {
                BufferedImage imagemOriginal = ImageIO.read(caminhoLogo);
                if (imagemOriginal != null) {
                    BufferedImage imagemRecortada = recortarBordasTransparentes(imagemOriginal);
                    Image imagemRedimensionada = redimensionarProporcional( imagemRecortada, LOGO_MAX_LARGURA, LOGO_MAX_ALTURA );
                    return new JLabel(new ImageIcon(imagemRedimensionada));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JLabel logoTexto = new JLabel("e-task");
        logoTexto.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoTexto.setForeground(BRANCO);
        return logoTexto;
    }

    private URL localizarLogo() {
        String[] caminhos = {
                "/images/logoBranco.png",
                "../../images/logoBranco.png",
                "../../../images/logoBranco.png"
        };
        for (String caminho : caminhos) {
            URL url = getClass().getResource(caminho);
            if (url != null) {
                return url;
            }
        }
        System.out.println("Logo não encontrada em nenhum dos caminhos esperados.");
        return null;
    }

    private BufferedImage recortarBordasTransparentes(BufferedImage imagem) {
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();
        int minX = largura;
        int minY = altura;
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                int pixel = imagem.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;
                if (alpha > 10) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        if (maxX < minX || maxY < minY) {
            return imagem;
        }
        return imagem.getSubimage( minX, minY, (maxX - minX) + 1, (maxY - minY) + 1 );
    }

    private Image redimensionarProporcional(
            BufferedImage imagem,
            int maxLargura,
            int maxAltura
    ) {
        int larguraOriginal = imagem.getWidth();
        int alturaOriginal = imagem.getHeight();
        double escala = Math.min( (double) maxLargura / larguraOriginal, (double) maxAltura / alturaOriginal );
        int novaLargura = Math.max(1, (int) Math.round(larguraOriginal * escala));
        int novaAltura = Math.max(1, (int) Math.round(alturaOriginal * escala));
        return imagem.getScaledInstance( novaLargura, novaAltura, Image.SCALE_SMOOTH );
    }

    // MENU

    private JPanel criarMenu(
            Dashboard dashboard,
            Usuario usuarioLogado,
            String paginaAtiva
    ) {
        JPanel menu = new JPanel();
        menu.setLayout( new BoxLayout( menu, BoxLayout.Y_AXIS ) );
        menu.setBackground(SIDEBAR);
        menu.setBorder( new EmptyBorder( 5, 14, 10, 14 ) );
        botaoDashboard = item( FontAwesomeSolid.TH_LARGE, "Dashboard", paginaAtiva.equals("dashboard") );
        botaoDashboard.addActionListener(e ->
                dashboard.mostrarPagina("dashboard") );
        adicionarSecao( menu, "PRINCIPAL", botaoDashboard );
        menu.add( Box.createVerticalStrut(18) );
        botaoEventos = item( FontAwesomeSolid.CALENDAR_ALT, "Eventos", paginaAtiva.equals("eventos") );
        botaoEventos.addActionListener(e ->
                dashboard.mostrarPagina("eventos") );
        botaoAlunos = item( FontAwesomeSolid.USER_GRADUATE, "Alunos", paginaAtiva.equals("alunos") );
        botaoAlunos.addActionListener(e ->
                dashboard.mostrarPagina("alunos") );
        botaoInscricoes = item( FontAwesomeSolid.CHECK, "Inscrições", paginaAtiva.equals("inscricoes") );
        botaoInscricoes.addActionListener(e ->
                dashboard.mostrarPagina("inscricoes") );
        botaoProfessores = item( FontAwesomeSolid.USER_TIE, "Professores", paginaAtiva.equals("professores") );
        botaoProfessores.addActionListener(e ->
                dashboard.mostrarPagina("professores") );
        botaoComissoes = item( FontAwesomeSolid.USERS, "Comissões", paginaAtiva.equals("comissoes") );
        botaoComissoes.addActionListener(e ->
                dashboard.mostrarPagina("comissoes") );
        botaoAgentesExternos = item( FontAwesomeSolid.USER_TIE, "Agentes Externos", paginaAtiva.equals("agentesExternos") );
        botaoAgentesExternos.addActionListener(e ->
                dashboard.mostrarPagina("agentesExternos") );
        adicionarSecao(
                menu,
                "GESTÃO",
                botaoEventos,
                botaoAlunos,
                botaoProfessores,
                botaoInscricoes,
                botaoComissoes,
                botaoAgentesExternos
        );
        menu.add( Box.createVerticalStrut(18) );
        botaoResumo = item( FontAwesomeSolid.CHART_BAR, "Resumo", paginaAtiva.equals("resumo") );
        botaoResumo.addActionListener(e ->
                dashboard.mostrarPagina("resumo") );
        adicionarSecao(
                menu,
                "RELATÓRIOS",
                botaoResumo );
        menu.add( Box.createVerticalStrut(18) );
        botaoConfiguracoes = item( FontAwesomeSolid.COG, "Configurações", paginaAtiva.equals("configuracoes") );
        botaoConfiguracoes.addActionListener(e ->
                dashboard.mostrarPagina("configuracoes") );
        adicionarSecao( menu, "CONFIGURAÇÃO", botaoConfiguracoes );
        return menu;
    }

    private void adicionarSecao(
            JPanel menu,
            String titulo,
            JButton... itens
    ) {
        JLabel categoria = new JLabel(titulo);
        categoria.setFont( new Font( "Segoe UI", Font.BOLD, 10 ) );
        categoria.setForeground( new Color(100, 116, 139) );
        categoria.setBorder( new EmptyBorder( 8, 12, 8, 0 ) );
        menu.add(categoria);
        for (JButton item : itens) {
            menu.add(item);
        }
    }

    private JButton item(
            Ikon icone,
            String texto,
            boolean ativo
    ) {
        JButton botao = new JButton();
        botao.setPreferredSize( new Dimension(210, 44) );
        botao.setMaximumSize( new Dimension( Integer.MAX_VALUE, 44 ) );
        botao.setBorder( new EmptyBorder( 0, 12, 0, 12 ) );
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        botao.putClientProperty( "ativo", ativo );
        Color corTexto = ativo ? BRANCO : TEXTO;
        Color corIcone = ativo ? BRANCO : ICONE;
        JLabel label = labelIcone( texto, icone, corTexto, NORMAL );
        label.setIconTextGap(10);
        FontIcon icon = FontIcon.of(icone);
        icon.setIconSize(17);
        icon.setIconColor(corIcone);
        label.setIcon(icon);
        botao.putClientProperty( "label", label );
        botao.putClientProperty( "icone", icon );
        botao.add(label);
        botao.setBackground( ativo ? AZUL : SIDEBAR );
        return botao;
    }

    public void atualizarPaginaAtiva(
            String pagina
    ) {
        atualizarBotao( botaoDashboard, pagina.equals("dashboard") );
        atualizarBotao( botaoEventos, pagina.equals("eventos") );
        atualizarBotao( botaoAlunos, pagina.equals("alunos") );
        atualizarBotao( botaoProfessores, pagina.equals("professores") );
        atualizarBotao( botaoInscricoes, pagina.equals("inscricoes") );
        atualizarBotao( botaoComissoes, pagina.equals("comissoes") );
        atualizarBotao( botaoAgentesExternos, pagina.equals("agentesExternos") );
        atualizarBotao( botaoResumo, pagina.equals("resumo") );
        atualizarBotao( botaoConfiguracoes, pagina.equals("configuracoes") );
    }

    private void atualizarBotao(
            JButton botao,
            boolean ativo
    ) {
        if (botao == null) {
            return;
        }
        botao.putClientProperty( "ativo", ativo );
        botao.setBackground( ativo ? AZUL : SIDEBAR );
        JLabel label = (JLabel) botao.getClientProperty( "label" );
        if (label != null) {
            label.setForeground( ativo ? BRANCO : TEXTO );
        }
        FontIcon icon = (FontIcon) botao.getClientProperty( "icone" );
        if (icon != null) {
            icon.setIconColor( ativo ? BRANCO : ICONE );
        }
    }

    private JPanel criarSair(
            Dashboard dashboard
    ) {
        JPanel painel = new JPanel( new BorderLayout() );
        painel.setBackground(SIDEBAR);
        painel.setBorder( new EmptyBorder( 10, 14, 20, 14 ) );
        JButton sair = item( FontAwesomeSolid.SIGN_OUT_ALT, "Sair", false );
        sair.addActionListener(e -> dashboard.sairParaLogin());
        painel.add(sair);
        return painel;
    }

    private JLabel labelIcone(
            String texto,
            Ikon icone,
            Color cor,
            Font fonte
    ) {
        FontIcon icon = FontIcon.of(icone);
        icon.setIconSize(18);
        icon.setIconColor(cor);
        JLabel label = new JLabel( texto, icon, JLabel.LEFT );
        label.setFont(fonte);
        label.setForeground(cor);
        return label;
    }
}
