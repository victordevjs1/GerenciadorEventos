package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.Aluno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    // =====================================================
    // LISTAR ALUNOS ATIVOS
    // =====================================================

    public List<Aluno> listarAlunos() {

        // Mantido por compatibilidade; a consulta já é feita
        // por listarAlunosPorStatus(true) logo abaixo.

        return listarAlunosPorStatus(true);
    }


    // =====================================================
    // CADASTRAR ALUNO
    // =====================================================

    public boolean cadastrarAluno(Aluno aluno) {

        String sql = """
            INSERT INTO aluno (
                rm,
                nome,
                data_nascimento,
                turma,
                curso,
                email,
                telefone,
                serie,
                ano_conclusao,
                ativo
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE)
            """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setString(
                    1,
                    aluno.getRm()
            );

            stmt.setString(
                    2,
                    aluno.getNome()
            );

            // =============================================
            // DATA DE NASCIMENTO
            // =============================================

            if (aluno.getDataNascimento() != null
                    && !aluno.getDataNascimento().isBlank()) {

                stmt.setDate(
                        3,
                        Date.valueOf(
                                aluno.getDataNascimento()
                        )
                );

            } else {

                stmt.setNull(
                        3,
                        Types.DATE
                );
            }

            // =============================================
            // OUTROS CAMPOS
            // =============================================

            stmt.setString(
                    4,
                    aluno.getTurma()
            );

            stmt.setString(
                    5,
                    aluno.getCurso()
            );

            stmt.setString(
                    6,
                    aluno.getEmail()
            );

            stmt.setString(
                    7,
                    aluno.getTelefone()
            );

            // =============================================
            // SÉRIE
            // =============================================

            if (aluno.getSerie() != null) {

                stmt.setInt(
                        8,
                        aluno.getSerie()
                );

            } else {

                stmt.setNull(
                        8,
                        Types.INTEGER
                );
            }

            // =============================================
            // ANO DE CONCLUSÃO
            // =============================================

            if (aluno.getAnoConclusao() != null) {

                stmt.setInt(
                        9,
                        aluno.getAnoConclusao()
                );

            } else {

                stmt.setNull(
                        9,
                        Types.INTEGER
                );
            }

            // =============================================
            // EXECUTAR
            // =============================================

            int linhasAfetadas =
                    stmt.executeUpdate();

            return linhasAfetadas > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // DESATIVAR ALUNO (manual)
    // =====================================================

    public boolean desativar(long idAluno) {

        String sql = """
                UPDATE aluno
                SET ativo = FALSE
                WHERE id_aluno = ?
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    idAluno
            );

            int linhasAfetadas =
                    stmt.executeUpdate();

            return linhasAfetadas == 1;

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao desativar aluno."
            );

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // DESATIVAR FORMANDOS (automático)
    //
    // Desativa em lote todos os alunos ativos cujo
    // ano_conclusao já ficou para trás em relação ao ano
    // informado (normalmente o ano atual). Ex.: aluno com
    // ano_conclusao = 2028 é desativado a partir de 2029.
    // =====================================================

    public int desativarFormados(int anoReferencia) {

        String sql = """
                UPDATE aluno
                SET ativo = FALSE
                WHERE ativo = TRUE
                  AND ano_conclusao IS NOT NULL
                  AND ano_conclusao < ?
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setInt(
                    1,
                    anoReferencia
            );

            return stmt.executeUpdate();

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao desativar alunos formados."
            );

            e.printStackTrace();

            return 0;
        }
    }


    // =====================================================
    // BUSCAR ALUNO POR RM
    //
    // Usado na importação de presença via CSV: cada linha
    // do formulário traz o RM digitado pelo aluno, e
    // precisamos descobrir o id_aluno correspondente.
    // =====================================================

    public Aluno buscarPorRm(String rm) {

        String sql = """
                SELECT
                    id_aluno,
                    rm,
                    nome,
                    turma,
                    curso,
                    email,
                    telefone,
                    serie,
                    ano_conclusao,
                    ativo
                FROM aluno
                WHERE rm = ?
                LIMIT 1
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, rm);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    Aluno aluno = new Aluno();

                    aluno.setId(rs.getLong("id_aluno"));
                    aluno.setRm(rs.getString("rm"));
                    aluno.setNome(rs.getString("nome"));
                    aluno.setTurma(rs.getString("turma"));
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

                    return aluno;
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    // =====================================================
    // LISTAR POR STATUS
    // =====================================================

    public List<Aluno> listarAlunosPorStatus(boolean ativo) {

        List<Aluno> alunos = new ArrayList<>();

        String sql = """
            SELECT
                id_aluno,
                rm,
                nome,
                data_nascimento,
                turma,
                curso,
                email,
                telefone,
                serie,
                ano_conclusao,
                ativo
            FROM aluno
            WHERE ativo = ?
            ORDER BY nome ASC
            """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setBoolean(1, ativo);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Aluno aluno = new Aluno();

                    aluno.setId(
                            rs.getLong("id_aluno")
                    );

                    aluno.setRm(
                            rs.getString("rm")
                    );

                    aluno.setNome(
                            rs.getString("nome")
                    );

                    Date dataNascimento =
                            rs.getDate("data_nascimento");

                    if (dataNascimento != null) {

                        aluno.setDataNascimento(
                                dataNascimento
                                        .toLocalDate()
                                        .toString()
                        );
                    }

                    aluno.setTurma(
                            rs.getString("turma")
                    );

                    aluno.setCurso(
                            rs.getString("curso")
                    );

                    aluno.setEmail(
                            rs.getString("email")
                    );

                    aluno.setTelefone(
                            rs.getString("telefone")
                    );

                    // =========================================
                    // SÉRIE (pode ser NULL para alunos antigos)
                    // =========================================

                    int serie =
                            rs.getInt("serie");

                    if (!rs.wasNull()) {

                        aluno.setSerie(serie);
                    }

                    // =========================================
                    // ANO DE CONCLUSÃO (pode ser NULL)
                    // =========================================

                    int anoConclusao =
                            rs.getInt("ano_conclusao");

                    if (!rs.wasNull()) {

                        aluno.setAnoConclusao(anoConclusao);
                    }

                    aluno.setAtivo(
                            rs.getBoolean("ativo")
                    );

                    alunos.add(aluno);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return alunos;
    }

}