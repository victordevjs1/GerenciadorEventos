package br.com.gerenciadoreventos.model;

public class ProfessorCsvImportacao {

    private final int linha;
    private final Professor professor;
    private final boolean valido;
    private final String situacao;

    public ProfessorCsvImportacao(int linha, Professor professor, boolean valido, String situacao) {
        this.linha = linha;
        this.professor = professor;
        this.valido = valido;
        this.situacao = situacao;
    }

    public int getLinha() {
        return linha;
    }

    public Professor getProfessor() {
        return professor;
    }

    public boolean isValido() {
        return valido;
    }

    public String getSituacao() {
        return situacao;
    }
}
