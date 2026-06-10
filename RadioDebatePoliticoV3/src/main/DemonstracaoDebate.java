package main;

import builder.CandidatoBuilder;
import builder.EleitorConcreteBuilder;
import fachada.Fachada;
import model.Candidato;
import model.Eleitor;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * DemonstracaoDebate v3.0 — corrigida.
 *
 * Fluxo correto:
 *  1. Configura debate e eleitores
 *  2. Candidatos pressionam DR durante o debate
 *  3. Roda ciclo completo PERGUNTA->RESPOSTA->REPLICA->TREPLICA
 *  4. APÓS a tréplica, gerente concede os DRs
 *  5. Cada solicitante faz sua defesa (eleitores são notificados)
 *  6. Tentativa bloqueada de novo DR durante defesas
 *  7. Estado volta ao Normal
 *  8. Relatório final
 */
public class DemonstracaoDebate {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        System.out.println("==========================================");
        System.out.println(" RADIO DEBATE POLITICO v3.0");
        System.out.println(" Builder + Prototype + Observer + State");
        System.out.println("==========================================\n");

        Fachada fachada = Fachada.getInstance();

        // ── ETAPA 1: Configura candidatos via Builder ──────────
        System.out.println(">>> ETAPA 1: Candidatos (Builder)\n");

        Candidato ana    = new CandidatoBuilder().comId(1).comNome("Ana")
                .comPartido("Partido A").comNumeroEleitoral(10).build();
        Candidato bruno  = new CandidatoBuilder().comId(2).comNome("Bruno")
                .comPartido("Partido B").comNumeroEleitoral(20).build();
        Candidato carlos = new CandidatoBuilder().comId(3).comNome("Carlos")
                .comPartido("Partido C").comNumeroEleitoral(30).build();

        List<Candidato> candidatos = new ArrayList<>();
        candidatos.add(ana);
        candidatos.add(bruno);
        candidatos.add(carlos);

        // tempos[0]=pergunta, [1]=resposta, [2]=replica, [3]=treplica, [4]=DR
        int[] tempos = {30, 60, 30, 30, 60};
        fachada.configurarDebate(candidatos, tempos);

        // ── ETAPA 2: Eleitores via Builder + Observer ──────────
        System.out.println(">>> ETAPA 2: Eleitores (Observer)\n");

        Eleitor maria = new EleitorConcreteBuilder().comId(101).comNome("Maria")
                .comZonaEleitoral("Zona 5").comSecao(42).build();
        Eleitor joao  = new EleitorConcreteBuilder().comId(102).comNome("Joao")
                .comZonaEleitoral("Zona 3").comSecao(17).build();
        Eleitor sofia = new EleitorConcreteBuilder().comId(103).comNome("Sofia")
                .comZonaEleitoral("Zona 1").comSecao(5).build();

        fachada.cadastrarEleitor(maria, 1);  // Maria -> Ana
        fachada.cadastrarEleitor(joao,  2);  // Joao  -> Bruno
        fachada.cadastrarEleitor(sofia, 3);  // Sofia -> Carlos

        // ── ETAPA 3: Inicia rodada Ana pergunta Bruno ──────────
        System.out.println("\n>>> ETAPA 3: Rodada normal — Ana pergunta a Bruno");
        System.out.println("[State] Estado: " + fachada.getEstadoDebate() + "\n");

        ana.marcarComoInquiridor();
        forcarCampo(fachada, "inquiridor", ana);
        fachada.definirInquirido(2);

        // PERGUNTA
        forcarFase(fachada, "PERGUNTA");
        System.out.println("--- FASE: PERGUNTA ---");
        fachada.getGerenciador().iniciarFase(tempos[0]);

        // ── ETAPA 4: Carlos e Bruno pressionam DR durante o debate
        System.out.println("\n>>> ETAPA 4: Carlos e Bruno pressionam botao DR\n");
        carlos.pressionarBotaoDR();  // 1o na fila
        bruno.pressionarBotaoDR();   // 2o na fila
        System.out.println("\nFila DR: " + fachada.getGerenciador()
                .getFilaDireitoResposta().size() + " solicitante(s)");

        // ── ETAPA 5: Continua o ciclo completo ────────────────
        System.out.println("\n>>> ETAPA 5: Continuando ciclo — RESPOSTA, REPLICA, TREPLICA\n");

        forcarFase(fachada, "RESPOSTA");
        System.out.println("--- FASE: RESPOSTA ---");
        fachada.getGerenciador().iniciarFase(tempos[1]);

        forcarFase(fachada, "REPLICA");
        System.out.println("\n--- FASE: REPLICA ---");
        fachada.getGerenciador().iniciarFase(tempos[2]);

        forcarFase(fachada, "TREPLICA");
        System.out.println("\n--- FASE: TREPLICA ---");
        fachada.getGerenciador().iniciarFase(tempos[3]);

        // ── ETAPA 6: APÓS a tréplica — gerente concede DRs ────
        System.out.println("\n>>> ETAPA 6: Fim do ciclo. Gerente CONCEDE os Direitos de Resposta");
        System.out.println("[State] Estado antes: " + fachada.getEstadoDebate());
        fachada.concederDireitosResposta();
        // Estado agora = DIREITO_RESPOSTA
        // Carlos faz defesa (eleitores de Carlos são notificados)
        // Bruno faz defesa  (eleitores de Bruno são notificados)
        // Fila esgotada -> volta ao Normal

        // ── ETAPA 7: Tentativa bloqueada durante defesas ──────
        System.out.println("\n>>> ETAPA 7: Ana tenta pressionar DR durante as defesas");
        System.out.println("[State] Estado: " + fachada.getEstadoDebate());
        ana.pressionarBotaoDR();  // deve ser bloqueado

        // ── ETAPA 8: Estado após encerramento das defesas ─────
        System.out.println("\n>>> ETAPA 8: Apos encerramento das defesas");
        System.out.println("[State] Estado: " + fachada.getEstadoDebate());

        // ── ETAPA 9: Relatório ────────────────────────────────
        System.out.println("\n>>> ETAPA 9: Relatorio final");
        fachada.finalizarDebate();
    }

    private static void forcarFase(Fachada fachada, String fase) {
        try {
            java.lang.reflect.Field f =
                    fachada.getGerenciador().getClass().getDeclaredField("faseAtual");
            f.setAccessible(true);
            f.set(fachada.getGerenciador(), fase);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void forcarCampo(Fachada fachada, String campo, Object valor) {
        try {
            java.lang.reflect.Field f =
                    fachada.getGerenciador().getClass().getDeclaredField(campo);
            f.setAccessible(true);
            f.set(fachada.getGerenciador(), valor);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
