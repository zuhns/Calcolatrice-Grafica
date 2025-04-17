package math;

import java.awt.Color;
import java.util.function.DoubleUnaryOperator;

import idk.Funzione;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

public class ScomposizioneEquazione {

    public static Funzione[] scomponiFunzione(String input) {
        input = input.trim();

        if (!input.contains("=")) {
            // Se non contiene '=', trattala come funzione singola
            Funzione f = scomponiSingolaFunzione(input, Color.RED);
            return new Funzione[] { f };
        }

        String[] parti = input.split("=");
        if (parti.length != 2) {
            throw new IllegalArgumentException("L'equazione deve contenere un solo '=' oppure nessuno.");
        }

        String sinistra = parti[0].trim();
        String destra = parti[1].trim();

        if (sinistra.equals("y")) {
            // Caso: y = f(x)
            Funzione f = scomponiSingolaFunzione(destra, Color.RED);
            return new Funzione[] { f };
        } else if (destra.equals("y")) {
            // Caso: f(x) = y → funzione singola
            Funzione f = scomponiSingolaFunzione(sinistra, Color.RED);
            return new Funzione[] { f };
        }

        // Caso generale: equazione mista
        Funzione f1 = scomponiSingolaFunzione(sinistra, Color.RED);
        Funzione f2 = scomponiSingolaFunzione(destra, Color.BLUE);

        return new Funzione[] { f1, f2 };
    }

    private static Funzione scomponiSingolaFunzione(String expr, Color colore) {
        // Conversione compatibilità sintassi
        expr = expr.replace("log(", "log10("); // log(x) → base 10

        // Definizione funzione logaritmo naturale: ln(x)
        Function ln = new Function("ln", 1) {
            @Override
            public double apply(double... args) {
                return Math.log(args[0]); // logaritmo naturale base e
            }
        };

        Expression expression = new ExpressionBuilder(expr)
                .variable("x")
                .function(ln)
                .build();

        DoubleUnaryOperator op = x -> {
            expression.setVariable("x", x);
            return expression.evaluate();
        };

        return new Funzione(expr, op, colore);
    }
}
