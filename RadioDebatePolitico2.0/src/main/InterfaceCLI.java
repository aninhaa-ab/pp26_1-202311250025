package main;

import builder.CandidatoBuilder;
import builder.EleitorConcreteBuilder;
import fachada.Fachada;
import model.Candidato;
import model.Eleitor;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * InterfaceCLI – REFATORADA na versão 2.0.
 *
 * Mudanças em relação à v1.0:
 *  ─ cadastrarCandidatos() agora usa CandidatoBuilder em vez do
 *    construtor direto de Candidato.
 *  ─ cadastrarEleitor() agora usa EleitorConcreteBuilder em vez do
 *    construtor direto de Eleitor.
 *  ─ Adicionada opção de menu "8 – Clonar candidato (Prototype)".
 */
public class InterfaceCLI {

    private Fachada           fachada;
    private Scanner           scanner;
    private Map<Integer, Eleitor> eleitoresCadastrados;
    private int               proximoIdEleitor;
    private String[]          fases    = { "PERGUNTA", "RESPOSTA", "REPLICA", "TREPLICA" };
    private int               faseIndice;
    private int[]             tempos   = { 30, 60, 30, 30 };

    public InterfaceCLI() {
        this.fachada              = Fachada.getInstance();
        this.scanner              = new Scanner(System.in);
        this.eleitoresCadastrados = new HashMap<>();
        this.proximoIdEleitor     = 100;
        this.faseIndice           = 0;
    }

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        new InterfaceCLI().executar();
    }

    public void executar() {
        imprimirCabecalho();
        boolean executando = true;
        while (executando) {
            imprimirMenu();
            String entrada = scanner.nextLine().trim();
            switch (entrada) {
                case "1": cadastrarCandidatos();       break;
                case "2": cadastrarEleitor();          break;
                case "3": sortearInquiridor();         break;
                case "4": definirInquirido();          break;
                case "5": iniciarProximaFase();        break;
                case "6": cancelarCadastroEleitor();   break;
                case "7": finalizarDebate();           break;
                case "8": clonarCandidato();           break;   // NOVO v2.0
                case "0": executando = false; System.out.println("Encerrando CLI."); break;
                default:  System.out.println("Opcao invalida.");
            }
        }
        scanner.close();
    }

    private void imprimirCabecalho() {
        System.out.println("==========================================");
        System.out.println("  RADIO DEBATE POLITICO v2.0 - CLI");
        System.out.println("  Builder + Prototype + Observer");
        System.out.println("==========================================\n");
    }

    private void imprimirMenu() {
        System.out.println("\n=========== MENU ===========");
        System.out.println("1 - Cadastrar candidatos (Builder)");
        System.out.println("2 - Cadastrar eleitor (Builder)");
        System.out.println("3 - Sortear inquiridor");
        System.out.println("4 - Definir inquirido");
        System.out.println("5 - Iniciar proxima fase (" + faseAtualLabel() + ")");
        System.out.println("6 - Cancelar cadastro de eleitor");
        System.out.println("7 - Finalizar debate e gerar relatorio");
        System.out.println("8 - Clonar candidato (Prototype)");   // NOVO
        System.out.println("0 - Sair");
        System.out.println("============================");
        System.out.print("Escolha: ");
    }

    private String faseAtualLabel() {
        return faseIndice >= fases.length ? "rodada concluida" : "proxima: " + fases[faseIndice];
    }

    // ─── Opção 1 – Builder ──────────────────────────────────────────

    private void cadastrarCandidatos() {
        System.out.println("\n>>> Cadastrando candidatos via CandidatoBuilder...");

        List<Candidato> candidatos = new ArrayList<>();

        String[][] dados = {
            { "1", "Ana",    "Partido A", "10" },
            { "2", "Bruno",  "Partido B", "20" },
            { "3", "Carlos", "Partido C", "30" }
        };

        for (String[] d : dados) {
            Candidato c = new CandidatoBuilder()
                    .comId(Integer.parseInt(d[0]))
                    .comNome(d[1])
                    .comPartido(d[2])
                    .comNumeroEleitoral(Integer.parseInt(d[3]))
                    .build();
            candidatos.add(c);
            System.out.println("  Criado: " + c);
        }

        fachada.configurarDebate(candidatos, tempos);
    }

    // ─── Opção 2 – Builder ──────────────────────────────────────────

    private void cadastrarEleitor() {
        if (fachada.getGerenciador().getCandidatos().isEmpty()) {
            System.out.println("Cadastre os candidatos primeiro (opcao 1)."); return;
        }
        System.out.print("Nome do eleitor: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) { System.out.println("Nome invalido."); return; }

        System.out.print("Zona eleitoral: ");
        String zona = scanner.nextLine().trim();
        System.out.print("Secao: ");
        int secao = 0;
        try { secao = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException ignored) {}

        System.out.println("Candidatos:");
        for (Candidato c : fachada.getGerenciador().getCandidatos()) {
            System.out.println("  [" + c.getId() + "] " + c.getNome());
        }
        System.out.print("ID do candidato preferido: ");
        int idCandidato;
        try { idCandidato = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("ID invalido."); return; }

        // NOVO: usa EleitorConcreteBuilder
        Eleitor eleitor = new EleitorConcreteBuilder()
                .comId(proximoIdEleitor++)
                .comNome(nome)
                .comZonaEleitoral(zona)
                .comSecao(secao)
                .build();

        fachada.cadastrarEleitor(eleitor, idCandidato);
        eleitoresCadastrados.put(eleitor.getId(), eleitor);
        System.out.println("Eleitor criado: " + eleitor);
    }

    // ─── Opção 8 – Prototype ────────────────────────────────────────

    private void clonarCandidato() {
        if (fachada.getGerenciador().getCandidatos().isEmpty()) {
            System.out.println("Cadastre candidatos primeiro (opcao 1)."); return;
        }
        System.out.println("Candidatos disponiveis:");
        for (Candidato c : fachada.getGerenciador().getCandidatos()) {
            System.out.println("  [" + c.getId() + "] " + c.getNome());
        }
        System.out.print("ID do candidato a clonar: ");
        int id;
        try { id = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("ID invalido."); return; }

        Candidato original = fachada.getGerenciador().getCandidatos()
                .stream().filter(c -> c.getId() == id).findFirst().orElse(null);
        if (original == null) { System.out.println("Candidato nao encontrado."); return; }

        Candidato clone = fachada.clonarCandidato(original);
        System.out.println("Clone criado (sem eleitores, microfone novo): " + clone);
    }

    // ─── Restante igual à v1.0 ──────────────────────────────────────

    private void sortearInquiridor() {
        if (fachada.getGerenciador().getCandidatos().isEmpty()) {
            System.out.println("Cadastre os candidatos primeiro (opcao 1)."); return;
        }
        fachada.sortearInquiridor();
        Candidato inq = fachada.getGerenciador().getInquiridor();
        if (inq != null) System.out.println("Inquiridor sorteado: " + inq.getNome());
        faseIndice = 0;
    }

    private void definirInquirido() {
        if (fachada.getGerenciador().getInquiridor() == null) {
            System.out.println("Sorteie um inquiridor primeiro (opcao 3)."); return;
        }
        System.out.println("Candidatos:");
        for (Candidato c : fachada.getGerenciador().getCandidatos()) {
            String marca = (c == fachada.getGerenciador().getInquiridor()) ? " (inquiridor)" : "";
            System.out.println("  [" + c.getId() + "] " + c.getNome() + marca);
        }
        System.out.print("ID do candidato inquirido: ");
        int id;
        try { id = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("ID invalido."); return; }
        fachada.definirInquirido(id);
    }

    private void iniciarProximaFase() {
        if (fachada.getGerenciador().getInquiridor() == null
                || fachada.getGerenciador().getInquirido() == null) {
            System.out.println("Defina inquiridor e inquirido antes (opcoes 3 e 4)."); return;
        }
        if (faseIndice >= fases.length) {
            System.out.println("Todas as fases ja foram executadas."); return;
        }
        forcarFase(fases[faseIndice]);
        fachada.getGerenciador().iniciarFase(tempos[faseIndice]);
        faseIndice++;
        if (faseIndice >= fases.length) System.out.println("\n>>> Rodada concluida!");
    }

    private void cancelarCadastroEleitor() {
        if (eleitoresCadastrados.isEmpty()) { System.out.println("Nenhum eleitor cadastrado."); return; }
        for (Eleitor e : eleitoresCadastrados.values()) {
            String pref = e.getCandidatoPreferido() != null ? e.getCandidatoPreferido().getNome() : "(nenhum)";
            System.out.println("  [" + e.getId() + "] " + e.getNome() + " -> " + pref);
        }
        System.out.print("ID do eleitor: ");
        int id;
        try { id = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("ID invalido."); return; }
        Eleitor el = eleitoresCadastrados.get(id);
        if (el == null) { System.out.println("Eleitor nao encontrado."); return; }
        el.cancelarCadastro();
    }

    private void finalizarDebate() {
        fachada.finalizarDebate();
    }

    private void forcarFase(String fase) {
        try {
            java.lang.reflect.Field f =
                    fachada.getGerenciador().getClass().getDeclaredField("faseAtual");
            f.setAccessible(true);
            f.set(fachada.getGerenciador(), fase);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
