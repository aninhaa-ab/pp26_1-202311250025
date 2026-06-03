package builder;

import model.Candidato;
import model.Microfone;

/**
 * CandidatoBuilder – implementação concreta do padrão Builder.
 *
 * Monta passo a passo um objeto Candidato com todos os seus atributos,
 * criando automaticamente o Microfone associado.
 *
 * Uso:
 * <pre>
 *   Candidato ana = new CandidatoBuilder()
 *       .comId(1)
 *       .comNome("Ana")
 *       .comPartido("Partido A")
 *       .comNumeroEleitoral(10)
 *       .build();
 * </pre>
 *
 * NOVO – adicionado na versão 2.0 (padrões Builder + Prototype).
 */
public class CandidatoBuilder implements PoliticoBuilder {

    private int    id;
    private String nome;
    private String partido;
    private int    numeroEleitoral;

    private static int contadorMicrofone = 1;

    @Override
    public CandidatoBuilder comId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public CandidatoBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    @Override
    public CandidatoBuilder comPartido(String partido) {
        this.partido = partido;
        return this;
    }

    @Override
    public CandidatoBuilder comNumeroEleitoral(int numero) {
        this.numeroEleitoral = numero;
        return this;
    }

    /**
     * Valida os campos obrigatórios e constrói o Candidato.
     *
     * @throws IllegalStateException se nome ou id não foram definidos
     */
    @Override
    public Candidato build() {
        if (nome == null || nome.isBlank()) {
            throw new IllegalStateException("Nome do candidato é obrigatório.");
        }
        if (id <= 0) {
            throw new IllegalStateException("ID do candidato deve ser positivo.");
        }

        Microfone mic = new Microfone(contadorMicrofone++);
        return new Candidato(id, nome, partido, numeroEleitoral, mic);
    }

    /** Reinicia o builder para reutilização. */
    public CandidatoBuilder reset() {
        this.id             = 0;
        this.nome           = null;
        this.partido        = null;
        this.numeroEleitoral = 0;
        return this;
    }
}
