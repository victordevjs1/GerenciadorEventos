package br.com.gerenciadoreventos.model;

public class ResumoGeral {

    private int totalEventos;
    private int totalInscricoes;
    private double taxaComparecimento;

    public int getTotalEventos() {
        return totalEventos;
    }

    public void setTotalEventos(int totalEventos) {
        this.totalEventos = totalEventos;
    }

    public int getTotalInscricoes() {
        return totalInscricoes;
    }

    public void setTotalInscricoes(int totalInscricoes) {
        this.totalInscricoes = totalInscricoes;
    }

    public double getTaxaComparecimento() {
        return taxaComparecimento;
    }

    public void setTaxaComparecimento(double taxaComparecimento) {
        this.taxaComparecimento = taxaComparecimento;
    }
}