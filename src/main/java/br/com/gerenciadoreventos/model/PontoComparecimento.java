package br.com.gerenciadoreventos.model;

import java.time.LocalDate;

public class PontoComparecimento {

    private final LocalDate data;
    private final int total;

    public PontoComparecimento(LocalDate data, int total) {
        this.data = data;
        this.total = total;
    }

    public LocalDate getData() {
        return data;
    }

    public int getTotal() {
        return total;
    }
}