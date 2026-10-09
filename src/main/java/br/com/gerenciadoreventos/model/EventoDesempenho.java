package br.com.gerenciadoreventos.model;

public class EventoDesempenho {
    private int totalInscritos;
    private int presencasConfirmadas;
    private int ausentes;

    public int getTotalInscritos() {
        return totalInscritos;
    }

    public void setTotalInscritos(int totalInscritos) {
        this.totalInscritos = totalInscritos;
    }

    public int getPresencasConfirmadas() {
        return presencasConfirmadas;
    }

    public void setPresencasConfirmadas(int presencasConfirmadas) {
        this.presencasConfirmadas = presencasConfirmadas;
    }

    public int getAusentes() {
        return ausentes;
    }

    public void setAusentes(int ausentes) {
        this.ausentes = ausentes;
    }
}
