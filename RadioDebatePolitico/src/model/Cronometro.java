package model;

/**
 * Classe Cronometro (do diagrama original).
 *
 * Controla o tempo de cada fase do debate (pergunta, resposta, etc.).
 * Quando o tempo se esgota, opcionalmente avisa o mediador para
 * avançar para a próxima ação.
 *
 * Para fins didáticos esta implementação NÃO bloqueia uma Thread real.
 */
public class Cronometro extends Colaborador {

    private int tempoAtual;
    private boolean autoAvanco;

    public Cronometro() {
        this.tempoAtual = 0;
        this.autoAvanco = false; // por padrão NÃO avança sozinho
    }

    /**
     * Define se ao finalizar o tempo o cronômetro deve chamar
     * automaticamente mediador.proximaAcao().
     */
    public void setAutoAvanco(boolean autoAvanco) {
        this.autoAvanco = autoAvanco;
    }

    /**
     * Inicia a contagem regressiva por 'tempo' segundos.
     */
    public void iniciar(int tempo) {
        this.tempoAtual = tempo;
        System.out.println("[Cronometro] Iniciando contagem de " + tempo + " segundos");
        // Em uma implementação real haveria uma Thread/Timer aqui.
        this.tempoAtual = 0;
        finalizarTempo();
    }

    /**
     * Disparado quando o tempo se esgota; opcionalmente avisa o mediador.
     */
    public void finalizarTempo() {
        System.out.println("[Cronometro] Tempo finalizado");
        if (autoAvanco && mediador != null) {
            mediador.proximaAcao();
        }
    }

    public int getTempoAtual() {
        return tempoAtual;
    }
}
