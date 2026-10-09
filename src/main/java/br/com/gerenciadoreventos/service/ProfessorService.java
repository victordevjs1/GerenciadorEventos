package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.ProfessorDAO;
import br.com.gerenciadoreventos.model.Professor;

import java.util.List;

public class ProfessorService {
    private final ProfessorDAO professorDAO;

    public ProfessorService() {
        professorDAO = new ProfessorDAO();
    }

    public List<Professor> listarProfessores(
            boolean ativos
    ) {
        return professorDAO.listarProfessores(ativos);
    }

    public boolean cadastrarProfessor(
            Professor professor
    ) {
        return professorDAO.cadastrarProfessor( professor );
    }

    public boolean desativarProfessor(
            long idProfessor
    ) {
        return professorDAO.desativar( idProfessor );
    }

    public boolean ativarProfessor(
            long idProfessor
    ) {
        return professorDAO.ativar( idProfessor );
    }
}
