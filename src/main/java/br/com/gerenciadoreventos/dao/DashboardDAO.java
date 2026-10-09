package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.Evento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {

    // TOTAL DE EVENTOS

    public int contarEventos() {
        String sql = """
            SELECT COUNT(*)
            FROM evento
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

    // TOTAL DE INSCRITOS

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

    // EVENTOS ABERTOS

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

    // EVENTOS DE HOJE

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

    // EVENTOS POR MÊS

    public Map<Integer, Integer> contarEventosPorMes() {
        String sql = """
            SELECT
                MONTH(data_inicio) AS mes,
                COUNT(*) AS quantidade
            FROM evento
            WHERE YEAR(data_inicio) = YEAR(CURDATE())
            GROUP BY MONTH(data_inicio)
            ORDER BY MONTH(data_inicio)
            """;
        Map<Integer, Integer> resultado = new HashMap<>();
        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                int mes = rs.getInt("mes");
                int quantidade = rs.getInt("quantidade");
                resultado.put(mes, quantidade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    // PRÓXIMOS EVENTOS

    public List<Evento> buscarProximosEventos(int limite) {
        String sql = """
        SELECT
            id_evento,
            id_usuario_criador,
            nome,
            descricao,
            data_inicio,
            data_fim,
            local,
            capacidade,
            status
        FROM evento
        WHERE data_inicio > NOW()
        AND status <> 'CANCELADO'
        ORDER BY data_inicio ASC
        LIMIT ?
        """;
        List<Evento> eventos = new ArrayList<>();
        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Evento evento = new Evento();
                    evento.setId( rs.getLong("id_evento") );
                    evento.setIdUsuarioCriador( rs.getLong("id_usuario_criador") );
                    evento.setNome( rs.getString("nome") );
                    evento.setDescricao( rs.getString("descricao") );

                    // DATA DE INÍCIO
                    java.sql.Timestamp dataInicio = rs.getTimestamp("data_inicio");
                    if (dataInicio != null) {
                        evento.setDataInicio( dataInicio.toLocalDateTime() );
                    }

                    // DATA DE FIM
                    java.sql.Timestamp dataFim = rs.getTimestamp("data_fim");
                    if (dataFim != null) {
                        evento.setDataFim( dataFim.toLocalDateTime() );
                    } else {
                        evento.setDataFim(null);
                    }
                    evento.setLocal( rs.getString("local") );
                    evento.setCapacidade( rs.getInt("capacidade") );
                    evento.setStatus( rs.getString("status") );
                    eventos.add(evento);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventos;
    }

    // EVENTOS RECENTES

    public List<Evento> buscarEventosRecentes(int limite) {
        String sql = """
    SELECT
        id_evento,
        id_usuario_criador,
        nome,
        descricao,
        data_inicio,
        data_fim,
        local,
        capacidade,
        status
    FROM evento
    WHERE data_fim IS NOT NULL
      AND data_fim < NOW()
      AND status <> 'CANCELADO'
    ORDER BY data_fim DESC
    LIMIT ?
    """;

        List<Evento> eventos = new ArrayList<>();
        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Evento evento = new Evento();
                    evento.setId( rs.getLong("id_evento") );
                    evento.setIdUsuarioCriador( rs.getLong("id_usuario_criador") );
                    evento.setNome( rs.getString("nome") );
                    evento.setDescricao( rs.getString("descricao") );
                    if (rs.getTimestamp("data_inicio") != null) {
                        evento.setDataInicio( rs.getTimestamp("data_inicio") .toLocalDateTime() );
                    }
                    if (rs.getTimestamp("data_fim") != null) {
                        evento.setDataFim( rs.getTimestamp("data_fim") .toLocalDateTime() );
                    }
                    evento.setLocal( rs.getString("local") );
                    evento.setCapacidade( rs.getInt("capacidade") );
                    evento.setStatus( rs.getString("status") );
                    eventos.add(evento);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventos;
    }

    // TOTAL DE INSCRITOS POR EVENTO

    public int contarInscritosPorEvento(long idEvento) {
        String sql = """
            SELECT COUNT(*)
            FROM inscricao_evento
            WHERE id_evento = ?
            AND status = 'INSCRITO'
            """;
        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, idEvento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
