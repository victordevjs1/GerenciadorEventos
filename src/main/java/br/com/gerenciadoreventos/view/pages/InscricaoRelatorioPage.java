package br.com.gerenciadoreventos.view.pages;

public class InscricaoRelatorioPage {

    private long idAluno;
    private String nome;
    private String rm;
    private String turma;
    private String status;

    public InscricaoRelatorioPage() {
    }

    public InscricaoRelatorioPage(
            long idAluno,
            String nome,
            String rm,
            String turma,
            String status
    ) {
        this.idAluno = idAluno;
        this.nome = nome;
        this.rm = rm;
        this.turma = turma;
        this.status = status;
    }

    public long getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(long idAluno) {
        this.idAluno = idAluno;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRm() {
        return rm;
    }

    public void setRm(String rm) {
        this.rm = rm;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
