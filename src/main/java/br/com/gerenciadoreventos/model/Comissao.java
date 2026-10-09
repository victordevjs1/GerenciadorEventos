package br.com.gerenciadoreventos.model;

public class Comissao {
    private Long idComissao;
    private Long idEvento;

    private String evento;
    private String nome;
    private String descricao;

    private Boolean ativo;

    private Integer totalAlunos;
    private Integer totalAtividades;

    // CONSTRUTOR

    public Comissao() {
    }

    // ID COMISSÃO

    public Long getIdComissao() {
        return idComissao;
    }

    public void setIdComissao(Long idComissao) {
        this.idComissao = idComissao;
    }

    // ID EVENTO

    public Long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
    }

    // EVENTO

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    // NOME

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // DESCRIÇÃO

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // ATIVO

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    // TOTAL DE ALUNOS

    public Integer getTotalAlunos() {
        return totalAlunos;
    }

    public void setTotalAlunos(Integer totalAlunos) {
        this.totalAlunos = totalAlunos;
    }

    // TOTAL DE ATIVIDADES

    public Integer getTotalAtividades() {
        return totalAtividades;
    }

    public void setTotalAtividades(Integer totalAtividades) {
        this.totalAtividades = totalAtividades;
    }

    // COMPATIBILIDADE COM isAtivo()

    public boolean isAtivo() {
        return Boolean.TRUE.equals(ativo);
    }
}
