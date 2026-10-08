package br.com.gerenciadoreventos.view.components;

import br.com.gerenciadoreventos.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class DashboardCenter extends JPanel {

    private static final Color FUNDO =
            new Color(246, 248, 252);

    public DashboardCenter() {

        setLayout(
                new GridLayout(
                        1,
                        2,
                        18,
                        0
                )
        );

        setBackground(ThemeManager.getFundo());

        setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        220
                )
        );

        add(
                new DashboardChart()
        );

        add(
                new UpcomingEvents()
        );
    }
}