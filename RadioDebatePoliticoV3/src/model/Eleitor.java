package model;

import observer.Observador;
import prototype.Clonavel;

/**
 * Classe Eleitor – REFATORADA na versão 2.0.
 *
 * Mudanças em relação à versão 1.0 (Observer):
 *  ─ Agora implementa {@link Clonavel} (padrão Prototype).
 *    O método clonar() cria um novo Eleitor com os mesmos dados mas
 *    SEM candidato preferido – cópias precisam ser cadastradas
 *    explicitamente.
 *
 *  ─ Ganhou os campos {@code zonaEleitoral} e {@code secao},
 *    exigidos pelo EleitorConcreteBuilder (padrão Builder).
 *
 *  ─ Construtor público agora aceita os campos completos;
 *    o EleitorConcreteBuilder é o meio preferido de criação.
 *
 * Mantidas da versão 1.0:
 *  ─ Implementa {@link Observador} (Observer do padrão Observer).
 *  ─ seCadastrar() / cancelarCadastro() com regra "1 candidato por eleitor".
 */
public class Eleitor implements Observador, Clonavel<Eleitor> {

    private int       id;
    private String    nome;
    private String    zonaEleitoral;    // NOVO (Builder)
    private int       secao;            // NOVO (Builder)
    private Candidato candidatoPreferido;

    /**
     * Construtor completo – usado pelo EleitorConcreteBuilder.
     */
    public Eleitor(int id, String nome, String zonaEleitoral, int secao) {
        this.id             = id;
        this.nome           = nome;
        this.zonaEleitoral  = zonaEleitoral;
        this.secao          = secao;
        this.candidatoPreferido = null;
    }

    /**
     * Construtor simplificado – mantido para compatibilidade retroativa.
     */
    public Eleitor(int id, String nome) {
        this(id, nome, null, 0);
    }

    // ========================= Prototype =========================

    /**
     * Cria uma cópia deste eleitor com os mesmos dados cadastrais,
     * mas sem candidato preferido (candidatoPreferido = null).
     *
     * Útil para criar "modelos" de eleitores e replicá-los rapidamente
     * com cadastros distintos.
     *
     * NOVO – padrão Prototype, versão 2.0.
     */
    @Override
    public Eleitor clonar() {
        Eleitor clone = new Eleitor(this.id, this.nome, this.zonaEleitoral, this.secao);
        System.out.println("[Prototype] Eleitor clonado: " + this.nome);
        return clone;
    }

    // ========================= Observer =========================

    /**
     * Recebe notificação do candidato preferido (quando ele vai falar).
     */
    @Override
    public void atualizar(String mensagem) {
        System.out.println("[Eleitor " + nome + "] >>> " + mensagem);
    }

    /**
     * Cadastra este eleitor no candidato 'c'.
     * Se já estava em outro candidato, cancela antes (regra: 1 por eleitor).
     */
    public void seCadastrar(Candidato c) {
        if (candidatoPreferido != null) {
            candidatoPreferido.removerObservador(this);
        }
        this.candidatoPreferido = c;
        c.adicionarObservador(this);
        System.out.println("[Eleitor " + nome + "] cadastrado para receber notificacoes de "
                + c.getNome());
    }

    /** Cancela o cadastro no candidato preferido (se houver). */
    public void cancelarCadastro() {
        if (candidatoPreferido != null) {
            candidatoPreferido.removerObservador(this);
            System.out.println("[Eleitor " + nome + "] cancelou cadastro em "
                    + candidatoPreferido.getNome());
            candidatoPreferido = null;
        }
    }

    // ========================= Getters =========================

    public int getId()                    { return id; }
    public String getNome()               { return nome; }
    public String getZonaEleitoral()      { return zonaEleitoral; }
    public int getSecao()                 { return secao; }
    public Candidato getCandidatoPreferido() { return candidatoPreferido; }

    @Override
    public String toString() {
        return "Eleitor{id=" + id + ", nome='" + nome + "', zona='" + zonaEleitoral
                + "', secao=" + secao + "}";
    }
}
