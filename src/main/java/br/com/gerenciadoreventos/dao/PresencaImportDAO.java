package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PresencaImportDAO {

    // =====================================================
    // OPÇÃO DE EVENTO (usada só para preencher o combo box
    // da tela de importação)
    // =====================================================

    public static class EventoOpcao {

        public final long id;
        public final String nome;
        public final LocalDate dataInicio;
        public final LocalDate dataFim;

        public EventoOpcao(
                long id,
                String nome,
                LocalDate dataInicio,
                LocalDate dataFim
        ) {
            this.id = id;
            this.nome = nome;
            this.dataInicio = dataInicio;
            this.dataFim = dataFim;
        }

        @Override
        public String toString() {
            return nome;
        }
    }


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
    // OBTER OU CRIAR INSCRIÇÃO
    //
    // Se o aluno já está inscrito no evento, retorna o
    // id_inscricao existente. Se não estiver (ex.: só
    // respondeu o formulário e nunca tinha se inscrito),
    // cria a inscrição na hora.
    // =====================================================

    public Long obterOuCriarInscricao(
            long idAluno,
            long idEvento
    ) {

        String selecionar = """
                SELECT id_inscricao
                FROM inscricao_evento
                WHERE id_aluno = ? AND id_evento = ?
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
    // REGISTRAR PRESENÇA
    //
    // Grava (ou atualiza, se já existir registro naquele
    // dia) a presença de uma inscrição em uma data.
    // =====================================================

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
    // OPÇÃO DE ATIVIDADE (usada para preencher o combo box
    // no modo "Atividade" da tela de importação)
    // =====================================================

    public static class AtividadeOpcao {

        public final long id;
        public final String rotulo;

        public AtividadeOpcao(long id, String rotulo) {
            this.id = id;
            this.rotulo = rotulo;
        }

        @Override
        public String toString() {
            return rotulo;
        }
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