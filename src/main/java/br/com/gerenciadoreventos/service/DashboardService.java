package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.DashboardDAO;

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
}