package br.com.gerenciadoreventos.session;

import br.com.gerenciadoreventos.model.Usuario;

public final class SessaoUsuario {
    private static volatile Usuario usuarioAtual;

    private SessaoUsuario() {
    }

    public static void iniciar(Usuario usuario) {
        usuarioAtual = usuario;
    }

    public static void encerrar() {
        usuarioAtual = null;
    }

    public static Usuario getUsuarioAtual() {
        return usuarioAtual;
    }

    public static Long getIdUsuarioAtual() {
        return usuarioAtual == null ? null : usuarioAtual.getId();
    }
}
