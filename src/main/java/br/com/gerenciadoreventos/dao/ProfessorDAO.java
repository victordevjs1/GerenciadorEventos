package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.Professor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessorDAO {

    // =====================================================
    // LISTAR POR STATUS
    // =====================================================

    public List<Professor> listarProfessores(boolean ativos) {

        List<Professor> professores = new ArrayList<>();

        String sql = """
                SELECT
                    id_professor,
                    nome,
                    email,
                    telefone,
                    area_atuacao,
                    ativo
                FROM professor
                WHERE ativo = ?
                ORDER BY nome ASC
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setBoolean(1, ativos);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Professor professor =
                            new Professor();

                    professor.setId(
                            rs.getLong("id_professor")
                    );

                    professor.setNome(
                            rs.getString("nome")
                    );

                    professor.setEmail(
                            rs.getString("email")
                    );

                    professor.setTelefone(
                            rs.getString("telefone")
                    );

                    professor.setAreaAtuacao(
                            rs.getString("area_atuacao")
                    );

                    professor.setAtivo(
                            rs.getBoolean("ativo")
                    );

                    professores.add(professor);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return professores;
    }


    // =====================================================
    // CADASTRAR
    // =====================================================

    public boolean cadastrarProfessor(
            Professor professor
    ) {

        String sql = """
                INSERT INTO professor (
                    nome,
                    email,
                    telefone,
                    area_atuacao,
                    ativo
                )
                VALUES (?, ?, ?, ?, TRUE)
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setString(
                    1,
                    professor.getNome()
            );

            stmt.setString(
                    2,
                    professor.getEmail()
            );

            stmt.setString(
                    3,
                    professor.getTelefone()
            );

            stmt.setString(
                    4,
                    professor.getAreaAtuacao()
            );

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // DESATIVAR
    // =====================================================

    public boolean desativar(
            long idProfessor
    ) {

        String sql = """
                UPDATE professor
                SET ativo = FALSE
                WHERE id_professor = ?
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    idProfessor
            );

            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // REATIVAR
    // =====================================================

    public boolean ativar(
            long idProfessor
    ) {

        String sql = """
                UPDATE professor
                SET ativo = TRUE
                WHERE id_professor = ?
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    idProfessor
            );

            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }

    // =====================================================
    // VERIFICAR DUPLICIDADE PARA IMPORTAÇÃO CSV
    // =====================================================

    public boolean existeProfessorCadastrado(
            String nome,
            String email,
            String telefone,
            String areaAtuacao
    ) {

        String emailLimpo = email == null ? "" : email.trim();
        String telefoneLimpo = telefone == null ? "" : telefone.replaceAll("\\D+", "");
        String nomeLimpo = nome == null ? "" : nome.trim();
        String areaLimpa = areaAtuacao == null ? "" : areaAtuacao.trim();

        String sql;

        if (!emailLimpo.isBlank()) {
            sql = """
                    SELECT 1
                    FROM professor
                    WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))
                    LIMIT 1
                    """;
        } else if (!telefoneLimpo.isBlank()) {
            sql = """
                    SELECT 1
                    FROM professor
                    WHERE LOWER(TRIM(nome)) = LOWER(TRIM(?))
                      AND REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
                              COALESCE(telefone, ''), '(', ''), ')', ''), '-', ''), ' ', ''), '+', '') = ?
                    LIMIT 1
                    """;
        } else {
            sql = """
                    SELECT 1
                    FROM professor
                    WHERE LOWER(TRIM(nome)) = LOWER(TRIM(?))
                      AND LOWER(TRIM(COALESCE(area_atuacao, ''))) = LOWER(TRIM(?))
                    LIMIT 1
                    """;
        }

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            if (!emailLimpo.isBlank()) {
                stmt.setString(1, emailLimpo);
            } else if (!telefoneLimpo.isBlank()) {
                stmt.setString(1, nomeLimpo);
                stmt.setString(2, telefoneLimpo);
            } else {
                stmt.setString(1, nomeLimpo);
                stmt.setString(2, areaLimpa);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
