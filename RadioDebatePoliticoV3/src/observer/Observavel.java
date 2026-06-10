package observer;

/** Interface Observavel (Subject) – sem alterações em relação à v1.0. */
public interface Observavel {
    void adicionarObservador(Observador o);
    void removerObservador(Observador o);
    void notificarObservadores();
}
