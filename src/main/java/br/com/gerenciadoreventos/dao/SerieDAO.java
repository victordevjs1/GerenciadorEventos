package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SerieDAO {

    public List<String> listarNomesAtivosPorCurso(String nomeCurso) {
        List<String> series = new ArrayList<>();
        String sql = """
                SELECT s.nome
                FROM serie s
                INNER JOIN curso c ON c.id_curso = s.id_curso
                WHERE s.ativo = TRUE
                  AND c.ativo = TRUE
                  AND c.nome = ?
                ORDER BY s.numero, s.nome
                """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeCurso);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) series.add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar séries do curso.", e);
        }
        return series;
    }

    public Integer buscarNumeroPorCursoENome(String nomeCurso, String nomeSerie) {
        String sql = """
                SELECT s.numero
                FROM serie s
                INNER JOIN curso c ON c.id_curso = s.id_curso
                WHERE c.nome = ? AND s.nome = ? AND s.ativo = TRUE AND c.ativo = TRUE
                LIMIT 1
                """;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeCurso);
            stmt.setString(2, nomeSerie);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("numero");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao consultar série.", e);
        }
        return null;
    }

    public List<String> listarNomesAtivos() {
        List<String> series = new ArrayList<>();
        String sql = """
                SELECT DISTINCT s.nome, s.numero
                FROM serie s
                INNER JOIN curso c ON c.id_curso = s.id_curso
                WHERE s.ativo = TRUE
                  AND c.ativo = TRUE
                ORDER BY s.numero, s.nome
                """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                series.add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar séries.", e);
        }

        return series;
    }
}
