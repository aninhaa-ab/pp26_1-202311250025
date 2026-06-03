package builder;

import model.Candidato;

/**
 * Interface PoliticoBuilder – padrão Builder.
 *
 * Define as etapas de construção de um Candidato (Político).
 * Permite construir diferentes "sabores" de candidato sem expor
 * a lógica de montagem ao código cliente.
 *
 * NOVO – adicionado na versão 2.0 (padrões Builder + Prototype).
 */
public interface PoliticoBuilder {

    PoliticoBuilder comId(int id);
    PoliticoBuilder comNome(String nome);
    PoliticoBuilder comPartido(String partido);
    PoliticoBuilder comNumeroEleitoral(int numero);

    /** Constrói e retorna o Candidato configurado. */
    Candidato build();
}
