package model;

import interfaces.Mediador;

/**
 * Classe abstrata Colaborador (do diagrama original – sem alterações).
 *
 * Representa qualquer entidade que coopera com o Mediador
 * (GerenciadorDebate) durante o debate.
 */
public abstract class Colaborador {

    protected Mediador mediador;

    public void setMediador(Mediador mediador) {
        this.mediador = mediador;
    }

    public Mediador getMediador() {
        return mediador;
    }
}
