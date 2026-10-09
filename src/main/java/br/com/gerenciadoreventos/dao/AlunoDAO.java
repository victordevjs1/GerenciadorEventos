package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.Aluno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {
    public List<Aluno> listarAlunos() {
        return listarAlunosPorStatus(true);
    }

    public boolean cadastrarAluno(Aluno aluno) {
        String sql = """
                INSERT INTO aluno (
                    rm,
                    nome,
                    data_nascimento,
                    id_curso,
                    id_serie,
                    ano_conclusao,
                    email,
                    telefone,
                    ativo
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, TRUE)
                """;
        try (Connection conn = Conexao.conectar()) {
            CursoSerieIds ids = resolverCursoSerie(conn, aluno.getCurso(), aluno.getSerie());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, aluno.getRm());
                stmt.setString(2, aluno.getNome());
                if (aluno.getDataNascimento() != null && !aluno.getDataNascimento().isBlank()) {
                    stmt.setDate(3, Date.valueOf(aluno.getDataNascimento()));
                } else {
                    stmt.setNull(3, Types.DATE);
                }
                if (ids.idCurso != null) {
                    stmt.setLong(4, ids.idCurso);
                } else {
                    stmt.setNull(4, Types.BIGINT);
                }
                if (ids.idSerie != null) {
                    stmt.setLong(5, ids.idSerie);
                } else {
                    stmt.setNull(5, Types.BIGINT);
                }
                if (aluno.getAnoConclusao() != null) {
                    stmt.setInt(6, aluno.getAnoConclusao());
                } else {
                    stmt.setNull(6, Types.INTEGER);
                }
                stmt.setString(7, aluno.getEmail());
                stmt.setString(8, aluno.getTelefone());
                return stmt.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean desativar(long idAluno) {
        String sql = """
                UPDATE aluno
                SET ativo = FALSE
                WHERE id_aluno = ?
                """;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idAluno);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int desativarFormados(int anoReferencia) {
        String sql = """
                UPDATE aluno
                SET ativo = FALSE
                WHERE ativo = TRUE
                  AND ano_conclusao IS NOT NULL
                  AND ano_conclusao < ?
                """;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, anoReferencia);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Aluno buscarPorRm(String rm) {
        String sql = """
                SELECT
                    a.id_aluno,
                    a.rm,
                    a.nome,
                    c.nome AS curso,
                    s.numero AS serie,
                    a.email,
                    a.telefone,
                    a.ativo,
                    a.ano_conclusao
                FROM aluno a
                LEFT JOIN curso c ON c.id_curso = a.id_curso
                LEFT JOIN serie s ON s.id_serie = a.id_serie
                WHERE a.rm = ?
                LIMIT 1
                """;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rm);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearAluno(rs, false);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Aluno> listarAlunosPorStatus(boolean ativo) {
        List<Aluno> alunos = new ArrayList<>();
        String sql = """
                SELECT
                    a.id_aluno,
                    a.rm,
                    a.nome,
                    a.data_nascimento,
                    c.nome AS curso,
                    s.numero AS serie,
                    a.email,
                    a.telefone,
                    a.ano_conclusao,
                    a.ativo
                FROM aluno a
                LEFT JOIN curso c ON c.id_curso = a.id_curso
                LEFT JOIN serie s ON s.id_serie = a.id_serie
                WHERE a.ativo = ?
                ORDER BY a.nome ASC
                """;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, ativo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alunos.add(mapearAluno(rs, true));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alunos;
    }

    private Aluno mapearAluno(ResultSet rs, boolean incluiData) throws SQLException {
        Aluno aluno = new Aluno();
        aluno.setId(rs.getLong("id_aluno"));
        aluno.setRm(rs.getString("rm"));
        aluno.setNome(rs.getString("nome"));
        aluno.setCurso(rs.getString("curso"));
        aluno.setEmail(rs.getString("email"));
        aluno.setTelefone(rs.getString("telefone"));
        aluno.setAtivo(rs.getBoolean("ativo"));
        int serie = rs.getInt("serie");
        if (!rs.wasNull()) {
            aluno.setSerie(serie);
        }
        int anoConclusao = rs.getInt("ano_conclusao");
        if (!rs.wasNull()) {
            aluno.setAnoConclusao(anoConclusao);
        }
        if (incluiData) {
            Date data = rs.getDate("data_nascimento");
            if (data != null) {
                aluno.setDataNascimento(data.toLocalDate().toString());
            }
        }
        return aluno;
    }

    // O banco guarda somente as chaves estrangeiras. O nome do curso e o número da série permanecem no model apenas como dados de apresentação.
    private CursoSerieIds resolverCursoSerie(
            Connection conn,
            String nomeCurso,
            Integer numeroSerie
    ) throws SQLException {
        if (nomeCurso == null || nomeCurso.isBlank()) {
            return new CursoSerieIds(null, null);
        }
        String sql = """
                SELECT
                    c.id_curso,
                    s.id_serie
                FROM curso c
                LEFT JOIN serie s
                    ON s.id_curso = c.id_curso
                   AND (? IS NOT NULL AND s.numero = ?)
                WHERE c.nome = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (numeroSerie == null) {
                stmt.setNull(1, Types.INTEGER);
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(1, numeroSerie);
                stmt.setInt(2, numeroSerie);
            }
            stmt.setString(3, nomeCurso.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Curso não encontrado: " + nomeCurso);
                }
                Long idCurso = rs.getLong("id_curso");
                Long idSerie = rs.getLong("id_serie");
                if (rs.wasNull()) {
                    idSerie = null;
                }
                if (numeroSerie != null && idSerie == null) {
                    throw new SQLException( "Série " + numeroSerie + " não encontrada para o curso " + nomeCurso);
                }
                return new CursoSerieIds(idCurso, idSerie);
            }
        }
    }

    private static class CursoSerieIds {
        private final Long idCurso;
        private final Long idSerie;
        private CursoSerieIds(Long idCurso, Long idSerie) {
            this.idCurso = idCurso;
            this.idSerie = idSerie;
        }
    }
}
