package br.com.gerenciadoreventos.audit;

import br.com.gerenciadoreventos.session.SessaoUsuario;

import java.util.function.Supplier;

public final class ContextoAuditoria {
    private static final ThreadLocal<Ator> ATOR_TEMPORARIO = new ThreadLocal<>();

    private ContextoAuditoria() {
    }

    public static Long getIdUsuario() {
        Ator temporario = ATOR_TEMPORARIO.get();
        return temporario != null ? temporario.idUsuario() : SessaoUsuario.getIdUsuarioAtual();
    }

    public static void executarComo(long idUsuario, Runnable acao) {
        executar(new Ator(idUsuario), () -> {
            acao.run();
            return null;
        });
    }

    public static <T> T executarComo(long idUsuario, Supplier<T> acao) {
        return executar(new Ator(idUsuario), acao);
    }

    public static void executarComoSistema(Runnable acao) {
        executar(new Ator(null), () -> {
            acao.run();
            return null;
        });
    }

    public static <T> T executarComoSistema(Supplier<T> acao) {
        return executar(new Ator(null), acao);
    }

    private static <T> T executar(Ator ator, Supplier<T> acao) {
        Ator anterior = ATOR_TEMPORARIO.get();
        ATOR_TEMPORARIO.set(ator);
        try {
            return acao.get();
        } finally {
            if (anterior == null) {
                ATOR_TEMPORARIO.remove();
            } else {
                ATOR_TEMPORARIO.set(anterior);
            }
        }
    }

    private record Ator(Long idUsuario) {
    }
}
