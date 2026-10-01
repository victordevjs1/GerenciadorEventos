package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.EventoDAO;
import br.com.gerenciadoreventos.dao.CursoDAO;
import br.com.gerenciadoreventos.dao.SerieDAO;
import br.com.gerenciadoreventos.dao.EventoPublicoDAO;
import br.com.gerenciadoreventos.model.Evento;
import br.com.gerenciadoreventos.model.EventoPublico;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class EventoService {

    private final EventoDAO eventoDAO;
    private final CursoDAO cursoDAO;
    private final SerieDAO serieDAO;
    private final EventoPublicoDAO eventoPublicoDAO;

    public EventoService() {
        eventoDAO = new EventoDAO();
        cursoDAO = new CursoDAO();
        serieDAO = new SerieDAO();
        eventoPublicoDAO = new EventoPublicoDAO();
    }

    // =====================================================
    // CURSOS DA ESCOLA
    // =====================================================

    public List<String> listarCursos() {
        return cursoDAO.listarNomesAtivos();
    }

    // =====================================================
    // SÉRIES
    // =====================================================

    public List<String> listarSeries() {
        return serieDAO.listarNomesAtivos();
    }

    // =====================================================
    // PÚBLICOS DISPONÍVEIS
    // =====================================================

    public List<EventoPublico> listarPublicosDisponiveis() {
        return eventoPublicoDAO.listarPublicosDisponiveis();
    }

    // =====================================================
    // EVENTOS
    // =====================================================

    public List<Evento> listarEventos() {
        return eventoDAO.listarEventos();
    }

    public boolean cadastrarEvento(
            Evento evento
    ) {

        validarPublico(evento);

        return eventoDAO.cadastrarEvento(
                evento
        );
    }

    public Evento buscarPorId(
            long idEvento
    ) {

        return eventoDAO.buscarPorId(
                idEvento
        );
    }

    public boolean atualizarEvento(
            Evento evento
    ) {

        validarPublico(evento);

        return eventoDAO.atualizarEvento(
                evento
        );
    }

    // =====================================================
    // STATUS
    // =====================================================

    public String calcularStatus(
            Evento evento
    ) {

        LocalDateTime agora =
                LocalDateTime.now();

        if ("CANCELADO".equals(
                evento.getStatus()
        )) {

            return "CANCELADO";
        }

        if (evento.getDataInicio() == null) {
            return "PLANEJADO";
        }

        if (agora.isBefore(
                evento.getDataInicio()
        )) {

            return "PLANEJADO";
        }

        if (
                evento.getDataFim() != null
                        && agora.isAfter(
                        evento.getDataFim()
                )
        ) {

            return "ENCERRADO";
        }

        return "EM_ANDAMENTO";
    }

    // =====================================================
    // VALIDAR PÚBLICO
    // =====================================================

    private void validarPublico(
            Evento evento
    ) {

        if (evento.isPublicoTodos()) {

            evento.limparPublicos();

            return;
        }

        if (
                evento.getPublicos() == null
                        || evento.getPublicos().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Selecione pelo menos um público para o evento."
            );
        }

        /*
         * Remove possíveis duplicidades.
         */
        Set<String> cursosInteiros =
                new HashSet<>();

        for (EventoPublico publico : evento.getPublicos()) {
            if (publico != null
                    && publico.getCurso() != null
                    && !publico.getCurso().isBlank()
                    && publico.isCursoInteiro()) {
                cursosInteiros.add(publico.getCurso().trim());
            }
        }

        Set<String> chaves =
                new LinkedHashSet<>();

        List<EventoPublico> listaFinal =
                new ArrayList<>();

        for (EventoPublico publico : evento.getPublicos()) {
            if (publico == null
                    || publico.getCurso() == null
                    || publico.getCurso().isBlank()) {
                continue;
            }

            String curso = publico.getCurso().trim();

            // Se o curso inteiro foi selecionado, as séries desse mesmo
            // curso seriam redundantes e não são persistidas.
            if (!publico.isCursoInteiro() && cursosInteiros.contains(curso)) {
                continue;
            }

            if (chaves.add(publico.getChave())) {
                listaFinal.add(new EventoPublico(
                        curso,
                        publico.isCursoInteiro() ? null : publico.getSerie().trim()
                ));
            }
        }

        if (listaFinal.isEmpty()) {

            throw new IllegalArgumentException(
                    "Selecione pelo menos um público para o evento."
            );
        }

        evento.setPublicos(
                listaFinal
        );
    }
}
