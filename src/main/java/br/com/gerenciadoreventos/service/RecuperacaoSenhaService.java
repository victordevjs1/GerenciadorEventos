package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.RecuperacaoSenhaDAO;
import br.com.gerenciadoreventos.dao.UsuarioDAO;
import br.com.gerenciadoreventos.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.time.LocalDateTime;

public class RecuperacaoSenhaService {
    private static final int MINUTOS_EXPIRACAO = 10;
    private static final int MAX_TENTATIVAS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RecuperacaoSenhaDAO recuperacaoDAO = new RecuperacaoSenhaDAO();
    private final EmailService emailService = new EmailService();

    public void solicitarCodigo(String email) {
        Usuario usuario = buscarUsuario(email);
        String codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
        String hash = BCrypt.hashpw(codigo, BCrypt.gensalt(10));
        long idRecuperacao = recuperacaoDAO.criar( usuario.getId(), hash, LocalDateTime.now().plusMinutes(MINUTOS_EXPIRACAO) );
        try {
            emailService.enviarCodigoRecuperacao(usuario.getEmail(), usuario.getNome(), codigo);
        } catch (RuntimeException e) {
            recuperacaoDAO.excluir(idRecuperacao);
            throw e;
        }
    }

    public void validarCodigo(String email, String codigo) {
        validarCodigoInterno(buscarUsuario(email), codigo);
    }

    public void redefinirSenha(String email, String codigo, String novaSenha, String confirmacao) {
        Usuario usuario = buscarUsuario(email);
        validarSenha(novaSenha, confirmacao);
        RecuperacaoSenhaDAO.CodigoRecuperacao recuperacao = validarCodigoInterno(usuario, codigo);
        String hash = BCrypt.hashpw(novaSenha, BCrypt.gensalt(10));
        recuperacaoDAO.redefinirSenha(recuperacao.id(), usuario.getId(), hash);
    }

    private RecuperacaoSenhaDAO.CodigoRecuperacao validarCodigoInterno(Usuario usuario, String codigo) {
        if (codigo == null || !codigo.matches("\\d{6}")) {
            throw new IllegalArgumentException("Informe o código de 6 dígitos.");
        }
        RecuperacaoSenhaDAO.CodigoRecuperacao recuperacao = recuperacaoDAO.buscarValido(usuario.getId());
        if (recuperacao == null) {
            throw new IllegalArgumentException("O código expirou ou já foi utilizado. Solicite um novo código.");
        }
        boolean correto;
        try {
            correto = BCrypt.checkpw(codigo, recuperacao.hash());
        } catch (Exception e) {
            correto = false;
        }
        if (!correto) {
            int novaTentativa = recuperacao.tentativas() + 1;
            recuperacaoDAO.registrarTentativa(recuperacao.id(), novaTentativa >= MAX_TENTATIVAS);
            if (novaTentativa >= MAX_TENTATIVAS) {
                throw new IllegalArgumentException("Código inválido. O limite de tentativas foi atingido; solicite outro código.");
            }
            throw new IllegalArgumentException("Código inválido. Restam " + (MAX_TENTATIVAS - novaTentativa) + " tentativa(s).");
        }
        return recuperacao;
    }

    private Usuario buscarUsuario(String email) {
        String normalizado = email == null ? "" : email.trim();
        if (!normalizado.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        Usuario usuario = usuarioDAO.buscarPorEmail(normalizado);
        if (usuario == null) {
            throw new IllegalArgumentException("Não existe usuário ativo com esse e-mail.");
        }
        return usuario;
    }

    private void validarSenha(String senha, String confirmacao) {
        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("A nova senha deve ter pelo menos 6 caracteres.");
        }
        if (!senha.equals(confirmacao)) {
            throw new IllegalArgumentException("A confirmação da nova senha não confere.");
        }
    }
}
