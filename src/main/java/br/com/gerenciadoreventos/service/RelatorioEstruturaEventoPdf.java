package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.RelatorioEstruturaEventoDAO;
import br.com.gerenciadoreventos.dao.RelatorioEstruturaEventoDAO.Atividade;
import br.com.gerenciadoreventos.dao.RelatorioEstruturaEventoDAO.AtividadeVinculada;
import br.com.gerenciadoreventos.dao.RelatorioEstruturaEventoDAO.Comissao;
import br.com.gerenciadoreventos.dao.RelatorioEstruturaEventoDAO.Estrutura;
import br.com.gerenciadoreventos.dao.RelatorioEstruturaEventoDAO.Integrante;
import br.com.gerenciadoreventos.dao.RelatorioEstruturaEventoDAO.Responsabilidade;
import br.com.gerenciadoreventos.model.EventoOpcao;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.sql.SQLException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Gera as páginas detalhadas do relatório de desempenho com atividades, comissões, integrantes e responsabilidades do evento.
public class RelatorioEstruturaEventoPdf {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final RelatorioEstruturaEventoDAO dao = new RelatorioEstruturaEventoDAO();

    public void adicionarAoDocumento(PDDocument documento, EventoOpcao evento) throws IOException {
        if (documento == null || evento == null) {
            throw new IllegalArgumentException("Documento e evento são obrigatórios.");
        }
        final Estrutura dados;
        try {
            dados = dao.buscar(evento.getId());
        } catch (SQLException e) {
            throw new IOException("Não foi possível consultar as comissões e atividades do evento.", e);
        }
        adicionarDados(documento, evento, dados);
    }

    // Separação da consulta e da renderização para permitir testes sem MySQL.
    void adicionarDados(PDDocument documento, EventoOpcao evento, Estrutura dados) throws IOException {
        Set<String> alunosUnicos = new HashSet<>();
        int totalVinculos = 0;
        for (Comissao comissao : dados.comissoes()) {
            totalVinculos += comissao.integrantes().size();
            for (Integrante integrante : comissao.integrantes()) {
                // RM é único no banco; nome fica como fallback para dados incompletos.
                alunosUnicos.add(valor(integrante.rm(), integrante.nome()));
            }
        }
        try (Escritor escritor = new Escritor(documento, evento)) {
            escritor.secao("Estrutura e execução do evento", null);
            escritor.cardsResumo( dados.atividades().size(), dados.comissoes().size(), alunosUnicos.size(), dados.totalResponsabilidades() );
            escritor.textoDiscreto(
                    "Os status representam os registros atuais do sistema. " +
                    "Atividades canceladas ou ainda planejadas aparecem identificadas, " + "para não serem confundidas com atividades concluídas." );
            escritor.secao("Atividades cadastradas no evento", dados.atividades().size());
            if (dados.atividades().isEmpty()) {
                escritor.vazio("Nenhuma atividade cadastrada para este evento.");
            }
            int indice = 1;
            for (Atividade atividade : dados.atividades()) {
                escritor.item(indice++ + ". " + valor(atividade.nome(), "Atividade sem nome"), "Status: " + status(atividade.status()));
                escritor.detalhe("Período", periodo(atividade.inicio(), atividade.fim()));
                escritor.detalhe("Local", valor(atividade.local(), "Não informado"));
                if (preenchido(atividade.descricao())) {
                    escritor.detalhe("Descrição", atividade.descricao());
                }
                escritor.detalhe("Comissões envolvidas", atividade.comissoes().isEmpty()
                        ? "Nenhuma comissão vinculada" : String.join(", ", atividade.comissoes()));
                escritor.espaco(10);
            }
            escritor.secao("Comissões, integrantes e responsabilidades", dados.comissoes().size());
            if (dados.comissoes().isEmpty()) {
                escritor.vazio("Nenhuma comissão cadastrada para este evento.");
            }
            indice = 1;
            for (Comissao comissao : dados.comissoes()) {
                escritor.cabecalhoComissao(indice++ + ". " + valor(comissao.nome(), "Comissão sem nome"), comissao.ativo() ? "Ativa" : "Inativa");
                if (preenchido(comissao.descricao())) {
                    escritor.detalhe("Descrição", comissao.descricao());
                }
                escritor.subtitulo("Integrantes", comissao.integrantes().size());
                if (comissao.integrantes().isEmpty()) {
                    escritor.vazio("Nenhum aluno vinculado a esta comissão.");
                }
                for (Integrante integrante : comissao.integrantes()) {
                    String identificacao = valor(integrante.nome(), "Aluno sem nome") + "  |  RM: " + valor(integrante.rm(), "Não informado");
                    if (!integrante.ativo()) identificacao += "  |  Vínculo inativo";
                    escritor.marcador(identificacao);
                    if (preenchido(integrante.funcao())) {
                        escritor.detalhe("Função", integrante.funcao());
                    }
                }
                escritor.subtitulo("Responsabilidades", comissao.responsabilidades().size());
                if (comissao.responsabilidades().isEmpty()) {
                    escritor.vazio("Nenhuma responsabilidade atribuída a esta comissão.");
                }
                for (Responsabilidade responsabilidade : comissao.responsabilidades()) {
                    escritor.marcador(valor(responsabilidade.nome(), "Responsabilidade sem nome") + "  |  " + status(responsabilidade.status()));
                    if (preenchido(responsabilidade.descricao())) {
                        escritor.detalhe("Descrição", responsabilidade.descricao());
                    }
                    if (preenchido(responsabilidade.observacao())) {
                        escritor.detalhe("Observação", responsabilidade.observacao());
                    }
                }
                escritor.subtitulo("Atividades vinculadas", comissao.atividades().size());
                if (comissao.atividades().isEmpty()) {
                    escritor.vazio("Nenhuma atividade atribuída a esta comissão.");
                }
                for (AtividadeVinculada atividade : comissao.atividades()) {
                    escritor.marcador(valor(atividade.nome(), "Atividade sem nome"));
                    escritor.detalhe("Situação", "Atividade: " + status(atividade.statusAtividade())
                            + "  |  Atribuição: " + status(atividade.statusAtribuicao()));
                }
                escritor.espaco(18);
            }
            if (!dados.responsabilidadesSemComissao().isEmpty()) {
                escritor.secao("Responsabilidades sem comissão", dados.responsabilidadesSemComissao().size());
                for (Responsabilidade responsabilidade : dados.responsabilidadesSemComissao()) {
                    escritor.marcador(valor(responsabilidade.nome(), "Responsabilidade sem nome") + "  |  " + status(responsabilidade.status()));
                    if (preenchido(responsabilidade.descricao())) {
                        escritor.detalhe("Descrição", responsabilidade.descricao());
                    }
                    if (preenchido(responsabilidade.observacao())) {
                        escritor.detalhe("Observação", responsabilidade.observacao());
                    }
                    escritor.espaco(6);
                }
            }
            escritor.textoDiscreto("Total de vínculos aluno/comissão: " + totalVinculos + ". Um mesmo aluno pode integrar mais de uma comissão.");
        }
    }

    private static boolean preenchido(String texto) {
        return texto != null && !texto.isBlank();
    }

    private static String valor(String texto, String padrao) {
        return preenchido(texto) ? texto.trim() : padrao;
    }

    private static String status(String valor) {
        if (valor == null) return "Não informado";
        return switch (valor) {
            case "CONCLUIDA" -> "Concluída";
            case "PLANEJADA" -> "Planejada";
            case "PENDENTE" -> "Pendente";
            case "ABERTA" -> "Aberta";
            case "EM_ANDAMENTO" -> "Em andamento";
            case "CANCELADA" -> "Cancelada";
            case "CONCLUIDO" -> "Concluído";
            default -> valor.replace('_', ' ');
        };
    }

    private static String periodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null && fim == null) return "Não informado";
        if (inicio == null) return "Até " + fim.format(FORMATO_DATA);
        if (fim == null) return inicio.format(FORMATO_DATA);
        return inicio.format(FORMATO_DATA) + " até " + fim.format(FORMATO_DATA);
    }

    private static final class Escritor implements AutoCloseable {
        private static final float MARGEM = 46f;
        private static final float LIMITE_INFERIOR = 67f;
        private static final float LARGURA = PDRectangle.A4.getWidth() - 2 * MARGEM;
        private static final PDFont NORMAL = PDType1Font.HELVETICA;
        private static final PDFont NEGRITO = PDType1Font.HELVETICA_BOLD;
        private final PDDocument documento;
        private final EventoOpcao evento;
        private PDPageContentStream conteudo;
        private float y;
        Escritor(PDDocument documento, EventoOpcao evento) throws IOException {
            this.documento = documento;
            this.evento = evento;
            novaPagina();
        }
        private void novaPagina() throws IOException {
            finalizarPagina();
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);
            conteudo = new PDPageContentStream(documento, pagina);
            float altura = PDRectangle.A4.getHeight();

            // Faixa de identificação, independente do tema claro/escuro da interface.
            preencher(0, altura - 82, PDRectangle.A4.getWidth(), 82, 15, 23, 42);
            escrever("e-task  /  RELATÓRIO DE DESEMPENHO", NEGRITO, 12, MARGEM, altura - 30, 255, 255, 255);
            String nome = valor(evento.getNome(), "Evento sem nome");
            List<String> linhas = quebrar(nome, NORMAL, 10, LARGURA);
            float linhaY = altura - 51;
            for (int i = 0; i < Math.min(2, linhas.size()); i++) {
                escrever(linhas.get(i), NORMAL, 10, MARGEM, linhaY, 216, 226, 241);
                linhaY -= 12;
            }
            y = altura - 106;
        }
        private void finalizarPagina() throws IOException {
            if (conteudo == null) return;
            float larguraPagina = PDRectangle.A4.getWidth();
            conteudo.setStrokingColor(220, 226, 235);
            conteudo.moveTo(MARGEM, 51);
            conteudo.lineTo(larguraPagina - MARGEM, 51);
            conteudo.stroke();
            escrever("Documento gerado por e-task", NORMAL, 8, MARGEM, 36, 100, 116, 139);
            String pagina = "Página " + documento.getNumberOfPages();
            escrever(pagina, NORMAL, 8, larguraPagina - MARGEM - medir(NORMAL, 8, pagina), 36, 100, 116, 139);
            conteudo.close();
            conteudo = null;
        }
        private void garantir(float alturaNecessaria) throws IOException {
            if (y - alturaNecessaria < LIMITE_INFERIOR) novaPagina();
        }
        void espaco(float pontos) throws IOException {
            garantir(pontos);
            y -= pontos;
        }
        void secao(String titulo, Integer total) throws IOException {
            garantir(49);
            y -= 7;
            preencher(MARGEM, y - 29, LARGURA, 32, 233, 241, 253);
            preencher(MARGEM, y - 29, 4, 32, 37, 99, 235);
            escrever(titulo, NEGRITO, 11, MARGEM + 13, y - 17, 30, 64, 125);
            if (total != null) {
                String contagem = "Total: " + total;
                escrever(contagem, NORMAL, 9, MARGEM + LARGURA - medir(NORMAL, 9, contagem) - 12, y - 17, 71, 85, 105);
            }
            y -= 47;
        }
        void subtitulo(String titulo, int total) throws IOException {
            garantir(38);
            y -= 10;
            String texto = titulo + " (" + total + ")";
            escrever(texto, NEGRITO, 10, MARGEM + 12, y - 5, 37, 99, 235);
            y -= 23;
        }
        void cardsResumo(int atividades, int comissoes, int alunos, int responsabilidades)
                throws IOException {
            garantir(82);
            String[] rotulos = {"Atividades", "Comissões", "Alunos únicos", "Responsabilidades"};
            String[] numeros = {"" + atividades, "" + comissoes, "" + alunos, "" + responsabilidades};
            float intervalo = 9;
            float larguraCard = (LARGURA - 3 * intervalo) / 4;
            for (int i = 0; i < rotulos.length; i++) {
                float x = MARGEM + i * (larguraCard + intervalo);
                preencher(x, y - 59, larguraCard, 60, 247, 250, 255);
                contornar(x, y - 59, larguraCard, 60, 221, 231, 246);
                escrever(numeros[i], NEGRITO, 20, x + 10, y - 27, 37, 99, 235);
                escrever(rotulos[i], NORMAL, 8, x + 10, y - 45, 71, 85, 105);
            }
            y -= 78;
        }
        void cabecalhoComissao(String nome, String situacao) throws IOException {
            List<String> linhas = quebrar(nome, NEGRITO, 11, LARGURA - 32);
            float altura = Math.max(46, 20 + linhas.size() * 14);
            garantir(altura + 16);
            preencher(MARGEM, y - altura, LARGURA, altura, 30, 64, 125);
            float yy = y - 18;
            for (String linha : linhas) {
                escrever(linha, NEGRITO, 11, MARGEM + 12, yy, 255, 255, 255);
                yy -= 14;
            }
            escrever("Situação: " + situacao, NORMAL, 9, MARGEM + 12, y - altura + 12, 215, 229, 250);
            y -= altura + 9;
        }
        void item(String titulo, String situacao) throws IOException {
            List<String> linhas = quebrar(titulo, NEGRITO, 10, LARGURA - 34);
            float altura = Math.max(39, 23 + linhas.size() * 13);
            garantir(altura + 6);
            preencher(MARGEM + 3, y - altura + 2, 3, altura - 2, 37, 99, 235);
            float yy = y - 11;
            for (String linha : linhas) {
                escrever(linha, NEGRITO, 10, MARGEM + 14, yy, 15, 23, 42);
                yy -= 13;
            }
            escrever(situacao, NORMAL, 9, MARGEM + 14, y - altura + 9, 71, 85, 105);
            y -= altura + 3;
        }
        void marcador(String texto) throws IOException {
            List<String> linhas = quebrar(texto, NEGRITO, 9, LARGURA - 31);
            garantir(14);
            escrever("-", NEGRITO, 10, MARGEM + 12, y - 1, 37, 99, 235);
            for (String linha : linhas) {
                garantir(14);
                escrever(linha, NEGRITO, 9, MARGEM + 27, y - 1, 31, 41, 55);
                y -= 14;
            }
            y -= 2;
        }
        void detalhe(String rotulo, String texto) throws IOException {
            bloco(rotulo + ": " + valor(texto, "Não informado"), NORMAL, 9, MARGEM + 25, LARGURA - 34, 13, 71, 85, 105);
        }
        void vazio(String texto) throws IOException {
            bloco(texto, NORMAL, 9, MARGEM + 13, LARGURA - 26, 14, 112, 128, 150);
            y -= 4;
        }
        void textoDiscreto(String texto) throws IOException {
            espaco(4);
            bloco(texto, NORMAL, 8, MARGEM + 6, LARGURA - 12, 12, 100, 116, 139);
            espaco(12);
        }
        private void bloco(String texto, PDFont fonte, int tamanho, float x, float largura,
                           float alturaLinha, int r, int g, int b) throws IOException {
            for (String linha : quebrar(texto, fonte, tamanho, largura)) {
                garantir(alturaLinha);
                escrever(linha, fonte, tamanho, x, y - 1, r, g, b);
                y -= alturaLinha;
            }
        }
        private void preencher(float x, float y, float largura, float altura,
                               int r, int g, int b) throws IOException {
            conteudo.setNonStrokingColor(r, g, b);
            conteudo.addRect(x, y, largura, altura);
            conteudo.fill();
        }
        private void contornar(float x, float y, float largura, float altura,
                               int r, int g, int b) throws IOException {
            conteudo.setStrokingColor(r, g, b);
            conteudo.addRect(x, y, largura, altura);
            conteudo.stroke();
        }
        private void escrever(String texto, PDFont fonte, int tamanho, float x, float y,
                              int r, int g, int b) throws IOException {
            conteudo.setNonStrokingColor(r, g, b);
            conteudo.beginText();
            conteudo.setFont(fonte, tamanho);
            conteudo.newLineAtOffset(x, y);
            conteudo.showText(sanitizar(texto));
            conteudo.endText();
        }
        private static float medir(PDFont fonte, int tamanho, String texto) throws IOException {
            return fonte.getStringWidth(sanitizar(texto)) * tamanho / 1000f;
        }

        // Quebra linhas por largura real da fonte, inclusive palavras muito longas.
        private static List<String> quebrar(String texto, PDFont fonte, int tamanho, float largura)
                throws IOException {
            String normalizado = sanitizar(texto).replace('\r', ' ').replace('\t', ' ');
            List<String> linhas = new ArrayList<>();
            for (String paragrafo : normalizado.split("\\n", -1)) {
                StringBuilder linha = new StringBuilder();
                for (String palavra : paragrafo.trim().split(" +")) {
                    if (palavra.isEmpty()) continue;
                    String candidato = linha.isEmpty() ? palavra : linha + " " + palavra;
                    if (medir(fonte, tamanho, candidato) <= largura) {
                        linha.setLength(0);
                        linha.append(candidato);
                        continue;
                    }
                    if (!linha.isEmpty()) {
                        linhas.add(linha.toString());
                        linha.setLength(0);
                    }
                    // Se uma palavra isolada for maior que a coluna, quebrar em caracteres.
                    for (int i = 0; i < palavra.length(); i++) {
                        String proximo = linha.toString() + palavra.charAt(i);
                        if (medir(fonte, tamanho, proximo) > largura && !linha.isEmpty()) {
                            linhas.add(linha.toString());
                            linha.setLength(0);
                        }
                        linha.append(palavra.charAt(i));
                    }
                }
                linhas.add(linha.toString());
            }
            return linhas;
        }

        // Mantém acentos portugueses e substitui símbolos fora do WinAnsi/Helvetica.
        private static String sanitizar(String texto) {
            if (texto == null) return "";
            String entrada = texto.replace('–', '-').replace('—', '-')
                    .replace('“', '"').replace('”', '"') .replace('‘', '\'').replace('’', '\'') .replace('•', '-').replace('\u00a0', ' ');
            StringBuilder saida = new StringBuilder();
            entrada.codePoints().forEach(cp -> {
                if (cp == '\n' || cp == '\r' || cp == '\t') {
                    saida.appendCodePoint(cp);
                } else if ((cp >= 32 && cp <= 126) || (cp >= 160 && cp <= 255)) {
                    saida.appendCodePoint(cp);
                } else if (cp > 255) {
                    String decomposicao = Normalizer.normalize(
                            new String(Character.toChars(cp)), Normalizer.Form.NFD)
                            .replaceAll("\\p{M}", "");
                    for (int i = 0; i < decomposicao.length(); i++) {
                        char ch = decomposicao.charAt(i);
                        saida.append(ch >= 32 && ch <= 255 ? ch : '?');
                    }
                }
            });
            return saida.toString();
        }
        @Override
        public void close() throws IOException {
            finalizarPagina();
        }
    }
}
