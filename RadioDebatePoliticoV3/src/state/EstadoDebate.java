package state;

import model.Candidato;

/**
 * Interface EstadoDebate – padrão State (NOVO v3.0).
 *
 * Define as operações que variam conforme o estado atual do debate.
 * O GerenciadorDebate delega para o estado atual todas as ações
 * que dependem do contexto (fluxo normal vs. direito de resposta).
 *
 * Estados concretos:
 *  - EstadoDebateNormal     : fluxo pergunta→resposta→réplica→tréplica
 *  - EstadoDireitoResposta  : defesas dos solicitantes em fila
 */
public interface EstadoDebate {

    /**
     * Avança para a próxima ação dentro deste estado.
     * Chamado pelo Cronometro ao finalizar o tempo de cada fase.
     */
    void proximaAcao();

    /**
     * Tenta registrar solicitação de Direito de Resposta de um candidato.
     * Em EstadoDebateNormal acumula na fila.
     * Em EstadoDireitoResposta recusa (sem ciclos infinitos).
     *
     * @param candidato quem pressionou o botão DR
     */
    void solicitarDireitoResposta(Candidato candidato);

    /**
     * Retorna o nome do estado para log/debug.
     */
    String getNome();
}
