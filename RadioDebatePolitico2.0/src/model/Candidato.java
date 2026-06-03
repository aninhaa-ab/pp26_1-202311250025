package model;

import observer.Observavel;
import observer.Observador;
import prototype.Clonavel;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe Candidato – REFATORADA na versão 2.0.
 *
 * Mudanças em relação à versão 1.0 (Observer):
 *  ─ Agora implementa {@link Clonavel} (padrão Prototype).
 *    O método clonar() cria um novo Candidato com os mesmos dados
 *    (id, nome, partido, numero) mas com Microfone NOVO e lista de
 *    eleitores VAZIA – cópias de candidatos não herdam observadores.
 *
 *  ─ Ganhou os campos {@code partido} e {@code numeroEleitoral},
 *    exigidos pelo CandidatoBuilder (padrão Builder).
 *
 *  ─ Construtor público agora aceita os campos completos;
 *    o CandidatoBuilder é o meio preferido de criação.
 *
 * Mantidas da versão 1.0:
 *  ─ Implementa {@link Observavel} (Subject do Observer).
 *  ─ Estende {@link Colaborador} (parte do Mediator).
 *  ─ receberFala() notifica ANTES de ligar o microfone.
 */
public class Candidato extends Colaborador implements Observavel, Clonavel<Candidato> {

    private int    id;
    private String nome;
    private String partido;            // NOVO (Builder)
    private int    numeroEleitoral;    // NOVO (Builder)
    private boolean jaPerguntou;
    private Microfone microfone;
    private List<Observador> eleitores;

    /**
     * Construtor completo – usado pelo CandidatoBuilder.
     */
    public Candidato(int id, String nome, String partido, int numeroEleitoral, Microfone microfone) {
        this.id              = id;
        this.nome            = nome;
        this.partido         = partido;
        this.numeroEleitoral = numeroEleitoral;
        this.jaPerguntou     = false;
        this.microfone       = microfone;
        this.eleitores       = new ArrayList<>();
    }

    /**
     * Construtor simplificado – mantido para compatibilidade retroativa.
     */
    public Candidato(int id, String nome, Microfone microfone) {
        this(id, nome, null, 0, microfone);
    }

    // ========================= Prototype =========================

    /**
     * Cria uma cópia deste candidato com os mesmos dados cadastrais,
     * mas com um Microfone NOVO e sem eleitores observando.
     *
     * Útil para duplicar um template de candidato antes de adicioná-lo
     * ao debate com credenciais distintas.
     *
     * NOVO – padrão Prototype, versão 2.0.
     */
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
        if (!eleitores.contains(o)) {
            eleitores.add(o);
        }
    }

    @Override
    public void removerObservador(Observador o) {
        eleitores.remove(o);
    }

    @Override
    public void notificarObservadores() {
        String mensagem = "Candidato " + nome + " esta falando";
        for (Observador e : eleitores) {
            e.atualizar(mensagem);
        }
    }

    // ========================= Domínio =========================

    /** Marca este candidato como já tendo perguntado nesta rodada. */
    public void marcarComoInquiridor() {
        this.jaPerguntou = true;
    }

    public boolean getJaPerguntou() {
        return jaPerguntou;
    }

    /**
     * Concede direito de fala a este candidato.
     *
     * Sequência obrigatória:
     *  1) Notifica TODOS os eleitores cadastrados (antes de tudo).
     *  2) Só DEPOIS liga o microfone.
     *
     * @param tipoFala "PERGUNTA", "RESPOSTA", "REPLICA" ou "TREPLICA"
     */
    public void receberFala(String tipoFala) {
        System.out.println("\n--- " + nome + " vai iniciar: " + tipoFala + " ---");
        notificarObservadores();   // (1) notificar ANTES
        microfone.ligar();         // (2) ligar DEPOIS
    }

    // ========================= Getters/Setters =========================

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
