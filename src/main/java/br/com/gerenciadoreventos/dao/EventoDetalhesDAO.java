package br.com.gerenciadoreventos.dao;

import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Operações auxiliares usadas nas abas internas do editor de evento. Mantém a tela de Eventos focada na interface e concentra o SQL aqui.
public class EventoDetalhesDAO {
    public static class AtividadeItem {
        public long id;
        public String nome;
        public String descricao;
        public LocalDateTime inicio;
        public LocalDateTime fim;
        public String local;
        public Integer capacidade;
        public String status;
        @Override public String toString() { return nome + " — " + status; }
    }

    public static class ResponsabilidadeItem {
        public long id;
        public String nome;
        public String descricao;
        public String status;
        public String observacao;
        public Long idComissao;
        public String comissao;
        @Override public String toString() { return nome + " — " + status; }
    }

    public static class AgenteVinculo {
        public AgenteExterno agente;
        public String tipoParticipacao;
        public String tema;
        public String observacao;
        @Override public String toString() {
            return agente.getNome() + " — " + tipoParticipacao + (tema == null || tema.isBlank() ? "" : " — " + tema);
        }
    }

    // COMISSÕES - STATUS AUTOMÁTICO PELO EVENTO

    private void sincronizarStatusComissoes(Connection c) throws SQLException {
        String sql = """
                UPDATE comissao co
                INNER JOIN evento e ON e.id_evento = co.id_evento
                SET co.ativo = CASE
                    WHEN e.data_fim IS NOT NULL AND NOW() >= e.data_fim THEN FALSE
                    ELSE TRUE
                END
                WHERE co.ativo <> CASE
                    WHEN e.data_fim IS NOT NULL AND NOW() >= e.data_fim THEN FALSE
                    ELSE TRUE
                END
                """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    private boolean statusComissaoAutomatico(Connection c, long idEvento)
            throws SQLException {
        String sql = """
                SELECT CASE
                    WHEN data_fim IS NOT NULL AND NOW() >= data_fim THEN FALSE
                    ELSE TRUE
                END AS ativo
                FROM evento
                WHERE id_evento = ?
                """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBoolean("ativo");
            }
        }
        throw new SQLException("Evento não encontrado para definir o status da comissão.");
    }

    public List<Comissao> listarComissoes(long idEvento) {
        List<Comissao> lista = new ArrayList<>();
        String sql = """
                SELECT id_comissao, id_evento, nome, descricao, ativo
                FROM comissao
                WHERE id_evento = ?
                ORDER BY ativo DESC, nome
                """;
        try (Connection c = Conexao.conectar()) {
            sincronizarStatusComissoes(c);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, idEvento);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Comissao item = new Comissao();
                        item.setIdComissao(rs.getLong("id_comissao"));
                        item.setIdEvento(rs.getLong("id_evento"));
                        item.setNome(rs.getString("nome"));
                        item.setDescricao(rs.getString("descricao"));
                        item.setAtivo(rs.getBoolean("ativo"));
                        lista.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar comissões do evento.", e);
        }
        return lista;
    }

    public void salvarComissao(Comissao item) {
        boolean novo = item.getIdComissao() == null;
        String sql = novo
                ? "INSERT INTO comissao (id_evento,nome,descricao,ativo) VALUES (?,?,?,?)"
                : "UPDATE comissao SET nome=?, descricao=?, ativo=? WHERE id_comissao=? AND id_evento=?";
        try (Connection c = Conexao.conectar();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            boolean ativoAutomatico = statusComissaoAutomatico(c, item.getIdEvento());
            item.setAtivo(ativoAutomatico);
            if (novo) {
                ps.setLong(1, item.getIdEvento());
                ps.setString(2, item.getNome());
                ps.setString(3, item.getDescricao());
                ps.setBoolean(4, ativoAutomatico);
            } else {
                ps.setString(1, item.getNome());
                ps.setString(2, item.getDescricao());
                ps.setBoolean(3, ativoAutomatico);
                ps.setLong(4, item.getIdComissao());
                ps.setLong(5, item.getIdEvento());
            }
            ps.executeUpdate();
            if (novo) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) item.setIdComissao(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar comissão.", e);
        }
    }

    public void excluirComissao(long idEvento, long idComissao) {
        try (Connection c = Conexao.conectar(); PreparedStatement ps = c.prepareStatement("DELETE FROM comissao WHERE id_comissao=? AND id_evento=?")) {
            ps.setLong(1, idComissao); ps.setLong(2, idEvento); ps.executeUpdate();
        } catch (SQLException e) { throw new IllegalStateException("Erro ao excluir comissão.", e); }
    }

    public List<Professor> listarProfessoresVinculados(long idEvento) {
        List<Professor> lista = new ArrayList<>();
        String sql = """
                SELECT p.id_professor,p.nome,p.email,p.telefone,p.area_atuacao,p.ativo
                FROM evento_professor ep JOIN professor p ON p.id_professor=ep.id_professor
                WHERE ep.id_evento=? ORDER BY ep.principal DESC,p.nome
                """;
        try (Connection c=Conexao.conectar(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,idEvento); try(ResultSet rs=ps.executeQuery()){ while(rs.next()){ lista.add(mapProfessor(rs)); } }
        } catch(SQLException e){ throw new IllegalStateException("Erro ao listar professores do evento.",e); }
        return lista;
    }

    public List<Professor> listarProfessoresDisponiveis(long idEvento) {
        List<Professor> lista = new ArrayList<>();
        String sql = """
                SELECT p.id_professor,p.nome,p.email,p.telefone,p.area_atuacao,p.ativo
                FROM professor p
                WHERE p.ativo=TRUE AND NOT EXISTS (
                    SELECT 1 FROM evento_professor ep WHERE ep.id_evento=? AND ep.id_professor=p.id_professor
                ) ORDER BY p.nome
                """;
        try(Connection c=Conexao.conectar(); PreparedStatement ps=c.prepareStatement(sql)){ ps.setLong(1,idEvento); try(ResultSet rs=ps.executeQuery()){while(rs.next())lista.add(mapProfessor(rs));}}
        catch(SQLException e){throw new IllegalStateException("Erro ao listar professores disponíveis.",e);} return lista;
    }

    public void vincularProfessor(long idEvento,long idProfessor,boolean principal){
        String sql="INSERT INTO evento_professor(id_evento,id_professor,principal) VALUES(?,?,?) ON DUPLICATE KEY UPDATE principal=VALUES(principal)";
        try(Connection c=Conexao.conectar(); PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,idEvento);ps.setLong(2,idProfessor);ps.setBoolean(3,principal);ps.executeUpdate();}
        catch(SQLException e){throw new IllegalStateException("Erro ao vincular professor.",e);}
    }
    public void removerProfessor(long idEvento,long idProfessor){ executarDelete("DELETE FROM evento_professor WHERE id_evento=? AND id_professor=?",idEvento,idProfessor,"professor"); }

    public List<Aluno> listarAlunosInscritos(long idEvento){
        List<Aluno> lista=new ArrayList<>(); String sql="""
                SELECT a.id_aluno,a.rm,a.nome,c.nome curso,s.numero serie,a.email,a.telefone,a.ativo
                FROM inscricao_evento i JOIN aluno a ON a.id_aluno=i.id_aluno
                LEFT JOIN curso c ON c.id_curso=a.id_curso LEFT JOIN serie s ON s.id_serie=a.id_serie
                WHERE i.id_evento=? AND i.status<>'CANCELADO' ORDER BY a.nome
                """;
        try(Connection c=Conexao.conectar(); PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,idEvento);try(ResultSet rs=ps.executeQuery()){while(rs.next())lista.add(mapAluno(rs));}}
        catch(SQLException e){throw new IllegalStateException("Erro ao listar alunos inscritos.",e);}return lista;
    }

    public List<Aluno> listarAlunosDisponiveis(long idEvento){
        List<Aluno> lista=new ArrayList<>(); String sql="""
                SELECT a.id_aluno,a.rm,a.nome,c.nome curso,s.numero serie,a.email,a.telefone,a.ativo
                FROM aluno a LEFT JOIN curso c ON c.id_curso=a.id_curso LEFT JOIN serie s ON s.id_serie=a.id_serie
                WHERE a.ativo=TRUE AND NOT EXISTS(
                    SELECT 1 FROM inscricao_evento i WHERE i.id_evento=? AND i.id_aluno=a.id_aluno AND i.status<>'CANCELADO'
                ) ORDER BY a.nome
                """;
        try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,idEvento);try(ResultSet rs=ps.executeQuery()){while(rs.next())lista.add(mapAluno(rs));}}
        catch(SQLException e){throw new IllegalStateException("Erro ao listar alunos disponíveis.",e);}return lista;
    }

    public void inscreverAluno(long idEvento,long idAluno){
        String sql="""
                INSERT INTO inscricao_evento(id_aluno,id_evento,status) VALUES(?,?,'INSCRITO')
                ON DUPLICATE KEY UPDATE status='INSCRITO', data_inscricao=CURRENT_TIMESTAMP
                """;
        try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,idAluno);ps.setLong(2,idEvento);ps.executeUpdate();}
        catch(SQLException e){throw new IllegalStateException("Erro ao inscrever aluno.",e);}
    }
    public void cancelarInscricao(long idEvento,long idAluno){
        try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement("UPDATE inscricao_evento SET status='CANCELADO' WHERE id_evento=? AND id_aluno=?")){ps.setLong(1,idEvento);ps.setLong(2,idAluno);ps.executeUpdate();}
        catch(SQLException e){throw new IllegalStateException("Erro ao cancelar inscrição.",e);}
    }

    public List<AgenteVinculo> listarAgentesVinculados(long idEvento){
        List<AgenteVinculo> lista=new ArrayList<>(); String sql="""
                SELECT a.id_agente,a.nome,a.email,a.telefone,a.empresa,a.cargo,a.especialidade,a.observacao,a.ativo,
                       ea.tipo_participacao,ea.tema,ea.observacao observacao_vinculo
                FROM evento_agente_externo ea JOIN agente_externo a ON a.id_agente=ea.id_agente
                WHERE ea.id_evento=? ORDER BY a.nome
                """;
        try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,idEvento);try(ResultSet rs=ps.executeQuery()){while(rs.next()){AgenteVinculo v=new AgenteVinculo();v.agente=mapAgente(rs);v.tipoParticipacao=rs.getString("tipo_participacao");v.tema=rs.getString("tema");v.observacao=rs.getString("observacao_vinculo");lista.add(v);}}}
        catch(SQLException e){throw new IllegalStateException("Erro ao listar agentes do evento.",e);}return lista;
    }

    public List<AgenteExterno> listarAgentesDisponiveis(long idEvento){
        List<AgenteExterno> lista=new ArrayList<>(); String sql="""
                SELECT a.id_agente,a.nome,a.email,a.telefone,a.empresa,a.cargo,a.especialidade,a.observacao,a.ativo
                FROM agente_externo a WHERE a.ativo=TRUE AND NOT EXISTS(
                    SELECT 1 FROM evento_agente_externo ea WHERE ea.id_evento=? AND ea.id_agente=a.id_agente
                ) ORDER BY a.nome
                """;
        try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,idEvento);try(ResultSet rs=ps.executeQuery()){while(rs.next())lista.add(mapAgente(rs));}}
        catch(SQLException e){throw new IllegalStateException("Erro ao listar agentes disponíveis.",e);}return lista;
    }
    public void vincularAgente(long idEvento,long idAgente,String tipo,String tema,String obs){
        String sql="""
                INSERT INTO evento_agente_externo(id_evento,id_agente,tipo_participacao,tema,observacao)
                VALUES(?,?,?,?,?)
                ON DUPLICATE KEY UPDATE
                    tipo_participacao=VALUES(tipo_participacao),
                    tema=VALUES(tema),
                    observacao=VALUES(observacao)
                """;
        try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,idEvento);ps.setLong(2,idAgente);ps.setString(3,tipo);ps.setString(4,tema);ps.setString(5,obs);ps.executeUpdate();}
        catch(SQLException e){throw new IllegalStateException("Erro ao vincular agente externo.",e);}
    }
    public void removerAgente(long idEvento,long idAgente){executarDelete("DELETE FROM evento_agente_externo WHERE id_evento=? AND id_agente=?",idEvento,idAgente,"agente externo");}

    public List<AtividadeItem> listarAtividades(long idEvento){
        List<AtividadeItem> lista=new ArrayList<>();String sql="SELECT * FROM atividade WHERE id_evento=? ORDER BY data_inicio,nome";
        try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,idEvento);try(ResultSet rs=ps.executeQuery()){while(rs.next()){AtividadeItem a=new AtividadeItem();a.id=rs.getLong("id_atividade");a.nome=rs.getString("nome");a.descricao=rs.getString("descricao");Timestamp i=rs.getTimestamp("data_inicio");Timestamp f=rs.getTimestamp("data_fim");a.inicio=i==null?null:i.toLocalDateTime();a.fim=f==null?null:f.toLocalDateTime();a.local=rs.getString("local");int cap=rs.getInt("capacidade");a.capacidade=rs.wasNull()?null:cap;a.status=rs.getString("status");lista.add(a);}}}
        catch(SQLException e){throw new IllegalStateException("Erro ao listar atividades.",e);}return lista;
    }
    public void salvarAtividade(long idEvento,AtividadeItem a){
        boolean novo=a.id==0;String sql=novo?"INSERT INTO atividade(id_evento,nome,descricao,data_inicio,data_fim,local,capacidade,status) VALUES(?,?,?,?,?,?,?,?)":"UPDATE atividade SET nome=?,descricao=?,data_inicio=?,data_fim=?,local=?,capacidade=?,status=? WHERE id_atividade=? AND id_evento=?";
        try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sql)){int p=1;if(novo)ps.setLong(p++,idEvento);ps.setString(p++,a.nome);ps.setString(p++,a.descricao);ps.setTimestamp(p++,Timestamp.valueOf(a.inicio));if(a.fim==null)ps.setNull(p++,Types.TIMESTAMP);else ps.setTimestamp(p++,Timestamp.valueOf(a.fim));ps.setString(p++,a.local);if(a.capacidade==null)ps.setNull(p++,Types.INTEGER);else ps.setInt(p++,a.capacidade);ps.setString(p++,a.status);if(!novo){ps.setLong(p++,a.id);ps.setLong(p,idEvento);}ps.executeUpdate();}
        catch(SQLException e){throw new IllegalStateException("Erro ao salvar atividade.",e);}
    }
    public void excluirAtividade(long idEvento,long id){executarDelete("DELETE FROM atividade WHERE id_evento=? AND id_atividade=?",idEvento,id,"atividade");}

    public List<ResponsabilidadeItem> listarResponsabilidades(long idEvento){
        List<ResponsabilidadeItem> lista=new ArrayList<>();String sql="""
                SELECT r.id_responsabilidade,r.nome,r.descricao,r.status,r.observacao,MIN(c.id_comissao) id_comissao,MIN(c.nome) comissao
                FROM responsabilidade r
                LEFT JOIN responsabilidade_comissao rc ON rc.id_responsabilidade=r.id_responsabilidade
                LEFT JOIN comissao c ON c.id_comissao=rc.id_comissao
                WHERE r.id_evento=? GROUP BY r.id_responsabilidade,r.nome,r.descricao,r.status,r.observacao ORDER BY r.nome
                """;
        try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,idEvento);try(ResultSet rs=ps.executeQuery()){while(rs.next()){ResponsabilidadeItem r=new ResponsabilidadeItem();r.id=rs.getLong("id_responsabilidade");r.nome=rs.getString("nome");r.descricao=rs.getString("descricao");r.status=rs.getString("status");r.observacao=rs.getString("observacao");long cid=rs.getLong("id_comissao");r.idComissao=rs.wasNull()?null:cid;r.comissao=rs.getString("comissao");lista.add(r);}}}
        catch(SQLException e){throw new IllegalStateException("Erro ao listar responsabilidades.",e);}return lista;
    }
    public void salvarResponsabilidade(long idEvento,ResponsabilidadeItem r){
        boolean novo=r.id==0;Connection c=null;try{c=Conexao.conectar();c.setAutoCommit(false);String sql=novo?"INSERT INTO responsabilidade(id_evento,nome,descricao,status,observacao) VALUES(?,?,?,?,?)":"UPDATE responsabilidade SET nome=?,descricao=?,status=?,observacao=? WHERE id_responsabilidade=? AND id_evento=?";try(PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){int p=1;if(novo)ps.setLong(p++,idEvento);ps.setString(p++,r.nome);ps.setString(p++,r.descricao);ps.setString(p++,r.status);ps.setString(p++,r.observacao);if(!novo){ps.setLong(p++,r.id);ps.setLong(p,idEvento);}ps.executeUpdate();if(novo)try(ResultSet rs=ps.getGeneratedKeys()){if(rs.next())r.id=rs.getLong(1);}}
            try(PreparedStatement del=c.prepareStatement("DELETE FROM responsabilidade_comissao WHERE id_responsabilidade=?")){del.setLong(1,r.id);del.executeUpdate();}
            if(r.idComissao!=null)try(PreparedStatement ins=c.prepareStatement("INSERT INTO responsabilidade_comissao(id_responsabilidade,id_comissao) VALUES(?,?)")){ins.setLong(1,r.id);ins.setLong(2,r.idComissao);ins.executeUpdate();}
            c.commit();
        }catch(SQLException e){if(c!=null)try{c.rollback();}catch(SQLException ignored){}throw new IllegalStateException("Erro ao salvar responsabilidade.",e);}finally{if(c!=null)try{c.close();}catch(SQLException ignored){}}
    }
    public void excluirResponsabilidade(long idEvento,long id){executarDelete("DELETE FROM responsabilidade WHERE id_evento=? AND id_responsabilidade=?",idEvento,id,"responsabilidade");}

    private void executarDelete(String sql,long idEvento,long id,String nome){try(Connection c=Conexao.conectar();PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,idEvento);ps.setLong(2,id);ps.executeUpdate();}catch(SQLException e){throw new IllegalStateException("Erro ao remover "+nome+" do evento.",e);}}
    private Professor mapProfessor(ResultSet rs)throws SQLException{Professor p=new Professor();p.setId(rs.getLong("id_professor"));p.setNome(rs.getString("nome"));p.setEmail(rs.getString("email"));p.setTelefone(rs.getString("telefone"));p.setAreaAtuacao(rs.getString("area_atuacao"));p.setAtivo(rs.getBoolean("ativo"));return p;}
    private Aluno mapAluno(ResultSet rs)throws SQLException{Aluno a=new Aluno();a.setId(rs.getLong("id_aluno"));a.setRm(rs.getString("rm"));a.setNome(rs.getString("nome"));a.setCurso(rs.getString("curso"));int s=rs.getInt("serie");a.setSerie(rs.wasNull()?null:s);a.setEmail(rs.getString("email"));a.setTelefone(rs.getString("telefone"));a.setAtivo(rs.getBoolean("ativo"));return a;}
    private AgenteExterno mapAgente(ResultSet rs)throws SQLException{AgenteExterno a=new AgenteExterno();a.setId(rs.getLong("id_agente"));a.setNome(rs.getString("nome"));a.setEmail(rs.getString("email"));a.setTelefone(rs.getString("telefone"));a.setEmpresa(rs.getString("empresa"));a.setCargo(rs.getString("cargo"));a.setEspecialidade(rs.getString("especialidade"));a.setObservacao(rs.getString("observacao"));a.setAtivo(rs.getBoolean("ativo"));return a;}
}
