package idk;

import javax.swing.*;

import math.RisolutoreEquazioneMista;
import math.ScomposizioneEquazione;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PannelloBottoni extends JPanel {
    private final JButton reset;
    private final JButton toggleInput;
    private JPanel inputPanel;
    private boolean inputVisible = false;

    private JLabel rispostaLabel;  // Etichetta per mostrare la risposta
    private PianoCartesiano piano;  // Variabile piano

    public PannelloBottoni(PianoCartesiano piano) {
        this.piano = piano;  // Inizializza la variabile piano
        setOpaque(false);
        setLayout(null);

        // Bottone Reset
        reset = new JButton("Reset View");
        reset.setSize(120, 30);
        reset.addActionListener(e -> piano.resetView());
        add(reset);

        // Bottone Toggle Input
        toggleInput = new JButton("+");
        toggleInput.setSize(50, 30);
        toggleInput.addActionListener(e -> toggleInputPanel(piano));
        add(toggleInput);

        // Creazione del pannello di input
        createInputPanel(piano);
        add(inputPanel);
        inputPanel.setVisible(false);

        // Creazione del rispostaLabel
        rispostaLabel = new JLabel("Risultato: ");
        rispostaLabel.setOpaque(true);
        rispostaLabel.setBackground(Color.WHITE);  // Imposta il background bianco
        rispostaLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));  // Bordo per evidenziarlo
        rispostaLabel.setPreferredSize(new Dimension(300, 30));
        rispostaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(rispostaLabel);  // Aggiungi il pannello della risposta
    }

    public void aggiungiFunzioni(List<Funzione> list) {
        for (Funzione f : list)
            piano.funzioni.add(f);
        repaint();
        calcolaSoluzione();  // Ricalcola la soluzione quando una nuova funzione viene aggiunta
    }

    // Funzione per calcolare la soluzione
    private void calcolaSoluzione() {
        System.out.println("DEBUG: calcolaSoluzione() chiamato.");

        if (piano.funzioni.size() < 2) {
            rispostaLabel.setText("Aggiungi due funzioni per risolvere!");
            rispostaLabel.revalidate();
            rispostaLabel.repaint();
            return;
        }

        Funzione f1 = piano.funzioni.get(0);
        Funzione f2 = piano.funzioni.get(1);

        // Intervallo di ricerca
        double minX = -10;
        double maxX = 10;

        // Trova le intersezioni
        List<Double> soluzioniX = RisolutoreEquazioneMista.trovaIntersezioni(f1, f2, minX, maxX);

        // Debug: Stampa le soluzioni in console
        System.out.println("Soluzioni trovate: " + soluzioniX);

        // Aggiorna il testo del rispostaLabel
        if (!soluzioniX.isEmpty()) {
            StringBuilder risultato = new StringBuilder("Punti di intersezione: ");
            for (Double x : soluzioniX) {
                double y = f1.calcola(x); // Calcola il valore di y usando f1
                String formattedX = formatNumber(x);
                String formattedY = formatNumber(y);
                risultato.append(String.format("(%s, %s) ", formattedX, formattedY));
            }
            rispostaLabel.setText(risultato.toString());
        } else {
            rispostaLabel.setText("Nessuna soluzione trovata");
        }

        // Forza il ridisegno del componente
        rispostaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rispostaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rispostaLabel.revalidate();
        rispostaLabel.repaint();
        
        // Disegna le intersezioni sul piano cartesiano
        piano.setIntersezioni(soluzioniX, f1, f2);
    }

    // Funzione di supporto per formattare i numeri
    private String formatNumber(double value) {
        // Imposta il locale per garantire che il separatore decimale sia sempre il punto
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Assicura che il separatore decimale sia il punto

        // Formatta il numero con quattro cifre decimali
        DecimalFormat formatter = new DecimalFormat("#.####", symbols);
        formatter.setGroupingUsed(false); // Disabilita la formattazione di gruppi (migliaia)

        return formatter.format(value);
    }

    private void createInputPanel(PianoCartesiano piano) {
        inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBounds(20, 20, 300, 120);
        inputPanel.setBackground(new Color(240, 240, 240));
        inputPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BorderLayout());

        JTextField inputField = new JTextField();
        JButton submitButton = new JButton("Aggiungi");
        JButton clearButton = new JButton("Clear");

        submitButton.addActionListener(e -> {
            String input = inputField.getText().toLowerCase();
            try {
            	piano.clearFunzioni();
                Funzione[] fns = ScomposizioneEquazione.scomponiFunzione(input);
                piano.aggiungiFunzioni(Arrays.asList(fns));
                calcolaSoluzione();
                inputField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            piano.clearFunzioni();
            piano.repaint();
            rispostaLabel.setText("Risultato: ");  // Rimuovi la risposta quando viene cancellata
        });

        innerPanel.add(inputField, BorderLayout.CENTER);
        innerPanel.add(submitButton, BorderLayout.EAST);

        inputPanel.add(innerPanel, BorderLayout.CENTER);
        inputPanel.add(clearButton, BorderLayout.SOUTH);
    }

    private void toggleInputPanel(PianoCartesiano piano) {
        inputVisible = !inputVisible;
        inputPanel.setVisible(inputVisible);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int margin = 20;

        // Posiziona reset
        reset.setLocation(getWidth() - reset.getWidth() - margin, getHeight() - reset.getHeight() - margin);

        // Posiziona toggleInput
        toggleInput.setLocation(margin, getHeight() - toggleInput.getHeight() - margin);

        // Posiziona rispostaLabel
        rispostaLabel.setBounds(getWidth() - 320, margin, 300, 30);
        rispostaLabel.revalidate();
        rispostaLabel.repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1200, 800);
    }
}