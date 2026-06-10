package model;

/**
 * Classe Microfone – REFATORADA na versão 3.0.
 *
 * Novidade: botão DR (Direito de Resposta) integrado.
 * Quando pressionado, aciona o GerenciadorDebate via Mediador,
 * que repassa ao estado atual do debate.
 *
 * Mantido da v2.0: ligar(), desligar(), isLigado().
 */
public class Microfone extends Colaborador {

    private int       id;
    private boolean   ligado;
    private Candidato dono;   // candidato dono deste microfone

    public Microfone(int id) {
        this.id     = id;
        this.ligado = false;
    }

    /** Define o candidato dono deste microfone (chamado pelo Candidato). */
    public void setDono(Candidato dono) {
        this.dono = dono;
    }

    public void ligar() {
        this.ligado = true;
        System.out.println("[Microfone " + id + "] LIGADO");
    }

    public void desligar() {
        this.ligado = false;
        System.out.println("[Microfone " + id + "] DESLIGADO");
    }

    /**
     * Botão DR pressionado pelo candidato dono deste microfone.
     *
     * NOVO – v3.0 (padrão State).
     * Delega ao Mediador (GerenciadorDebate) que repassa
     * ao estado atual. Em EstadoDebateNormal: enfileira.
     * Em EstadoDireitoResposta: bloqueia.
     */
    public void pressionarBotaoDR() {
        if (dono == null) {
            System.out.println("[Microfone " + id + "] Botao DR: dono nao definido.");
            return;
        }
        System.out.println("[Microfone " + id + "] Botao DR pressionado por: "
            + dono.getNome());
        if (mediador != null) {
            mediador.solicitarDireitoResposta(dono);
        }
    }

    public boolean isLigado() { return ligado; }
    public int getId()         { return id; }
}
