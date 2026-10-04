package br.com.gerenciadoreventos;

import br.com.gerenciadoreventos.view.Login;

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

            new Login().setVisible(true);
        });
    }
}