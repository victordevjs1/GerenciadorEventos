package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;

import java.sql.*;
import java.time.LocalDateTime;

public class RecuperacaoSenhaDAO {
    public long criar(long idUsuario, String codigoHash, LocalDateTime expiracao) {
        String invalidar = "UPDATE recuperacao_senha SET utilizado = TRUE WHERE id_usuario = ? AND utilizado = FALSE";
        String inserir = """
                INSERT INTO recuperacao_senha (id_usuario, codigo_hash, data_expiracao)
                VALUES (?, ?, ?)
                """;
        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtInvalidar = conn.prepareStatement(invalidar);
                 PreparedStatement stmtInserir = conn.prepareStatement(inserir, Statement.RETURN_GENERATED_KEYS)) {
                stmtInvalidar.setLong(1, idUsuario);
                stmtInvalidar.executeUpdate();
                stmtInserir.setLong(1, idUsuario);
                stmtInserir.setString(2, codigoHash);
                stmtInserir.setTimestamp(3, Timestamp.valueOf(expiracao));
                stmtInserir.executeUpdate();
                try (ResultSet rs = stmtInserir.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Não foi possível obter o código de recuperação criado.");
                    }
                    long id = rs.getLong(1);
                    conn.commit();
                    return id;
                }
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar recuperação de senha.", e);
        }
    }

    public CodigoRecuperacao buscarValido(long idUsuario) {
        String sql = """
                SELECT id_recuperacao, codigo_hash, tentativas
                FROM recuperacao_senha
                WHERE id_usuario = ?
                  AND utilizado = FALSE
                  AND data_expiracao >= NOW()
                ORDER BY data_solicitacao DESC, id_recuperacao DESC
                LIMIT 1
                """;
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CodigoRecuperacao( rs.getLong("id_recuperacao"), rs.getString("codigo_hash"), rs.getInt("tentativas") );
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar recuperação de senha.", e);
        }
    }

    public void registrarTentativa(long idRecuperacao, boolean invalidar) {
        String sql = invalidar
                ? "UPDATE recuperacao_senha SET tentativas = tentativas + 1, utilizado = TRUE WHERE id_recuperacao = ?"
                : "UPDATE recuperacao_senha SET tentativas = tentativas + 1 WHERE id_recuperacao = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idRecuperacao);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar tentativa de recuperação.", e);
        }
    }

    public void excluir(long idRecuperacao) {
        String sql = "DELETE FROM recuperacao_senha WHERE id_recuperacao = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idRecuperacao);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao cancelar recuperação de senha.", e);
        }
    }

    public void redefinirSenha(long idRecuperacao, long idUsuario, String senhaHash) {
        String atualizarUsuario = "UPDATE usuario SET senha = ? WHERE id_usuario = ? AND ativo = TRUE";
        String concluirRecuperacao = "UPDATE recuperacao_senha SET utilizado = TRUE WHERE id_recuperacao = ?";
        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtUsuario = conn.prepareStatement(atualizarUsuario);
                 PreparedStatement stmtRecuperacao = conn.prepareStatement(concluirRecuperacao)) {
                stmtUsuario.setString(1, senhaHash);
                stmtUsuario.setLong(2, idUsuario);
                if (stmtUsuario.executeUpdate() == 0) {
                    throw new SQLException("Usuário não encontrado ou inativo.");
                }
                stmtRecuperacao.setLong(1, idRecuperacao);
                stmtRecuperacao.executeUpdate();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao redefinir a senha.", e);
        }
    }

    public record CodigoRecuperacao(long id, String hash, int tentativas) {
    }
}
