package br.com.gerenciadoreventos.util;

import org.mindrot.jbcrypt.BCrypt;

public class GeradorSenha {
    public static void main(String[] args) {
        String senha = "1234";
        String hash = BCrypt.hashpw( senha, BCrypt.gensalt(10) );
        System.out.println("Senha: " + senha);
        System.out.println("Hash: " + hash);
    }
}
