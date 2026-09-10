package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.UsuarioDAO;
import br.com.gerenciadoreventos.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario autenticar(String email, String senha) {

        if (email == null || email.isBlank()) {
            return null;
        }

        if (senha == null || senha.isBlank()) {
            return null;
        }

        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        if (usuario == null) {
            return null;
        }

        try {

            if (BCrypt.checkpw(
                    senha,
                    usuario.getSenha()
            )) {

                return usuario;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }
}
