package br.com.gerenciadoreventos.model;

public class LinhaRelatorioTurma {

    private String turma;
    public int matriculados;
    private int totalRegistros;   // total de chamadas registradas
    private int totalPresentes;   // total de PRESENTE registrados

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public int getMatriculados() {
        return matriculados;
    }

    public void setMatriculados(int matriculados) {
        this.matriculados = matriculados;
    }

    public int getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(int totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public int getTotalPresentes() {
        return totalPresentes;
    }

    public void setTotalPresentes(int totalPresentes) {
        this.totalPresentes = totalPresentes;
    }

    // =====================================================
    // VALORES DERIVADOS
    // =====================================================

    public double presencasMedia() {
        return matriculados == 0
                ? 0
                : (double) totalPresentes / matriculados;
    }

    public double faltasMedia() {
        return matriculados == 0
                ? 0
                : (double) (totalRegistros - totalPresentes) / matriculados;
    }

    public double taxaComparecimento() {
        return totalRegistros == 0
                ? 0
                : (totalPresentes * 100.0) / totalRegistros;
    }
}
