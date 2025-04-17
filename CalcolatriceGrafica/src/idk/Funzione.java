package idk;

import java.awt.Color;
import java.util.function.DoubleUnaryOperator;

public class Funzione {
    private final DoubleUnaryOperator funzione;
    private final Color colore;
    private final String espressione;

    public Funzione(String espressione, DoubleUnaryOperator funzione, Color colore) {
        this.funzione = funzione;
        this.colore = colore;
        this.espressione = espressione;
    }

    public double calcola(double x) {
        return funzione.applyAsDouble(x);
    }

    public Color getColore() {
        return colore;
    }

    public String getEspressione() {
        return espressione;
    }
}