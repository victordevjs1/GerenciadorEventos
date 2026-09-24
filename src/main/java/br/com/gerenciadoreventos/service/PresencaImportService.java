package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.AlunoDAO;
import br.com.gerenciadoreventos.dao.PresencaImportDAO;
import br.com.gerenciadoreventos.model.Aluno;
import br.com.gerenciadoreventos.model.AtividadeOpcao;
import br.com.gerenciadoreventos.model.EventoOpcao;

import java.time.LocalDate;
import java.util.List;

public class PresencaImportService {

    private final AlunoDAO alunoDAO;
    private final PresencaImportDAO presencaDAO;

    public PresencaImportService() {
        alunoDAO = new AlunoDAO();
        presencaDAO = new PresencaImportDAO();
    }


    // =====================================================
    // ALUNO
    // =====================================================

    public Aluno buscarAlunoPorRm(String rm) {
        return alunoDAO.buscarPorRm(rm);
    }


    // =====================================================
    // LISTAS PARA OS COMBOS
    // =====================================================

    public List<EventoOpcao> listarEventos() {
        return presencaDAO.listarEventos();
    }

    public List<AtividadeOpcao> listarAtividades() {
        return presencaDAO.listarAtividades();
    }


    // =====================================================
    // PRESENÇA EM EVENTO
    // =====================================================

    public boolean registrarPresencaEvento(
            long idAluno,
            long idEvento,
            LocalDate data,
            String status
    ) {

        Long idInscricao = presencaDAO.obterOuCriarInscricao(idAluno, idEvento);

        if (idInscricao == null) {
            return false;
        }

        return presencaDAO.registrarPresenca(idInscricao, data, status);
    }


    // =====================================================
    // PARTICIPAÇÃO EM ATIVIDADE
    // =====================================================

    public boolean registrarParticipacaoAtividade(
            long idAluno,
            long idAtividade,
            String status
    ) {
        return presencaDAO.registrarParticipacaoAtividade(idAluno, idAtividade, status);
    }
}
