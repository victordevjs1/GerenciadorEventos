package br.com.gerenciadoreventos.model;

/**
 * Alvo de público de um evento.
 *
 * serie == null representa o curso inteiro.
 * A opção "toda a escola" não usa esta classe: ela é representada por
 * Evento.publicoTodos e por uma linha publico_todos=TRUE em evento_publico.
 */
public class EventoPublico {

    private String curso;
    private String serie;

    public EventoPublico() {
    }

    public EventoPublico(String curso, String serie) {
        this.curso = curso;
        this.serie = serie;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public boolean isCursoInteiro() {
        return serie == null || serie.isBlank();
    }

    public String getChave() {
        return curso + "|" + (isCursoInteiro() ? "*" : serie);
    }

    @Override
    public String toString() {
        return curso + " — " + (isCursoInteiro() ? "Todas as séries" : serie);
    }
}
