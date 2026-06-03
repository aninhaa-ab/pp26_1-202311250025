package model;

/**
 * Classe Cronometro (sem alterações em relação à versão 1.0).
 */
public class Cronometro extends Colaborador {

    private int     tempoAtual;
    private boolean autoAvanco;

    public Cronometro() {
        this.tempoAtual  = 0;
        this.autoAvanco  = false;
    }

    public void setAutoAvanco(boolean autoAvanco) {
        this.autoAvanco = autoAvanco;
    }

    public void iniciar(int tempo) {
        this.tempoAtual = tempo;
        System.out.println("[Cronometro] Iniciando contagem de " + tempo + " segundos");
        this.tempoAtual = 0;
        finalizarTempo();
    }

    public void finalizarTempo() {
        System.out.println("[Cronometro] Tempo finalizado");
        if (autoAvanco && mediador != null) {
            mediador.proximaAcao();
        }
    }

    public int getTempoAtual() { return tempoAtual; }
}
