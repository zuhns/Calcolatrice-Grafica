package idk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Calcolatrice Grafica");

            // Crea il PianoCartesiano
            PianoCartesiano grafico = new PianoCartesiano();

            // Crea il PannelloBottoni, passandogli l'istanza del PianoCartesiano
            PannelloBottoni bottoni = new PannelloBottoni(grafico); 

            // Crea il JLayeredPane per sovrapporre gli elementi
            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setLayout(null);
            frame.setContentPane(layeredPane);

            // Aggiungi grafico e bottoni
            layeredPane.add(grafico, JLayeredPane.DEFAULT_LAYER);
            layeredPane.add(bottoni, JLayeredPane.PALETTE_LAYER);

            // Imposta le dimensioni della finestra
            frame.setSize(1200, 800);
            grafico.setBounds(0, 0, frame.getWidth(), frame.getHeight());
            bottoni.setBounds(0, 0, frame.getWidth(), frame.getHeight());

            // Ridimensionamento dinamico della finestra
            frame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    Dimension size = frame.getContentPane().getSize();
                    grafico.setBounds(0, 0, size.width, size.height);
                    bottoni.setBounds(0, 0, size.width, size.height);
                }
            });

            // Configura il frame
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
