package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.EventoPublico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoPublicoDAO {

    /**
     * Retorna somente combinações que realmente existem no cadastro da escola.
     * A primeira linha de cada curso representa o curso inteiro (série nula).
     */
    public List<EventoPublico> listarPublicosDisponiveis() {
        List<EventoPublico> publicos = new ArrayList<>();

        /*
         * Não usamos UNION aqui de propósito.
         *
         * A coluna curso.nome e a coluna serie.nome podem estar com
         * collations diferentes em um banco que foi criado/alterado por
         * versões anteriores do projeto. Um UNION força o MySQL a comparar
         * os tipos de texto das duas partes e pode produzir:
         *
         *   Illegal mix of collations for operation 'UNION'
         *
         * Além disso, não precisamos de UNION para montar a lista.
         * Fazemos duas consultas independentes dentro da mesma conexão:
         *  1) cursos inteiros (serie = null);
         *  2) séries específicas.
         *
         * Assim a regra de negócio continua a mesma e a consulta não depende
         * da collation da conexão para combinar strings de tabelas diferentes.
         */
        String cursosSql = """
                SELECT c.nome AS curso
                FROM curso c
                WHERE c.ativo = TRUE
                ORDER BY c.nome
                """;

        String seriesSql = """
                SELECT
                    c.nome AS curso,
                    s.nome AS serie,
                    s.numero AS ordem_serie
                FROM curso c
                INNER JOIN serie s ON s.id_curso = c.id_curso
                WHERE c.ativo = TRUE
                  AND s.ativo = TRUE
                ORDER BY c.nome, s.numero
                """;

        try (Connection conn = Conexao.conectar()) {
            try (PreparedStatement stmt = conn.prepareStatement(cursosSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    publicos.add(new EventoPublico(
                            rs.getString("curso"),
                            null
                    ));
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(seriesSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    publicos.add(new EventoPublico(
                            rs.getString("curso"),
                            rs.getString("serie")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar públicos disponíveis.", e);
        }

        return publicos;
    }
}
