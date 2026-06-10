package state;

import gerenciador.GerenciadorDebate;
import model.Candidato;

import java.util.LinkedList;
import java.util.Queue;

/**
 * EstadoDireitoResposta – padrão State (NOVO v3.0).
 *
 * Estado ativado quando o gerente concede os Direitos de Resposta
 * após o fim de um ciclo completo (TRÉPLICA).
 *
 * Comportamento:
 *  - Abre o microfone de cada solicitante na ordem de solicitação.
 *  - Eleitores são notificados via Observer antes de cada defesa.
 *  - NENHUM novo DR pode ser solicitado durante este estado.
 *  - Ao esgotar a fila, transiciona de volta para EstadoDebateNormal.
 */
public class EstadoDireitoResposta implements EstadoDebate {

    private final GerenciadorDebate  gerenciador;
    private final Queue<Candidato>   fila;
    private       Candidato          atual;

    public EstadoDireitoResposta(GerenciadorDebate gerenciador) {
        this.gerenciador = gerenciador;
        // Copia a fila de solicitantes para processamento local
        this.fila = new LinkedList<>(gerenciador.getFilaDireitoResposta());
    }

    /**
     * Inicia a primeira defesa assim que o estado é criado.
     * Chamado por GerenciadorDebate.concederDireitosResposta().
     */
    public void iniciar() {
        gerenciador.registrarAcao("=== INICIO DOS DIREITOS DE RESPOSTA ===");
        System.out.println("\n========================================");
        System.out.println("  DIREITO DE RESPOSTA — " + fila.size() + " solicitante(s)");
        System.out.println("========================================");
        processarProximo();
    }

    /** Abre o microfone do próximo solicitante na fila. */
    private void processarProximo() {
        if (fila.isEmpty()) {
            encerrar();
            return;
        }
        atual = fila.poll();
        gerenciador.registrarAcao("[DR] Defesa concedida a: " + atual.getNome());

        // Observer: notifica eleitores ANTES de ligar o microfone
        atual.receberFala("DIREITO_DE_RESPOSTA");

        // Inicia o cronômetro com o tempo de DR (tempos[4])
        gerenciador.getCronometro().iniciar(gerenciador.getTempos()[4]);
    }

    @Override
    public void proximaAcao() {
        // Chamado pelo Cronometro ao fim do tempo de cada defesa
        if (atual != null) {
            atual.getMicrofone().desligar();
            gerenciador.registrarAcao("[DR] Defesa encerrada: " + atual.getNome());
        }
        processarProximo();
    }

    @Override
    public void solicitarDireitoResposta(Candidato candidato) {
        // Bloqueado durante as defesas — sem ciclos infinitos
        System.out.println("[DR] Solicitacao de " + candidato.getNome()
            + " NEGADA: Direitos de Resposta em andamento."
            + " Nenhum novo DR pode ser solicitado agora.");
        gerenciador.registrarAcao("[DR] Solicitacao negada (DR em andamento): "
            + candidato.getNome());
    }

    /** Encerra o estado DR e retorna ao fluxo normal. */
    private void encerrar() {
        gerenciador.registrarAcao("=== FIM DOS DIREITOS DE RESPOSTA ===");
        System.out.println("\n=== FIM DOS DIREITOS DE RESPOSTA ===");
        System.out.println("Retornando ao fluxo normal do debate...\n");

        // Limpa a fila global
        gerenciador.getFilaDireitoResposta().clear();

        // Transição de volta para EstadoDebateNormal
        gerenciador.setEstado(new EstadoDebateNormal(gerenciador));
        gerenciador.setFaseAtual("PERGUNTA");
    }

    @Override
    public String getNome() {
        return "DIREITO_RESPOSTA";
    }
}
