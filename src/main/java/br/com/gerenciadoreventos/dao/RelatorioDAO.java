package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.AlunoStatus;
import br.com.gerenciadoreventos.model.EventoDesempenho;
import br.com.gerenciadoreventos.model.PontoComparecimento;
import br.com.gerenciadoreventos.model.ResumoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {

    public ResumoGeral buscarResumoGeral() {

        ResumoGeral resumo = new ResumoGeral();

        try (Connection conn = Conexao.conectar()) {

            String sqlEventos =
                    "SELECT COUNT(*) FROM evento";

            try (
                    PreparedStatement stmt =
                            conn.prepareStatement(sqlEventos);
                    ResultSet rs = stmt.executeQuery()
            ) {
                if (rs.next()) {
                    resumo.setTotalEventos(rs.getInt(1));
                }
            }

            String sqlInscricoes = """
                    SELECT COUNT(*)
                    FROM inscricao_evento
                    WHERE status <> 'CANCELADO'
                    """;

            try (
                    PreparedStatement stmt =
                            conn.prepareStatement(sqlInscricoes);
                    ResultSet rs = stmt.executeQuery()
            ) {
                if (rs.next()) {
                    resumo.setTotalInscricoes(rs.getInt(1));
                }
            }

            String sqlPresencas = """
                    SELECT
                        COUNT(*) AS total,
                        COALESCE(
                            SUM(
                                CASE
                                    WHEN status = 'PRESENTE'
                                    THEN 1
                                    ELSE 0
                                END
                            ),
                            0
                        ) AS presentes
                    FROM presenca_evento
                    """;

            try (
                    PreparedStatement stmt =
                            conn.prepareStatement(sqlPresencas);
                    ResultSet rs = stmt.executeQuery()
            ) {
                if (rs.next()) {

                    int total = rs.getInt("total");
                    int presentes = rs.getInt("presentes");

                    resumo.setTaxaComparecimento(
                            total == 0
                                    ? 0
                                    : presentes * 100.0 / total
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resumo;
    }

    public EventoDesempenho buscarDesempenhoEvento(
            long idEvento
    ) {

        EventoDesempenho desempenho =
                new EventoDesempenho();

        String sql = """
                SELECT
                    COUNT(DISTINCT ie.id_inscricao)
                        AS total_inscritos,

                    COUNT(
                        DISTINCT CASE
                            WHEN pe.status = 'PRESENTE'
                            THEN ie.id_inscricao
                        END
                    ) AS presencas_confirmadas

                FROM inscricao_evento ie

                LEFT JOIN presenca_evento pe
                    ON pe.id_inscricao = ie.id_inscricao

                WHERE ie.id_evento = ?
                  AND ie.status <> 'CANCELADO'
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, idEvento);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    int inscritos =
                            rs.getInt("total_inscritos");

                    int presentes =
                            rs.getInt("presencas_confirmadas");

                    desempenho.setTotalInscritos(inscritos);
                    desempenho.setPresencasConfirmadas(presentes);
                    desempenho.setAusentes(
                            inscritos - presentes
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return desempenho;
    }

    public List<PontoComparecimento> buscarComparecimentoPorData(
            long idEvento
    ) {

        List<PontoComparecimento> pontos =
                new ArrayList<>();

        String sql = """
                SELECT
                    pe.data_presenca AS data,
                    SUM(
                        CASE
                            WHEN pe.status = 'PRESENTE'
                            THEN 1
                            ELSE 0
                        END
                    ) AS total

                FROM presenca_evento pe

                INNER JOIN inscricao_evento ie
                    ON ie.id_inscricao = pe.id_inscricao

                WHERE ie.id_evento = ?

                GROUP BY pe.data_presenca

                ORDER BY pe.data_presenca
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, idEvento);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Date data =
                            rs.getDate("data");

                    pontos.add(
                            new PontoComparecimento(
                                    data.toLocalDate(),
                                    rs.getInt("total")
                            )
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pontos;
    }

    public List<AlunoStatus> listarStatusAlunos(
            long idEvento
    ) {

        List<AlunoStatus> alunos =
                new ArrayList<>();

        String sql = """
                SELECT
                    a.nome,
                    COUNT(pe.id_presenca)
                        AS total_registros,

                    COALESCE(
                        SUM(
                            CASE
                                WHEN pe.status = 'PRESENTE'
                                THEN 1
                                ELSE 0
                            END
                        ),
                        0
                    ) AS total_presentes

                FROM inscricao_evento ie

                INNER JOIN aluno a
                    ON a.id_aluno = ie.id_aluno

                LEFT JOIN presenca_evento pe
                    ON pe.id_inscricao = ie.id_inscricao

                WHERE ie.id_evento = ?
                  AND ie.status <> 'CANCELADO'

                GROUP BY a.id_aluno, a.nome

                ORDER BY a.nome
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, idEvento);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    int registros =
                            rs.getInt("total_registros");

                    int presentes =
                            rs.getInt("total_presentes");

                    String status;

                    if (registros == 0) {
                        status = "Pendente";
                    } else if (presentes > 0) {
                        status = "Confirmado";
                    } else {
                        status = "Ausente";
                    }

                    alunos.add(
                            new AlunoStatus(
                                    rs.getString("nome"),
                                    status
                            )
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alunos;
    }
}
