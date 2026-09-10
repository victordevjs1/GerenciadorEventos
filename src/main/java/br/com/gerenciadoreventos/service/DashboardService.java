package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.DashboardDAO;
import br.com.gerenciadoreventos.model.Evento;

import java.util.List;
import java.util.Map;

public class DashboardService {

    private final DashboardDAO dashboardDAO =
            new DashboardDAO();

    public int contarEventos() {
        return dashboardDAO.contarEventos();
    }

    public int contarInscritos() {
        return dashboardDAO.contarInscritos();
    }

    public int contarEventosAbertos() {
        return dashboardDAO.contarEventosAbertos();
    }

    public int contarEventosHoje() {
        return dashboardDAO.contarEventosHoje();
    }

    public Map<Integer, Integer> obterEventosPorMes() {
        return dashboardDAO.contarEventosPorMes();
    }

    public List<Evento> obterProximosEventos(int limite) {
        return dashboardDAO.buscarProximosEventos(limite);
    }

    public List<Evento> obterEventosRecentes(int limite) {
        return dashboardDAO.buscarEventosRecentes(limite);
    }

    public int contarInscritosPorEvento(long idEvento) {
        return dashboardDAO.contarInscritosPorEvento(idEvento);
    }
}
