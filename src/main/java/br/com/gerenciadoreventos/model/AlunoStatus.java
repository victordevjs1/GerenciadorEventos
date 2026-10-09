package br.com.gerenciadoreventos.model;

public class AlunoStatus {
    private final String nome;
    private final String status;

    public AlunoStatus(String nome, String status) {
        this.nome = nome;
        this.status = status;
    }

    public String getNome() {
        return nome;
    }

    public String getStatus() {
        return status;
    }
}
