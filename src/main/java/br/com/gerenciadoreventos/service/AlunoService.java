package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.AlunoDAO;
import br.com.gerenciadoreventos.model.Aluno;

import java.util.List;

public class AlunoService {

    private final AlunoDAO alunoDAO;

    public AlunoService() {
        alunoDAO = new AlunoDAO();
    }

    public List<Aluno> listarAlunos() {
        return alunoDAO.listarAlunos();
    }

    public boolean cadastrarAluno(Aluno aluno) {
        return alunoDAO.cadastrarAluno(aluno);
    }

    public boolean desativarAluno(long idAluno) {
        return alunoDAO.desativar(idAluno);
    }
    public List<Aluno> listarAlunosPorStatus(boolean ativo) {
        return alunoDAO.listarAlunosPorStatus(ativo);
    }

}
