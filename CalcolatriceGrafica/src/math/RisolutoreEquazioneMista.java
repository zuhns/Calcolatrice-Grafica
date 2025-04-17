package math;

import idk.Funzione;
import java.util.ArrayList;
import java.util.List;

public class RisolutoreEquazioneMista {

    /**
     * Trova le intersezioni (soluzioni) tra due funzioni nell'intervallo dato
     * @param f1 prima funzione
     * @param f2 seconda funzione
     * @param minX limite inferiore dell'intervallo
     * @param maxX limite superiore dell'intervallo
     * @return lista di valori x dove f1(x) ≈ f2(x)
     */
    public static List<Double> trovaIntersezioni(Funzione f1, Funzione f2, double minX, double maxX) {
        List<Double> soluzioni = new ArrayList<>();
        double step = 0.01;

        for (double x = minX; x < maxX; x += step) {
            double y1 = f1.calcola(x);
            double y2 = f2.calcola(x);
            double diff1 = y1 - y2;

            double xNext = x + step;
            double y1Next = f1.calcola(xNext);
            double y2Next = f2.calcola(xNext);
            double diff2 = y1Next - y2Next;

            // Se cambia segno tra diff1 e diff2 -> c'è un'intersezione
            if (Double.isFinite(diff1) && Double.isFinite(diff2) && diff1 * diff2 < 0) {
                double soluzione = bisezione(f1, f2, x, xNext);
                if (!Double.isNaN(soluzione)) {
                    soluzioni.add(soluzione);
                }
            }
        }

        return soluzioni;
    }

    /**
     * Metodo di bisezione per trovare zero della differenza tra f1 e f2
     */
    private static double bisezione(Funzione f1, Funzione f2, double a, double b) {
        double tol = 1e-6;
        int maxIter = 100;

        for (int i = 0; i < maxIter; i++) {
            double mid = (a + b) / 2;
            double fa = f1.calcola(a) - f2.calcola(a);
            double fm = f1.calcola(mid) - f2.calcola(mid);

            if (!Double.isFinite(fa) || !Double.isFinite(fm)) return Double.NaN;

            if (Math.abs(fm) < tol) return mid;

            if (fa * fm < 0) {
                b = mid;
            } else {
                a = mid;
            }
        }

        return (a + b) / 2;
    }
}
