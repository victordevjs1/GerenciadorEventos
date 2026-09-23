package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.AlunoDAO;
import br.com.gerenciadoreventos.model.Aluno;

import java.time.Year;
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

    // =====================================================
    // FORMATURA AUTOMÁTICA
    //
    // Calcula o ano em que um aluno cursando a série
    // informada (1, 2 ou 3) conclui o ensino médio.
    // Ex.: em 2026, um aluno no 1º ano conclui em 2028.
    // =====================================================

    public int calcularAnoConclusao(int serie) {

        int anoAtual =
                Year.now().getValue();

        return anoAtual + (3 - serie);
    }

    // =====================================================
    // Verifica todos os alunos ativos e desativa quem já
    // passou do ano de conclusão previsto (formados).
    // Deve ser chamado ao abrir a tela de Alunos (ou no
    // início da aplicação) para manter a base atualizada
    // a cada virada de ano.
    // =====================================================

    public int desativarAlunosFormados() {

        int anoAtual =
                Year.now().getValue();

        return alunoDAO.desativarFormados(anoAtual);
    }
}