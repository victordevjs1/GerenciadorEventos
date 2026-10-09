package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.ComissaoDAO;
import br.com.gerenciadoreventos.model.Comissao;

import java.sql.SQLException;
import java.util.List;

public class ComissaoService {
    private final ComissaoDAO dao;

    public ComissaoService() {
        dao = new ComissaoDAO();
    }

    // LISTAR

    public List<Comissao> listar() throws SQLException {
        return dao.listar();
    }

    // BUSCAR

    public Comissao buscarPorId(Long id) throws SQLException {
        return dao.buscarPorId(id);
    }

    // CADASTRAR

    public void cadastrar(Comissao comissao)
            throws SQLException {
        validar(comissao);
        if (comissao.getAtivo() == null) {
            comissao.setAtivo(true);
        }
        dao.cadastrar(comissao);
    }

    // ALTERAR

    public void alterar(Comissao comissao)
            throws SQLException {
        validar(comissao);
        if (comissao.getIdComissao() == null) {
            throw new IllegalArgumentException( "Comissão inválida." );
        }
        dao.alterar(comissao);
    }

    // EXCLUIR

    public void excluir(Long id)
            throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException( "Comissão inválida." );
        }
        dao.excluir(id);
    }

    // VALIDAÇÃO

    private void validar(Comissao comissao) {
        if (comissao == null) {
            throw new IllegalArgumentException( "Comissão inválida." );
        }
        if (comissao.getIdEvento() == null) {
            throw new IllegalArgumentException( "Selecione um evento." );
        }
        if (comissao.getNome() == null
                || comissao.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException( "Informe o nome da comissão." );
        }
        if (comissao.getNome().length() > 150) {
            throw new IllegalArgumentException( "O nome da comissão deve ter no máximo 150 caracteres." );
        }
    }
}
