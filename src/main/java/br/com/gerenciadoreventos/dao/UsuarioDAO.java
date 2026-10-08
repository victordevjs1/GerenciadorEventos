package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

    public Usuario buscarPorEmail(String email) {

        String sql = """
            SELECT
                id_usuario,
                nome,
                email,
                senha,
                tipo_usuario,
                ativo
            FROM usuario
            WHERE email = ?
            AND ativo = TRUE
            """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    Usuario u = new Usuario();

                    u.setId(rs.getLong("id_usuario"));
                    u.setNome(rs.getString("nome"));
                    u.setEmail(rs.getString("email"));
                    u.setSenha(rs.getString("senha"));
                    u.setTipoUsuario(rs.getString("tipo_usuario"));
                    u.setAtivo(rs.getBoolean("ativo"));

                    return u;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean emailEmUsoPorOutroUsuario(String email, long idUsuario) {

        String sql = """
            SELECT 1
            FROM usuario
            WHERE email = ?
              AND id_usuario <> ?
            LIMIT 1
            """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, email);
            stmt.setLong(2, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao verificar e-mail do usuário.", e);
        }
    }

    public void atualizarPerfil(long idUsuario, String nome, String email, String senhaHash) {

        String sql = """
            UPDATE usuario
            SET nome = ?, email = ?, senha = ?
            WHERE id_usuario = ?
              AND ativo = TRUE
            """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senhaHash);
            stmt.setLong(4, idUsuario);

            int alterados = stmt.executeUpdate();
            if (alterados == 0) {
                throw new RuntimeException("Usuário não encontrado ou inativo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar perfil do usuário.", e);
        }
    }

}
