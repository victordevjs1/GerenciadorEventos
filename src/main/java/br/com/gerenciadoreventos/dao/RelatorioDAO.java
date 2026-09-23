package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável pelos dados exibidos na tela de Relatórios
 * (aba "Resumo"): totais gerais e desempenho por evento.
 *
 * Consultas em cima de: evento, inscricao_evento, presenca_evento,
 * aluno (ver script do banco).
 */
public class RelatorioDAO {

    // =====================================================
    // RESUMO GERAL (cards do topo da lista de relatórios)
    // =====================================================

    public ResumoGeral buscarResumoGeral() {

        ResumoGeral resumo = new ResumoGeral();

        String sqlEventos =
                "SELECT COUNT(*) FROM evento";

        String sqlInscricoes =
                "SELECT COUNT(*) FROM inscricao_evento " +
                        "WHERE status <> 'CANCELADO'";

        String sqlPresencas =
                "SELECT " +
                        "COUNT(*) AS total, " +
                        "SUM(status = 'PRESENTE') AS presentes " +
                        "FROM presenca_evento";

        try (Connection conexao = Conexao.conectar();
             Statement stmt = conexao.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(sqlEventos)) {
                if (rs.next()) {
                    resumo.totalEventos = rs.getInt(1);
                }
            }

            try (ResultSet rs = stmt.executeQuery(sqlInscricoes)) {
                if (rs.next()) {
                    resumo.totalInscricoes = rs.getInt(1);
                }
            }

            try (ResultSet rs = stmt.executeQuery(sqlPresencas)) {
                if (rs.next()) {

                    int total = rs.getInt("total");
                    int presentes = rs.getInt("presentes");

                    resumo.taxaComparecimento = total == 0
                            ? 0.0
                            : (presentes * 100.0) / total;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resumo;
    }


    // =====================================================
    // DESEMPENHO DE UM EVENTO (cards da tela de detalhe)
    // =====================================================

    public EventoDesempenho buscarDesempenhoEvento(long idEvento) {

        EventoDesempenho desempenho = new EventoDesempenho();

        String sqlInscritos =
                "SELECT COUNT(*) FROM inscricao_evento " +
                        "WHERE id_evento = ? AND status <> 'CANCELADO'";

        String sqlPresentes =
                "SELECT COUNT(*) FROM presenca_evento pe " +
                        "INNER JOIN inscricao_evento ie ON ie.id_inscricao = pe.id_inscricao " +
                        "WHERE ie.id_evento = ? AND pe.status = 'PRESENTE'";

        String sqlAusentes =
                "SELECT COUNT(*) FROM presenca_evento pe " +
                        "INNER JOIN inscricao_evento ie ON ie.id_inscricao = pe.id_inscricao " +
                        "WHERE ie.id_evento = ? AND pe.status IN ('AUSENTE', 'JUSTIFICADO')";

        try (Connection conexao = Conexao.conectar()) {

            desempenho.totalInscritos = contar(conexao, sqlInscritos, idEvento);
            desempenho.presencasConfirmadas = contar(conexao, sqlPresentes, idEvento);
            desempenho.ausentes = contar(conexao, sqlAusentes, idEvento);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return desempenho;
    }

    private int contar(Connection conexao, String sql, long idEvento) throws SQLException {

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, idEvento);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }


    // =====================================================
    // GRÁFICO DE COMPARECIMENTO (presenças "PRESENTE" por dia)
    // =====================================================

    public List<PontoComparecimento> buscarComparecimentoPorData(long idEvento) {

        List<PontoComparecimento> pontos = new ArrayList<>();

        String sql =
                "SELECT pe.data_presenca, COUNT(*) AS total " +
                        "FROM presenca_evento pe " +
                        "INNER JOIN inscricao_evento ie ON ie.id_inscricao = pe.id_inscricao " +
                        "WHERE ie.id_evento = ? AND pe.status = 'PRESENTE' " +
                        "GROUP BY pe.data_presenca " +
                        "ORDER BY pe.data_presenca";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, idEvento);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    PontoComparecimento ponto = new PontoComparecimento();
                    ponto.data = rs.getDate("data_presenca").toLocalDate();
                    ponto.total = rs.getInt("total");

                    pontos.add(ponto);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pontos;
    }


    // =====================================================
    // STATUS DE PRESENÇA DE CADA ALUNO INSCRITO NO EVENTO
    //
    // Confirmado  -> tem ao menos 1 presença PRESENTE
    // Ausente     -> não tem PRESENTE, mas tem AUSENTE/JUSTIFICADO
    // Pendente    -> ainda não tem nenhuma presença registrada
    // =====================================================

    public List<AlunoStatus> listarStatusAlunos(long idEvento) {

        List<AlunoStatus> lista = new ArrayList<>();

        String sql =
                "SELECT a.nome, " +
                        "  CASE " +
                        "    WHEN EXISTS ( " +
                        "      SELECT 1 FROM presenca_evento pe " +
                        "      WHERE pe.id_inscricao = ie.id_inscricao AND pe.status = 'PRESENTE' " +
                        "    ) THEN 'Confirmado' " +
                        "    WHEN EXISTS ( " +
                        "      SELECT 1 FROM presenca_evento pe " +
                        "      WHERE pe.id_inscricao = ie.id_inscricao AND pe.status IN ('AUSENTE', 'JUSTIFICADO') " +
                        "    ) THEN 'Ausente' " +
                        "    ELSE 'Pendente' " +
                        "  END AS status_presenca " +
                        "FROM inscricao_evento ie " +
                        "INNER JOIN aluno a ON a.id_aluno = ie.id_aluno " +
                        "WHERE ie.id_evento = ? AND ie.status <> 'CANCELADO' " +
                        "ORDER BY a.nome";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, idEvento);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    AlunoStatus status = new AlunoStatus();
                    status.nome = rs.getString("nome");
                    status.status = rs.getString("status_presenca");

                    lista.add(status);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    // =====================================================
    // DTOs
    // =====================================================

    public static class ResumoGeral {
        public int totalEventos;
        public int totalInscricoes;
        public double taxaComparecimento;
    }

    public static class EventoDesempenho {
        public int totalInscritos;
        public int presencasConfirmadas;
        public int ausentes;
    }

    public static class PontoComparecimento {
        public LocalDate data;
        public int total;
    }

    public static class AlunoStatus {
        public String nome;
        public String status;
    }
}