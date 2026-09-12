package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.EventoDAO;
import br.com.gerenciadoreventos.model.Evento;

import java.time.LocalDateTime;
import java.util.List;

public class EventoService {

    private final EventoDAO eventoDAO;

    public EventoService() {
        eventoDAO = new EventoDAO();
    }

    public List<Evento> listarEventos() {
        return eventoDAO.listarEventos();
    }

    public boolean cadastrarEvento(Evento evento) {
        return eventoDAO.cadastrarEvento(evento);
    }

    public Evento buscarPorId(long idEvento) {
        return eventoDAO.buscarPorId(idEvento);
    }

    public String calcularStatus(Evento evento) {

        LocalDateTime agora = LocalDateTime.now();

        if ("CANCELADO".equals(evento.getStatus())) {
            return "CANCELADO";
        }

        if (agora.isBefore(evento.getDataInicio())) {
            return "PLANEJADO";
        }

        if (agora.isAfter(evento.getDataFim())) {
            return "ENCERRADO";
        }

        return "EM_ANDAMENTO";
    }

    public boolean atualizarEvento(Evento evento) {
        return eventoDAO.atualizarEvento(evento);
    }
}