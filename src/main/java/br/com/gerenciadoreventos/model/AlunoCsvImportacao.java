package br.com.gerenciadoreventos.model;

public class AlunoCsvImportacao {
    private final int linha;
    private final Aluno aluno;
    private final boolean valido;
    private final String situacao;

    public AlunoCsvImportacao(int linha, Aluno aluno, boolean valido, String situacao) {
        this.linha = linha;
        this.aluno = aluno;
        this.valido = valido;
        this.situacao = situacao;
    }

    public int getLinha() {
        return linha;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public boolean isValido() {
        return valido;
    }

    public String getSituacao() {
        return situacao;
    }
}
