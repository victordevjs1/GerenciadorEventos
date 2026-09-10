package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.DashboardDAO;
import java.util.Map;
import br.com.gerenciadoreventos.model.Evento;

import java.util.List;
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
}