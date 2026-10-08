package br.com.gerenciadoreventos;

import br.com.gerenciadoreventos.view.Login;
import br.com.gerenciadoreventos.theme.ThemeManager;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {


        SwingUtilities.invokeLater(() -> {

            try {

                UIManager.setLookAndFeel(
                        UIManager.getCrossPlatformLookAndFeelClassName()
                );

            } catch (Exception e) {
                e.printStackTrace();
            }

            ThemeManager.instalarAtualizacaoAutomatica();

            new Login().setVisible(true);
        });
    }
}