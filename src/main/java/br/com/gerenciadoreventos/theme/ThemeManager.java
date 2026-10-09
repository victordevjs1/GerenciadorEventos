package br.com.gerenciadoreventos.theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.prefs.Preferences;

// Tema claro/escuro do e-task usando somente Swing/AWT. A regra principal desta versão é: nunca usamos a cor "atual" de um componente como fonte definitiva para voltar ao tema claro. O estado original (claro) é guardado no próprio JComponent e restaurado depois. Isso evita o efeito acumulativo que deixava bordas, textos e cards com cores do tema escuro depois de alternar o tema algumas vezes.
public final class ThemeManager {
    private static final Preferences PREFS = Preferences.userRoot().node("br.com.gerenciadoreventos.etask");

    private static final String CHAVE_MODO_ESCURO = "modoEscuro";

    private static boolean modoEscuro = PREFS.getBoolean(CHAVE_MODO_ESCURO, false);
    private static boolean listenerGlobalInstalado = false;
    private static boolean atualizacaoAgendada = false;

    public static final Color AZUL = new Color(37, 99, 235);

    private static final Color FUNDO_CLARO = new Color(246, 248, 252);
    private static final Color PAINEL_CLARO = Color.WHITE;
    private static final Color CAMPO_CLARO = Color.WHITE;
    private static final Color TEXTO_CLARO = new Color(15, 23, 42);
    private static final Color TEXTO_SECUNDARIO_CLARO = new Color(100, 116, 139);
    private static final Color BORDA_CLARA = new Color(203, 213, 225);
    private static final Color TABELA_HEADER_CLARO = new Color(241, 245, 249);
    private static final Color SELECAO_CLARA = new Color(219, 234, 254);

    private static final Color FUNDO_ESCURO = new Color(15, 23, 42);
    private static final Color PAINEL_ESCURO = new Color(30, 41, 59);
    private static final Color CAMPO_ESCURO = new Color(17, 24, 39);
    private static final Color TEXTO_ESCURO = new Color(241, 245, 249);
    private static final Color TEXTO_SECUNDARIO_ESCURO = new Color(203, 213, 225);
    private static final Color BORDA_ESCURA = new Color(100, 116, 139);
    private static final Color TABELA_HEADER_ESCURO = new Color(51, 65, 85);
    private static final Color SELECAO_ESCURA = new Color(30, 64, 175);

    private static final String P_CAPTURADO = "theme.original.captured";
    private static final String P_BG = "theme.original.background";
    private static final String P_FG = "theme.original.foreground";
    private static final String P_BORDER = "theme.original.border";
    private static final String P_OPAQUE = "theme.original.opaque";
    private static final String P_LAST_BG = "theme.last.background";
    private static final String P_LAST_FG = "theme.last.foreground";
    private static final String P_LAST_BORDER = "theme.last.border";
    private static final String P_LAST_OPAQUE = "theme.last.opaque";

    private static final String P_TABLE_GRID = "theme.original.table.grid";
    private static final String P_TABLE_SEL_BG = "theme.original.table.selectionBg";
    private static final String P_TABLE_SEL_FG = "theme.original.table.selectionFg";
    private static final String P_TEXT_CARET = "theme.original.text.caret";
    private static final String P_TEXT_SEL_BG = "theme.original.text.selectionBg";
    private static final String P_TEXT_SEL_FG = "theme.original.text.selectionFg";
    private static final String P_LIST_SEL_BG = "theme.original.list.selectionBg";
    private static final String P_LIST_SEL_FG = "theme.original.list.selectionFg";

    private static final Object NULL_MARKER = new Object();

    private ThemeManager() {
    }

    public static boolean isModoEscuro() {
        return modoEscuro;
    }

    public static void setModoEscuro(boolean valor) {
        modoEscuro = valor;
        PREFS.putBoolean(CHAVE_MODO_ESCURO, valor);
        agendarAplicacaoGlobal();
    }

    // Monitora apenas eventos que podem criar/recriar interface. As atualizações são agrupadas em uma única execução posterior na EDT; isso evita dezenas de aplicações de tema enquanto um formulário ainda está sendo montado.
    public static synchronized void instalarAtualizacaoAutomatica() {
        if (listenerGlobalInstalado) {
            return;
        }
        AWTEventListener listener = evento -> {
            if (evento instanceof WindowEvent we
                    && (we.getID() == WindowEvent.WINDOW_OPENED
                    || we.getID() == WindowEvent.WINDOW_ACTIVATED)) {
                agendarAplicacaoGlobal();
                return;
            }
            if (evento instanceof ContainerEvent ce
                    && ce.getID() == ContainerEvent.COMPONENT_ADDED) {
                if (!deveIgnorar(ce.getChild())) {
                    agendarAplicacaoGlobal();
                }
                return;
            }
            if (evento instanceof ActionEvent || evento instanceof ItemEvent) {
                agendarAplicacaoGlobal();
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(
                listener, AWTEvent.WINDOW_EVENT_MASK | AWTEvent.CONTAINER_EVENT_MASK | AWTEvent.ACTION_EVENT_MASK | AWTEvent.ITEM_EVENT_MASK );
        listenerGlobalInstalado = true;
    }

    private static synchronized void agendarAplicacaoGlobal() {
        if (!listenerGlobalInstalado || atualizacaoAgendada) {
            return;
        }
        atualizacaoAgendada = true;
        SwingUtilities.invokeLater(() -> {
            synchronized (ThemeManager.class) {
                atualizacaoAgendada = false;
            }
            aplicarEmTodasJanelas();
        });
    }

    public static Color getFundo() {
        return modoEscuro ? FUNDO_ESCURO : FUNDO_CLARO;
    }

    public static Color getPainel() {
        return modoEscuro ? PAINEL_ESCURO : PAINEL_CLARO;
    }

    public static Color getCampo() {
        return modoEscuro ? CAMPO_ESCURO : CAMPO_CLARO;
    }

    public static Color getTexto() {
        return modoEscuro ? TEXTO_ESCURO : TEXTO_CLARO;
    }

    public static Color getTextoSecundario() {
        return modoEscuro ? TEXTO_SECUNDARIO_ESCURO : TEXTO_SECUNDARIO_CLARO;
    }

    public static Color getBorda() {
        return modoEscuro ? BORDA_ESCURA : BORDA_CLARA;
    }

    public static Color getTabelaHeader() {
        return modoEscuro ? TABELA_HEADER_ESCURO : TABELA_HEADER_CLARO;
    }

    public static Color getSelecao() {
        return modoEscuro ? SELECAO_ESCURA : SELECAO_CLARA;
    }

    public static void aplicarEmTodasJanelas() {
        for (Window janela : Window.getWindows()) {
            if (janela.isDisplayable()) {
                aplicarTema(janela);
            }
        }
    }

    public static void aplicarTema(Component componente) {
        Color fundo = ThemeManager.isModoEscuro() ? new Color(15, 23, 42) : Color.WHITE;
        Color texto = ThemeManager.isModoEscuro() ? new Color(241, 245, 249) : new Color(15, 23, 42);
        UIManager.put("OptionPane.background", fundo);
        UIManager.put("Panel.background", fundo);
        UIManager.put("OptionPane.messageForeground", texto);
        UIManager.put("Label.foreground", texto);
        if (componente == null || deveIgnorar(componente)) {
            return;
        }
        if (componente instanceof JComponent jc) {
            capturarOuAtualizarEstadoOriginal(jc);
        }
        if (modoEscuro) {
            aplicarEscuro(componente);
        } else {
            restaurarClaro(componente);
        }
        if (componente instanceof Container container) {
            for (Component filho : container.getComponents()) {
                aplicarTema(filho);
            }
        }
        if (componente instanceof JComponent jc) {
            registrarUltimoEstado(jc);
            jc.revalidate();
            jc.repaint();
        }
    }

    // theme.ignore vale para toda a subárvore. Isso é importante para a Sidebar: o listener global recebe eventos dos filhos individualmente, então verificar somente o próprio componente não era suficiente.
    private static boolean deveIgnorar(Component componente) {
        Component atual = componente;
        while (atual != null) {
            if (atual instanceof JComponent jc
                    && Boolean.TRUE.equals(jc.getClientProperty("theme.ignore"))) {
                return true;
            }
            atual = atual.getParent();
        }
        return false;
    }

    private static void capturarOuAtualizarEstadoOriginal(JComponent jc) {
        boolean capturado = Boolean.TRUE.equals(jc.getClientProperty(P_CAPTURADO));
        if (!capturado) {
            salvar(jc, P_BG, normalizarFundoOriginal(jc.getBackground()));
            salvar(jc, P_FG, normalizarTextoOriginal(jc.getForeground()));
            salvar(jc, P_BORDER, normalizarBordaOriginal(jc.getBorder()));
            jc.putClientProperty(P_OPAQUE, jc.isOpaque());
            capturarExtras(jc);
            jc.putClientProperty(P_CAPTURADO, Boolean.TRUE);
            return;
        }
        Color ultimoBg = lerColor(jc, P_LAST_BG);
        Color ultimoFg = lerColor(jc, P_LAST_FG);
        Border ultimoBorder = lerBorder(jc, P_LAST_BORDER);
        Boolean ultimoOpaque = lerBoolean(jc, P_LAST_OPAQUE);

        // Se o próprio código da tela mudou uma propriedade depois da última aplicação do tema, essa nova configuração passa a ser o original.
        if (ultimoBg != null && !Objects.equals(jc.getBackground(), ultimoBg)) {
            salvar(jc, P_BG, normalizarFundoOriginal(jc.getBackground()));
        }
        if (ultimoFg != null && !Objects.equals(jc.getForeground(), ultimoFg)) {
            salvar(jc, P_FG, normalizarTextoOriginal(jc.getForeground()));
        }
        if (ultimoBorder != null && jc.getBorder() != ultimoBorder) {
            salvar(jc, P_BORDER, normalizarBordaOriginal(jc.getBorder()));
        }
        if (ultimoOpaque != null && jc.isOpaque() != ultimoOpaque) {
            jc.putClientProperty(P_OPAQUE, jc.isOpaque());
        }
    }

    private static void capturarExtras(JComponent jc) {
        if (jc instanceof JTable tabela) {
            salvar(jc, P_TABLE_GRID, tabela.getGridColor());
            salvar(jc, P_TABLE_SEL_BG, tabela.getSelectionBackground());
            salvar(jc, P_TABLE_SEL_FG, tabela.getSelectionForeground());
        }
        if (jc instanceof JTextComponent texto) {
            salvar(jc, P_TEXT_CARET, texto.getCaretColor());
            salvar(jc, P_TEXT_SEL_BG, texto.getSelectionColor());
            salvar(jc, P_TEXT_SEL_FG, texto.getSelectedTextColor());
        }
        if (jc instanceof JList<?> lista) {
            salvar(jc, P_LIST_SEL_BG, lista.getSelectionBackground());
            salvar(jc, P_LIST_SEL_FG, lista.getSelectionForeground());
        }
    }

    private static void aplicarEscuro(Component c) {
        if (c instanceof JFrame frame) {
            frame.getContentPane().setBackground(getFundo());
            return;
        }
        if (c instanceof JDialog dialog) {
            dialog.getContentPane().setBackground(getFundo());
            return;
        }
        if (!(c instanceof JComponent jc)) {
            return;
        }
        Color fundoOriginal = lerColor(jc, P_BG);
        Color textoOriginal = lerColor(jc, P_FG);
        if (c instanceof JTable tabela) {
            tabela.setBackground(getPainel());
            tabela.setForeground(getTexto());
            tabela.setGridColor(getBorda());
            tabela.setSelectionBackground(getSelecao());
            tabela.setSelectionForeground(getTexto());
            tabela.setFillsViewportHeight(true);
        } else if (c instanceof JTableHeader header) {
            header.setBackground(getTabelaHeader());
            header.setForeground(getTexto());
        } else if (c instanceof JTextComponent campo) {
            campo.setBackground(getCampo());
            campo.setForeground(getTexto());
            campo.setCaretColor(getTexto());
            campo.setSelectionColor(getSelecao());
            campo.setSelectedTextColor(getTexto());
        } else if (c instanceof JComboBox<?> combo) {
            combo.setBackground(getCampo());
            combo.setForeground(getTexto());
            if (combo.isEditable() && combo.getEditor() != null) {
                Component editor = combo.getEditor().getEditorComponent();
                if (editor != null) aplicarTema(editor);
            }
        } else if (c instanceof JList<?> lista) {
            lista.setBackground(getCampo());
            lista.setForeground(getTexto());
            lista.setSelectionBackground(getSelecao());
            lista.setSelectionForeground(getTexto());
        } else if (c instanceof JSpinner spinner) {
            spinner.setBackground(getCampo());
            spinner.setForeground(getTexto());
            aplicarTema(spinner.getEditor());
        } else if (c instanceof JScrollPane scroll) {
            scroll.setBackground(getPainel());
        } else if (c instanceof JScrollBar barra) {
            barra.setBackground(getPainel());
            barra.setForeground(getBorda());
        } else if (c instanceof JViewport viewport) {
            viewport.setBackground(getPainel());
        } else if (c instanceof JTabbedPane abas) {
            abas.setBackground(getPainel());
            abas.setForeground(getTexto());
            for (int i = 0; i < abas.getTabCount(); i++) {
                abas.setBackgroundAt(i, getPainel());
                abas.setForegroundAt(i, getTexto());
            }
        } else if (c instanceof JRadioButton radio) {
            radio.setForeground(mapearTextoEscuro(textoOriginal));
            radio.setOpaque(false);
        } else if (c instanceof JCheckBox check) {
            check.setForeground(mapearTextoEscuro(textoOriginal));
            check.setOpaque(false);
        } else if (c instanceof JLabel label) {
            label.setForeground(mapearTextoEscuro(textoOriginal));
            if (label.isOpaque() && ehSuperficieNeutraClara(fundoOriginal)) {
                label.setBackground(getPainel());
            }
        } else if (c instanceof JButton botao) {
            if (ehAzul(fundoOriginal)) {
                botao.setBackground(fundoOriginal != null ? fundoOriginal : AZUL);
                botao.setForeground(Color.WHITE);
            } else if (ehSuperficieNeutraClara(fundoOriginal)) {
                botao.setBackground(getPainel());
                botao.setForeground(mapearTextoEscuro(textoOriginal));
            } else {
                if (fundoOriginal != null) botao.setBackground(fundoOriginal);
                botao.setForeground(mapearTextoEscuro(textoOriginal));
            }
        } else if (c instanceof JToggleButton toggle) {
            if (ehAzul(fundoOriginal)) {
                toggle.setBackground(fundoOriginal);
                toggle.setForeground(Color.WHITE);
            } else if (ehSuperficieNeutraClara(fundoOriginal)) {
                toggle.setBackground(getPainel());
                toggle.setForeground(mapearTextoEscuro(textoOriginal));
            }
        } else if (c instanceof JPanel painel) {
            painel.setBackground(mapearFundoEscuro(fundoOriginal));
        } else if (c instanceof JSeparator separator) {
            separator.setForeground(getBorda());
            separator.setBackground(getBorda());
        }
        Border original = lerBorder(jc, P_BORDER);
        if (original != null) {
            jc.setBorder(criarBordaEscura(original));
        }
    }

    private static void restaurarClaro(Component c) {
        if (!(c instanceof JComponent jc)) {
            if (c instanceof JFrame frame) frame.getContentPane().setBackground(getFundo());
            if (c instanceof JDialog dialog) dialog.getContentPane().setBackground(getFundo());
            return;
        }
        Color bg = lerColor(jc, P_BG);
        Color fg = lerColor(jc, P_FG);
        Border border = lerBorder(jc, P_BORDER);
        Boolean opaque = lerBoolean(jc, P_OPAQUE);
        if (bg != null) jc.setBackground(bg);
        if (fg != null) jc.setForeground(fg);
        jc.setBorder(border);
        if (opaque != null) jc.setOpaque(opaque);
        if (jc instanceof JTable tabela) {
            Color grid = lerColor(jc, P_TABLE_GRID);
            Color selBg = lerColor(jc, P_TABLE_SEL_BG);
            Color selFg = lerColor(jc, P_TABLE_SEL_FG);
            if (grid != null) tabela.setGridColor(grid);
            if (selBg != null) tabela.setSelectionBackground(selBg);
            if (selFg != null) tabela.setSelectionForeground(selFg);
        }
        if (jc instanceof JTextComponent texto) {
            Color caret = lerColor(jc, P_TEXT_CARET);
            Color selBg = lerColor(jc, P_TEXT_SEL_BG);
            Color selFg = lerColor(jc, P_TEXT_SEL_FG);
            if (caret != null) texto.setCaretColor(caret);
            if (selBg != null) texto.setSelectionColor(selBg);
            if (selFg != null) texto.setSelectedTextColor(selFg);
        }
        if (jc instanceof JList<?> lista) {
            Color selBg = lerColor(jc, P_LIST_SEL_BG);
            Color selFg = lerColor(jc, P_LIST_SEL_FG);
            if (selBg != null) lista.setSelectionBackground(selBg);
            if (selFg != null) lista.setSelectionForeground(selFg);
        }
        if (jc instanceof JTabbedPane abas) {
            Color abaBg = bg != null ? bg : PAINEL_CLARO;
            Color abaFg = fg != null ? fg : TEXTO_CLARO;
            for (int i = 0; i < abas.getTabCount(); i++) {
                abas.setBackgroundAt(i, abaBg);
                abas.setForegroundAt(i, abaFg);
            }
        }
    }

    private static void registrarUltimoEstado(JComponent jc) {
        salvar(jc, P_LAST_BG, jc.getBackground());
        salvar(jc, P_LAST_FG, jc.getForeground());
        salvar(jc, P_LAST_BORDER, jc.getBorder());
        jc.putClientProperty(P_LAST_OPAQUE, jc.isOpaque());
    }

    private static Color mapearFundoEscuro(Color original) {
        if (original == null) return getPainel();
        if (ehFundoPaginaClaro(original)) {
            return getFundo();
        }
        if (ehSuperficieNeutraClara(original)) {
            return getPainel();
        }
        return original;
    }

    private static Color mapearTextoEscuro(Color original) {
        if (original == null) return getTexto();
        if (Color.WHITE.equals(original)) {
            return Color.WHITE;
        }
        if (ehTextoSecundarioClaro(original)) {
            return getTextoSecundario();
        }
        if (ehTextoPrincipalClaro(original)) {
            return getTexto();
        }
        return original;
    }

    // Se um componente foi criado enquanto o modo escuro já estava ativo e a própria tela usou ThemeManager.getXxx(), convertemos esse valor de volta para o equivalente claro antes de guardar o estado original.
    private static Color normalizarFundoOriginal(Color atual) {
        if (!modoEscuro || atual == null) return atual;
        if (Objects.equals(atual, FUNDO_ESCURO)) return FUNDO_CLARO;
        if (Objects.equals(atual, PAINEL_ESCURO)) return PAINEL_CLARO;
        if (Objects.equals(atual, CAMPO_ESCURO)) return CAMPO_CLARO;
        if (Objects.equals(atual, TABELA_HEADER_ESCURO)) return TABELA_HEADER_CLARO;
        if (Objects.equals(atual, SELECAO_ESCURA)) return SELECAO_CLARA;
        return atual;
    }

    private static Color normalizarTextoOriginal(Color atual) {
        if (!modoEscuro || atual == null) return atual;
        if (Objects.equals(atual, TEXTO_ESCURO)) return TEXTO_CLARO;
        if (Objects.equals(atual, TEXTO_SECUNDARIO_ESCURO)) return TEXTO_SECUNDARIO_CLARO;
        if (Objects.equals(atual, BORDA_ESCURA)) return BORDA_CLARA;
        return atual;
    }

    private static Border normalizarBordaOriginal(Border border) {
        if (!modoEscuro || border == null) return border;
        if (border instanceof LineBorder line) {
            Color cor = line.getLineColor();
            if (Objects.equals(cor, BORDA_ESCURA)) {
                return new LineBorder( BORDA_CLARA, line.getThickness(), line.getRoundedCorners() );
            }
            return border;
        }
        if (border instanceof CompoundBorder compound) {
            return new CompoundBorder( normalizarBordaOriginal(compound.getOutsideBorder()), normalizarBordaOriginal(compound.getInsideBorder()) );
        }
        if (border instanceof TitledBorder titled) {
            Color titulo = titled.getTitleColor();
            if (Objects.equals(titulo, TEXTO_ESCURO)) titulo = TEXTO_CLARO;
            return new TitledBorder(
                    normalizarBordaOriginal(titled.getBorder()),
                    titled.getTitle(), titled.getTitleJustification(), titled.getTitlePosition(), titled.getTitleFont(), titulo );
        }
        return border;
    }

    private static Border criarBordaEscura(Border original) {
        if (original == null) return null;
        if (original instanceof LineBorder line) {
            Color cor = line.getLineColor();
            if (ehBordaNeutra(cor)) {
                cor = getBorda();
            }
            return new LineBorder(cor, line.getThickness(), line.getRoundedCorners());
        }
        if (original instanceof CompoundBorder compound) {
            return new CompoundBorder( criarBordaEscura(compound.getOutsideBorder()), criarBordaEscura(compound.getInsideBorder()) );
        }
        if (original instanceof TitledBorder titled) {
            return new TitledBorder(
                    criarBordaEscura(titled.getBorder()),
                    titled.getTitle(), titled.getTitleJustification(), titled.getTitlePosition(), titled.getTitleFont(), getTexto() );
        }

        // Bordas customizadas são preservadas. As quatro bordas arredondadas do Dashboard foram ajustadas para consultar ThemeManager no paint.
        return original;
    }

    private static boolean ehFundoPaginaClaro(Color c) {
        return corIgual(c, 246, 248, 252) || corIgual(c, 248, 249, 251) || corIgual(c, 248, 250, 252) || corIgual(c, 245, 247, 250);
    }

    private static boolean ehSuperficieNeutraClara(Color c) {
        if (c == null) return false;
        if (Color.WHITE.equals(c)
                || corIgual(c, 241, 245, 249)
                || corIgual(c, 242, 246, 255)
                || corIgual(c, 249, 250, 252)
                || corIgual(c, 250, 250, 250)
                || corIgual(c, 245, 245, 245)
                || corIgual(c, 238, 238, 238)) {
            return true;
        }
        int max = Math.max(c.getRed(), Math.max(c.getGreen(), c.getBlue()));
        int min = Math.min(c.getRed(), Math.min(c.getGreen(), c.getBlue()));
        return min >= 228 && (max - min) <= 10;
    }

    private static boolean ehTextoPrincipalClaro(Color c) {
        return Color.BLACK.equals(c)
                || corIgual(c, 15, 23, 42)
                || corIgual(c, 35, 35, 45) || corIgual(c, 35, 38, 45) || corIgual(c, 40, 40, 40) || corIgual(c, 71, 85, 105);
    }

    private static boolean ehTextoSecundarioClaro(Color c) {
        return corIgual(c, 100, 116, 139)
                || corIgual(c, 120, 125, 135) || corIgual(c, 100, 100, 100) || corIgual(c, 110, 110, 120) || corIgual(c, 148, 163, 184);
    }

    private static boolean ehBordaNeutra(Color c) {
        return c != null && (
                corIgual(c, 203, 213, 225)
                        || corIgual(c, 226, 232, 240)
                        || corIgual(c, 235, 236, 240)
                        || corIgual(c, 225, 225, 225)
                        || corIgual(c, 191, 219, 254)
                        || corIgual(c, 100, 116, 139) || (c.getRed() > 190 && c.getGreen() > 190 && c.getBlue() > 190) );
    }

    private static boolean ehAzul(Color c) {
        return corIgual(c, 37, 99, 235) || corIgual(c, 65, 105, 225) || corIgual(c, 29, 78, 216) || corIgual(c, 122, 162, 247);
    }

    private static boolean corIgual(Color c, int r, int g, int b) {
        return c != null && c.getRed() == r && c.getGreen() == g && c.getBlue() == b;
    }

    private static void salvar(JComponent jc, String chave, Object valor) {
        jc.putClientProperty(chave, valor == null ? NULL_MARKER : valor);
    }

    private static Object ler(JComponent jc, String chave) {
        Object valor = jc.getClientProperty(chave);
        return valor == NULL_MARKER ? null : valor;
    }

    private static Color lerColor(JComponent jc, String chave) {
        Object valor = ler(jc, chave);
        return valor instanceof Color c ? c : null;
    }

    private static Border lerBorder(JComponent jc, String chave) {
        Object valor = ler(jc, chave);
        return valor instanceof Border b ? b : null;
    }

    private static Boolean lerBoolean(JComponent jc, String chave) {
        Object valor = ler(jc, chave);
        return valor instanceof Boolean b ? b : null;
    }

    public static Border bordaCampo() {
        return new LineBorder(getBorda(), 1, true);
    }
}
