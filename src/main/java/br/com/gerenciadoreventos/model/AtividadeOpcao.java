package br.com.gerenciadoreventos.model;

public class AtividadeOpcao {
    private final long id;
    private final String rotulo;

    public AtividadeOpcao(long id, String rotulo) {
        this.id = id;
        this.rotulo = rotulo;
    }

    public long getId() {
        return id;
    }

    public String getRotulo() {
        return rotulo;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
