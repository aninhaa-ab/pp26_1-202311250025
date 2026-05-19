package main;

import fachada.Fachada;
import model.Candidato;
import model.Eleitor;
import model.Microfone;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de demonstração da Questão 5.
 *
 * Mostra na prática o funcionamento do padrão Observer:
 *  1) Cria candidatos.
 *  2) Cria eleitores e os cadastra nos candidatos preferidos.
 *  3) Inicia o debate e percorre as fases.
 *  4) A cada início de fase, ANTES do microfone ser ligado, os eleitores
 *     do candidato falante recebem a notificação "Candidato xxxx está
 *     falando".
 *
 * Cenário simulado:
 *  - 3 candidatos: Ana, Bruno e Carlos.
 *  - 5 eleitores distribuídos entre os candidatos.
 *  - Rodada 1: Ana (inquiridor) → Bruno (inquirido).
 *  - Rodada 2: Carlos (inquiridor) → Bruno (inquirido).
 */
public class DemonstracaoDebate {

    public static void main(String[] args) {
        // Garante saída em UTF-8 (acentos não ficarão como "?")
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        System.out.println("==========================================");
        System.out.println(" DEMONSTRACAO - Radio Debate Politico");
        System.out.println(" Questao 5 - Padrao Observer");
        System.out.println("==========================================\n");

        // --- 1) Criar candidatos ---
        Candidato ana    = new Candidato(1, "Ana",    new Microfone(1));
        Candidato bruno  = new Candidato(2, "Bruno",  new Microfone(2));
        Candidato carlos = new Candidato(3, "Carlos", new Microfone(3));

        List<Candidato> candidatos = new ArrayList<>();
        candidatos.add(ana);
        candidatos.add(bruno);
        candidatos.add(carlos);

        // --- 2) Configurar a fachada ---
        int[] tempos = { 30, 60, 30, 30 };
        Fachada fachada = Fachada.getInstance();
        fachada.configurarDebate(candidatos, tempos);

        // --- 3) Cadastrar eleitores ---
        System.out.println(">>> ETAPA 1: Cadastro de eleitores\n");
        Eleitor maria = new Eleitor(101, "Maria");
        Eleitor joao  = new Eleitor(102, "Joao");
        Eleitor sofia = new Eleitor(103, "Sofia");
        Eleitor pedro = new Eleitor(104, "Pedro");
        Eleitor lucia = new Eleitor(105, "Lucia");

        // Maria e Lucia preferem Ana
        fachada.cadastrarEleitor(maria, 1);
        fachada.cadastrarEleitor(lucia, 1);
        // Joao prefere Bruno
        fachada.cadastrarEleitor(joao, 2);
        // Sofia e Pedro preferem Carlos
        fachada.cadastrarEleitor(sofia, 3);
        fachada.cadastrarEleitor(pedro, 3);

        // --- 4) Demonstração: Lucia muda de candidato preferido ---
        System.out.println("\n>>> ETAPA 2: Lucia muda de candidato preferido (de Ana para Carlos)\n");
        fachada.cadastrarEleitor(lucia, 3);

        // --- 5) Simular RODADA 1: Ana pergunta para Bruno ---
        System.out.println("\n>>> ETAPA 3: RODADA 1 - Ana pergunta a Bruno\n");
        ana.marcarComoInquiridor();
        forcarInquiridor(fachada, ana);
        fachada.definirInquirido(2);

        executarFase(fachada, "PERGUNTA",  tempos[0]);
        executarFase(fachada, "RESPOSTA",  tempos[1]);
        executarFase(fachada, "REPLICA",   tempos[2]);
        executarFase(fachada, "TREPLICA",  tempos[3]);

        // --- 6) Simular RODADA 2: Carlos pergunta para Bruno ---
        System.out.println("\n>>> ETAPA 4: RODADA 2 - Carlos pergunta a Bruno\n");
        carlos.marcarComoInquiridor();
        forcarInquiridor(fachada, carlos);
        fachada.definirInquirido(2);

        executarFase(fachada, "PERGUNTA",  tempos[0]);
        executarFase(fachada, "RESPOSTA",  tempos[1]);
        executarFase(fachada, "REPLICA",   tempos[2]);
        executarFase(fachada, "TREPLICA",  tempos[3]);

        // --- 7) Cancelamento de cadastro ---
        System.out.println("\n>>> ETAPA 5: Pedro cancela seu cadastro\n");
        pedro.cancelarCadastro();

        // --- 8) Finalizar ---
        System.out.println("\n>>> ETAPA 6: Finalizando debate\n");
        fachada.finalizarDebate();
    }

    /**
     * Helper: força a fase atual no gerenciador e chama iniciarFase().
     */
    private static void executarFase(Fachada fachada, String fase, int tempo) {
        try {
            java.lang.reflect.Field f = fachada.getGerenciador().getClass()
                    .getDeclaredField("faseAtual");
            f.setAccessible(true);
            f.set(fachada.getGerenciador(), fase);
        } catch (Exception e) {
            e.printStackTrace();
        }
        fachada.getGerenciador().iniciarFase(tempo);
    }

    /**
     * Helper: define manualmente o inquiridor (para a demo).
     */
    private static void forcarInquiridor(Fachada fachada, Candidato c) {
        try {
            java.lang.reflect.Field f = fachada.getGerenciador().getClass()
                    .getDeclaredField("inquiridor");
            f.setAccessible(true);
            f.set(fachada.getGerenciador(), c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
