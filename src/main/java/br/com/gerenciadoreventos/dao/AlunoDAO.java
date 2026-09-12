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
                    ativo
                FROM aluno
                WHERE ativo = TRUE
                ORDER BY nome ASC
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

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

                aluno.setAtivo(
                        rs.getBoolean("ativo")
                );

                alunos.add(aluno);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return alunos;
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
                ativo
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, TRUE)
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
            // EXECUTAR
            // =============================================

            int linhasAfetadas =
                    stmt.executeUpdate();

            return linhasAfetadas > 0;

        } catch (SQLException e) {

            return false;
        }
    }



    // =====================================================
    // DESATIVAR ALUNO
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

            System.out.println(
                    "================================="
            );

            System.out.println(
                    "DESATIVANDO ALUNO"
            );

            System.out.println(
                    "ID recebido: " + idAluno
            );

            System.out.println(
                    "Linhas afetadas: " + linhasAfetadas
            );

            System.out.println(
                    "================================="
            );

            return linhasAfetadas == 1;

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao desativar aluno."
            );

            e.printStackTrace();

            return false;
        }
    }
}
