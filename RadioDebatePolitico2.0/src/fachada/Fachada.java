package fachada;

import builder.CandidatoBuilder;
import builder.EleitorConcreteBuilder;
import gerenciador.GerenciadorDebate;
import model.Candidato;
import model.Eleitor;

import java.util.List;

/**
 * Classe Fachada – REFATORADA na versão 2.0.
 *
 * Novidades em relação à v1.0:
 *  ─ criarCandidato(...) – fábrica conveniente que encapsula
 *    o CandidatoBuilder (padrão Builder), expondo uma API simples
 *    para as interfaces criarem candidatos com todos os atributos.
 *
 *  ─ criarEleitor(...) – fábrica conveniente que encapsula o
 *    EleitorConcreteBuilder (padrão Builder).
 *
 *  ─ clonarCandidato(Candidato) – delega ao Prototype de Candidato,
 *    permitindo duplicar um candidato template antes de adicioná-lo
 *    ao debate.
 *
 *  ─ clonarEleitor(Eleitor) – idem para Eleitor.
 *
 * Mantidas da v1.0:
 *  ─ Singleton, configurarDebate, sortearInquiridor, definirInquirido,
 *    iniciarDebate, avancarEtapa, finalizarDebate, cadastrarEleitor.
 */
public class Fachada {

    private static Fachada        instance;
    private        GerenciadorDebate gerenciador;

    private Fachada() {
        this.gerenciador = new GerenciadorDebate();
    }

    public static Fachada getInstance() {
        if (instance == null) {
            instance = new Fachada();
        }
        return instance;
    }

    // ====================== Builder helpers ======================

    /**
     * Cria um Candidato usando o CandidatoBuilder.
     *
     * NOVO – versão 2.0 (padrão Builder).
     *
     * @param id              identificador único
     * @param nome            nome do político
     * @param partido         sigla ou nome do partido
     * @param numeroEleitoral número na urna
     * @return Candidato construído pelo builder
     */
    public Candidato criarCandidato(int id, String nome, String partido, int numeroEleitoral) {
        return new CandidatoBuilder()
                .comId(id)
                .comNome(nome)
                .comPartido(partido)
                .comNumeroEleitoral(numeroEleitoral)
                .build();
    }

    /**
     * Cria um Eleitor usando o EleitorConcreteBuilder.
     *
     * NOVO – versão 2.0 (padrão Builder).
     *
     * @param id            identificador único
     * @param nome          nome do eleitor
     * @param zona          zona eleitoral
     * @param secao         seção eleitoral
     * @return Eleitor construído pelo builder
     */
    public Eleitor criarEleitor(int id, String nome, String zona, int secao) {
        return new EleitorConcreteBuilder()
                .comId(id)
                .comNome(nome)
                .comZonaEleitoral(zona)
                .comSecao(secao)
                .build();
    }

    // ====================== Prototype helpers ======================

    /**
     * Clona um Candidato existente (Prototype).
     *
     * NOVO – versão 2.0 (padrão Prototype).
     *
     * @param original o candidato a ser clonado
     * @return cópia com mesmo dados mas sem eleitores e microfone novo
     */
    public Candidato clonarCandidato(Candidato original) {
        return original.clonar();
    }

    /**
     * Clona um Eleitor existente (Prototype).
     *
     * NOVO – versão 2.0 (padrão Prototype).
     *
     * @param original o eleitor a ser clonado
     * @return cópia com mesmos dados mas sem candidato preferido
     */
    public Eleitor clonarEleitor(Eleitor original) {
        return original.clonar();
    }

    // ====================== API da v1.0 ======================

    public void configurarDebate(List<Candidato> candidatos, int[] tempos) {
        if (candidatos == null || candidatos.isEmpty() || tempos == null || tempos.length < 4) {
            System.out.println("Erro: dados invalidos");
            return;
        }
        gerenciador.setCandidatos(candidatos);
        gerenciador.setTempos(tempos);
    }

    public void sortearInquiridor() {
        gerenciador.sortearInquiridor();
    }

    public void definirInquirido(int idCandidato) {
        gerenciador.definirInquirido(idCandidato);
    }

    public void iniciarDebate() {
        if (gerenciador.getCandidatos().isEmpty()) {
            System.out.println("Erro: debate nao configurado");
            return;
        }
        gerenciador.registrarAcao("Debate iniciado");
        gerenciador.sortearInquiridor();
    }

    public void avancarEtapa() {
        gerenciador.proximaAcao();
    }

    public void finalizarDebate() {
        gerenciador.registrarAcao("Debate finalizado");
        gerenciador.getLogger().gerarRelatorio();
    }

    public void cadastrarEleitor(Eleitor eleitor, int idCandidato) {
        for (Candidato c : gerenciador.getCandidatos()) {
            if (c.getId() == idCandidato) {
                eleitor.seCadastrar(c);
                gerenciador.registrarAcao("Eleitor " + eleitor.getNome()
                        + " cadastrado em " + c.getNome());
                return;
            }
        }
        System.out.println("Candidato invalido");
    }

    public GerenciadorDebate getGerenciador() {
        return gerenciador;
    }
}
