package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.EventoDAO;
import br.com.gerenciadoreventos.model.Evento;

import java.util.List;

public class EventoService {

    private final EventoDAO eventoDAO;

    public EventoService() {
        eventoDAO = new EventoDAO();
    }

    public List<Evento> listarEventos() {
        return eventoDAO.listarEventos();
    }
}
