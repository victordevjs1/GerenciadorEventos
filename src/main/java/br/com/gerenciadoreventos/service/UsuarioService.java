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
    public void atualizarPerfil(
            Usuario usuario,
            String nome,
            String email,
            String senhaAtual,
            String novaSenha,
            String confirmarNovaSenha
    ) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário inválido.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome de usuário.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Informe o e-mail.");
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        if (usuarioDAO.emailEmUsoPorOutroUsuario(email, usuario.getId())) {
            throw new IllegalArgumentException("Este e-mail já está sendo utilizado por outro usuário.");
        }
        String senhaHash = usuario.getSenha();
        boolean desejaAlterarSenha =
                (senhaAtual != null && !senhaAtual.isBlank())
                        || (novaSenha != null && !novaSenha.isBlank()) || (confirmarNovaSenha != null && !confirmarNovaSenha.isBlank());
        if (desejaAlterarSenha) {
            if (senhaAtual == null || senhaAtual.isBlank()) {
                throw new IllegalArgumentException("Informe a senha atual.");
            }
            if (novaSenha == null || novaSenha.isBlank()) {
                throw new IllegalArgumentException("Informe a nova senha.");
            }
            if (novaSenha.length() < 6) {
                throw new IllegalArgumentException("A nova senha deve ter pelo menos 6 caracteres.");
            }
            if (!novaSenha.equals(confirmarNovaSenha)) {
                throw new IllegalArgumentException("A confirmação da nova senha não confere.");
            }
            try {
                if (!BCrypt.checkpw(senhaAtual, usuario.getSenha())) {
                    throw new IllegalArgumentException("A senha atual está incorreta.");
                }
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException("Não foi possível validar a senha atual.");
            }
            senhaHash = BCrypt.hashpw(novaSenha, BCrypt.gensalt(10));
        }
        usuarioDAO.atualizarPerfil( usuario.getId(), nome.trim(), email.trim(), senhaHash );
        usuario.setNome(nome.trim());
        usuario.setEmail(email.trim());
        usuario.setSenha(senhaHash);
    }
}
