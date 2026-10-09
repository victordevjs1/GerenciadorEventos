package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.AuditoriaDAO;
import br.com.gerenciadoreventos.model.Usuario;

public class AuditoriaService {
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    public void registrarLogin(Usuario usuario) {
        registrar(usuario, "LOGIN", "Usuário entrou no sistema.");
    }

    public void registrarLogout(Usuario usuario) {
        registrar(usuario, "LOGOUT", "Usuário saiu do sistema.");
    }

    private void registrar(Usuario usuario, String acao, String descricao) {
        if (usuario == null) {
            return;
        }
        try {
            auditoriaDAO.registrarAcesso(usuario.getId(), acao, descricao);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}
