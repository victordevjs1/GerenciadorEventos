package br.com.gerenciadoreventos.database;

import br.com.gerenciadoreventos.audit.ContextoAuditoria;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class Conexao {
    private static final String URL = "jdbc:mysql://localhost:3306/gerenciador_eventos";
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    public static Connection conectar() throws SQLException {
        Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
        definirUsuarioAuditoria(conexao, ContextoAuditoria.getIdUsuario());
        return conexao;
    }

    public static void definirUsuarioAuditoria(Connection conexao, Long idUsuario) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement("SET @etask_usuario_id = ?")) {
            if (idUsuario == null) {
                stmt.setNull(1, Types.BIGINT);
            } else {
                stmt.setLong(1, idUsuario);
            }
            stmt.execute();
        }
    }
}
