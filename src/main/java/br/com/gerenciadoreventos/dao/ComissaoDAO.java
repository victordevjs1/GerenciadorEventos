package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.Comissao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComissaoDAO {

    // STATUS AUTOMÁTICO PELO TÉRMINO DO EVENTO

    // Antes e durante o evento a comissão fica ativa. A partir de data_fim, ela passa automaticamente para inativa. Se o evento não possuir data_fim, a comissão permanece ativa.
    public void sincronizarStatusComEventos() throws SQLException {
        try (Connection conexao = Conexao.conectar()) {
            sincronizarStatusComEventos(conexao);
        }
    }

    private void sincronizarStatusComEventos(Connection conexao)
            throws SQLException {
        String sql =
                "UPDATE comissao c " +
                        "INNER JOIN evento e ON e.id_evento = c.id_evento " +
                        "SET c.ativo = CASE " +
                        "WHEN e.data_fim IS NOT NULL AND NOW() >= e.data_fim THEN FALSE " +
                        "ELSE TRUE END " +
                        "WHERE c.ativo <> CASE " + "WHEN e.data_fim IS NOT NULL AND NOW() >= e.data_fim THEN FALSE " + "ELSE TRUE END";
        Long usuarioAnterior = br.com.gerenciadoreventos.audit.ContextoAuditoria.getIdUsuario();
        Conexao.definirUsuarioAuditoria(conexao, null);
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.executeUpdate();
        } finally {
            Conexao.definirUsuarioAuditoria(conexao, usuarioAnterior);
        }
    }

    private boolean calcularStatusAutomatico(Connection conexao, Long idEvento)
            throws SQLException {
        String sql =
                "SELECT CASE " +
                        "WHEN data_fim IS NOT NULL AND NOW() >= data_fim THEN FALSE " + "ELSE TRUE END AS ativo " + "FROM evento WHERE id_evento = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idEvento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("ativo");
                }
            }
        }
        throw new SQLException( "Evento não encontrado para definir o status da comissão." );
    }

    // LISTAR

    public List<Comissao> listar() throws SQLException {
        List<Comissao> lista = new ArrayList<>();
        String sql =
                "SELECT " +
                        "id_comissao, " +
                        "id_evento, " +
                        "evento, " +
                        "comissao, " +
                        "descricao, " + "ativo, " + "total_alunos, " + "total_atividades " + "FROM vw_comissoes " + "ORDER BY evento, comissao";
        try (Connection conexao = Conexao.conectar()) {

            // Garante que os registros antigos também acompanhem o fim do evento.
            sincronizarStatusComEventos(conexao);
            try (PreparedStatement stmt = conexao.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // BUSCAR POR ID

    public Comissao buscarPorId(Long id)
            throws SQLException {
        if (id == null) {
            return null;
        }
        String sql =
                "SELECT " +
                        "id_comissao, " +
                        "id_evento, " +
                        "evento, " +
                        "comissao, " +
                        "descricao, " + "ativo, " + "total_alunos, " + "total_atividades " + "FROM vw_comissoes " + "WHERE id_comissao = ?";
        try (Connection conexao = Conexao.conectar()) {
            sincronizarStatusComEventos(conexao);
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setLong(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapear(rs);
                    }
                }
            }
        }
        return null;
    }

    // CADASTRAR

    public void cadastrar(Comissao comissao)
            throws SQLException {
        String sql = "INSERT INTO comissao " + "(id_evento, nome, descricao, ativo) " + "VALUES (?, ?, ?, ?)";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {
            boolean ativoAutomatico = calcularStatusAutomatico( conexao, comissao.getIdEvento() );
            comissao.setAtivo(ativoAutomatico);
            stmt.setLong(1, comissao.getIdEvento());
            stmt.setString(2, comissao.getNome());
            stmt.setString(3, comissao.getDescricao());
            stmt.setBoolean(4, ativoAutomatico);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    comissao.setIdComissao(rs.getLong(1));
                }
            }
        }
    }

    // ALTERAR

    public void alterar(Comissao comissao)
            throws SQLException {
        String sql = "UPDATE comissao SET " + "id_evento = ?, " + "nome = ?, " + "descricao = ?, " + "ativo = ? " + "WHERE id_comissao = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            boolean ativoAutomatico = calcularStatusAutomatico( conexao, comissao.getIdEvento() );
            comissao.setAtivo(ativoAutomatico);
            stmt.setLong(1, comissao.getIdEvento());
            stmt.setString(2, comissao.getNome());
            stmt.setString(3, comissao.getDescricao());
            stmt.setBoolean(4, ativoAutomatico);
            stmt.setLong(5, comissao.getIdComissao());
            stmt.executeUpdate();
        }
    }

    // EXCLUIR

    public void excluir(Long id)
            throws SQLException {
        String sql = "DELETE FROM comissao " + "WHERE id_comissao = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    // MAPEAR RESULTADO

    private Comissao mapear(ResultSet rs)
            throws SQLException {
        Comissao comissao = new Comissao();
        comissao.setIdComissao(rs.getLong("id_comissao"));
        comissao.setIdEvento(rs.getLong("id_evento"));
        comissao.setEvento(rs.getString("evento"));
        comissao.setNome(rs.getString("comissao"));
        comissao.setDescricao(rs.getString("descricao"));
        comissao.setAtivo(rs.getBoolean("ativo"));
        comissao.setTotalAlunos(rs.getInt("total_alunos"));
        comissao.setTotalAtividades(rs.getInt("total_atividades"));
        return comissao;
    }
}
