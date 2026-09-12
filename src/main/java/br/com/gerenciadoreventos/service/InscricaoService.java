package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.InscricaoDAO;

import java.util.List;

public class InscricaoService {

    private final InscricaoDAO inscricaoDAO;

    public InscricaoService() {
        inscricaoDAO = new InscricaoDAO();
    }

    // =====================================================
    // LISTAR TODAS AS INSCRIÇÕES
    // =====================================================

    public List<InscricaoDAO.Inscricao> listarInscricoes() {

        return inscricaoDAO.listarInscricoes(
                null,
                "",
                "TODOS"
        );
    }

    // =====================================================
    // LISTAR COM FILTROS
    // =====================================================

    public List<InscricaoDAO.Inscricao> listarInscricoes(
            Long idEvento,
            String busca,
            String status
    ) {

        return inscricaoDAO.listarInscricoes(
                idEvento,
                busca,
                status
        );
    }

    // =====================================================
    // CADASTRAR INSCRIÇÃO
    // =====================================================

    public boolean cadastrarInscricao(
            long idAluno,
            long idEvento,
            String observacao
    ) {

        // Verifica se o aluno já está inscrito
        if (
                inscricaoDAO.alunoJaInscrito(
                        idAluno,
                        idEvento
                )
        ) {

            return false;
        }

        // Cadastra no banco
        return inscricaoDAO.cadastrarInscricao(
                idAluno,
                idEvento,
                observacao
        );
    }

    // =====================================================
    // CANCELAR INSCRIÇÃO
    // =====================================================

    public boolean cancelarInscricao(
            long idInscricao
    ) {

        return inscricaoDAO.cancelarInscricao(
                idInscricao
        );
    }

    // =====================================================
    // VERIFICAR SE JÁ ESTÁ INSCRITO
    // =====================================================

    public boolean alunoJaInscrito(
            long idAluno,
            long idEvento
    ) {

        return inscricaoDAO.alunoJaInscrito(
                idAluno,
                idEvento
        );
    }

    // =====================================================
    // BUSCAR ALUNOS
    // =====================================================

    public List<br.com.gerenciadoreventos.model.Aluno> buscarAlunos(
            String busca
    ) {

        return inscricaoDAO.buscarAlunos(busca);
    }

    // =====================================================
    // LISTAR EVENTOS
    // =====================================================

    public List<br.com.gerenciadoreventos.model.Evento> listarEventos() {

        return inscricaoDAO.listarEventos();
    }
}
