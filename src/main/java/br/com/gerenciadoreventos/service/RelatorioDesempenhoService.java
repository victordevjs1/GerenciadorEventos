package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.RelatorioDAO;
import br.com.gerenciadoreventos.database.Conexao;
import br.com.gerenciadoreventos.model.AlunoStatus;
import br.com.gerenciadoreventos.model.EventoDesempenho;
import br.com.gerenciadoreventos.model.EventoOpcao;
import br.com.gerenciadoreventos.model.PontoComparecimento;
import br.com.gerenciadoreventos.model.ResumoGeral;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDesempenhoService {

    private final RelatorioDAO dao =
            new RelatorioDAO();

    // =====================================================
    // RESUMO GERAL
    // =====================================================

    public ResumoGeral buscarResumoGeral() {
        return dao.buscarResumoGeral();
    }

    // =====================================================
    // DESEMPENHO DO EVENTO
    // =====================================================

    public EventoDesempenho buscarDesempenhoEvento(
            long idEvento
    ) {
        return dao.buscarDesempenhoEvento(idEvento);
    }

    // =====================================================
    // COMPARECIMENTO POR DATA
    // =====================================================

    public List<PontoComparecimento> buscarComparecimentoPorData(
            long idEvento
    ) {
        return dao.buscarComparecimentoPorData(idEvento);
    }

    // =====================================================
    // STATUS DOS ALUNOS
    // =====================================================

    public List<AlunoStatus> listarStatusAlunos(
            long idEvento
    ) {
        return dao.listarStatusAlunos(idEvento);
    }

    // =====================================================
    // EXPORTAR INSCRIÇÕES DO EVENTO
    // =====================================================

    public void exportarInscricoesEvento(
            File destino,
            EventoOpcao evento
    ) throws IOException {

        if (destino == null) {
            throw new IllegalArgumentException(
                    "Arquivo de destino não informado."
            );
        }

        if (evento == null) {
            throw new IllegalArgumentException(
                    "Evento não informado."
            );
        }

        List<AlunoInscricaoPdf> alunos =
                buscarInscricoesEvento(evento.getId());

        try (PDDocument documento = new PDDocument()) {

            int indiceAluno = 0;

            boolean primeiraPagina = true;

            while (
                    primeiraPagina
                            || indiceAluno < alunos.size()
            ) {

                PDPage pagina =
                        new PDPage(PDRectangle.A4);

                documento.addPage(pagina);

                try (
                        PDPageContentStream conteudo =
                                new PDPageContentStream(
                                        documento,
                                        pagina
                                )
                ) {

                    float margem = 50;

                    float larguraPagina =
                            pagina.getMediaBox().getWidth();

                    float alturaPagina =
                            pagina.getMediaBox().getHeight();

                    float largura =
                            larguraPagina - (2 * margem);

                    float y =
                            alturaPagina - margem;

                    // =====================================
                    // CABEÇALHO
                    // =====================================

                    escreverTexto(
                            conteudo,
                            PDType1Font.HELVETICA_BOLD,
                            18,
                            margem,
                            y,
                            "Relatório de Inscrições"
                    );

                    y -= 25;

                    escreverTexto(
                            conteudo,
                            PDType1Font.HELVETICA,
                            11,
                            margem,
                            y,
                            "Evento: " + evento
                    );

                    y -= 17;

                    escreverTexto(
                            conteudo,
                            PDType1Font.HELVETICA,
                            10,
                            margem,
                            y,
                            montarPeriodoEvento(evento)
                    );

                    y -= 17;

                    escreverTexto(
                            conteudo,
                            PDType1Font.HELVETICA,
                            10,
                            margem,
                            y,
                            "Total de inscritos: "
                                    + alunos.size()
                    );

                    y -= 30;

                    // =====================================
                    // TABELA
                    // =====================================

                    float[] larguras = {
                            largura * 0.15f,
                            largura * 0.55f,
                            largura * 0.30f
                    };

                    String[] cabecalho = {
                            "RM",
                            "Aluno",
                            "Status"
                    };

                    float alturaLinha = 24;

                    y = desenharLinha(
                            conteudo,
                            margem,
                            y,
                            larguras,
                            cabecalho,
                            true,
                            alturaLinha
                    );

                    // =====================================
                    // ALUNOS
                    // =====================================

                    while (indiceAluno < alunos.size()) {

                        // Espaço mínimo para mais uma linha
                        if (y < 65) {
                            break;
                        }

                        AlunoInscricaoPdf aluno =
                                alunos.get(indiceAluno);

                        String[] valores = {
                                aluno.rm,
                                aluno.nome,
                                aluno.status
                        };

                        y = desenharLinha(
                                conteudo,
                                margem,
                                y,
                                larguras,
                                valores,
                                false,
                                alturaLinha
                        );

                        indiceAluno++;
                    }

                    // =====================================
                    // RODAPÉ
                    // =====================================

                    escreverTexto(
                            conteudo,
                            PDType1Font.HELVETICA,
                            8,
                            margem,
                            30,
                            "Documento gerado pelo sistema de gerenciamento de eventos."
                    );

                    escreverTexto(
                            conteudo,
                            PDType1Font.HELVETICA,
                            8,
                            larguraPagina - margem - 45,
                            30,
                            "Página " + documento.getNumberOfPages()
                    );
                }

                primeiraPagina = false;
            }

            documento.save(destino);
        }
    }
    // =====================================================
// PÁGINA DE STATUS DOS ALUNOS
// =====================================================

    private void criarPaginaStatusAlunos(
            PDDocument documento,
            EventoOpcao evento,
            List<AlunoStatus> alunos
    ) throws IOException {

        PDPage pagina =
                new PDPage(
                        PDRectangle.A4
                );

        documento.addPage(pagina);

        try (
                PDPageContentStream conteudo =
                        new PDPageContentStream(
                                documento,
                                pagina
                        )
        ) {

            float margem = 50;

            float larguraPagina =
                    pagina.getMediaBox()
                            .getWidth();

            float alturaPagina =
                    pagina.getMediaBox()
                            .getHeight();

            float largura =
                    larguraPagina
                            - (2 * margem);

            float y =
                    alturaPagina - margem;

            escreverTexto(
                    conteudo,
                    PDType1Font.HELVETICA_BOLD,
                    18,
                    margem,
                    y,
                    "Status dos Alunos"
            );

            y -= 25;

            escreverTexto(
                    conteudo,
                    PDType1Font.HELVETICA,
                    10,
                    margem,
                    y,
                    "Evento: " + evento
            );

            y -= 30;

            float alturaLinha = 24;

            float[] larguras = {
                    largura * 0.65f,
                    largura * 0.35f
            };

            y = desenharLinha(
                    conteudo,
                    margem,
                    y,
                    larguras,
                    new String[]{
                            "Aluno",
                            "Situação"
                    },
                    true,
                    alturaLinha
            );

            for (
                    AlunoStatus aluno :
                    alunos
            ) {

                if (y < 60) {

                    escreverTexto(
                            conteudo,
                            PDType1Font.HELVETICA,
                            8,
                            margem,
                            30,
                            "Página "
                                    + documento.getNumberOfPages()
                    );

                    pagina =
                            new PDPage(
                                    PDRectangle.A4
                            );

                    documento.addPage(pagina);

                    y =
                            alturaPagina
                                    - margem;

                    try (
                            PDPageContentStream novaPagina =
                                    new PDPageContentStream(
                                            documento,
                                            pagina
                                    )
                    ) {

                        escreverTexto(
                                novaPagina,
                                PDType1Font.HELVETICA_BOLD,
                                15,
                                margem,
                                y,
                                "Status dos Alunos - continuação"
                        );

                        y -= 25;

                        y = desenharLinha(
                                novaPagina,
                                margem,
                                y,
                                larguras,
                                new String[]{
                                        "Aluno",
                                        "Situação"
                                },
                                true,
                                alturaLinha
                        );

                        for (
                                AlunoStatus restante :
                                alunos.subList(
                                        alunos.indexOf(aluno),
                                        alunos.size()
                                )
                        ) {

                            if (y < 60) {
                                break;
                            }

                            y = desenharLinha(
                                    novaPagina,
                                    margem,
                                    y,
                                    larguras,
                                    new String[]{
                                            restante.getNome(),
                                            restante.getStatus()
                                    },
                                    false,
                                    alturaLinha
                            );
                        }

                        escreverTexto(
                                novaPagina,
                                PDType1Font.HELVETICA,
                                8,
                                margem,
                                30,
                                "Página "
                                        + documento.getNumberOfPages()
                        );
                    }

                    return;
                }

                y = desenharLinha(
                        conteudo,
                        margem,
                        y,
                        larguras,
                        new String[]{
                                aluno.getNome(),
                                aluno.getStatus()
                        },
                        false,
                        alturaLinha
                );
            }

            escreverTexto(
                    conteudo,
                    PDType1Font.HELVETICA,
                    8,
                    margem,
                    30,
                    "Documento gerado pelo sistema de gerenciamento de eventos."
            );

            escreverTexto(
                    conteudo,
                    PDType1Font.HELVETICA,
                    8,
                    larguraPagina - margem - 45,
                    30,
                    "Página "
                            + documento.getNumberOfPages()
            );
        }
    }

    // =====================================================
// GRÁFICO DE COMPARECIMENTO
// =====================================================

    private void desenharGraficoComparecimento(
            PDPageContentStream conteudo,
            List<PontoComparecimento> pontos,
            float x,
            float y,
            float largura,
            float altura
    ) throws IOException {

        // Área do gráfico
        conteudo.setStrokingColor(
                226,
                232,
                240
        );

        conteudo.addRect(
                x,
                y - altura,
                largura,
                altura
        );

        conteudo.stroke();

        if (
                pontos == null
                        || pontos.isEmpty()
        ) {

            escreverTexto(
                    conteudo,
                    PDType1Font.HELVETICA,
                    10,
                    x + 15,
                    y - 30,
                    "Nenhum registro de presença encontrado."
            );

            return;
        }

        int maior = 1;

        for (
                PontoComparecimento ponto :
                pontos
        ) {

            maior =
                    Math.max(
                            maior,
                            ponto.getTotal()
                    );
        }

        float margemEsquerda = 45;

        float margemDireita = 20;

        float margemSuperior = 25;

        float margemInferior = 35;

        float areaLargura =
                largura
                        - margemEsquerda
                        - margemDireita;

        float areaAltura =
                altura
                        - margemSuperior
                        - margemInferior;

        float espacamento = 10;

        float larguraBarra =
                (
                        areaLargura
                                - (
                                espacamento
                                        * (
                                        pontos.size() - 1
                                )
                        )
                ) / pontos.size();

        larguraBarra =
                Math.max(
                        8,
                        larguraBarra
                );

        float baseY =
                y - altura + margemInferior;

        // =============================================
        // LINHA BASE
        // =============================================

        conteudo.setStrokingColor(
                180,
                185,
                195
        );

        conteudo.moveTo(
                x + margemEsquerda,
                baseY
        );

        conteudo.lineTo(
                x + largura - margemDireita,
                baseY
        );

        conteudo.stroke();

        // =============================================
        // BARRAS
        // =============================================

        float xAtual =
                x + margemEsquerda;

        for (
                PontoComparecimento ponto :
                pontos
        ) {

            float alturaBarra =
                    (
                            (float)
                                    ponto.getTotal()
                                    / maior
                    )
                            * areaAltura;

            float yBarra =
                    baseY
                            + alturaBarra;

            // Barra
            conteudo.setNonStrokingColor(
                    37,
                    99,
                    235
            );

            conteudo.addRect(
                    xAtual,
                    baseY,
                    larguraBarra,
                    alturaBarra
            );

            conteudo.fill();

            // Valor
            String valor =
                    String.valueOf(
                            ponto.getTotal()
                    );

            float larguraTexto =
                    larguraTexto(
                            PDType1Font.HELVETICA_BOLD,
                            9,
                            valor
                    );

            escreverTexto(
                    conteudo,
                    PDType1Font.HELVETICA_BOLD,
                    9,
                    xAtual
                            + (
                            larguraBarra
                                    - larguraTexto
                    ) / 2,
                    Math.min(
                            yBarra + 10,
                            y - 8
                    ),
                    valor
            );

            // Data
            String data =
                    ponto.getData()
                            .format(
                                    java.time.format.DateTimeFormatter
                                            .ofPattern("dd/MM")
                            );

            float larguraData =
                    larguraTexto(
                            PDType1Font.HELVETICA,
                            8,
                            data
                    );

            escreverTexto(
                    conteudo,
                    PDType1Font.HELVETICA,
                    8,
                    xAtual
                            + (
                            larguraBarra
                                    - larguraData
                    ) / 2,
                    baseY - 15,
                    data
            );

            xAtual +=
                    larguraBarra
                            + espacamento;

            if (
                    xAtual
                            > x
                            + largura
                            - margemDireita
            ) {

                break;
            }
        }

        conteudo.setNonStrokingColor(
                0,
                0,
                0
        );
    }

    // =====================================================
// DESENHAR CARD DE RESUMO
// =====================================================

    private void desenharCardResumo(
            PDPageContentStream conteudo,
            float x,
            float y,
            float largura,
            float altura,
            String titulo,
            String valor,
            int[] fundo,
            int[] cor
    ) throws IOException {

        conteudo.setNonStrokingColor(
                fundo[0],
                fundo[1],
                fundo[2]
        );

        conteudo.addRect(
                x,
                y - altura,
                largura,
                altura
        );

        conteudo.fill();

        conteudo.setStrokingColor(
                226,
                232,
                240
        );

        conteudo.addRect(
                x,
                y - altura,
                largura,
                altura
        );

        conteudo.stroke();

        escreverTexto(
                conteudo,
                PDType1Font.HELVETICA_BOLD,
                9,
                x + 12,
                y - 18,
                titulo
        );

        conteudo.setNonStrokingColor(
                cor[0],
                cor[1],
                cor[2]
        );

        escreverTexto(
                conteudo,
                PDType1Font.HELVETICA_BOLD,
                22,
                x + 12,
                y - 44,
                valor
        );

        conteudo.setNonStrokingColor(
                0,
                0,
                0
        );
    }

// =====================================================
// EXPORTAR DESEMPENHO DO EVENTO
// =====================================================

    public void exportarDesempenhoEvento(

            File destino,
            EventoOpcao evento
    ) throws IOException {

        if (destino == null) {
            throw new IllegalArgumentException(
                    "Arquivo de destino não informado."
            );
        }

        if (evento == null) {
            throw new IllegalArgumentException(
                    "Evento não informado."
            );
        }

        EventoDesempenho desempenho =
                buscarDesempenhoEvento(
                        evento.getId()
                );

        List<PontoComparecimento> pontos =
                buscarComparecimentoPorData(
                        evento.getId()
                );

        List<AlunoStatus> alunos =
                listarStatusAlunos(
                        evento.getId()
                );

        try (
                PDDocument documento =
                        new PDDocument()
        ) {

            // =================================================
            // PÁGINA 1
            // =================================================

            PDPage pagina =
                    new PDPage(
                            PDRectangle.A4
                    );

            documento.addPage(pagina);

            try (
                    PDPageContentStream conteudo =
                            new PDPageContentStream(
                                    documento,
                                    pagina
                            )
            ) {

                float margem = 45;

                float larguraPagina =
                        pagina.getMediaBox()
                                .getWidth();

                float alturaPagina =
                        pagina.getMediaBox()
                                .getHeight();

                float largura =
                        larguraPagina
                                - (2 * margem);

                float y =
                        alturaPagina
                                - margem;

                // =============================================
                // TÍTULO
                // =============================================

                escreverTexto(
                        conteudo,
                        PDType1Font.HELVETICA_BOLD,
                        20,
                        margem,
                        y,
                        "Relatório de Desempenho"
                );

                y -= 27;

                escreverTexto(
                        conteudo,
                        PDType1Font.HELVETICA,
                        11,
                        margem,
                        y,
                        "Evento: " + evento
                );

                y -= 17;

                escreverTexto(
                        conteudo,
                        PDType1Font.HELVETICA,
                        10,
                        margem,
                        y,
                        montarPeriodoEvento(evento)
                );

                y -= 35;

                // =============================================
                // CARDS DE RESUMO
                // =============================================

                float espacamento = 10;

                float larguraCard =
                        (
                                largura
                                        - (espacamento * 2)
                        ) / 3;

                float alturaCard = 65;

                desenharCardResumo(
                        conteudo,
                        margem,
                        y,
                        larguraCard,
                        alturaCard,
                        "Total de Inscritos",
                        String.valueOf(
                                desempenho.getTotalInscritos()
                        ),
                        new int[]{
                                239,
                                246,
                                255
                        },
                        new int[]{
                                37,
                                99,
                                235
                        }
                );

                desenharCardResumo(
                        conteudo,
                        margem
                                + larguraCard
                                + espacamento,
                        y,
                        larguraCard,
                        alturaCard,
                        "Presenças Confirmadas",
                        String.valueOf(
                                desempenho
                                        .getPresencasConfirmadas()
                        ),
                        new int[]{
                                240,
                                253,
                                244
                        },
                        new int[]{
                                22,
                                163,
                                74
                        }
                );

                desenharCardResumo(
                        conteudo,
                        margem
                                + (
                                larguraCard
                                        + espacamento
                        ) * 2,
                        y,
                        larguraCard,
                        alturaCard,
                        "Ausentes",
                        String.valueOf(
                                desempenho.getAusentes()
                        ),
                        new int[]{
                                255,
                                247,
                                237
                        },
                        new int[]{
                                234,
                                88,
                                12
                        }
                );

                y -= 95;

                // =============================================
                // GRÁFICO
                // =============================================

                escreverTexto(
                        conteudo,
                        PDType1Font.HELVETICA_BOLD,
                        14,
                        margem,
                        y,
                        "Comparecimento por Data"
                );

                y -= 20;

                float alturaGrafico = 245;

                desenharGraficoComparecimento(
                        conteudo,
                        pontos,
                        margem,
                        y,
                        largura,
                        alturaGrafico
                );

                y -= alturaGrafico + 35;

                // =============================================
                // RODAPÉ
                // =============================================

                escreverTexto(
                        conteudo,
                        PDType1Font.HELVETICA,
                        8,
                        margem,
                        30,
                        "Documento gerado pelo sistema de gerenciamento de eventos."
                );

                escreverTexto(
                        conteudo,
                        PDType1Font.HELVETICA,
                        8,
                        larguraPagina - margem - 45,
                        30,
                        "Página 1"
                );
            }

            // =================================================
            // PÁGINA 2 - STATUS DOS ALUNOS
            // =================================================

            if (!alunos.isEmpty()) {

                criarPaginaStatusAlunos(
                        documento,
                        evento,
                        alunos
                );
            }

            documento.save(destino);
        }
    }

    // =====================================================
    // BUSCAR INSCRIÇÕES
    // =====================================================

    private List<AlunoInscricaoPdf> buscarInscricoesEvento(
            long idEvento
    ) {

        List<AlunoInscricaoPdf> lista =
                new ArrayList<>();

        String sql = """
                SELECT
                    a.rm,
                    a.nome,
                    ie.status
                FROM inscricao_evento ie
                INNER JOIN aluno a
                    ON a.id_aluno = ie.id_aluno
                WHERE ie.id_evento = ?
                ORDER BY a.nome ASC
                """;

        try (
                Connection conn =
                        Conexao.conectar();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    idEvento
            );

            try (
                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                while (rs.next()) {

                    lista.add(
                            new AlunoInscricaoPdf(
                                    rs.getString("rm"),
                                    rs.getString("nome"),
                                    rs.getString("status")
                            )
                    );
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao buscar inscrições do evento.",
                    e
            );
        }

        return lista;
    }

    // =====================================================
    // PERÍODO DO EVENTO
    // =====================================================

    private String montarPeriodoEvento(
            EventoOpcao evento
    ) {

        if (
                evento.getDataInicio() == null
                        && evento.getDataFim() == null
        ) {

            return "Período: não informado";
        }

        if (
                evento.getDataInicio() != null
                        && evento.getDataFim() != null
        ) {

            return "Período: "
                    + evento.getDataInicio()
                    + " até "
                    + evento.getDataFim();
        }

        if (evento.getDataInicio() != null) {

            return "Data de início: "
                    + evento.getDataInicio();
        }

        return "Data de término: "
                + evento.getDataFim();
    }

    // =====================================================
    // DESENHAR LINHA DA TABELA
    // =====================================================

    private float desenharLinha(
            PDPageContentStream conteudo,
            float x,
            float y,
            float[] larguras,
            String[] valores,
            boolean cabecalho,
            float altura
    ) throws IOException {

        float larguraTotal = 0;

        for (float largura : larguras) {
            larguraTotal += largura;
        }

        // ================================================
        // FUNDO DO CABEÇALHO
        // ================================================

        if (cabecalho) {

            conteudo.setNonStrokingColor(
                    235,
                    238,
                    243
            );

            conteudo.addRect(
                    x,
                    y - altura + 5,
                    larguraTotal,
                    altura
            );

            conteudo.fill();

            conteudo.setNonStrokingColor(
                    0,
                    0,
                    0
            );
        }

        // ================================================
        // BORDA DA LINHA
        // ================================================

        conteudo.setStrokingColor(
                190,
                195,
                202
        );

        conteudo.addRect(
                x,
                y - altura + 5,
                larguraTotal,
                altura
        );

        conteudo.stroke();

        // ================================================
        // TEXTO
        // ================================================

        float xAtual = x;

        for (
                int i = 0;
                i < valores.length;
                i++
        ) {

            String valor =
                    valores[i] == null
                            ? ""
                            : valores[i];

            PDFont fonte =
                    cabecalho
                            ? PDType1Font.HELVETICA_BOLD
                            : PDType1Font.HELVETICA;

            String textoLimitado =
                    limitarTexto(
                            fonte,
                            valor,
                            larguras[i],
                            9
                    );

            conteudo.beginText();

            conteudo.setFont(
                    fonte,
                    9
            );

            conteudo.newLineAtOffset(
                    xAtual + 5,
                    y - altura + 13
            );

            conteudo.showText(
                    textoLimitado
            );

            conteudo.endText();

            xAtual += larguras[i];

            // ============================================
            // DIVISÓRIA VERTICAL
            // ============================================

            if (
                    i < valores.length - 1
            ) {

                conteudo.moveTo(
                        xAtual,
                        y - altura + 5
                );

                conteudo.lineTo(
                        xAtual,
                        y + 5
                );

                conteudo.stroke();
            }
        }

        return y - altura;
    }

    // =====================================================
    // LIMITAR TEXTO
    // =====================================================

    private String limitarTexto(
            PDFont fonte,
            String texto,
            float largura,
            int tamanho
    ) throws IOException {

        if (texto == null) {
            return "";
        }

        if (texto.isEmpty()) {
            return "";
        }

        float larguraDisponivel =
                largura - 10;

        // Se couber normalmente
        if (
                larguraTexto(
                        fonte,
                        tamanho,
                        texto
                ) <= larguraDisponivel
        ) {

            return texto;
        }

        String resultado = texto;

        // Reduz o texto até caber
        while (
                resultado.length() > 3
                        && larguraTexto(
                        fonte,
                        tamanho,
                        resultado + "..."
                ) > larguraDisponivel
        ) {

            resultado =
                    resultado.substring(
                            0,
                            resultado.length() - 1
                    );
        }

        return resultado + "...";
    }

    // =====================================================
    // CALCULAR LARGURA DO TEXTO
    // =====================================================

    private float larguraTexto(
            PDFont fonte,
            int tamanho,
            String texto
    ) throws IOException {

        if (
                texto == null
                        || texto.isEmpty()
        ) {

            return 0f;
        }

        return fonte.getStringWidth(texto)
                * tamanho
                / 1000f;
    }

    // =====================================================
    // ESCREVER TEXTO
    // =====================================================

    private void escreverTexto(
            PDPageContentStream conteudo,
            PDFont fonte,
            int tamanho,
            float x,
            float y,
            String texto
    ) throws IOException {

        conteudo.beginText();

        conteudo.setFont(
                fonte,
                tamanho
        );

        conteudo.newLineAtOffset(
                x,
                y
        );

        conteudo.showText(
                texto == null
                        ? ""
                        : texto
        );

        conteudo.endText();
    }

    // =====================================================
    // MODELO INTERNO PARA O PDF
    // =====================================================

    private static class AlunoInscricaoPdf {

        private final String rm;

        private final String nome;

        private final String status;

        private AlunoInscricaoPdf(
                String rm,
                String nome,
                String status
        ) {

            this.rm =
                    rm == null
                            ? ""
                            : rm;

            this.nome =
                    nome == null

                            ? ""
                            : nome;

            this.status =
                    status == null
                            ? ""
                            : status;
        }
    }
}
