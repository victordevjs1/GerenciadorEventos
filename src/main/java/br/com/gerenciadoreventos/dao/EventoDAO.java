package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.Evento;
import br.com.gerenciadoreventos.model.EventoPublico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    public List<Evento> listarEventos() {
        List<Evento> eventos = new ArrayList<>();

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
                ORDER BY data_inicio ASC
                """;

        try (
                Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Evento evento = mapearEvento(rs);
                carregarPublicos(conexao, evento);
                eventos.add(evento);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar eventos.", e);
        }

        return eventos;
    }

    public Evento buscarPorId(long idEvento) {
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
                WHERE id_evento = ?
                """;

        try (
                Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setLong(1, idEvento);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Evento evento = mapearEvento(rs);
                    carregarPublicos(conexao, evento);
                    return evento;
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar evento " + idEvento + ".", e);
        }

        return null;
    }

    public boolean cadastrarEvento(Evento evento) {
        String sql = """
                INSERT INTO evento (
                    id_usuario_criador,
                    nome,
                    descricao,
                    data_inicio,
                    data_fim,
                    local,
                    capacidade,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        Connection conexao = null;

        try {
            conexao = Conexao.conectar();
            conexao.setAutoCommit(false);

            try (PreparedStatement stmt = conexao.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {
                preencherEventoStatement(stmt, evento, true);

                if (stmt.executeUpdate() != 1) {
                    conexao.rollback();
                    return false;
                }

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conexao.rollback();
                        return false;
                    }
                    evento.setId(rs.getLong(1));
                }
            }

            salvarPublicos(conexao, evento);
            conexao.commit();
            return true;

        } catch (SQLException e) {
            rollbackQuietly(conexao);
            throw new IllegalStateException("Erro ao cadastrar evento.", e);
        } finally {
            closeQuietly(conexao);
        }
    }

    public boolean atualizarEvento(Evento evento) {
        String sql = """
                UPDATE evento
                SET
                    nome = ?,
                    descricao = ?,
                    data_inicio = ?,
                    data_fim = ?,
                    local = ?,
                    capacidade = ?,
                    status = ?
                WHERE id_evento = ?
                """;

        Connection conexao = null;

        try {
            conexao = Conexao.conectar();
            conexao.setAutoCommit(false);

            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                preencherEventoStatement(stmt, evento, false);
                if (stmt.executeUpdate() != 1) {
                    conexao.rollback();
                    return false;
                }
            }

            try (PreparedStatement stmt = conexao.prepareStatement(
                    "DELETE FROM evento_publico WHERE id_evento = ?")) {
                stmt.setLong(1, evento.getId());
                stmt.executeUpdate();
            }

            salvarPublicos(conexao, evento);
            conexao.commit();
            return true;

        } catch (SQLException e) {
            rollbackQuietly(conexao);
            throw new IllegalStateException("Erro ao atualizar evento " + evento.getId() + ".", e);
        } finally {
            closeQuietly(conexao);
        }
    }

    private void preencherEventoStatement(
            PreparedStatement stmt,
            Evento evento,
            boolean cadastro
    ) throws SQLException {
        int p = 1;

        if (cadastro) {
            stmt.setLong(p++, evento.getIdUsuarioCriador());
        }

        stmt.setString(p++, evento.getNome());
        stmt.setString(p++, evento.getDescricao());
        stmt.setTimestamp(p++, Timestamp.valueOf(evento.getDataInicio()));

        if (evento.getDataFim() != null) {
            stmt.setTimestamp(p++, Timestamp.valueOf(evento.getDataFim()));
        } else {
            stmt.setNull(p++, Types.TIMESTAMP);
        }

        stmt.setString(p++, evento.getLocal());
        stmt.setInt(p++, evento.getCapacidade());
        stmt.setString(p++, evento.getStatus());

        if (!cadastro) {
            stmt.setLong(p, evento.getId());
        }
    }

    /**
     * O tipo de público é representado exclusivamente em evento_publico:
     *
     * - toda a escola: publico_todos = TRUE, curso/série = NULL;
     * - curso inteiro: publico_todos = FALSE, curso preenchido, série = NULL;
     * - série: publico_todos = FALSE, curso e série preenchidos.
     */
    private void salvarPublicos(Connection conexao, Evento evento) throws SQLException {
        if (evento.isPublicoTodos()) {
            String sql = """
                    INSERT INTO evento_publico (
                        id_evento, id_curso, id_serie, publico_todos
                    ) VALUES (?, NULL, NULL, TRUE)
                    """;

            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setLong(1, evento.getId());
                stmt.executeUpdate();
            }
            return;
        }

        if (evento.getPublicos() == null || evento.getPublicos().isEmpty()) {
            throw new SQLException("Evento específico sem público definido.");
        }

        String cursoSql = """
                INSERT INTO evento_publico (
                    id_evento, id_curso, id_serie, publico_todos
                )
                SELECT ?, c.id_curso, NULL, FALSE
                FROM curso c
                WHERE c.nome = ?
                """;

        String serieSql = """
                INSERT INTO evento_publico (
                    id_evento, id_curso, id_serie, publico_todos
                )
                SELECT ?, c.id_curso, s.id_serie, FALSE
                FROM curso c
                INNER JOIN serie s ON s.id_curso = c.id_curso
                WHERE c.nome = ?
                  AND s.nome = ?
                """;

        for (EventoPublico publico : evento.getPublicos()) {
            if (publico == null || publico.getCurso() == null || publico.getCurso().isBlank()) {
                throw new SQLException("Público de evento sem curso definido.");
            }

            if (publico.isCursoInteiro()) {
                try (PreparedStatement stmt = conexao.prepareStatement(cursoSql)) {
                    stmt.setLong(1, evento.getId());
                    stmt.setString(2, publico.getCurso());
                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException("Curso não encontrado: " + publico.getCurso());
                    }
                }
            } else {
                try (PreparedStatement stmt = conexao.prepareStatement(serieSql)) {
                    stmt.setLong(1, evento.getId());
                    stmt.setString(2, publico.getCurso());
                    stmt.setString(3, publico.getSerie());
                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException(
                                "Combinação curso/série não encontrada: "
                                        + publico.getCurso() + " / " + publico.getSerie());
                    }
                }
            }
        }
    }

    private void carregarPublicos(Connection conexao, Evento evento) throws SQLException {
        evento.limparPublicos();
        evento.setPublicoTodos(false);

        String sql = """
                SELECT
                    ep.publico_todos,
                    c.nome AS curso,
                    s.nome AS serie
                FROM evento_publico ep
                LEFT JOIN curso c ON c.id_curso = ep.id_curso
                LEFT JOIN serie s ON s.id_serie = ep.id_serie
                WHERE ep.id_evento = ?
                ORDER BY c.nome, s.numero
                """;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, evento.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (rs.getBoolean("publico_todos")) {
                        evento.setPublicoTodos(true);
                        evento.limparPublicos();
                        return;
                    }

                    evento.adicionarPublico(new EventoPublico(
                            rs.getString("curso"),
                            rs.getString("serie")
                    ));
                }
            }
        }
    }

    private Evento mapearEvento(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setId(rs.getLong("id_evento"));
        evento.setIdUsuarioCriador(rs.getLong("id_usuario_criador"));
        evento.setNome(rs.getString("nome"));
        evento.setDescricao(rs.getString("descricao"));

        Timestamp inicio = rs.getTimestamp("data_inicio");
        if (inicio != null) {
            evento.setDataInicio(inicio.toLocalDateTime());
        }

        Timestamp fim = rs.getTimestamp("data_fim");
        if (fim != null) {
            evento.setDataFim(fim.toLocalDateTime());
        }

        evento.setLocal(rs.getString("local"));
        evento.setCapacidade(rs.getInt("capacidade"));
        evento.setStatus(rs.getString("status"));
        return evento;
    }

    private void rollbackQuietly(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.rollback();
            } catch (SQLException ignored) {
                // Mantém a exceção original como causa.
            }
        }
    }

    private void closeQuietly(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException ignored) {
                // Conexão já está em processo de encerramento.
            }
        }
    }
}
