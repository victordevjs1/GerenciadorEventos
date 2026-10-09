package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.AlunoDAO;
import br.com.gerenciadoreventos.dao.InscricaoDAO;
import br.com.gerenciadoreventos.model.Aluno;
import br.com.gerenciadoreventos.model.Evento;
import br.com.gerenciadoreventos.model.InscricaoCsvImportacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.*;

public class InscricaoCsvImportService {
    private final AlunoDAO alunoDAO = new AlunoDAO();
    private final InscricaoDAO inscricaoDAO = new InscricaoDAO();

    public List<InscricaoCsvImportacao> analisar(File arquivo, Evento evento) throws IOException {
        if (evento == null) {
            throw new IllegalArgumentException("Selecione um evento antes de analisar o CSV.");
        }
        List<InscricaoCsvImportacao> resultado = new ArrayList<>();
        int inscritosAtuais = inscricaoDAO.contarInscricoesAtivasEvento(evento.getId());
        int vagasReservadasNoCsv = 0;
        try (BufferedReader reader = Files.newBufferedReader(arquivo.toPath(), StandardCharsets.UTF_8)) {
            String cabecalhoBruto = reader.readLine();
            if (cabecalhoBruto == null || cabecalhoBruto.isBlank()) {
                throw new IllegalArgumentException("O arquivo CSV está vazio.");
            }
            cabecalhoBruto = removerBom(cabecalhoBruto);
            char delimitador = detectarDelimitador(cabecalhoBruto);
            List<String> cabecalhos = separarLinha(cabecalhoBruto, delimitador);
            Map<String, Integer> colunas = mapearColunas(cabecalhos);
            Integer colRm = localizarColuna(colunas, "rm", "r m", "registro matricula", "registro de matricula");
            Integer colNome = localizarColuna(colunas, "nome", "nome completo", "aluno");
            Integer colObservacao = localizarColuna(colunas, "observacao", "observações", "observacoes", "comentario", "comentários");
            if (colRm == null) {
                throw new IllegalArgumentException( "Coluna obrigatória ausente: RM.\n\nUse, por exemplo: RM,Nome,Observacao" );
            }
            Set<String> rmsNoArquivo = new HashSet<>();
            String linhaBruta;
            int numeroLinha = 1;
            while ((linhaBruta = reader.readLine()) != null) {
                numeroLinha++;
                if (linhaBruta.isBlank()) continue;
                List<String> valores = separarLinha(linhaBruta, delimitador);
                String rm = valor(valores, colRm).trim();
                String nomeCsv = valor(valores, colNome).trim();
                String observacao = valor(valores, colObservacao).trim();
                Aluno aluno = null;
                String problema = null;
                String chaveRm = normalizarRm(rm);
                if (rm.isBlank()) {
                    problema = "RM não informado";
                } else if (!rmsNoArquivo.add(chaveRm)) {
                    problema = "RM repetido no próprio CSV";
                } else {
                    aluno = alunoDAO.buscarPorRm(rm);
                    if (aluno == null) {
                        problema = "RM não encontrado no cadastro de alunos";
                    } else if (!aluno.isAtivo()) {
                        problema = "Aluno está inativo";
                    } else if (!inscricaoDAO.alunoPertenceAoPublicoEvento(aluno.getId(), evento.getId())) {
                        problema = "Aluno fora do público deste evento";
                    } else {
                        String statusAtual = inscricaoDAO.buscarStatusInscricao(aluno.getId(), evento.getId());
                        if (statusAtual != null && !"CANCELADO".equalsIgnoreCase(statusAtual)) {
                            problema = "Aluno já inscrito neste evento";
                        } else if (evento.getCapacidade() > 0
                                && inscritosAtuais + vagasReservadasNoCsv >= evento.getCapacidade()) {
                            problema = "Capacidade do evento esgotada";
                        }
                    }
                }
                boolean valido = problema == null;
                if (valido) {
                    vagasReservadasNoCsv++;
                    if (nomeCsv.isBlank() && aluno != null) {
                        nomeCsv = aluno.getNome();
                    }
                }
                resultado.add(new InscricaoCsvImportacao(
                        numeroLinha, rm, nomeCsv, observacao, aluno, valido, valido ? "Pronto para inscrever" : problema ));
            }
        }
        return resultado;
    }

    public int importar(List<InscricaoCsvImportacao> linhas, Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("Evento não informado.");
        }
        int importados = 0;
        for (InscricaoCsvImportacao linha : linhas) {
            if (!linha.isValido() || linha.getAluno() == null) continue;
            boolean sucesso = inscricaoDAO.cadastrarOuReativarInscricao(
                    linha.getAluno().getId(), evento.getId(), linha.getObservacao().isBlank() ? null : linha.getObservacao() );
            if (sucesso) importados++;
        }
        return importados;
    }

    private Map<String, Integer> mapearColunas(List<String> cabecalhos) {
        Map<String, Integer> colunas = new LinkedHashMap<>();
        for (int i = 0; i < cabecalhos.size(); i++) {
            colunas.put(normalizar(cabecalhos.get(i)), i);
        }
        return colunas;
    }

    private Integer localizarColuna(Map<String, Integer> colunas, String... aliases) {
        for (String alias : aliases) {
            Integer indice = colunas.get(normalizar(alias));
            if (indice != null) return indice;
        }
        for (Map.Entry<String, Integer> entry : colunas.entrySet()) {
            for (String alias : aliases) {
                String normalizado = normalizar(alias);
                if (entry.getKey().contains(normalizado)) return entry.getValue();
            }
        }
        return null;
    }

    private String valor(List<String> valores, Integer indice) {
        if (indice == null || indice < 0 || indice >= valores.size()) return "";
        return valores.get(indice);
    }

    private char detectarDelimitador(String linha) {
        char[] candidatos = {';', ',', '\t'};
        char melhor = ',';
        int maior = -1;
        for (char candidato : candidatos) {
            int qtd = contarDelimitadores(linha, candidato);
            if (qtd > maior) {
                maior = qtd;
                melhor = candidato;
            }
        }
        return melhor;
    }

    private int contarDelimitadores(String linha, char delimitador) {
        int total = 0;
        boolean aspas = false;
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"') {
                if (aspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                    i++;
                } else {
                    aspas = !aspas;
                }
            } else if (c == delimitador && !aspas) {
                total++;
            }
        }
        return total;
    }

    private List<String> separarLinha(String linha, char delimitador) {
        List<String> campos = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        boolean aspas = false;
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"') {
                if (aspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                    atual.append('"');
                    i++;
                } else {
                    aspas = !aspas;
                }
            } else if (c == delimitador && !aspas) {
                campos.add(atual.toString());
                atual.setLength(0);
            } else {
                atual.append(c);
            }
        }
        campos.add(atual.toString());
        return campos;
    }

    private String removerBom(String texto) {
        if (texto != null && !texto.isEmpty() && texto.charAt(0) == '\uFEFF') {
            return texto.substring(1);
        }
        return texto;
    }

    private String normalizarRm(String rm) {
        return rm == null ? "" : rm.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcentos.toLowerCase(Locale.ROOT) .replaceAll("[^a-z0-9]+", " ") .trim();
    }
}
