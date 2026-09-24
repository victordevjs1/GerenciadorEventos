package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.LinhaRelatorioTurma;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RelatorioFrequenciaDAO {

    // =====================================================
    // DIAS COM CHAMADA REGISTRADA NO MÊS
    // =====================================================

    public int contarDiasLetivos(
            int ano,
            int mes
    ) {

        String sql = """
                SELECT COUNT(DISTINCT data_presenca) AS total
                FROM presenca_evento
                WHERE YEAR(data_presenca) = ?
                  AND MONTH(data_presenca) = ?
                """;

        try (
                Connection conn =
                        Conexao.conectar();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, ano);
            stmt.setInt(2, mes);

            try (
                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getInt(
                            "total"
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return 0;
    }

    // =====================================================
    // RELATÓRIO POR TURMA
    //
    // O CSV importa somente os PRESENTES.
    //
    // A turma é formada pelos alunos ativos que possuem
    // inscrição em evento e que possuem registros de
    // presença no mês.
    //
    // total_registros = registros de chamada
    // total_presentes = registros PRESENTE
    // =====================================================

    public List<LinhaRelatorioTurma>
    gerarRelatorioPorTurma(
            int ano,
            int mes
    ) {

        List<LinhaRelatorioTurma> linhas =
                new ArrayList<>();

        String sql = """
                SELECT
                    a.turma,
                    COUNT(DISTINCT a.id_aluno) AS matriculados,
                    COUNT(pe.id_presenca) AS total_registros,
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
                FROM aluno a
                INNER JOIN inscricao_evento ie
                    ON ie.id_aluno = a.id_aluno
                INNER JOIN presenca_evento pe
                    ON pe.id_inscricao = ie.id_inscricao
                WHERE a.ativo = TRUE
                  AND ie.status <> 'CANCELADO'
                  AND YEAR(pe.data_presenca) = ?
                  AND MONTH(pe.data_presenca) = ?
                GROUP BY a.turma
                ORDER BY a.turma ASC
                """;

        try (
                Connection conn =
                        Conexao.conectar();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, ano);
            stmt.setInt(2, mes);

            try (
                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                while (rs.next()) {

                    LinhaRelatorioTurma linha =
                            new LinhaRelatorioTurma();

                    linha.setTurma(
                            rs.getString(
                                    "turma"
                            )
                    );

                    linha.setMatriculados(
                            rs.getInt(
                                    "matriculados"
                            )
                    );

                    linha.setTotalRegistros(
                            rs.getInt(
                                    "total_registros"
                            )
                    );

                    linha.setTotalPresentes(
                            rs.getInt(
                                    "total_presentes"
                            )
                    );

                    linhas.add(linha);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return linhas;
    }

    // =====================================================
    // TAXA GLOBAL
    // =====================================================

    public double calcularTaxaGlobal(
            List<LinhaRelatorioTurma> linhas
    ) {

        int totalRegistros = 0;

        int totalPresentes = 0;

        for (
                LinhaRelatorioTurma linha
                : linhas
        ) {

            totalRegistros +=
                    linha.getTotalRegistros();

            totalPresentes +=
                    linha.getTotalPresentes();
        }

        if (totalRegistros == 0) {
            return 0;
        }

        return (
                totalPresentes * 100.0
        ) / totalRegistros;
    }
}
