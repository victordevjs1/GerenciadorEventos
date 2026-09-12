package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.EventoDAO;
import br.com.gerenciadoreventos.model.Evento;

import java.util.List;
import java.time.LocalDateTime;

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
    public String calcularStatus(Evento evento) {

        LocalDateTime agora = LocalDateTime.now();

        if (evento.getStatus().equals("CANCELADO")) {
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
}
