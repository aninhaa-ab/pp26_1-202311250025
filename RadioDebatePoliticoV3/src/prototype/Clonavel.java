package prototype;

/**
 * Interface Clonavel – padrão Prototype.
 *
 * Qualquer entidade do debate (Candidato, Eleitor) que precise ser
 * copiada com rapidez implementa esta interface.
 *
 * O método clonar() retorna uma cópia "rasa" (shallow) do objeto.
 * Quando uma cópia profunda for necessária, a classe concreta deve
 * sobrescrever o comportamento adequadamente.
 *
 * NOVO – adicionado na versão 2.0 (padrões Builder + Prototype).
 *
 * @param <T> o tipo do objeto clonado
 */
public interface Clonavel<T> {

    /**
     * Cria e retorna uma cópia deste objeto.
     * @return cópia do objeto original
     */
    T clonar();
}
