package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.Aluno;
import br.com.gerenciadoreventos.model.Evento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscricaoDAO {

    // =====================================================
    // LISTAR INSCRIÇÕES
    // =====================================================

    public List<Inscricao> listarInscricoes(
            Long idEvento,
            String busca,
            String status
    ) {

        List<Inscricao> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append("""
                SELECT
                    i.id_inscricao,
                    i.id_aluno,
                    i.id_evento,
                    a.rm,
                    a.nome AS nome_aluno,
                    c.nome AS curso,
                    s.nome AS serie,
                    e.nome AS nome_evento,
                    i.data_inscricao,
                    i.status,
                    i.observacao
                FROM inscricao_evento i
                INNER JOIN aluno a
                    ON a.id_aluno = i.id_aluno
                LEFT JOIN curso c
                    ON c.id_curso = a.id_curso
                LEFT JOIN serie s
                    ON s.id_serie = a.id_serie
                INNER JOIN evento e
                    ON e.id_evento = i.id_evento
                WHERE 1 = 1
                """);

        List<Object> parametros = new ArrayList<>();


        // =================================================
        // FILTRO POR EVENTO
        // =================================================

        if (idEvento != null) {

            sql.append(
                    " AND i.id_evento = ? "
            );

            parametros.add(idEvento);
        }


        // =================================================
        // FILTRO POR BUSCA
        // =================================================

        if (busca != null && !busca.trim().isEmpty()) {

            sql.append("""
                     AND (
                        LOWER(a.nome) LIKE ?
                        OR LOWER(a.rm) LIKE ?
                     )
                    """);

            String valor =
                    "%"
                            + busca.trim().toLowerCase()
                            + "%";

            parametros.add(valor);
            parametros.add(valor);
        }


        // =================================================
        // FILTRO POR STATUS
        // =================================================

        if (
                status != null
                        && !status.isBlank()
                        && !status.equalsIgnoreCase("TODOS")
        ) {

            sql.append(
                    " AND i.status = ? "
            );

            parametros.add(status);
        }


        sql.append(
                " ORDER BY i.data_inscricao DESC"
        );


        try (
                Connection conexao =
                        Conexao.conectar();

                PreparedStatement stmt =
                        conexao.prepareStatement(
                                sql.toString()
                        )
        ) {

            for (
                    int i = 0;
                    i < parametros.size();
                    i++
            ) {

                stmt.setObject(
                        i + 1,
                        parametros.get(i)
                );
            }


            ResultSet rs =
                    stmt.executeQuery();


            while (rs.next()) {

                Inscricao inscricao =
                        new Inscricao(
                                rs.getLong("id_inscricao"),
                                rs.getLong("id_aluno"),
                                rs.getLong("id_evento"),
                                rs.getString("rm"),
                                rs.getString("nome_aluno"),
                                rs.getString("curso"),
                                rs.getString("serie"),
                                rs.getString("nome_evento"),
                                rs.getTimestamp("data_inscricao"),
                                rs.getString("status"),
                                rs.getString("observacao")
                        );


                lista.add(inscricao);
            }

        } catch (SQLException e) {

            e.printStackTrace();

        }


        return lista;
    }


    // =====================================================
    // LISTAR EVENTOS
    // =====================================================

    public List<Evento> listarEventos() {

        List<Evento> eventos =
                new ArrayList<>();


        String sql = """
                SELECT
                    id_evento,
                    id_usuario_criador,
                    nome,
                    descricao,
                    data_inicio,
                    data_fim,
                    local,
                    capacidade,
                    status
                FROM evento
                WHERE status <> 'CANCELADO'
                ORDER BY data_inicio ASC
                """;


        try (
                Connection conexao =
                        Conexao.conectar();

                PreparedStatement stmt =
                        conexao.prepareStatement(sql);

                ResultSet rs =
                        stmt.executeQuery()
        ) {


            while (rs.next()) {

                Evento evento =
                        new Evento();


                evento.setId(
                        rs.getLong("id_evento")
                );

                evento.setIdUsuarioCriador(
                        rs.getLong(
                                "id_usuario_criador"
                        )
                );

                evento.setNome(
                        rs.getString("nome")
                );

                evento.setDescricao(
                        rs.getString("descricao")
                );


                Timestamp inicio =
                        rs.getTimestamp(
                                "data_inicio"
                        );

                if (inicio != null) {

                    evento.setDataInicio(
                            inicio.toLocalDateTime()
                    );
                }


                Timestamp fim =
                        rs.getTimestamp(
                                "data_fim"
                        );

                if (fim != null) {

                    evento.setDataFim(
                            fim.toLocalDateTime()
                    );
                }


                evento.setLocal(
                        rs.getString("local")
                );

                evento.setCapacidade(
                        rs.getInt("capacidade")
                );

                evento.setStatus(
                        rs.getString("status")
                );


                eventos.add(evento);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }


        return eventos;
    }


    // =====================================================
    // BUSCAR ALUNOS
    // =====================================================

    public List<Aluno> buscarAlunos(
            String busca
    ) {

        List<Aluno> alunos =
                new ArrayList<>();


        String sql = """
                SELECT
                    id_aluno,
                    rm,
                    nome,
                    data_nascimento,
                    c.nome AS curso,
                    s.numero AS serie,
                    email,
                    telefone,
                    ativo
                FROM aluno a
                LEFT JOIN curso c ON c.id_curso = a.id_curso
                LEFT JOIN serie s ON s.id_serie = a.id_serie
                WHERE a.ativo = TRUE
                  AND (
                    LOWER(nome) LIKE ?
                    OR LOWER(rm) LIKE ?
                  )
                ORDER BY a.nome
                LIMIT 30
                """;


        String pesquisa =
                "%"
                        + (busca == null
                        ? ""
                        : busca.trim().toLowerCase())
                        + "%";


        try (
                Connection conexao =
                        Conexao.conectar();

                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setString(
                    1,
                    pesquisa
            );

            stmt.setString(
                    2,
                    pesquisa
            );


            ResultSet rs =
                    stmt.executeQuery();


            while (rs.next()) {

                Aluno aluno =
                        new Aluno();


                aluno.setId(
                        rs.getLong("id_aluno")
                );

                aluno.setRm(
                        rs.getString("rm")
                );

                aluno.setNome(
                        rs.getString("nome")
                );


                Date data =
                        rs.getDate(
                                "data_nascimento"
                        );

                if (data != null) {

                    aluno.setDataNascimento(
                            data.toString()
                    );
                }


                aluno.setCurso(
                        rs.getString("curso")
                );

                int serie = rs.getInt("serie");
                if (!rs.wasNull()) {
                    aluno.setSerie(serie);
                }

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
    // VERIFICAR SE ALUNO JÁ ESTÁ INSCRITO
    // =====================================================

    public boolean alunoJaInscrito(
            long idAluno,
            long idEvento
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM inscricao_evento
                WHERE id_aluno = ?
                  AND id_evento = ?
                  AND status <> 'CANCELADO'
                """;


        try (
                Connection conexao =
                        Conexao.conectar();

                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    idAluno
            );

            stmt.setLong(
                    2,
                    idEvento
            );


            ResultSet rs =
                    stmt.executeQuery();


            if (rs.next()) {

                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }


        return false;
    }


    // =====================================================
    // REALIZAR INSCRIÇÃO
    // =====================================================

    public boolean cadastrarInscricao(
            long idAluno,
            long idEvento,
            String observacao
    ) {

        if (
                alunoJaInscrito(
                        idAluno,
                        idEvento
                )
        ) {

            return false;
        }


        String sql = """
                INSERT INTO inscricao_evento
                (
                    id_aluno,
                    id_evento,
                    status,
                    observacao
                )
                VALUES
                (
                    ?,
                    ?,
                    'INSCRITO',
                    ?
                )
                """;


        try (
                Connection conexao =
                        Conexao.conectar();

                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    idAluno
            );

            stmt.setLong(
                    2,
                    idEvento
            );

            stmt.setString(
                    3,
                    observacao
            );


            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // CANCELAR INSCRIÇÃO
    // =====================================================

    public boolean cancelarInscricao(
            long idInscricao
    ) {

        String sql = """
                UPDATE inscricao_evento
                SET status = 'CANCELADO'
                WHERE id_inscricao = ?
                """;


        try (
                Connection conexao =
                        Conexao.conectar();

                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    idInscricao
            );


            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // CLASSE INSCRIÇÃO
    // =====================================================

    public static class Inscricao {

        private long idInscricao;

        private long idAluno;

        private long idEvento;

        private String rm;

        private String nomeAluno;

        private String curso;

        private String serie;

        private String nomeEvento;

        private Timestamp dataInscricao;

        private String status;

        private String observacao;


        public Inscricao(
                long idInscricao,
                long idAluno,
                long idEvento,
                String rm,
                String nomeAluno,
                String curso,
                String serie,
                String nomeEvento,
                Timestamp dataInscricao,
                String status,
                String observacao
        ) {

            this.idInscricao =
                    idInscricao;

            this.idAluno =
                    idAluno;

            this.idEvento =
                    idEvento;

            this.rm =
                    rm;

            this.nomeAluno =
                    nomeAluno;

            this.curso =
                    curso;

            this.serie =
                    serie;

            this.nomeEvento =
                    nomeEvento;

            this.dataInscricao =
                    dataInscricao;

            this.status =
                    status;

            this.observacao =
                    observacao;
        }


        public long getIdInscricao() {
            return idInscricao;
        }


        public long getIdAluno() {
            return idAluno;
        }


        public long getIdEvento() {
            return idEvento;
        }


        public String getRm() {
            return rm;
        }


        public String getNomeAluno() {
            return nomeAluno;
        }


        public String getCurso() {
            return curso;
        }

        public String getSerie() {
            return serie;
        }


        public String getNomeEvento() {
            return nomeEvento;
        }


        public Timestamp getDataInscricao() {
            return dataInscricao;
        }


        public String getStatus() {
            return status;
        }


        public String getObservacao() {
            return observacao;
        }
    }
}
