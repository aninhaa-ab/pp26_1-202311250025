package model;

import observer.Observavel;
import observer.Observador;
import prototype.Clonavel;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe Candidato – REFATORADA na versão 3.0.
 *
 * Novidade em relação à v2.0:
 *  ─ pressionarBotaoDR(): delega ao microfone para solicitar DR.
 *  ─ receberFala() agora aceita "DIREITO_DE_RESPOSTA" além dos tipos anteriores.
 *  ─ setDono() registra este candidato como dono do microfone ao construir.
 *
 * Mantidas da v2.0:
 *  ─ Builder (partido, numeroEleitoral), Prototype (clonar()),
 *    Observer (adicionarObservador, notificarObservadores).
 */
public class Candidato extends Colaborador implements Observavel, Clonavel<Candidato> {

    private int    id;
    private String nome;
    private String partido;
    private int    numeroEleitoral;
    private boolean jaPerguntou;
    private Microfone microfone;
    private List<Observador> eleitores;

    public Candidato(int id, String nome, String partido, int numeroEleitoral, Microfone microfone) {
        this.id              = id;
        this.nome            = nome;
        this.partido         = partido;
        this.numeroEleitoral = numeroEleitoral;
        this.jaPerguntou     = false;
        this.microfone       = microfone;
        this.eleitores       = new ArrayList<>();
        // NOVO v3.0: registra este candidato como dono do microfone
        this.microfone.setDono(this);
    }

    public Candidato(int id, String nome, Microfone microfone) {
        this(id, nome, null, 0, microfone);
    }

    // ========================= Prototype =========================

    @Override
    public Candidato clonar() {
        Microfone novoMic = new Microfone(this.microfone.getId() + 100);
        Candidato clone = new Candidato(this.id, this.nome, this.partido,
                                        this.numeroEleitoral, novoMic);
        System.out.println("[Prototype] Candidato clonado: " + this.nome);
        return clone;
    }

    // ========================= Observer (Subject) =========================

    @Override
    public void adicionarObservador(Observador o) {
        if (!eleitores.contains(o)) eleitores.add(o);
    }

    @Override
    public void removerObservador(Observador o) {
        eleitores.remove(o);
    }

    @Override
    public void notificarObservadores() {
        String mensagem = "Candidato " + nome + " esta falando";
        for (Observador e : eleitores) e.atualizar(mensagem);
    }

    // ========================= Domínio =========================

    public void marcarComoInquiridor() { this.jaPerguntou = true; }
    public boolean getJaPerguntou()    { return jaPerguntou; }

    /**
     * Concede direito de fala a este candidato.
     * Aceita: PERGUNTA, RESPOSTA, REPLICA, TREPLICA, DIREITO_DE_RESPOSTA.
     *
     * Sequência: notifica eleitores ANTES de ligar o microfone.
     */
    public void receberFala(String tipoFala) {
        System.out.println("\n--- " + nome + " vai iniciar: " + tipoFala + " ---");
        notificarObservadores();
        microfone.ligar();
    }

    /**
     * Pressiona o botão DR deste candidato.
     * NOVO – v3.0 (padrão State): delega ao Microfone → Mediador → EstadoDebate.
     */
    public void pressionarBotaoDR() {
        microfone.pressionarBotaoDR();
    }

    // ========================= Getters =========================

    public int getId()                    { return id; }
    public String getNome()               { return nome; }
    public String getPartido()            { return partido; }
    public int getNumeroEleitoral()       { return numeroEleitoral; }
    public Microfone getMicrofone()       { return microfone; }
    public List<Observador> getEleitores(){ return eleitores; }

    @Override
    public String toString() {
        return "Candidato{id=" + id + ", nome='" + nome + "', partido='" + partido
                + "', numero=" + numeroEleitoral + "}";
    }
}
