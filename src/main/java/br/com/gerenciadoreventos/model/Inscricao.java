package br.com.gerenciadoreventos.model;

import java.sql.Timestamp;

public class Inscricao {

    private long idInscricao;
    private long idAluno;
    private long idEvento;

    private String rm;
    private String aluno;
    private String turma;
    private String evento;

    private Timestamp dataInscricao;

    private String status;
    private String observacao;


    public Inscricao() {
    }


    public long getIdInscricao() {
        return idInscricao;
    }

    public void setIdInscricao(long idInscricao) {
        this.idInscricao = idInscricao;
    }


    public long getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(long idAluno) {
        this.idAluno = idAluno;
    }


    public long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }


    public String getRm() {
        return rm;
    }

    public void setRm(String rm) {
        this.rm = rm;
    }


    public String getAluno() {
        return aluno;
    }

    public void setAluno(String aluno) {
        this.aluno = aluno;
    }


    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }


    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }


    public Timestamp getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(
            Timestamp dataInscricao
    ) {
        this.dataInscricao =
                dataInscricao;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(
            String observacao
    ) {
        this.observacao = observacao;
    }
}
