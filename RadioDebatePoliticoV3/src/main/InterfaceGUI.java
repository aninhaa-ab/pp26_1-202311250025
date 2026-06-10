package main;

import fachada.Fachada;

/** Classe InterfaceGUI – sem alterações em relação à v1.0 (stub). */
public class InterfaceGUI {
    public Fachada fachada;
    public InterfaceGUI() { this.fachada = Fachada.getInstance(); }
    public static void main(String[] args) {
        System.out.println("InterfaceGUI inicializada. Execute DemonstracaoDebate para a demo completa.");
    }
}
