package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.Evento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    public List<Evento> listarEventos() {

        List<Evento> eventos = new ArrayList<>();

        String sql = """
                SELECT *
                FROM evento
                ORDER BY data_inicio ASC
                """;

        try (
                Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                Evento evento = new Evento();

                evento.setId(rs.getLong("id_evento"));

                evento.setIdUsuarioCriador(
                        rs.getLong("id_usuario_criador")
                );

                evento.setNome(
                        rs.getString("nome")
                );

                evento.setDescricao(
                        rs.getString("descricao")
                );

                Timestamp dataInicio =
                        rs.getTimestamp("data_inicio");

                if (dataInicio != null) {
                    evento.setDataInicio(
                            dataInicio.toLocalDateTime()
                    );
                }

                Timestamp dataFim =
                        rs.getTimestamp("data_fim");

                if (dataFim != null) {
                    evento.setDataFim(
                            dataFim.toLocalDateTime()
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return eventos;
    }

    // CADASTRAR EVENTO
    public boolean cadastrarEvento(Evento evento) {

        String sql = """
                INSERT INTO evento (
                    id_usuario_criador,
                    nome,
                    descricao,
                    data_inicio,
                    data_fim,
                    local,
                    capacidade,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conexao = Conexao.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    evento.getIdUsuarioCriador()
            );

            stmt.setString(
                    2,
                    evento.getNome()
            );

            stmt.setString(
                    3,
                    evento.getDescricao()
            );

            stmt.setTimestamp(
                    4,
                    Timestamp.valueOf(evento.getDataInicio())
            );

            stmt.setTimestamp(
                    5,
                    Timestamp.valueOf(evento.getDataFim())
            );

            stmt.setString(
                    6,
                    evento.getLocal()
            );

            stmt.setInt(
                    7,
                    evento.getCapacidade()
            );

            stmt.setString(
                    8,
                    evento.getStatus()
            );

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}