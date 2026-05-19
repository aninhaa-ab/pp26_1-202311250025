package main;

import fachada.Fachada;
import model.Candidato;
import model.Eleitor;
import model.Microfone;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Interface de Linha de Comando (CLI) interativa para o debate político.
 *
 * Permite ao usuário, através de um menu numerado, controlar todas as
 * etapas do debate em tempo real:
 *   - Cadastrar candidatos
 *   - Cadastrar eleitores (que vão receber notificações)
 *   - Sortear inquiridor
 *   - Definir inquirido
 *   - Avançar pelas fases (PERGUNTA, RESPOSTA, REPLICA, TREPLICA)
 *   - Cancelar cadastro de eleitor
 *   - Finalizar o debate e gerar relatório
 */
public class InterfaceCLI {

    private Fachada fachada;
    private Scanner scanner;
    private Map<Integer, Eleitor> eleitoresCadastrados;
    private int proximoIdEleitor;
    private String[] fases = { "PERGUNTA", "RESPOSTA", "REPLICA", "TREPLICA" };
    private int faseIndice;
    private int[] tempos;

    public InterfaceCLI() {
        this.fachada = Fachada.getInstance();
        this.scanner = new Scanner(System.in);
        this.eleitoresCadastrados = new HashMap<>();
        this.proximoIdEleitor = 100;
        this.faseIndice = 0;
        this.tempos = new int[] { 30, 60, 30, 30 };
    }

    public static void main(String[] args) {
        // Garante saída em UTF-8 (acentos não ficam como "?")
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        InterfaceCLI cli = new InterfaceCLI();
        cli.executar();
    }

    public void executar() {
        imprimirCabecalho();

        boolean executando = true;
        while (executando) {
            imprimirMenu();
            String entrada = scanner.nextLine().trim();

            switch (entrada) {
                case "1": cadastrarCandidatos();   break;
                case "2": cadastrarEleitor();      break;
                case "3": sortearInquiridor();     break;
                case "4": definirInquirido();      break;
                case "5": iniciarProximaFase();    break;
                case "6": cancelarCadastroEleitor(); break;
                case "7": finalizarDebate();       break;
                case "0":
                    executando = false;
                    System.out.println("Encerrando CLI.");
                    break;
                default:
                    System.out.println("Opcao invalida. Tente novamente.");
            }
        }
        scanner.close();
    }

    private void imprimirCabecalho() {
        System.out.println("==========================================");
        System.out.println("  RADIO DEBATE POLITICO - CLI Interativa");
        System.out.println("  Padrao Observer em acao");
        System.out.println("==========================================\n");
    }

    private void imprimirMenu() {
        System.out.println("\n=========== MENU ===========");
        System.out.println("1 - Cadastrar candidatos do debate");
        System.out.println("2 - Cadastrar eleitor");
        System.out.println("3 - Sortear inquiridor");
        System.out.println("4 - Definir inquirido");
        System.out.println("5 - Iniciar proxima fase (" + faseAtualLabel() + ")");
        System.out.println("6 - Cancelar cadastro de eleitor");
        System.out.println("7 - Finalizar debate e gerar relatorio");
        System.out.println("0 - Sair");
        System.out.println("============================");
        System.out.print("Escolha: ");
    }

    private String faseAtualLabel() {
        if (faseIndice >= fases.length) {
            return "rodada concluida";
        }
        return "proxima: " + fases[faseIndice];
    }

    // ------------------ AÇÕES DO MENU ------------------

    private void cadastrarCandidatos() {
        System.out.println("\n>>> Cadastrando 3 candidatos para a demo...");

        Candidato ana    = new Candidato(1, "Ana",    new Microfone(1));
        Candidato bruno  = new Candidato(2, "Bruno",  new Microfone(2));
        Candidato carlos = new Candidato(3, "Carlos", new Microfone(3));

        List<Candidato> candidatos = new ArrayList<>();
        candidatos.add(ana);
        candidatos.add(bruno);
        candidatos.add(carlos);

        fachada.configurarDebate(candidatos, tempos);
        fachada.getGerenciador().registrarAcao("Debate configurado com 3 candidatos");

        System.out.println("Candidatos cadastrados:");
        for (Candidato c : candidatos) {
            System.out.println("  [" + c.getId() + "] " + c.getNome());
        }
    }

    private void cadastrarEleitor() {
        if (fachada.getGerenciador().getCandidatos().isEmpty()) {
            System.out.println("Cadastre os candidatos primeiro (opcao 1).");
            return;
        }

        System.out.print("Nome do eleitor: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) {
            System.out.println("Nome invalido.");
            return;
        }

        System.out.println("Candidatos disponiveis:");
        for (Candidato c : fachada.getGerenciador().getCandidatos()) {
            System.out.println("  [" + c.getId() + "] " + c.getNome());
        }
        System.out.print("ID do candidato preferido: ");
        int idCandidato;
        try {
            idCandidato = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID invalido.");
            return;
        }

        Eleitor eleitor = new Eleitor(proximoIdEleitor++, nome);
        fachada.cadastrarEleitor(eleitor, idCandidato);
        eleitoresCadastrados.put(eleitor.getId(), eleitor);
        System.out.println("(eleitor id=" + eleitor.getId() + ")");
    }

    private void sortearInquiridor() {
        if (fachada.getGerenciador().getCandidatos().isEmpty()) {
            System.out.println("Cadastre os candidatos primeiro (opcao 1).");
            return;
        }
        fachada.sortearInquiridor();
        Candidato inq = fachada.getGerenciador().getInquiridor();
        if (inq != null) {
            System.out.println("Inquiridor sorteado: " + inq.getNome());
        }
        faseIndice = 0;
    }

    private void definirInquirido() {
        if (fachada.getGerenciador().getInquiridor() == null) {
            System.out.println("Sorteie um inquiridor primeiro (opcao 3).");
            return;
        }
        System.out.println("Candidatos:");
        for (Candidato c : fachada.getGerenciador().getCandidatos()) {
            String marca = (c == fachada.getGerenciador().getInquiridor()) ? " (inquiridor)" : "";
            System.out.println("  [" + c.getId() + "] " + c.getNome() + marca);
        }
        System.out.print("ID do candidato inquirido: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID invalido.");
            return;
        }
        fachada.definirInquirido(id);
    }

    private void iniciarProximaFase() {
        if (fachada.getGerenciador().getInquiridor() == null
                || fachada.getGerenciador().getInquirido() == null) {
            System.out.println("Defina inquiridor e inquirido antes (opcoes 3 e 4).");
            return;
        }
        if (faseIndice >= fases.length) {
            System.out.println("Todas as fases ja foram executadas nesta rodada.");
            return;
        }

        String fase = fases[faseIndice];
        forcarFase(fase);
        fachada.getGerenciador().iniciarFase(tempos[faseIndice]);
        faseIndice++;

        if (faseIndice >= fases.length) {
            System.out.println("\n>>> Rodada concluida!");
        }
    }

    private void cancelarCadastroEleitor() {
        if (eleitoresCadastrados.isEmpty()) {
            System.out.println("Nenhum eleitor cadastrado.");
            return;
        }
        System.out.println("Eleitores cadastrados:");
        for (Eleitor e : eleitoresCadastrados.values()) {
            String pref = (e.getCandidatoPreferido() != null)
                    ? e.getCandidatoPreferido().getNome()
                    : "(nenhum)";
            System.out.println("  [" + e.getId() + "] " + e.getNome() + " -> " + pref);
        }
        System.out.print("ID do eleitor: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID invalido.");
            return;
        }
        Eleitor el = eleitoresCadastrados.get(id);
        if (el == null) {
            System.out.println("Eleitor nao encontrado.");
            return;
        }
        el.cancelarCadastro();
    }

    private void finalizarDebate() {
        fachada.finalizarDebate();
    }

    // ------------------ Helper de reflexão para a fase ------------------

    private void forcarFase(String fase) {
        try {
            java.lang.reflect.Field f = fachada.getGerenciador().getClass()
                    .getDeclaredField("faseAtual");
            f.setAccessible(true);
            f.set(fachada.getGerenciador(), fase);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
