package gerenciador;

import interfaces.Mediador;
import model.Candidato;
import model.Cronometro;
import state.EstadoDebate;
import state.EstadoDebateNormal;
import state.EstadoDireitoResposta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Classe GerenciadorDebate – REFATORADA na versão 3.0.
 *
 * Novidades em relação à v2.0:
 *  ─ Implementa o padrão State: mantém uma referência ao EstadoDebate
 *    atual e delega proximaAcao() e solicitarDireitoResposta() para ele.
 *
 *  ─ filaDireitoResposta: fila de Candidatos que solicitaram DR,
 *    respeitando a ordem de pressão do botão.
 *
 *  ─ concederDireitosResposta(): o gerente ativa o EstadoDireitoResposta.
 *  ─ negarDireitosResposta(): o gerente descarta a fila sem abrir DR.
 *
 *  ─ tempos[4] = tempo de cada defesa de DR (novo índice).
 *
 * Mantidas da v2.0: sortearInquiridor, definirInquirido, iniciarFase,
 * registrarAcao, getCandidatos, getLogger, etc.
 */
public class GerenciadorDebate implements Mediador {

    private List<Candidato>  candidatos;
    private Candidato        inquiridor;
    private Candidato        inquirido;
    private Cronometro       cronometro;
    private Logger           logger;
    private String           faseAtual;
    private int[]            tempos;

    // ── State ──────────────────────────────────────────────────
    private EstadoDebate         estadoAtual;           // NOVO v3.0
    private Queue<Candidato>     filaDireitoResposta;   // NOVO v3.0

    public GerenciadorDebate() {
        this.candidatos           = new ArrayList<>();
        this.cronometro           = new Cronometro();
        this.cronometro.setMediador(this);
        this.logger               = new Logger();
        this.faseAtual            = null;
        this.filaDireitoResposta  = new LinkedList<>();
        this.estadoAtual          = new EstadoDebateNormal(this); // estado inicial
    }

    // ── Configuração ──────────────────────────────────────────

    public void setCandidatos(List<Candidato> candidatos) {
        this.candidatos = candidatos;
        for (Candidato c : candidatos) {
            c.setMediador(this);
            c.getMicrofone().setMediador(this);
        }
    }

    public void setTempos(int[] tempos) {
        this.tempos = tempos;
    }

    // ── Fluxo normal ─────────────────────────────────────────

    public void sortearInquiridor() {
        if (faseAtual == null) faseAtual = "PERGUNTA";
        List<Candidato> disponiveis = new ArrayList<>();
        for (Candidato c : candidatos) {
            if (!c.getJaPerguntou()) disponiveis.add(c);
        }
        if (disponiveis.isEmpty()) {
            System.out.println("Todos ja foram inquiridores");
            return;
        }
        int indice = new Random().nextInt(disponiveis.size());
        inquiridor = disponiveis.get(indice);
        inquiridor.marcarComoInquiridor();
        registrarAcao("Inquiridor sorteado: " + inquiridor.getNome());
    }

    public void definirInquirido(int id) {
        for (Candidato c : candidatos) {
            if (c.getId() == id && c != inquiridor) {
                inquirido = c;
                registrarAcao("Inquirido definido: " + c.getNome());
                return;
            }
        }
        System.out.println("Candidato invalido");
    }

    public void iniciarFase(int tempo) {
        if ("PERGUNTA".equals(faseAtual)) {
            inquiridor.receberFala("PERGUNTA");
            inquirido.getMicrofone().desligar();
        } else if ("RESPOSTA".equals(faseAtual)) {
            inquiridor.getMicrofone().desligar();
            inquirido.receberFala("RESPOSTA");
        } else if ("REPLICA".equals(faseAtual)) {
            inquiridor.receberFala("REPLICA");
            inquirido.getMicrofone().desligar();
        } else if ("TREPLICA".equals(faseAtual)) {
            inquiridor.getMicrofone().desligar();
            inquirido.receberFala("TREPLICA");
        }
        registrarAcao("Fase iniciada: " + faseAtual);
        cronometro.iniciar(tempo);
    }

    public void registrarAcao(String acao) {
        logger.registrar(acao);
    }

    // ── Mediador – delegação ao Estado ───────────────────────

    /**
     * Chamado pelo Cronometro ao fim de cada fase.
     * Delega ao estado atual (Normal ou DireitoResposta).
     */
    @Override
    public void proximaAcao() {
        estadoAtual.proximaAcao();
    }

    /**
     * Chamado pelo Microfone quando o botão DR é pressionado.
     * Delega ao estado atual (Normal: enfileira / DR: bloqueia).
     *
     * NOVO – v3.0 (padrão State).
     */
    @Override
    public void solicitarDireitoResposta(Candidato candidato) {
        estadoAtual.solicitarDireitoResposta(candidato);
    }

    // ── Direito de Resposta – decisão do gerente ─────────────

    /**
     * Gerente CONCEDE os Direitos de Resposta pendentes.
     * Só pode ser chamado quando há solicitações na fila e
     * o estado atual é EstadoDebateNormal.
     *
     * NOVO – v3.0 (padrão State).
     */
    public void concederDireitosResposta() {
        if (filaDireitoResposta.isEmpty()) {
            System.out.println("Nao ha solicitacoes de Direito de Resposta.");
            return;
        }
        if (!"DEBATE_NORMAL".equals(estadoAtual.getNome())) {
            System.out.println("DR ja esta em andamento.");
            return;
        }
        registrarAcao("Gerente CONCEDEU os Direitos de Resposta.");
        EstadoDireitoResposta estadoDR = new EstadoDireitoResposta(this);
        setEstado(estadoDR);
        estadoDR.iniciar();
    }

    /**
     * Gerente NEGA os Direitos de Resposta pendentes.
     * Descarta a fila e mantém o fluxo normal.
     *
     * NOVO – v3.0 (padrão State).
     */
    public void negarDireitosResposta() {
        if (filaDireitoResposta.isEmpty()) {
            System.out.println("Nao ha solicitacoes pendentes.");
            return;
        }
        registrarAcao("Gerente NEGOU os Direitos de Resposta. Solicitantes: "
            + filaDireitoResposta.size());
        System.out.println("[DR] Gerente negou todos os Direitos de Resposta pendentes.");
        filaDireitoResposta.clear();
    }

    // ── State: getter/setter público ─────────────────────────

    public void setEstado(EstadoDebate novoEstado) {
        System.out.println("[State] Transicao: "
            + estadoAtual.getNome() + " -> " + novoEstado.getNome());
        registrarAcao("[State] Transicao de estado: "
            + estadoAtual.getNome() + " -> " + novoEstado.getNome());
        this.estadoAtual = novoEstado;
    }

    public EstadoDebate getEstadoAtual() { return estadoAtual; }

    // ── Getters ──────────────────────────────────────────────

    public List<Candidato>  getCandidatos()           { return candidatos; }
    public Logger           getLogger()               { return logger; }
    public String           getFaseAtual()            { return faseAtual; }
    public void             setFaseAtual(String fase) { this.faseAtual = fase; }
    public Candidato        getInquiridor()           { return inquiridor; }
    public Candidato        getInquirido()            { return inquirido; }
    public int[]            getTempos()               { return tempos; }
    public Cronometro       getCronometro()           { return cronometro; }
    public Queue<Candidato> getFilaDireitoResposta()  { return filaDireitoResposta; }
}
