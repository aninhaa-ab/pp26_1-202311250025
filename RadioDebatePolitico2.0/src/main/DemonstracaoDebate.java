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
 * DemonstracaoDebate v2.0
 *
 * Demonstra os padrões Builder e Prototype integrados ao sistema
 * existente (Observer + Mediator + Facade + Singleton).
 *
 * Roteiro:
 *  ETAPA 1 – Builder: cria candidatos e eleitores via builders.
 *  ETAPA 2 – Prototype: clona um candidato template e um eleitor template.
 *  ETAPA 3 – Observer: cadastra eleitores e executa o debate.
 *  ETAPA 4 – Relatório final.
 */
public class DemonstracaoDebate {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        System.out.println("==========================================");
        System.out.println(" RADIO DEBATE POLITICO v2.0");
        System.out.println(" Padroes: Builder + Prototype + Observer");
        System.out.println("==========================================\n");

        Fachada fachada = Fachada.getInstance();

        // ─────────────────────────────────────────────
        // ETAPA 1 – BUILDER: construir candidatos
        // ─────────────────────────────────────────────
        System.out.println(">>> ETAPA 1: Construção de Candidatos via Builder\n");

        Candidato ana = new CandidatoBuilder()
                .comId(1)
                .comNome("Ana")
                .comPartido("Partido A")
                .comNumeroEleitoral(10)
                .build();

        Candidato bruno = new CandidatoBuilder()
                .comId(2)
                .comNome("Bruno")
                .comPartido("Partido B")
                .comNumeroEleitoral(20)
                .build();

        Candidato carlos = new CandidatoBuilder()
                .comId(3)
                .comNome("Carlos")
                .comPartido("Partido C")
                .comNumeroEleitoral(30)
                .build();

        System.out.println("Candidatos criados:");
        System.out.println("  " + ana);
        System.out.println("  " + bruno);
        System.out.println("  " + carlos);

        // ─────────────────────────────────────────────
        // ETAPA 1b – BUILDER: construir eleitores
        // ─────────────────────────────────────────────
        System.out.println("\n>>> ETAPA 1b: Construção de Eleitores via Builder\n");

        Eleitor maria = new EleitorConcreteBuilder()
                .comId(101)
                .comNome("Maria")
                .comZonaEleitoral("Zona 5")
                .comSecao(42)
                .build();

        Eleitor joao = new EleitorConcreteBuilder()
                .comId(102)
                .comNome("Joao")
                .comZonaEleitoral("Zona 3")
                .comSecao(17)
                .build();

        System.out.println("Eleitores criados:");
        System.out.println("  " + maria);
        System.out.println("  " + joao);

        // ─────────────────────────────────────────────
        // ETAPA 2 – PROTOTYPE: clonar candidato e eleitor
        // ─────────────────────────────────────────────
        System.out.println("\n>>> ETAPA 2: Clonagem via Prototype\n");

        // Clona Ana para criar um candidato reserva (mesmo partido, dados iguais)
        Candidato anaReserva = fachada.clonarCandidato(ana);
        anaReserva = new CandidatoBuilder()
                .comId(4)
                .comNome("Ana (Reserva)")
                .comPartido(ana.getPartido())
                .comNumeroEleitoral(ana.getNumeroEleitoral())
                .build();
        System.out.println("Candidato reserva criado por Prototype + Builder: " + anaReserva);

        // Clona Maria para criar eleitor extra
        Eleitor mariaClone = fachada.clonarEleitor(maria);
        System.out.println("Eleitor clonado (sem candidato ainda): " + mariaClone);

        // ─────────────────────────────────────────────
        // ETAPA 3 – Configurar e executar debate
        // ─────────────────────────────────────────────
        System.out.println("\n>>> ETAPA 3: Configurando debate\n");

        List<Candidato> candidatos = new ArrayList<>();
        candidatos.add(ana);
        candidatos.add(bruno);
        candidatos.add(carlos);

        int[] tempos = { 30, 60, 30, 30 };
        fachada.configurarDebate(candidatos, tempos);

        // Cadastrar eleitores (Observer)
        System.out.println("\n>>> Cadastrando eleitores (Observer)\n");
        fachada.cadastrarEleitor(maria, 1);         // Maria -> Ana
        fachada.cadastrarEleitor(joao, 2);           // Joao  -> Bruno

        Eleitor sofia = fachada.criarEleitor(103, "Sofia", "Zona 1", 5);
        fachada.cadastrarEleitor(sofia, 3);          // Sofia -> Carlos

        // Cadastrar o clone de Maria no Bruno
        fachada.cadastrarEleitor(mariaClone, 2);    // MariaClone -> Bruno

        // ─────────────────────────────────────────────
        // Simular RODADA: Ana pergunta para Bruno
        // ─────────────────────────────────────────────
        System.out.println("\n>>> Rodada: Ana pergunta a Bruno\n");

        ana.marcarComoInquiridor();
        forcarInquiridor(fachada, ana);
        fachada.definirInquirido(2);

        String[] fases = { "PERGUNTA", "RESPOSTA", "REPLICA", "TREPLICA" };
        for (int i = 0; i < fases.length; i++) {
            forcarFase(fachada, fases[i]);
            fachada.getGerenciador().iniciarFase(tempos[i]);
        }

        // ─────────────────────────────────────────────
        // ETAPA 4 – Finalizar
        // ─────────────────────────────────────────────
        System.out.println("\n>>> ETAPA 4: Finalizando debate\n");
        fachada.finalizarDebate();
    }

    // ─── helpers de reflexão (usados apenas na demo) ───────────────

    private static void forcarFase(Fachada fachada, String fase) {
        try {
            java.lang.reflect.Field f =
                    fachada.getGerenciador().getClass().getDeclaredField("faseAtual");
            f.setAccessible(true);
            f.set(fachada.getGerenciador(), fase);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void forcarInquiridor(Fachada fachada, Candidato c) {
        try {
            java.lang.reflect.Field f =
                    fachada.getGerenciador().getClass().getDeclaredField("inquiridor");
            f.setAccessible(true);
            f.set(fachada.getGerenciador(), c);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
