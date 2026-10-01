package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    public List<String> listarNomesAtivos() {
        List<String> cursos = new ArrayList<>();
        String sql = """
                SELECT nome
                FROM curso
                WHERE ativo = TRUE
                ORDER BY nome
                """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                cursos.add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar cursos.", e);
        }

        return cursos;
    }
}
