package br.com.gerenciadoreventos.view.components;

import br.com.gerenciadoreventos.model.Evento;
import br.com.gerenciadoreventos.service.DashboardService;
import br.com.gerenciadoreventos.theme.ThemeManager;

import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UpcomingEvents extends JPanel {
    private final DashboardService service = new DashboardService();

    private static final Color FUNDO = Color.WHITE;
    private static final Color AZUL = new Color(122, 162, 247);
    private static final Color TEXTO = new Color(35, 35, 45);
    private static final Color CINZA = new Color(110, 110, 120);
    private static final Color BORDA = new Color(235, 236, 240);

    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd 'de' MMMM", new java.util.Locale("pt", "BR"));

    public UpcomingEvents() {
        setLayout(new BorderLayout());
        setBackground(FUNDO);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // CABEÇALHO

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(FUNDO);
        JLabel titulo = new JLabel("Próximos eventos");
        titulo.setFont(new Font("Arial", Font.BOLD, 17));
        titulo.setForeground(TEXTO);
        JLabel subtitulo = new JLabel("Eventos agendados");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitulo.setForeground(CINZA);
        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(FUNDO);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);
        cabecalho.add(textos, BorderLayout.WEST);
        add(cabecalho, BorderLayout.NORTH);

        // LISTA

        JPanel lista = new JPanel();
        lista.setLayout( new BoxLayout(lista, BoxLayout.Y_AXIS) );
        lista.setBackground(FUNDO);
        List<Evento> eventos = service.obterProximosEventos(4);
        if (eventos.isEmpty()) {
            JLabel vazio = new JLabel( "Nenhum evento próximo." );
            vazio.setFont( new Font("Arial", Font.PLAIN, 13) );
            vazio.setForeground(CINZA);
            vazio.setAlignmentX(Component.LEFT_ALIGNMENT);
            lista.add(Box.createVerticalStrut(25));
            lista.add(vazio);
        } else {
            lista.add(Box.createVerticalStrut(15));
            for (Evento evento : eventos) {
                JPanel card = criarCard(evento);
                card.setAlignmentX( Component.LEFT_ALIGNMENT );
                lista.add(card);
                lista.add(Box.createVerticalStrut(10));
            }
        }
        add(lista, BorderLayout.CENTER);
    }

    // CARD DO EVENTO

    private JPanel criarCard(Evento evento) {
        JPanel card = new JPanel( new BorderLayout(14, 0) );
        card.setBackground(ThemeManager.getPainel());
        card.setBorder( BorderFactory.createCompoundBorder( new RoundedBorder(10, BORDA), new EmptyBorder(12, 12, 12, 12) ) );

        // DATA

        JPanel painelData = new JPanel();
        painelData.setLayout( new BoxLayout( painelData, BoxLayout.Y_AXIS ) );
        painelData.setBackground( new Color(242, 246, 255) );
        painelData.setPreferredSize( new Dimension(62, 58) );
        painelData.setBorder( new EmptyBorder(7, 5, 7, 5) );
        String dia = String.valueOf( evento.getDataInicio().getDayOfMonth() );
        String mes = evento.getDataInicio()
                .format( DateTimeFormatter.ofPattern( "MMM", new java.util.Locale("pt", "BR") ) ) .replace(".", "") .toUpperCase();
        JLabel labelDia = new JLabel(dia);
        labelDia.setFont( new Font("Arial", Font.BOLD, 20) );
        labelDia.setForeground(AZUL);
        labelDia.setAlignmentX( Component.CENTER_ALIGNMENT );
        JLabel labelMes = new JLabel(mes);
        labelMes.setFont( new Font("Arial", Font.BOLD, 10) );
        labelMes.setForeground(AZUL);
        labelMes.setAlignmentX( Component.CENTER_ALIGNMENT );
        painelData.add(labelDia);
        painelData.add(Box.createVerticalStrut(1));
        painelData.add(labelMes);
        card.add( painelData, BorderLayout.WEST );

        // INFORMAÇÕES

        JPanel informacoes = new JPanel();
        informacoes.setLayout( new BoxLayout( informacoes, BoxLayout.Y_AXIS ) );
        informacoes.setBackground(ThemeManager.getPainel());
        JLabel nome = new JLabel( evento.getNome() );
        nome.setFont( new Font("Arial", Font.BOLD, 14) );
        nome.setForeground(TEXTO);
        nome.setAlignmentX( Component.LEFT_ALIGNMENT );
        informacoes.add(nome);
        informacoes.add( Box.createVerticalStrut(7) );

        // Horário

        JLabel horario = new JLabel( evento.getDataInicio() .format( DateTimeFormatter.ofPattern("HH:mm") ) );
        horario.setIcon( FontIcon.of( FontAwesomeSolid.CLOCK, 12, CINZA ) );
        horario.setIconTextGap(6);
        horario.setFont( new Font("Arial", Font.PLAIN, 12) );
        horario.setForeground(CINZA);
        horario.setAlignmentX( Component.LEFT_ALIGNMENT );
        informacoes.add(horario);
        informacoes.add( Box.createVerticalStrut(4) );

        // Local

        String local = evento.getLocal();
        if (local == null || local.isBlank()) {
            local = "Local não informado";
        }
        JLabel labelLocal = new JLabel( local );
        labelLocal.setIcon( FontIcon.of( FontAwesomeSolid.MAP_MARKER_ALT, 12, CINZA ) );
        labelLocal.setIconTextGap(6);
        labelLocal.setForeground(CINZA);
        labelLocal.setAlignmentX( Component.LEFT_ALIGNMENT );
        informacoes.add(labelLocal);
        card.add( informacoes, BorderLayout.CENTER );
        return card;
    }

    // BORDA ARREDONDADA

    private static class RoundedBorder
            implements javax.swing.border.Border {
        private final int radius;
        private final Color cor;
        public RoundedBorder(
                int radius,
                Color cor
        ) {
            this.radius = radius;
            this.cor = cor;
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets( radius, radius, radius, radius );
        }
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x,
                int y,
                int width,
                int height
        ) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2.setColor( ThemeManager.isModoEscuro() ? ThemeManager.getBorda() : cor );
            g2.drawRoundRect( x, y, width - 1, height - 1, radius, radius );
            g2.dispose();
        }
    }
}
