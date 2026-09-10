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

}
