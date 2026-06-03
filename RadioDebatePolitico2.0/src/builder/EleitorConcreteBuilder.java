package builder;

import model.Eleitor;

/**
 * EleitorConcreteBuilder – implementação concreta do padrão Builder.
 *
 * Monta passo a passo um objeto Eleitor com todos os atributos opcionais
 * (zona eleitoral, seção) sem poluir o construtor de Eleitor.
 *
 * Uso:
 * <pre>
 *   Eleitor maria = new EleitorConcreteBuilder()
 *       .comId(101)
 *       .comNome("Maria")
 *       .comZonaEleitoral("Zona 5")
 *       .comSecao(42)
 *       .build();
 * </pre>
 *
 * NOVO – adicionado na versão 2.0 (padrões Builder + Prototype).
 */
public class EleitorConcreteBuilder implements EleitorBuilder {

    private int    id;
    private String nome;
    private String zonaEleitoral;
    private int    secao;

    @Override
    public EleitorConcreteBuilder comId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public EleitorConcreteBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    @Override
    public EleitorConcreteBuilder comZonaEleitoral(String zona) {
        this.zonaEleitoral = zona;
        return this;
    }

    @Override
    public EleitorConcreteBuilder comSecao(int secao) {
        this.secao = secao;
        return this;
    }

    /**
     * Valida os campos obrigatórios e constrói o Eleitor.
     *
     * @throws IllegalStateException se nome ou id não foram definidos
     */
    @Override
    public Eleitor build() {
        if (nome == null || nome.isBlank()) {
            throw new IllegalStateException("Nome do eleitor é obrigatório.");
        }
        if (id <= 0) {
            throw new IllegalStateException("ID do eleitor deve ser positivo.");
        }
        return new Eleitor(id, nome, zonaEleitoral, secao);
    }

    /** Reinicia o builder para reutilização. */
    public EleitorConcreteBuilder reset() {
        this.id            = 0;
        this.nome          = null;
        this.zonaEleitoral = null;
        this.secao         = 0;
        return this;
    }
}
