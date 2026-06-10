package fachada;

import builder.CandidatoBuilder;
import builder.EleitorConcreteBuilder;
import gerenciador.GerenciadorDebate;
import model.Candidato;
import model.Eleitor;

import java.util.List;

/**
 * Classe Fachada – REFATORADA na versão 3.0.
 *
 * Novidades em relação à v2.0:
 *  ─ solicitarDireitoResposta(idCandidato): aciona o botão DR
 *    de um candidato pelo id.
 *  ─ concederDireitosResposta(): gerente concede os DRs pendentes.
 *  ─ negarDireitosResposta(): gerente nega os DRs pendentes.
 *  ─ getEstadoDebate(): expõe o nome do estado atual.
 *
 * Mantidas da v2.0:
 *  Singleton, Builder helpers, Prototype helpers, toda a API v1.0.
 */
public class Fachada {

    private static Fachada           instance;
    private        GerenciadorDebate gerenciador;

    private Fachada() {
        this.gerenciador = new GerenciadorDebate();
    }

    public static Fachada getInstance() {
        if (instance == null) instance = new Fachada();
        return instance;
    }

    // ====================== Builder helpers (v2.0) ======================

    public Candidato criarCandidato(int id, String nome, String partido, int numeroEleitoral) {
        return new CandidatoBuilder()
                .comId(id).comNome(nome).comPartido(partido)
                .comNumeroEleitoral(numeroEleitoral).build();
    }

    public Eleitor criarEleitor(int id, String nome, String zona, int secao) {
        return new EleitorConcreteBuilder()
                .comId(id).comNome(nome).comZonaEleitoral(zona)
                .comSecao(secao).build();
    }

    // ====================== Prototype helpers (v2.0) ====================

    public Candidato clonarCandidato(Candidato original) { return original.clonar(); }
    public Eleitor   clonarEleitor(Eleitor original)     { return original.clonar(); }

    // ====================== API v1.0 ====================================

    public void configurarDebate(List<Candidato> candidatos, int[] tempos) {
        if (candidatos == null || candidatos.isEmpty() || tempos == null || tempos.length < 5) {
            System.out.println("Erro: dados invalidos (tempos deve ter 5 posicoes: 0-3 fases + 4 DR)");
            return;
        }
        gerenciador.setCandidatos(candidatos);
        gerenciador.setTempos(tempos);
    }

    public void sortearInquiridor()           { gerenciador.sortearInquiridor(); }
    public void definirInquirido(int id)      { gerenciador.definirInquirido(id); }

    public void iniciarDebate() {
        if (gerenciador.getCandidatos().isEmpty()) {
            System.out.println("Erro: debate nao configurado"); return;
        }
        gerenciador.registrarAcao("Debate iniciado");
        gerenciador.sortearInquiridor();
    }

    public void avancarEtapa()  { gerenciador.proximaAcao(); }

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

    // ====================== DR – API v3.0 ===============================

    /**
     * Candidato pressiona o botão DR pelo id.
     * NOVO – v3.0 (padrão State).
     */
    public void solicitarDireitoResposta(int idCandidato) {
        for (Candidato c : gerenciador.getCandidatos()) {
            if (c.getId() == idCandidato) {
                c.pressionarBotaoDR();
                return;
            }
        }
        System.out.println("Candidato invalido para DR.");
    }

    /**
     * Gerente concede os Direitos de Resposta pendentes.
     * NOVO – v3.0 (padrão State).
     */
    public void concederDireitosResposta() {
        gerenciador.concederDireitosResposta();
    }

    /**
     * Gerente nega os Direitos de Resposta pendentes.
     * NOVO – v3.0 (padrão State).
     */
    public void negarDireitosResposta() {
        gerenciador.negarDireitosResposta();
    }

    /** Retorna o nome do estado atual do debate. */
    public String getEstadoDebate() {
        return gerenciador.getEstadoAtual().getNome();
    }

    public GerenciadorDebate getGerenciador() { return gerenciador; }
}
