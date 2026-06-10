package builder;

import model.Eleitor;

/**
 * Interface EleitorBuilder – padrão Builder.
 *
 * Define as etapas de construção de um Eleitor.
 *
 * NOVO – adicionado na versão 2.0 (padrões Builder + Prototype).
 */
public interface EleitorBuilder {

    EleitorBuilder comId(int id);
    EleitorBuilder comNome(String nome);
    EleitorBuilder comZonaEleitoral(String zona);
    EleitorBuilder comSecao(int secao);

    /** Constrói e retorna o Eleitor configurado. */
    Eleitor build();
}
