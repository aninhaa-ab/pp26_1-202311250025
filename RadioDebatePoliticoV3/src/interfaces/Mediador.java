package interfaces;

import model.Candidato;

/**
 * Interface Mediador – REFATORADA na versão 3.0.
 *
 * Novo método: solicitarDireitoResposta(Candidato).
 * O Microfone chama este método ao detectar o botão DR pressionado.
 * O GerenciadorDebate implementa e delega ao EstadoDebate atual.
 */
public interface Mediador {
    void proximaAcao();

    /**
     * Recebe solicitação de Direito de Resposta vinda do Microfone.
     * NOVO – v3.0 (padrão State).
     */
    void solicitarDireitoResposta(Candidato candidato);
}
