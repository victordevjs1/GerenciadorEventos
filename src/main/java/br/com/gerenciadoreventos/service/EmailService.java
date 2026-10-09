package br.com.gerenciadoreventos.service;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EmailService {
    public void enviarCodigoRecuperacao(String destinatario, String nome, String codigo) {
        Config config = Config.carregar();
        try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(config.host, config.port)) {
            socket.setSoTimeout(15_000);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
                esperar(in, 220);
                comando(out, in, "EHLO e-task", 250);
                comando(out, in, "AUTH LOGIN", 334);
                comando(out, in, base64(config.usuario), 334);
                comando(out, in, base64(config.senha), 235);
                comando(out, in, "MAIL FROM:<" + config.remetente + ">", 250);
                comando(out, in, "RCPT TO:<" + destinatario + ">", 250, 251);
                comando(out, in, "DATA", 354);
                escreverMensagem(out, config.remetente, destinatario, nome, codigo);
                esperar(in, 250);
                comando(out, in, "QUIT", 221);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível enviar o e-mail de recuperação. Verifique a configuração SMTP.", e);
        }
    }

    private void escreverMensagem(BufferedWriter out, String remetente, String destinatario, String nome, String codigo) throws IOException {
        out.write("From: e-task <" + remetente + ">\r\n");
        out.write("To: <" + destinatario + ">\r\n");
        out.write("Subject: Recuperacao de senha - e-task\r\n");
        out.write("MIME-Version: 1.0\r\n");
        out.write("Content-Type: text/plain; charset=UTF-8\r\n");
        out.write("Content-Transfer-Encoding: 8bit\r\n\r\n");
        out.write("Olá, " + (nome == null || nome.isBlank() ? "usuário" : nome) + ".\r\n\r\n");
        out.write("Seu código para redefinir a senha do e-task é: " + codigo + "\r\n\r\n");
        out.write("O código expira em 10 minutos. Se você não solicitou a alteração, ignore este e-mail.\r\n");
        out.write(".\r\n");
        out.flush();
    }

    private void comando(BufferedWriter out, BufferedReader in, String valor, int... codigosAceitos) throws IOException {
        out.write(valor + "\r\n");
        out.flush();
        Resposta resposta = lerResposta(in);
        for (int codigo : codigosAceitos) {
            if (resposta.codigo == codigo) {
                return;
            }
        }
        throw new IOException("Servidor SMTP recusou a operação: " + resposta.texto);
    }

    private void esperar(BufferedReader in, int codigoEsperado) throws IOException {
        Resposta resposta = lerResposta(in);
        if (resposta.codigo != codigoEsperado) {
            throw new IOException("Resposta SMTP inesperada: " + resposta.texto);
        }
    }

    private Resposta lerResposta(BufferedReader in) throws IOException {
        String primeira = in.readLine();
        if (primeira == null || primeira.length() < 3) {
            throw new IOException("Servidor SMTP não respondeu corretamente.");
        }
        int codigo = Integer.parseInt(primeira.substring(0, 3));
        StringBuilder texto = new StringBuilder(primeira);
        String linha = primeira;
        while (linha.length() > 3 && linha.charAt(3) == '-') {
            linha = in.readLine();
            if (linha == null) {
                break;
            }
            texto.append('\n').append(linha);
        }
        return new Resposta(codigo, texto.toString());
    }

    private String base64(String valor) {
        return Base64.getEncoder().encodeToString(valor.getBytes(StandardCharsets.UTF_8));
    }

    private record Resposta(int codigo, String texto) {
    }

    private record Config(String host, int port, String usuario, String senha, String remetente) {
        private static Config carregar() {
            String usuario = valor("etask.smtp.user", "ETASK_SMTP_USER", "");
            String senha = valor("etask.smtp.password", "ETASK_SMTP_PASSWORD", "");
            if (usuario.isBlank() || senha.isBlank()) {
                throw new IllegalStateException("Configure ETASK_SMTP_USER e ETASK_SMTP_PASSWORD para habilitar a recuperação por e-mail.");
            }
            String host = valor("etask.smtp.host", "ETASK_SMTP_HOST", "smtp.gmail.com");
            String remetente = valor("etask.smtp.from", "ETASK_SMTP_FROM", usuario);
            int porta;
            try {
                porta = Integer.parseInt(valor("etask.smtp.port", "ETASK_SMTP_PORT", "465"));
            } catch (NumberFormatException e) {
                throw new IllegalStateException("A porta SMTP configurada é inválida.");
            }
            return new Config(host, porta, usuario, senha, remetente);
        }
        private static String valor(String propriedade, String ambiente, String padrao) {
            String valor = System.getProperty(propriedade);
            if (valor == null || valor.isBlank()) {
                valor = System.getenv(ambiente);
            }
            return valor == null || valor.isBlank() ? padrao : valor.trim();
        }
    }
}
