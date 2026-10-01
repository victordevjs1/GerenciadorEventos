package br.com.gerenciadoreventos.service;

import br.com.gerenciadoreventos.dao.RelatorioFrequenciaDAO;
import br.com.gerenciadoreventos.model.LinhaRelatorioCursoSerie;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RelatorioFrequenciaService {

    private final RelatorioFrequenciaDAO dao;
    private final RelatorioPdfExporter exporter;

    public RelatorioFrequenciaService() {
        dao = new RelatorioFrequenciaDAO();
        exporter = new RelatorioPdfExporter();
    }

    public List<LinhaRelatorioCursoSerie> gerarRelatorioPorCursoSerie(int ano, int mes) {
        return dao.gerarRelatorioPorCursoSerie(ano, mes);
    }

    public int contarDiasLetivos(int ano, int mes) {
        return dao.contarDiasLetivos(ano, mes);
    }

    public double calcularTaxaGlobal(List<LinhaRelatorioCursoSerie> linhas) {
        return dao.calcularTaxaGlobal(linhas);
    }

    public void exportarRelatorioMensal(
            File destino,
            String nomeEscola,
            int ano,
            int mes
    ) throws IOException {

        List<LinhaRelatorioCursoSerie> linhas =
                gerarRelatorioPorCursoSerie(ano, mes);

        int diasLetivos = contarDiasLetivos(ano, mes);

        double taxaGlobal =
                calcularTaxaGlobal(linhas);

        exporter.exportarFrequencia(
                destino,
                nomeEscola,
                ano,
                mes,
                diasLetivos,
                linhas,
                taxaGlobal
        );
    }
}
