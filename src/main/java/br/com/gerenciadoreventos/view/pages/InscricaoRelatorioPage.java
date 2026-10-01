package br.com.gerenciadoreventos.view.pages;

public class InscricaoRelatorioPage {

    private long idAluno;
    private String nome;
    private String rm;
    private String status;

    public InscricaoRelatorioPage() {
    }

    public InscricaoRelatorioPage(
            long idAluno,
            String nome,
            String rm,
            String status
    ) {
        this.idAluno = idAluno;
        this.nome = nome;
        this.rm = rm;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
