package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AuditoriaDAO {
    public void registrarAcesso(long idUsuario, String acao, String descricao) {
        String sql = """
                INSERT INTO auditoria (id_usuario, acao, tabela, id_registro, registro_chave, descricao)
                VALUES (?, ?, NULL, NULL, NULL, ?)
                """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idUsuario);
            stmt.setString(2, acao);
            stmt.setString(3, descricao);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar auditoria.", e);
        }
    }
}
