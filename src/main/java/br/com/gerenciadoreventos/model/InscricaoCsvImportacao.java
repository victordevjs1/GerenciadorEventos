package br.com.gerenciadoreventos.model;

public class InscricaoCsvImportacao {

    private final int linha;
    private final String rmInformado;
    private final String nomeCsv;
    private final String observacao;
    private final Aluno aluno;
    private final boolean valido;
    private final String situacao;

    public InscricaoCsvImportacao(
            int linha,
            String rmInformado,
            String nomeCsv,
            String observacao,
            Aluno aluno,
            boolean valido,
            String situacao
    ) {
        this.linha = linha;
        this.rmInformado = rmInformado;
        this.nomeCsv = nomeCsv;
        this.observacao = observacao;
        this.aluno = aluno;
        this.valido = valido;
        this.situacao = situacao;
    }

    public int getLinha() { return linha; }
    public String getRmInformado() { return rmInformado; }
    public String getNomeCsv() { return nomeCsv; }
    public String getObservacao() { return observacao; }
    public Aluno getAluno() { return aluno; }
    public boolean isValido() { return valido; }
    public String getSituacao() { return situacao; }
}
