package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.AgenteExterno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgenteExternoDAO {
    public List<AgenteExterno> listarPorStatus(boolean ativo) {
        String sql = """
                SELECT
                    id_agente,
                    nome,
                    email,
                    telefone,
                    empresa,
                    cargo,
                    especialidade,
                    observacao,
                    ativo
                FROM agente_externo
                WHERE ativo = ?
                ORDER BY nome ASC
                """;
        List<AgenteExterno> agentes = new ArrayList<>();
        try (
                Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setBoolean(1, ativo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    agentes.add(mapearAgente(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return agentes;
    }

    public List<AgenteExterno> pesquisar(String texto, boolean ativo) {
        String sql = """
                SELECT
                    id_agente,
                    nome,
                    email,
                    telefone,
                    empresa,
                    cargo,
                    especialidade,
                    observacao,
                    ativo
                FROM agente_externo
                WHERE ativo = ?
                  AND (
                        nome LIKE ?
                        OR email LIKE ?
                        OR empresa LIKE ?
                        OR cargo LIKE ?
                        OR especialidade LIKE ?
                  )
                ORDER BY nome ASC
                """;
        List<AgenteExterno> agentes = new ArrayList<>();
        try (
                Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            String busca = "%" + texto.trim() + "%";
            stmt.setBoolean(1, ativo);
            stmt.setString(2, busca);
            stmt.setString(3, busca);
            stmt.setString(4, busca);
            stmt.setString(5, busca);
            stmt.setString(6, busca);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    agentes.add(mapearAgente(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return agentes;
    }

    public AgenteExterno buscarPorId(long id) {
        String sql = """
                SELECT
                    id_agente,
                    nome,
                    email,
                    telefone,
                    empresa,
                    cargo,
                    especialidade,
                    observacao,
                    ativo
                FROM agente_externo
                WHERE id_agente = ?
                """;
        try (
                Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearAgente(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean cadastrar(AgenteExterno agente) {
        String sql = """
                INSERT INTO agente_externo (
                    nome,
                    email,
                    telefone,
                    empresa,
                    cargo,
                    especialidade,
                    observacao,
                    ativo
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (
                Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            preencherStatement(stmt, agente);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizar(AgenteExterno agente) {
        String sql = """
                UPDATE agente_externo
                SET
                    nome = ?,
                    email = ?,
                    telefone = ?,
                    empresa = ?,
                    cargo = ?,
                    especialidade = ?,
                    observacao = ?
                WHERE id_agente = ?
                """;
        try (
                Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, agente.getNome());
            stmt.setString(2, agente.getEmail());
            stmt.setString(3, agente.getTelefone());
            stmt.setString(4, agente.getEmpresa());
            stmt.setString(5, agente.getCargo());
            stmt.setString(6, agente.getEspecialidade());
            stmt.setString(7, agente.getObservacao());
            stmt.setLong(8, agente.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean alterarStatus(long id, boolean ativo) {
        String sql = """
                UPDATE agente_externo
                SET ativo = ?
                WHERE id_agente = ?
                """;
        try (
                Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setBoolean(1, ativo);
            stmt.setLong(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private AgenteExterno mapearAgente(ResultSet rs) throws SQLException {
        AgenteExterno agente = new AgenteExterno();
        agente.setId(rs.getLong("id_agente"));
        agente.setNome(rs.getString("nome"));
        agente.setEmail(rs.getString("email"));
        agente.setTelefone(rs.getString("telefone"));
        agente.setEmpresa(rs.getString("empresa"));
        agente.setCargo(rs.getString("cargo"));
        agente.setEspecialidade(rs.getString("especialidade"));
        agente.setObservacao(rs.getString("observacao"));
        agente.setAtivo(rs.getBoolean("ativo"));
        return agente;
    }

    private void preencherStatement(
            PreparedStatement stmt,
            AgenteExterno agente
    ) throws SQLException {
        stmt.setString(1, agente.getNome());
        stmt.setString(2, agente.getEmail());
        stmt.setString(3, agente.getTelefone());
        stmt.setString(4, agente.getEmpresa());
        stmt.setString(5, agente.getCargo());
        stmt.setString(6, agente.getEspecialidade());
        stmt.setString(7, agente.getObservacao());
        stmt.setBoolean(8, agente.isAtivo());
    }
}
