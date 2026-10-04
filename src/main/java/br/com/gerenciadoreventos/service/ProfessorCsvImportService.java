package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.ProfessorDAO;
import br.com.gerenciadoreventos.model.Professor;
import br.com.gerenciadoreventos.model.ProfessorCsvImportacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.*;

public class ProfessorCsvImportService {

    private final ProfessorDAO professorDAO;

    public ProfessorCsvImportService() {
        professorDAO = new ProfessorDAO();
    }

    public List<ProfessorCsvImportacao> analisar(File arquivo) throws IOException {
        List<ProfessorCsvImportacao> resultado = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(arquivo.toPath(), StandardCharsets.UTF_8)) {
            String cabecalhoBruto = reader.readLine();

            if (cabecalhoBruto == null || cabecalhoBruto.isBlank()) {
                throw new IllegalArgumentException("O arquivo CSV está vazio.");
            }

            cabecalhoBruto = removerBom(cabecalhoBruto);
            char delimitador = detectarDelimitador(cabecalhoBruto);
            List<String> cabecalhos = separarLinha(cabecalhoBruto, delimitador);
            Map<String, Integer> colunas = mapearColunas(cabecalhos);

            Integer colNome = localizarColuna(colunas, "nome", "nome completo", "professor");
            Integer colEmail = localizarColuna(colunas, "email", "e mail");
            Integer colTelefone = localizarColuna(colunas, "telefone", "celular", "whatsapp");
            Integer colArea = localizarColuna(colunas, "area", "area de atuacao", "area atuacao", "disciplina");

            if (colNome == null) {
                throw new IllegalArgumentException(
                        "Coluna obrigatória ausente: Nome.\n\n" +
                                "Use, por exemplo: Nome,Email,Telefone,Area de Atuacao"
                );
            }

            Set<String> chavesNoArquivo = new HashSet<>();
            String linhaBruta;
            int numeroLinha = 1;

            while ((linhaBruta = reader.readLine()) != null) {
                numeroLinha++;
                if (linhaBruta.isBlank()) continue;

                List<String> valores = separarLinha(linhaBruta, delimitador);

                String nome = valor(valores, colNome).trim();
                String email = valor(valores, colEmail).trim();
                String telefone = valor(valores, colTelefone).trim();
                String area = valor(valores, colArea).trim();

                Professor professor = new Professor();
                professor.setNome(nome);
                professor.setEmail(email);
                professor.setTelefone(telefone);
                professor.setAreaAtuacao(area);
                professor.setAtivo(true);

                String problema = null;

                if (nome.isBlank()) {
                    problema = "Nome não informado";
                } else if (!email.isBlank() && !emailValido(email)) {
                    problema = "E-mail inválido";
                }

                String chave = criarChave(nome, email, telefone, area);

                if (problema == null && !chavesNoArquivo.add(chave)) {
                    problema = "Professor repetido no próprio CSV";
                }

                if (problema == null && professorDAO.existeProfessorCadastrado(nome, email, telefone, area)) {
                    problema = !email.isBlank()
                            ? "E-mail/professor já cadastrado"
                            : "Professor já cadastrado";
                }

                resultado.add(new ProfessorCsvImportacao(
                        numeroLinha,
                        professor,
                        problema == null,
                        problema == null ? "Pronto para importar" : problema
                ));
            }
        }

        return resultado;
    }

    public int importar(List<ProfessorCsvImportacao> linhas) {
        int importados = 0;

        for (ProfessorCsvImportacao linha : linhas) {
            if (!linha.isValido()) continue;

            Professor professor = linha.getProfessor();

            // Revalida antes de gravar, caso o banco tenha mudado entre o preview e a confirmação.
            if (professorDAO.existeProfessorCadastrado(
                    professor.getNome(),
                    professor.getEmail(),
                    professor.getTelefone(),
                    professor.getAreaAtuacao()
            )) {
                continue;
            }

            if (professorDAO.cadastrarProfessor(professor)) {
                importados++;
            }
        }

        return importados;
    }

    private boolean emailValido(String email) {
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private String criarChave(String nome, String email, String telefone, String area) {
        if (!email.isBlank()) {
            return "email:" + normalizar(email);
        }

        if (!telefone.isBlank()) {
            return "nome-telefone:" + normalizar(nome) + "|" + somenteDigitos(telefone);
        }

        return "nome-area:" + normalizar(nome) + "|" + normalizar(area);
    }

    private String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D+", "");
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
                String normalizadoAlias = normalizar(alias);
                if (entry.getKey().contains(normalizadoAlias)) {
                    return entry.getValue();
                }
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
        int melhorQuantidade = -1;
        char melhor = ',';

        for (char candidato : candidatos) {
            int quantidade = contarDelimitadores(linha, candidato);
            if (quantidade > melhorQuantidade) {
                melhorQuantidade = quantidade;
                melhor = candidato;
            }
        }

        return melhor;
    }

    private int contarDelimitadores(String linha, char delimitador) {
        boolean aspas = false;
        int total = 0;

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
        List<String> valores = new ArrayList<>();
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
                valores.add(atual.toString().trim());
                atual.setLength(0);
            } else {
                atual.append(c);
            }
        }

        valores.add(atual.toString().trim());
        return valores;
    }

    private String removerBom(String valor) {
        if (!valor.isEmpty() && valor.charAt(0) == '\uFEFF') {
            return valor.substring(1);
        }
        return valor;
    }

    private String normalizar(String texto) {
        if (texto == null) return "";

        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        return semAcento
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9@._+-]+", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }
}
