package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {

    // =========================================================
    // TOTAL DE EVENTOS
    // =========================================================

    public int contarEventos() {
        String sql = "SELECT COUNT(*) FROM evento";

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    // =========================================================
    // TOTAL DE INSCRITOS
    // =========================================================

    public int contarInscritos() {
        String sql = """
        SELECT COUNT(*)
        FROM inscricao_evento
        WHERE status = 'INSCRITO'
        """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    // =========================================================
    // EVENTOS ABERTOS
    // =========================================================

    public int contarEventosAbertos() {
        String sql = """
        SELECT COUNT(*)
        FROM evento
        WHERE status = 'ABERTO'
        """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    // =========================================================
    // EVENTOS DE HOJE
    // =========================================================

    public int contarEventosHoje() {
        String sql = """
        SELECT COUNT(*)
        FROM evento
        WHERE DATE(data_inicio) = CURDATE()
        """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}