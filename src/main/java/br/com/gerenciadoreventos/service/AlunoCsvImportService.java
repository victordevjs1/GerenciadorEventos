package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.AlunoDAO;
import br.com.gerenciadoreventos.dao.CursoDAO;
import br.com.gerenciadoreventos.dao.SerieDAO;
import br.com.gerenciadoreventos.model.Aluno;
import br.com.gerenciadoreventos.model.AlunoCsvImportacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class AlunoCsvImportService {

    private final AlunoDAO alunoDAO;
    private final CursoDAO cursoDAO;
    private final SerieDAO serieDAO;

    public AlunoCsvImportService() {
        alunoDAO = new AlunoDAO();
        cursoDAO = new CursoDAO();
        serieDAO = new SerieDAO();
    }

    public List<AlunoCsvImportacao> analisar(File arquivo) throws IOException {
        List<AlunoCsvImportacao> resultado = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(arquivo.toPath(), StandardCharsets.UTF_8)) {
            String cabecalhoBruto = reader.readLine();
            if (cabecalhoBruto == null || cabecalhoBruto.isBlank()) {
                throw new IllegalArgumentException("O arquivo CSV está vazio.");
            }

            cabecalhoBruto = removerBom(cabecalhoBruto);
            char delimitador = detectarDelimitador(cabecalhoBruto);
            List<String> cabecalhos = separarLinha(cabecalhoBruto, delimitador);
            Map<String, Integer> colunas = mapearColunas(cabecalhos);

            Integer colRm = localizarColuna(colunas, "rm", "r m");
            Integer colNome = localizarColuna(colunas, "nome", "nome completo", "aluno");
            Integer colCurso = localizarColuna(colunas, "curso");
            Integer colSerie = localizarColuna(colunas, "serie", "ano", "serie ano");
            Integer colData = localizarColuna(colunas, "data nascimento", "data de nascimento", "nascimento");
            Integer colEmail = localizarColuna(colunas, "email", "e mail");
            Integer colTelefone = localizarColuna(colunas, "telefone", "celular", "whatsapp");

            List<String> faltando = new ArrayList<>();
            if (colRm == null) faltando.add("RM");
            if (colNome == null) faltando.add("Nome");
            if (colCurso == null) faltando.add("Curso");
            if (colSerie == null) faltando.add("Serie");

            if (!faltando.isEmpty()) {
                throw new IllegalArgumentException(
                        "Colunas obrigatórias ausentes: " + String.join(", ", faltando)
                                + ".\n\nUse, por exemplo: RM,Nome,Curso,Serie"
                );
            }

            Map<String, String> cursosCanonicos = new HashMap<>();
            for (String curso : cursoDAO.listarNomesAtivos()) {
                cursosCanonicos.put(normalizar(curso), curso);
            }

            Set<String> rmsNoArquivo = new HashSet<>();
            String linhaBruta;
            int numeroLinha = 1;

            while ((linhaBruta = reader.readLine()) != null) {
                numeroLinha++;
                if (linhaBruta.isBlank()) continue;

                List<String> valores = separarLinha(linhaBruta, delimitador);

                String rm = valor(valores, colRm).trim();
                String nome = valor(valores, colNome).trim();
                String cursoInformado = valor(valores, colCurso).trim();
                String serieInformada = valor(valores, colSerie).trim();
                String dataInformada = valor(valores, colData).trim();
                String email = valor(valores, colEmail).trim();
                String telefone = valor(valores, colTelefone).trim();

                Aluno aluno = new Aluno();
                aluno.setRm(rm);
                aluno.setNome(nome);
                aluno.setEmail(email);
                aluno.setTelefone(telefone);
                aluno.setAtivo(true);

                String problema = null;

                if (rm.isBlank()) {
                    problema = "RM não informado";
                } else if (nome.isBlank()) {
                    problema = "Nome não informado";
                } else if (!rmsNoArquivo.add(normalizarRm(rm))) {
                    problema = "RM repetido no próprio CSV";
                } else if (alunoDAO.buscarPorRm(rm) != null) {
                    problema = "RM já cadastrado";
                }

                String cursoCanonico = cursosCanonicos.get(normalizar(cursoInformado));
                if (problema == null && cursoCanonico == null) {
                    problema = "Curso não encontrado ou inativo";
                }

                Integer serie = null;
                if (problema == null) {
                    serie = extrairNumeroSerie(serieInformada);
                    if (serie == null) {
                        problema = "Série inválida";
                    } else {
                        Integer numeroNoBanco = buscarSerie(cursoCanonico, serie, serieInformada);
                        if (numeroNoBanco == null) {
                            problema = "Série não pertence ao curso informado";
                        } else {
                            serie = numeroNoBanco;
                        }
                    }
                }

                if (problema == null) {
                    aluno.setCurso(cursoCanonico);
                    aluno.setSerie(serie);
                    aluno.setAnoConclusao(calcularAnoConclusao(serie));

                    if (!dataInformada.isBlank()) {
                        LocalDate data = converterData(dataInformada);
                        if (data == null) {
                            problema = "Data de nascimento inválida";
                        } else {
                            aluno.setDataNascimento(data.toString());
                        }
                    }
                }

                resultado.add(new AlunoCsvImportacao(
                        numeroLinha,
                        aluno,
                        problema == null,
                        problema == null ? "Pronto para importar" : problema
                ));
            }
        }

        return resultado;
    }

    public int importar(List<AlunoCsvImportacao> linhas) {
        int importados = 0;

        for (AlunoCsvImportacao linha : linhas) {
            if (!linha.isValido()) continue;

            if (alunoDAO.cadastrarAluno(linha.getAluno())) {
                importados++;
            }
        }

        return importados;
    }

    private Integer buscarSerie(String curso, int numero, String textoOriginal) {
        for (String nomeSerie : serieDAO.listarNomesAtivosPorCurso(curso)) {
            Integer numeroBanco = serieDAO.buscarNumeroPorCursoENome(curso, nomeSerie);
            if (numeroBanco != null && numeroBanco == numero) {
                return numeroBanco;
            }

            if (normalizar(nomeSerie).equals(normalizar(textoOriginal))) {
                return numeroBanco;
            }
        }
        return null;
    }

    private int calcularAnoConclusao(int serie) {
        return Year.now().getValue() + (3 - serie);
    }

    private LocalDate converterData(String texto) {
        String valor = texto.trim();
        DateTimeFormatter[] formatos = {
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("d/M/yyyy"),
                DateTimeFormatter.ISO_LOCAL_DATE
        };

        for (DateTimeFormatter formato : formatos) {
            try {
                return LocalDate.parse(valor, formato);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    private Integer extrairNumeroSerie(String texto) {
        if (texto == null) return null;
        String normalizado = normalizar(texto);

        for (char c : normalizado.toCharArray()) {
            if (c >= '1' && c <= '3') {
                return c - '0';
            }
        }

        if (normalizado.contains("primeir")) return 1;
        if (normalizado.contains("segund")) return 2;
        if (normalizado.contains("terceir")) return 3;
        return null;
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
                String a = normalizar(alias);
                if (entry.getKey().contains(a)) {
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

    private String normalizarRm(String rm) {
        return rm == null ? "" : rm.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return semAcento
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }
}
