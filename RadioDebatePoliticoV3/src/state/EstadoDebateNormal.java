package state;

import gerenciador.GerenciadorDebate;
import model.Candidato;

/**
 * EstadoDebateNormal – padrão State (NOVO v3.0).
 *
 * Estado padrão do debate: executa o ciclo
 * PERGUNTA → RESPOSTA → RÉPLICA → TRÉPLICA.
 *
 * Ao fim de cada ciclo (TRÉPLICA concluída), verifica se há
 * solicitações de Direito de Resposta pendentes. Se houver,
 * transiciona para EstadoDireitoResposta; caso contrário,
 * permanece em EstadoDebateNormal para o próximo sorteio.
 *
 * Durante este estado, qualquer candidato PODE acionar o botão DR.
 */
public class EstadoDebateNormal implements EstadoDebate {

    private final GerenciadorDebate gerenciador;

    public EstadoDebateNormal(GerenciadorDebate gerenciador) {
        this.gerenciador = gerenciador;
    }

    @Override
    public void proximaAcao() {
        String fase = gerenciador.getFaseAtual();

        if ("PERGUNTA".equals(fase)) {
            gerenciador.setFaseAtual("RESPOSTA");
            gerenciador.iniciarFase(gerenciador.getTempos()[1]);

        } else if ("RESPOSTA".equals(fase)) {
            gerenciador.setFaseAtual("REPLICA");
            gerenciador.iniciarFase(gerenciador.getTempos()[2]);

        } else if ("REPLICA".equals(fase)) {
            gerenciador.setFaseAtual("TREPLICA");
            gerenciador.iniciarFase(gerenciador.getTempos()[3]);

        } else if ("TREPLICA".equals(fase)) {
            // Fim do ciclo — desliga microfones
            if (gerenciador.getInquiridor() != null)
                gerenciador.getInquiridor().getMicrofone().desligar();
            if (gerenciador.getInquirido() != null)
                gerenciador.getInquirido().getMicrofone().desligar();

            gerenciador.registrarAcao("Rodada finalizada");

            // Verifica fila de Direito de Resposta
            if (!gerenciador.getFilaDireitoResposta().isEmpty()) {
                gerenciador.registrarAcao(
                    "Direitos de Resposta pendentes: "
                    + gerenciador.getFilaDireitoResposta().size()
                    + " solicitante(s). Gerente pode conceder ou negar.");
                // Transição: gerente decide via concederDireitosResposta()
                // A transição real ocorre em GerenciadorDebate.concederDireitosResposta()
            }
        }
    }

    @Override
    public void solicitarDireitoResposta(Candidato candidato) {
        // Aceita solicitação durante o fluxo normal
        if (gerenciador.getFilaDireitoResposta().contains(candidato)) {
            System.out.println("[DR] " + candidato.getNome()
                + " ja esta na fila de Direito de Resposta.");
            return;
        }
        gerenciador.getFilaDireitoResposta().add(candidato);
        gerenciador.registrarAcao("[DR] Solicitacao de Direito de Resposta: "
            + candidato.getNome()
            + " (posicao " + gerenciador.getFilaDireitoResposta().size() + ")");
        System.out.println("[DR] " + candidato.getNome()
            + " solicitou Direito de Resposta (posicao "
            + gerenciador.getFilaDireitoResposta().size() + ")");
    }

    @Override
    public String getNome() {
        return "DEBATE_NORMAL";
    }
}
