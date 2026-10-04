package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.AtividadeOpcao;
import br.com.gerenciadoreventos.model.EventoOpcao;



import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PresencaImportDAO {

    // =====================================================
    // LISTAR EVENTOS (para o combo box)
    // =====================================================

    public List<EventoOpcao> listarEventos() {

        List<EventoOpcao> eventos = new ArrayList<>();

        String sql = """
                SELECT
                    id_evento,
                    nome,
                    data_inicio,
                    data_fim
                FROM evento
                ORDER BY data_inicio DESC
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                Timestamp inicio = rs.getTimestamp("data_inicio");
                Timestamp fim = rs.getTimestamp("data_fim");

                eventos.add(
                        new EventoOpcao(
                                rs.getLong("id_evento"),
                                rs.getString("nome"),
                                inicio != null
                                        ? inicio.toLocalDateTime().toLocalDate()
                                        : null,
                                fim != null
                                        ? fim.toLocalDateTime().toLocalDate()
                                        : null
                        )
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return eventos;
    }


    // =====================================================
    // LISTAR ATIVIDADES (para o combo box)
    // =====================================================

    public List<AtividadeOpcao> listarAtividades() {

        List<AtividadeOpcao> atividades = new ArrayList<>();

        String sql = """
                SELECT
                    a.id_atividade,
                    a.nome AS atividade,
                    a.data_inicio,
                    e.nome AS evento
                FROM atividade a
                INNER JOIN evento e
                    ON e.id_evento = a.id_evento
                ORDER BY a.data_inicio DESC
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                Timestamp inicio = rs.getTimestamp("data_inicio");

                String rotulo =
                        rs.getString("atividade")
                                + " — " + rs.getString("evento")
                                + (inicio != null
                                        ? " (" + inicio.toLocalDateTime().toLocalDate() + ")"
                                        : "");

                atividades.add(
                        new AtividadeOpcao(
                                rs.getLong("id_atividade"),
                                rotulo
                        )
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return atividades;
    }


    // =====================================================
    // VERIFICAR PÚBLICO DO EVENTO
    // =====================================================

    public boolean alunoPertenceAoPublicoEvento(
            long idAluno,
            long idEvento
    ) {

        String sql = """
                SELECT 1
                FROM aluno a
                WHERE a.id_aluno = ?
                  AND a.ativo = TRUE
                  AND EXISTS (
                      SELECT 1
                      FROM evento_publico ep
                      WHERE ep.id_evento = ?
                        AND (
                            ep.publico_todos = TRUE
                            OR (
                                ep.publico_todos = FALSE
                                AND ep.id_curso = a.id_curso
                                AND (
                                    ep.id_serie IS NULL
                                    OR ep.id_serie = a.id_serie
                                )
                            )
                        )
                  )
                LIMIT 1
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, idAluno);
            stmt.setLong(2, idEvento);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // =====================================================
    // SINCRONIZAR INSCRIÇÕES COM O PÚBLICO DO EVENTO
    //
    // O Forms contém somente quem compareceu. Para ser
    // possível descobrir os AUSENTES, primeiro garantimos
    // uma inscrição para TODOS os alunos ativos que fazem
    // parte do público configurado em evento_publico.
    //
    // Não reativa inscrições canceladas: se já existir uma
    // inscrição, ela é preservada exatamente como está.
    // =====================================================

    public int sincronizarInscricoesPublicoEvento(long idEvento) {

        String sql = """
                INSERT INTO inscricao_evento (
                    id_aluno,
                    id_evento,
                    status
                )
                SELECT
                    a.id_aluno,
                    ?,
                    'INSCRITO'
                FROM aluno a
                WHERE a.ativo = TRUE
                  AND EXISTS (
                      SELECT 1
                      FROM evento_publico ep
                      WHERE ep.id_evento = ?
                        AND (
                            ep.publico_todos = TRUE
                            OR (
                                ep.publico_todos = FALSE
                                AND ep.id_curso = a.id_curso
                                AND (
                                    ep.id_serie IS NULL
                                    OR ep.id_serie = a.id_serie
                                )
                            )
                        )
                  )
                  AND NOT EXISTS (
                      SELECT 1
                      FROM inscricao_evento i
                      WHERE i.id_aluno = a.id_aluno
                        AND i.id_evento = ?
                  )
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, idEvento);
            stmt.setLong(2, idEvento);
            stmt.setLong(3, idEvento);

            return stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    // =====================================================
    // OBTER OU CRIAR INSCRIÇÃO
    //
    // Só permite inscrição automática quando o aluno faz
    // parte do público definido para o evento. Assim um RM
    // de outro curso/série nunca é inserido só porque apareceu
    // no CSV.
    // =====================================================

    public Long obterOuCriarInscricao(
            long idAluno,
            long idEvento
    ) {

        if (!alunoPertenceAoPublicoEvento(idAluno, idEvento)) {
            return null;
        }

        String selecionar = """
                SELECT
                    id_inscricao,
                    status
                FROM inscricao_evento
                WHERE id_aluno = ?
                  AND id_evento = ?
                """;

        try (
                Connection conn = Conexao.conectar()
        ) {

            try (
                    PreparedStatement stmt =
                            conn.prepareStatement(selecionar)
            ) {

                stmt.setLong(1, idAluno);
                stmt.setLong(2, idEvento);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        String status = rs.getString("status");

                        if ("CANCELADO".equalsIgnoreCase(status)) {
                            return null;
                        }

                        return rs.getLong("id_inscricao");
                    }
                }
            }

            String inserir = """
                    INSERT INTO inscricao_evento (
                        id_aluno,
                        id_evento,
                        status
                    )
                    VALUES (?, ?, 'INSCRITO')
                    """;

            try (
                    PreparedStatement stmt = conn.prepareStatement(
                            inserir,
                            Statement.RETURN_GENERATED_KEYS
                    )
            ) {

                stmt.setLong(1, idAluno);
                stmt.setLong(2, idEvento);

                stmt.executeUpdate();

                try (ResultSet chaves = stmt.getGeneratedKeys()) {

                    if (chaves.next()) {
                        return chaves.getLong(1);
                    }
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }


    // =====================================================
    // OBTER INSCRIÇÃO ATIVA EXISTENTE
    // =====================================================

    public Long obterInscricaoAtiva(
            long idAluno,
            long idEvento
    ) {

        String sql = """
                SELECT id_inscricao
                FROM inscricao_evento
                WHERE id_aluno = ?
                  AND id_evento = ?
                  AND status <> 'CANCELADO'
                LIMIT 1
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, idAluno);
            stmt.setLong(2, idEvento);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_inscricao");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // =====================================================
    // LISTAR INSCRIÇÕES ATIVAS DO PÚBLICO DO EVENTO
    //
    // Mesmo que o banco tenha alguma inscrição antiga ou
    // incorreta, somente alunos que ainda pertencem ao
    // público do evento entram no cálculo de ausência.
    // =====================================================

    public Map<String, Long> listarInscricoesAtivasEvento(long idEvento) {

        Map<String, Long> inscricoes = new LinkedHashMap<>();

        String sql = """
                SELECT
                    a.rm,
                    i.id_inscricao
                FROM inscricao_evento i
                INNER JOIN aluno a
                    ON a.id_aluno = i.id_aluno
                WHERE i.id_evento = ?
                  AND i.status <> 'CANCELADO'
                  AND a.ativo = TRUE
                  AND EXISTS (
                      SELECT 1
                      FROM evento_publico ep
                      WHERE ep.id_evento = i.id_evento
                        AND (
                            ep.publico_todos = TRUE
                            OR (
                                ep.publico_todos = FALSE
                                AND ep.id_curso = a.id_curso
                                AND (
                                    ep.id_serie IS NULL
                                    OR ep.id_serie = a.id_serie
                                )
                            )
                        )
                  )
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, idEvento);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String rm = rs.getString("rm");
                    long idInscricao = rs.getLong("id_inscricao");

                    if (rm != null && !rm.isBlank()) {
                        inscricoes.put(rm.trim().toUpperCase(), idInscricao);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inscricoes;
    }


    // =====================================================
    // REGISTRAR PRESENÇA EM EVENTO
    //
    // Grava (ou atualiza, se já existir registro naquele
    // dia) a presença de uma inscrição em uma data.
    // =====================================================

    public boolean registrarPresenca(
            long idInscricao,
            LocalDate data,
            String status
    ) {

        String sql = """
                INSERT INTO presenca_evento (
                    id_inscricao,
                    data_presenca,
                    status
                )
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    status = VALUES(status)
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, idInscricao);
            stmt.setDate(2, Date.valueOf(data));
            stmt.setString(3, status);

            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // REGISTRAR PARTICIPAÇÃO EM ATIVIDADE
    //
    // Diferente da presença em evento, não precisa de data
    // separada nem de "inscrição": a atividade já tem sua
    // própria data/horário, e a ligação é direta
    // aluno <-> atividade.
    // =====================================================

    public boolean registrarParticipacaoAtividade(
            long idAluno,
            long idAtividade,
            String status
    ) {

        String sql = """
                INSERT INTO participacao_atividade (
                    id_aluno,
                    id_atividade,
                    presenca
                )
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    presenca = VALUES(presenca)
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, idAluno);
            stmt.setLong(2, idAtividade);
            stmt.setString(3, status);

            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }
}
