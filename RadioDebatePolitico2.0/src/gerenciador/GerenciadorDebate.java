package gerenciador;

import interfaces.Mediador;
import model.Candidato;
import model.Cronometro;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe GerenciadorDebate – sem alterações de lógica em relação à v1.0.
 *
 * Recebe Candidatos construídos pelo CandidatoBuilder; não precisa
 * saber como eles foram criados.
 */
public class GerenciadorDebate implements Mediador {

    private List<Candidato> candidatos;
    private Candidato       inquiridor;
    private Candidato       inquirido;
    private Cronometro      cronometro;
    private Logger          logger;
    private String          faseAtual;
    private int[]           tempos;

    public GerenciadorDebate() {
        this.candidatos = new ArrayList<>();
        this.cronometro = new Cronometro();
        this.cronometro.setMediador(this);
        this.logger     = new Logger();
        this.faseAtual  = null;
    }

    public void setCandidatos(List<Candidato> candidatos) {
        this.candidatos = candidatos;
        for (Candidato c : candidatos) {
            c.setMediador(this);
            c.getMicrofone().setMediador(this);
        }
    }

    public void setTempos(int[] tempos) {
        this.tempos = tempos;
    }

    public void sortearInquiridor() {
        if (faseAtual == null) faseAtual = "PERGUNTA";

        List<Candidato> disponiveis = new ArrayList<>();
        for (Candidato c : candidatos) {
            if (!c.getJaPerguntou()) disponiveis.add(c);
        }
        if (disponiveis.isEmpty()) {
            System.out.println("Todos ja foram inquiridores");
            return;
        }
        int indice   = new Random().nextInt(disponiveis.size());
        inquiridor   = disponiveis.get(indice);
        inquiridor.marcarComoInquiridor();
        registrarAcao("Inquiridor sorteado: " + inquiridor.getNome());
    }

    public void definirInquirido(int id) {
        for (Candidato c : candidatos) {
            if (c.getId() == id && c != inquiridor) {
                inquirido = c;
                registrarAcao("Inquirido definido: " + c.getNome());
                return;
            }
        }
        System.out.println("Candidato invalido");
    }

    public void iniciarFase(int tempo) {
        if ("PERGUNTA".equals(faseAtual)) {
            inquiridor.receberFala("PERGUNTA");
            inquirido.getMicrofone().desligar();
        } else if ("RESPOSTA".equals(faseAtual)) {
            inquiridor.getMicrofone().desligar();
            inquirido.receberFala("RESPOSTA");
        } else if ("REPLICA".equals(faseAtual)) {
            inquiridor.receberFala("REPLICA");
            inquirido.getMicrofone().desligar();
        } else if ("TREPLICA".equals(faseAtual)) {
            inquiridor.getMicrofone().desligar();
            inquirido.receberFala("TREPLICA");
        }
        registrarAcao("Fase iniciada: " + faseAtual);
        cronometro.iniciar(tempo);
    }

    public void registrarAcao(String acao) {
        logger.registrar(acao);
    }

    @Override
    public void proximaAcao() {
        if ("PERGUNTA".equals(faseAtual)) {
            faseAtual = "RESPOSTA";
            iniciarFase(tempos[1]);
        } else if ("RESPOSTA".equals(faseAtual)) {
            faseAtual = "REPLICA";
            iniciarFase(tempos[2]);
        } else if ("REPLICA".equals(faseAtual)) {
            faseAtual = "TREPLICA";
            iniciarFase(tempos[3]);
        } else if ("TREPLICA".equals(faseAtual)) {
            registrarAcao("Rodada finalizada");
            if (inquiridor != null) inquiridor.getMicrofone().desligar();
            if (inquirido  != null) inquirido.getMicrofone().desligar();
        }
    }

    public List<Candidato> getCandidatos() { return candidatos; }
    public Logger           getLogger()     { return logger; }
    public String           getFaseAtual()  { return faseAtual; }
    public Candidato        getInquiridor() { return inquiridor; }
    public Candidato        getInquirido()  { return inquirido; }

    // package-private setter used by Fachada to force faseAtual (demo only)
    void setFaseAtual(String fase) { this.faseAtual = fase; }
}
