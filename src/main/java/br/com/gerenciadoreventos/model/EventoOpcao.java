package br.com.gerenciadoreventos.model;

import java.time.LocalDate;

public class EventoOpcao {

    private final long id;
    private final String nome;
    private final LocalDate dataInicio;
    private final LocalDate dataFim;

    public EventoOpcao(
            long id,
            String nome,
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        this.id = id;
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    @Override
    public String toString() {
        return nome;
    }
}
