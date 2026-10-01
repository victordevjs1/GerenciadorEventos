package br.com.gerenciadoreventos.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Evento {

    private long id;
    private long idUsuarioCriador;

    private String nome;
    private String descricao;

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    private String local;
    private int capacidade;
    private String status;

    /*
     * Estado derivado de evento_publico.
     * Não é uma coluna da tabela evento.
     *
     * true  = existe um registro publico_todos = TRUE
     * false = existem públicos por curso/série
     */
    private boolean publicoTodos = true;

    private List<EventoPublico> publicos = new ArrayList<>();

    public Evento() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdUsuarioCriador() {
        return idUsuarioCriador;
    }

    public void setIdUsuarioCriador(long idUsuarioCriador) {
        this.idUsuarioCriador = idUsuarioCriador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPublicoTodos() {
        return publicoTodos;
    }

    public void setPublicoTodos(boolean publicoTodos) {
        this.publicoTodos = publicoTodos;
    }

    public List<EventoPublico> getPublicos() {
        return publicos;
    }

    public void setPublicos(List<EventoPublico> publicos) {

        if (publicos == null) {
            this.publicos = new ArrayList<>();
        } else {
            this.publicos = new ArrayList<>(publicos);
        }
    }

    public void adicionarPublico(EventoPublico publico) {

        if (publico != null) {
            publicos.add(publico);
        }
    }

    public void limparPublicos() {
        publicos.clear();
    }
}
