package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.AlunoDAO;
import br.com.gerenciadoreventos.dao.PresencaImportDAO;
import br.com.gerenciadoreventos.model.Aluno;
import br.com.gerenciadoreventos.model.AtividadeOpcao;
import br.com.gerenciadoreventos.model.EventoOpcao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    // PÚBLICO / INSCRIÇÕES DO EVENTO
    // =====================================================

    public boolean alunoPertenceAoPublicoEvento(
            long idAluno,
            long idEvento
    ) {
        return presencaDAO.alunoPertenceAoPublicoEvento(
                idAluno,
                idEvento
        );
    }

    public int sincronizarInscricoesPublicoEvento(long idEvento) {
        return presencaDAO.sincronizarInscricoesPublicoEvento(idEvento);
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
    // MARCAR AUSENTES DO EVENTO
    //
    // O CSV do Google Forms contém somente quem respondeu.
    // Antes desta etapa, as inscrições são sincronizadas com
    // evento_publico. Assim, somente alunos do curso/série
    // correto entram no cálculo de AUSENTE.
    // =====================================================

    public int registrarAusenciasEvento(
            long idEvento,
            LocalDate data,
            Set<String> rmsPresentes
    ) {

        Map<String, Long> inscricoes =
                presencaDAO.listarInscricoesAtivasEvento(idEvento);

        int ausentesRegistrados = 0;

        for (Map.Entry<String, Long> entrada : inscricoes.entrySet()) {

            String rm = entrada.getKey();

            if (rmsPresentes.contains(rm)) {
                continue;
            }

            boolean sucesso = presencaDAO.registrarPresenca(
                    entrada.getValue(),
                    data,
                    "AUSENTE"
            );

            if (sucesso) {
                ausentesRegistrados++;
            }
        }

        return ausentesRegistrados;
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
