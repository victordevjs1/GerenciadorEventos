package br.com.gerenciadoreventos.model;

public class LinhaRelatorioCursoSerie {
    private String curso;
    private String serie;
    private int matriculados;
    private int totalRegistros;
    private int totalPresentes;

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }
    public int getMatriculados() { return matriculados; }
    public void setMatriculados(int matriculados) { this.matriculados = matriculados; }
    public int getTotalRegistros() { return totalRegistros; }
    public void setTotalRegistros(int totalRegistros) { this.totalRegistros = totalRegistros; }
    public int getTotalPresentes() { return totalPresentes; }
    public void setTotalPresentes(int totalPresentes) { this.totalPresentes = totalPresentes; }

    public double presencasMedia() {
        return matriculados == 0 ? 0 : (double) totalPresentes / matriculados;
    }
    public double faltasMedia() {
        return matriculados == 0 ? 0 : (double) (totalRegistros - totalPresentes) / matriculados;
    }
    public double taxaComparecimento() {
        return totalRegistros == 0 ? 0 : (totalPresentes * 100.0) / totalRegistros;
    }
}
