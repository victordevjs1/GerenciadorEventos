package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.LinhaRelatorioCursoSerie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RelatorioFrequenciaDAO {

    // DIAS COM CHAMADA REGISTRADA NO MÊS

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
                Connection conn = Conexao.conectar();
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
                    return rs.getInt( "total" );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // RELATÓRIO POR CURSO E SÉRIE O CSV importa somente os PRESENTES. O agrupamento é feito por curso e série a partir dos alunos ativos que possuem inscrição em evento e que possuem registros de presença no mês. total_registros = registros de chamada total_presentes = registros PRESENTE

    public List<LinhaRelatorioCursoSerie>
    gerarRelatorioPorCursoSerie(
            int ano,
            int mes
    ) {
        List<LinhaRelatorioCursoSerie> linhas = new ArrayList<>();
        String sql = """
                SELECT
                    c.nome AS curso,
                    s.nome AS serie,
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
                INNER JOIN curso c
                    ON c.id_curso = a.id_curso
                INNER JOIN serie s
                    ON s.id_serie = a.id_serie
                INNER JOIN inscricao_evento ie
                    ON ie.id_aluno = a.id_aluno
                INNER JOIN presenca_evento pe
                    ON pe.id_inscricao = ie.id_inscricao
                WHERE a.ativo = TRUE
                  AND ie.status <> 'CANCELADO'
                  AND YEAR(pe.data_presenca) = ?
                  AND MONTH(pe.data_presenca) = ?
                GROUP BY c.id_curso, c.nome, s.id_serie, s.nome
                ORDER BY c.nome ASC, s.numero ASC
                """;
        try (
                Connection conn = Conexao.conectar();
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
                    LinhaRelatorioCursoSerie linha = new LinhaRelatorioCursoSerie();
                    linha.setCurso(rs.getString("curso"));
                    linha.setSerie(rs.getString("serie"));
                    linha.setMatriculados( rs.getInt( "matriculados" ) );
                    linha.setTotalRegistros( rs.getInt( "total_registros" ) );
                    linha.setTotalPresentes( rs.getInt( "total_presentes" ) );
                    linhas.add(linha);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return linhas;
    }

    // TAXA GLOBAL

    public double calcularTaxaGlobal(
            List<LinhaRelatorioCursoSerie> linhas
    ) {
        int totalRegistros = 0;
        int totalPresentes = 0;
        for (
                LinhaRelatorioCursoSerie linha
                : linhas
        ) {
            totalRegistros += linha.getTotalRegistros();
            totalPresentes += linha.getTotalPresentes();
        }
        if (totalRegistros == 0) {
            return 0;
        }
        return ( totalPresentes * 100.0 ) / totalRegistros;
    }
}
