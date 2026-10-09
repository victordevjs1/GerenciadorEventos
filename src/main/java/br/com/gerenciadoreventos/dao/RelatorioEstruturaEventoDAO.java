package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Dados complementares do relatório de desempenho. Apenas consultas: nenhuma tabela é alterada por esta classe.
public class RelatorioEstruturaEventoDAO {
    public record Estrutura(
            List<Atividade> atividades,
            List<Comissao> comissoes,
            List<Responsabilidade> responsabilidadesSemComissao,
            int totalResponsabilidades
    ) { }

    public record Atividade(
            long id,
            String nome,
            String descricao,
            LocalDateTime inicio,
            LocalDateTime fim,
            String local,
            String status,
            List<String> comissoes
    ) { }

    public record Integrante(String nome, String rm, String funcao, boolean ativo) { }

    public record Responsabilidade(
            String nome,
            String descricao,
            String status,
            String observacao
    ) { }

    public record AtividadeVinculada(
            String nome,
            String statusAtividade,
            String statusAtribuicao
    ) { }

    public record Comissao(
            long id,
            String nome,
            String descricao,
            boolean ativo,
            List<Integrante> integrantes,
            List<Responsabilidade> responsabilidades,
            List<AtividadeVinculada> atividades
    ) { }

    public Estrutura buscar(long idEvento) throws SQLException {
        Map<Long, Atividade> atividades = new LinkedHashMap<>();
        Map<Long, Comissao> comissoes = new LinkedHashMap<>();
        List<Responsabilidade> semComissao = new ArrayList<>();
        Set<Long> idsSemComissao = new HashSet<>();
        Set<Long> idsResponsabilidades = new HashSet<>();
        try (Connection conexao = Conexao.conectar()) {
            // Uma única conexão garante que todas as seções consultem o mesmo banco.
            String sqlAtividades = """
                    SELECT id_atividade, nome, descricao, data_inicio,
                           data_fim, local, status
                    FROM atividade
                    WHERE id_evento = ?
                    ORDER BY data_inicio, id_atividade
                    """;
            try (PreparedStatement ps = conexao.prepareStatement(sqlAtividades)) {
                ps.setLong(1, idEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long id = rs.getLong("id_atividade");
                        atividades.put(id, new Atividade(
                                id, rs.getString("nome"), rs.getString("descricao"),
                                dataHora(rs, "data_inicio"), dataHora(rs, "data_fim"),
                                rs.getString("local"), rs.getString("status"), new ArrayList<>() ));
                    }
                }
            }
            String sqlComissoes = """
                    SELECT id_comissao, nome, descricao, ativo
                    FROM comissao
                    WHERE id_evento = ?
                    ORDER BY nome, id_comissao
                    """;
            try (PreparedStatement ps = conexao.prepareStatement(sqlComissoes)) {
                ps.setLong(1, idEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long id = rs.getLong("id_comissao");
                        comissoes.put(id, new Comissao(
                                id, rs.getString("nome"), rs.getString("descricao"),
                                rs.getBoolean("ativo"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>() ));
                    }
                }
            }

            // Alunos associados às comissões, inclusive vínculos encerrados (identificados como inativos, para preservar o histórico).
            String sqlIntegrantes = """
                    SELECT ca.id_comissao, a.nome, a.rm, ca.funcao, ca.ativo
                    FROM comissao_aluno ca
                    JOIN comissao c ON c.id_comissao = ca.id_comissao
                    JOIN aluno a ON a.id_aluno = ca.id_aluno
                    WHERE c.id_evento = ?
                    ORDER BY ca.id_comissao, ca.ativo DESC, a.nome, a.id_aluno
                    """;
            try (PreparedStatement ps = conexao.prepareStatement(sqlIntegrantes)) {
                ps.setLong(1, idEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Comissao comissao = comissoes.get(rs.getLong("id_comissao"));
                        if (comissao != null) {
                            comissao.integrantes().add(new Integrante(
                                    rs.getString("nome"), rs.getString("rm"), rs.getString("funcao"), rs.getBoolean("ativo") ));
                        }
                    }
                }
            }

            // Responsabilidades podem estar ligadas a várias comissões. As que não possuem vínculo aparecem em seção própria.
            String sqlResponsabilidades = """
                    SELECT r.id_responsabilidade, r.nome, r.descricao,
                           r.status, r.observacao, c.id_comissao
                    FROM responsabilidade r
                    LEFT JOIN responsabilidade_comissao rc
                        ON rc.id_responsabilidade = r.id_responsabilidade
                    LEFT JOIN comissao c
                        ON c.id_comissao = rc.id_comissao
                       AND c.id_evento = r.id_evento
                    WHERE r.id_evento = ?
                    ORDER BY r.nome, r.id_responsabilidade, c.id_comissao
                    """;
            try (PreparedStatement ps = conexao.prepareStatement(sqlResponsabilidades)) {
                ps.setLong(1, idEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        idsResponsabilidades.add(rs.getLong("id_responsabilidade"));
                        Responsabilidade responsabilidade = new Responsabilidade(
                                rs.getString("nome"), rs.getString("descricao"), rs.getString("status"), rs.getString("observacao") );
                        long idComissao = rs.getLong("id_comissao");
                        Comissao comissao = rs.wasNull() ? null : comissoes.get(idComissao);
                        if (comissao == null) {
                            if (idsSemComissao.add(rs.getLong("id_responsabilidade"))) {
                                semComissao.add(responsabilidade);
                            }
                        } else {
                            comissao.responsabilidades().add(responsabilidade);
                        }
                    }
                }
            }

            // Relacionamento atividade/comissão: mantém o status da atividade e também o status da atribuição para aquela comissão.
            String sqlAtividadesComissoes = """
                    SELECT ca.id_comissao, a.id_atividade, a.nome,
                           a.status AS status_atividade,
                           ca.status AS status_atribuicao, c.nome AS comissao_nome
                    FROM comissao_atividade ca
                    JOIN comissao c ON c.id_comissao = ca.id_comissao
                    JOIN atividade a ON a.id_atividade = ca.id_atividade
                    WHERE c.id_evento = ? AND a.id_evento = ?
                    ORDER BY ca.id_comissao, a.data_inicio, a.id_atividade
                    """;
            try (PreparedStatement ps = conexao.prepareStatement(sqlAtividadesComissoes)) {
                ps.setLong(1, idEvento);
                ps.setLong(2, idEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Comissao comissao = comissoes.get(rs.getLong("id_comissao"));
                        Atividade atividade = atividades.get(rs.getLong("id_atividade"));
                        if (comissao != null && atividade != null) {
                            comissao.atividades().add(new AtividadeVinculada(
                                    rs.getString("nome"), rs.getString("status_atividade"), rs.getString("status_atribuicao") ));
                            atividade.comissoes().add(rs.getString("comissao_nome"));
                        }
                    }
                }
            }
        }
        return new Estrutura( new ArrayList<>(atividades.values()), new ArrayList<>(comissoes.values()), semComissao, idsResponsabilidades.size() );
    }

    private static LocalDateTime dataHora(ResultSet rs, String coluna) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(coluna);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
