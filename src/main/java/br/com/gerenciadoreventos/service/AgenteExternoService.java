package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.AgenteExternoDAO;
import br.com.gerenciadoreventos.model.AgenteExterno;

import java.util.List;

public class AgenteExternoService {
    private final AgenteExternoDAO agenteExternoDAO;

    public AgenteExternoService() {
        agenteExternoDAO = new AgenteExternoDAO();
    }

    public List<AgenteExterno> listarAtivos() {
        return agenteExternoDAO.listarPorStatus(true);
    }

    public List<AgenteExterno> listarInativos() {
        return agenteExternoDAO.listarPorStatus(false);
    }

    public List<AgenteExterno> pesquisar(
            String texto,
            boolean ativo
    ) {
        return agenteExternoDAO.pesquisar( texto, ativo );
    }

    public AgenteExterno buscarPorId(long id) {
        return agenteExternoDAO.buscarPorId(id);
    }

    public boolean cadastrar(AgenteExterno agente) {
        if (agente.getNome() == null
                || agente.getNome().trim().isEmpty()) {
            return false;
        }
        agente.setNome( agente.getNome().trim() );
        agente.setEmail( limpar(agente.getEmail()) );
        agente.setTelefone( limpar(agente.getTelefone()) );
        agente.setEmpresa( limpar(agente.getEmpresa()) );
        agente.setCargo( limpar(agente.getCargo()) );
        agente.setEspecialidade( limpar(agente.getEspecialidade()) );
        agente.setObservacao( limpar(agente.getObservacao()) );
        agente.setAtivo(true);
        return agenteExternoDAO.cadastrar(agente);
    }

    public boolean atualizar(AgenteExterno agente) {
        if (agente.getNome() == null
                || agente.getNome().trim().isEmpty()) {
            return false;
        }
        agente.setNome( agente.getNome().trim() );
        agente.setEmail( limpar(agente.getEmail()) );
        agente.setTelefone( limpar(agente.getTelefone()) );
        agente.setEmpresa( limpar(agente.getEmpresa()) );
        agente.setCargo( limpar(agente.getCargo()) );
        agente.setEspecialidade( limpar(agente.getEspecialidade()) );
        agente.setObservacao( limpar(agente.getObservacao()) );
        return agenteExternoDAO.atualizar(agente);
    }

    public boolean ativar(long id) {
        return agenteExternoDAO.alterarStatus(id, true);
    }

    public boolean desativar(long id) {
        return agenteExternoDAO.alterarStatus(id, false);
    }

    private String limpar(String valor) {
        if (valor == null) {
            return null;
        }
        valor = valor.trim();
        return valor.isEmpty() ? null : valor;
    }
}
