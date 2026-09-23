package br.com.gerenciadoreventos.model;

public class Aluno {

    private long id;
    private String rm;
    private String nome;
    private String dataNascimento;
    private String turma;
    private String curso;
    private String email;
    private String telefone;
    private boolean ativo;

    // =====================================================
    // SÉRIE / FORMATURA AUTOMÁTICA
    // =====================================================

    // 1, 2 ou 3 (1º, 2º ou 3º ano do ensino médio)
    private Integer serie;

    // Ano em que o aluno conclui o 3º ano (calculado a partir
    // da série informada no cadastro). Quando o ano atual
    // ultrapassa este valor, o aluno é desativado automaticamente.
    private Integer anoConclusao;

    public Aluno() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRm() {
        return rm;
    }

    public void setRm(String rm) {
        this.rm = rm;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getSerie() {
        return serie;
    }

    public void setSerie(Integer serie) {
        this.serie = serie;
    }

    public Integer getAnoConclusao() {
        return anoConclusao;
    }

    public void setAnoConclusao(Integer anoConclusao) {
        this.anoConclusao = anoConclusao;
    }
}