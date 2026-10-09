package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.model.LinhaRelatorioCursoSerie;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RelatorioPdfExporter {
    private static final String[] MESES = {
            "Janeiro",
            "Fevereiro",
            "Março",
            "Abril",
            "Maio",
            "Junho",
            "Julho",
            "Agosto",
            "Setembro",
            "Outubro",
            "Novembro",
            "Dezembro"
    };

    private static final float MARGEM = 45;
    private static final float ALTURA_LINHA = 22;

    public void exportarFrequencia(
            File destino,
            String nomeEscola,
            int ano,
            int mes,
            int diasLetivos,
            List<LinhaRelatorioCursoSerie> linhas,
            double taxaGlobal
    ) throws IOException {
        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = criarPagina(documento);
            float largura = pagina.getMediaBox().getWidth() - MARGEM * 2;
            float y = pagina.getMediaBox().getHeight() - MARGEM;
            try (
                    PDPageContentStream pdf =
                            new PDPageContentStream(
                                    documento,
                                    pagina
                            )
            ) {
                y = titulo( pdf, y, "Relatório Mensal de Frequência" );
                y -= 12;
                y = texto( pdf, y, "Escola: " + nomeEscola, 10, false );
                y = texto( pdf, y, "Referência: " + MESES[mes - 1] + " / " + ano, 10, false );
                y = texto( pdf, y, "Dias com presença registrada: " + diasLetivos, 10, false );
                y -= 18;
                y = desenharTabela( pdf, y, largura, linhas );
                y -= 25;
                if (y < 150) {
                    pdf.close();
                    pagina = criarPagina(documento);
                    y = pagina.getMediaBox().getHeight() - MARGEM;
                    try (
                            PDPageContentStream novoPdf =
                                    new PDPageContentStream(
                                            documento,
                                            pagina
                                    )
                    ) {
                        desenharResumo( novoPdf, y, largura, ano, mes, taxaGlobal, linhas );
                    }
                } else {
                    desenharResumo( pdf, y, largura, ano, mes, taxaGlobal, linhas );
                }
            }
            documento.save(destino);
        }
    }

    private PDPage criarPagina(PDDocument documento) {
        PDPage pagina = new PDPage(PDRectangle.A4);
        documento.addPage(pagina);
        return pagina;
    }

    private float titulo(
            PDPageContentStream pdf,
            float y,
            String titulo
    ) throws IOException {
        return texto( pdf, y, titulo, 18, true );
    }

    private float texto(
            PDPageContentStream pdf,
            float y,
            String valor,
            int tamanho,
            boolean negrito
    ) throws IOException {
        pdf.beginText();
        pdf.setFont( negrito ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, tamanho );
        pdf.newLineAtOffset( MARGEM, y );
        pdf.showText( textoSeguro(valor) );
        pdf.endText();
        return y - tamanho - 5;
    }

    private float desenharTabela(
            PDPageContentStream pdf,
            float y,
            float largura,
            List<LinhaRelatorioCursoSerie> linhas
    ) throws IOException {
        float[] colunas = {
                largura * 0.23f,
                largura * 0.17f,
                largura * 0.20f,
                largura * 0.17f,
                largura * 0.23f
        };
        String[] cabecalho = {
                "Curso / Série",
                "Alunos",
                "Presenças",
                "Faltas",
                "Comparecimento"
        };
        y = linhaTabela( pdf, y, colunas, cabecalho, true );
        for (LinhaRelatorioCursoSerie linha : linhas) {
            String[] dados = {
                    linha.getCurso() + " / " + linha.getSerie(),
                    String.valueOf(
                            linha.getMatriculados()
                    ),
                    formatar(
                            linha.presencasMedia()
                    ),
                    formatar(
                            linha.faltasMedia()
                    ),
                    formatarPercentual(
                            linha.taxaComparecimento()
                    )
            };
            y = linhaTabela( pdf, y, colunas, dados, false );
        }
        return y;
    }

    private float linhaTabela(
            PDPageContentStream pdf,
            float y,
            float[] larguras,
            String[] valores,
            boolean cabecalho
    ) throws IOException {
        float larguraTotal = 0;
        for (float largura : larguras) {
            larguraTotal += largura;
        }
        float topo = y;
        float baixo = y - ALTURA_LINHA;
        if (cabecalho) {
            pdf.setNonStrokingColor( 37, 99, 235 );
            pdf.addRect( MARGEM, baixo, larguraTotal, ALTURA_LINHA );
            pdf.fill();
        }
        pdf.setStrokingColor( 210, 214, 220 );
        pdf.addRect( MARGEM, baixo, larguraTotal, ALTURA_LINHA );
        pdf.stroke();
        float x = MARGEM;
        for (int i = 0; i < valores.length; i++) {
            if (cabecalho) {
                pdf.setNonStrokingColor( 255, 255, 255 );
            } else {
                pdf.setNonStrokingColor( 30, 41, 59 );
            }
            pdf.beginText();
            pdf.setFont( cabecalho ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, 8 );
            pdf.newLineAtOffset( x + 5, baixo + 7 );
            pdf.showText( textoSeguro(valores[i]) );
            pdf.endText();
            x += larguras[i];
            if (i < valores.length - 1) {
                pdf.setStrokingColor( 210, 214, 220 );
                pdf.moveTo( x, baixo );
                pdf.lineTo( x, topo );
                pdf.stroke();
            }
        }
        return baixo;
    }

    private void desenharResumo(
            PDPageContentStream pdf,
            float y,
            float largura,
            int ano,
            int mes,
            double taxaGlobal,
            List<LinhaRelatorioCursoSerie> linhas
    ) throws IOException {
        pdf.beginText();
        pdf.setFont( PDType1Font.HELVETICA_BOLD, 13 );
        pdf.newLineAtOffset( MARGEM, y );
        pdf.showText("Resumo do período");
        pdf.endText();
        y -= 25;
        y = texto( pdf, y, String.format( Locale.US, "Taxa global de comparecimento: %.2f%%", taxaGlobal ), 10, false );
        y = texto( pdf, y, "Cursos/séries analisados: " + linhas.size(), 10, false );
        LinhaRelatorioCursoSerie melhor = encontrarMelhor(linhas);
        LinhaRelatorioCursoSerie pior = encontrarPior(linhas);
        if (melhor != null) {
            y = texto(
                    pdf,
                    y,
                    "Maior taxa: "
                            + melhor.getCurso() + " / " + melhor.getSerie()
                            + " (" + formatarPercentual( melhor.taxaComparecimento() ) + ")", 10, false );
        }
        if (pior != null) {
            texto(
                    pdf,
                    y,
                    "Menor taxa: "
                            + pior.getCurso() + " / " + pior.getSerie() + " (" + formatarPercentual( pior.taxaComparecimento() ) + ")", 10, false );
        }
    }

    private LinhaRelatorioCursoSerie encontrarMelhor(
            List<LinhaRelatorioCursoSerie> linhas
    ) {
        LinhaRelatorioCursoSerie resultado = null;
        for (LinhaRelatorioCursoSerie linha : linhas) {
            if (resultado == null
                    || linha.taxaComparecimento()
                    > resultado.taxaComparecimento()) {
                resultado = linha;
            }
        }
        return resultado;
    }

    private LinhaRelatorioCursoSerie encontrarPior(
            List<LinhaRelatorioCursoSerie> linhas
    ) {
        LinhaRelatorioCursoSerie resultado = null;
        for (LinhaRelatorioCursoSerie linha : linhas) {
            if (resultado == null
                    || linha.taxaComparecimento()
                    < resultado.taxaComparecimento()) {
                resultado = linha;
            }
        }
        return resultado;
    }

    private String formatar(double valor) {
        return String.format( Locale.forLanguageTag("pt-BR"), "%.1f", valor );
    }

    private String formatarPercentual(double valor) {
        return String.format( Locale.forLanguageTag("pt-BR"), "%.1f%%", valor );
    }

    private String textoSeguro(String texto) {
        if (texto == null) {
            return "";
        }
        return texto .replace("–", "-") .replace("—", "-") .replace("“", "\"") .replace("”", "\"") .replace("’", "'");
    }
}
