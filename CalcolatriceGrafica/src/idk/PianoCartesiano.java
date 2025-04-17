package idk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pannello che disegna un piano cartesiano interattivo con supersampling
 * per gestire correttamente asintoti verticali (es. ln x).
 */
public class PianoCartesiano extends JPanel
        implements MouseListener, MouseMotionListener, MouseWheelListener {

    private static final int WIDTH  = 1200;
    private static final int HEIGHT = 800;

    // trasformazione mondo → schermo
    private double offsetX;
    private double offsetY;
    private double scale = 50.0; // pixel per unità

    private Point dragStart;

    private final DecimalFormat labelFormat;
    public final List<Funzione> funzioni = new ArrayList<>();
    
 // Variabili per memorizzare le intersezioni e le funzioni coinvolte
    private List<Double> intersezioni = null;
    private Funzione funzione1 = null;
    private Funzione funzione2 = null;

    public PianoCartesiano() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        labelFormat = new DecimalFormat("0.##E0", symbols);

        offsetX = WIDTH  / 2.0;
        offsetY = HEIGHT / 2.0;
    }
    
    public void aggiungiFunzioni(List<Funzione> list) 
    {
        for (Funzione f : list)
            funzioni.add(f);
        repaint();
    }
    
    public void clearFunzioni() 
    {
    	funzioni.clear();
    	intersezioni = null;
        repaint();
		
	}

    /* reset viewport al centro */
    public void resetView() {
        scale   = 50.0;
        offsetX = getWidth()  / 2.0;
        offsetY = getHeight() / 2.0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        drawGrid(g2);
        drawAxes(g2);
        drawFunzioni(g2);
        
        // Disegna le intersezioni solo se sono state calcolate
        if (intersezioni != null && !intersezioni.isEmpty()) {
            drawIntersezioni(g2, intersezioni, funzione1, funzione2);
        }
    }

    /** Disegna le funzioni campionando ogni colonna di pixel (SAMPLES punti). */
    private void drawFunzioni(Graphics2D g2) {
        AdaptiveFunctionDrawer.draw(g2, funzioni, offsetX, offsetY, scale, getWidth(), getHeight());
    }
    
    public void setIntersezioni(List<Double> intersezioni, Funzione f1, Funzione f2) {
        this.intersezioni = intersezioni;
        this.funzione1 = f1;
        this.funzione2 = f2;
        repaint(); // Richiede il ridisegno del componente
    }
    
    public void drawIntersezioni(Graphics g, List<Double> intersezioni, Funzione f1, Funzione f2) {
        Graphics2D g2d = (Graphics2D) g;  // Casting a Graphics2D
        g2d.setColor(Color.GRAY);  // Imposta il colore del punto di intersezione

        int pointSize = 11;  // Dimensione del cerchio per i punti di intersezione

        for (double x : intersezioni) {
            // Calcola il valore di y usando f1
            double y = f1.calcola(x);

            // Verifica se il valore di y è finito
            if (!Double.isFinite(y)) {
                System.err.println("Valore di y non finito per x = " + x);
                continue; // Salta questo punto
            }

            // Calcola le coordinate schermo (screenX, screenY)
            int screenX = (int) (offsetX + x * scale);
            int screenY = (int) (offsetY - y * scale);

            // Disegna il cerchio (punto di intersezione) al punto (screenX, screenY)
            g2d.fillOval(screenX - pointSize / 2, screenY - pointSize / 2, pointSize, pointSize);
        }
    }



    /* griglia */
    private void drawGrid(Graphics2D g2) {
        int width  = getWidth();
        int height = getHeight();

        double stepLogical = calculateLogicalStep();
        double stepPixels  = stepLogical * scale;
        if (stepPixels < 1e-6) return;

        double startX = offsetX % stepPixels;
        double startY = offsetY % stepPixels;

        // linee secondarie (griglia 5×5)
        g2.setStroke(new BasicStroke(0.5f));
        g2.setColor(new Color(220, 220, 220));
        for (int i = 1; i < 5; i++) {
            double sub = i * stepPixels / 5.0;
            for (double x = startX + sub - stepPixels; x < width; x += stepPixels)
                g2.drawLine((int) x, 0, (int) x, height);
            for (double y = startY + sub - stepPixels; y < height; y += stepPixels)
                g2.drawLine(0, (int) y, width, (int) y);
        }

        // linee principali
        g2.setStroke(new BasicStroke(1.2f));
        g2.setColor(new Color(200, 200, 200));

        // verticali + etichette X
        for (double x = startX; x < width; x += stepPixels) {
            g2.drawLine((int) x, 0, (int) x, height);
            double worldX = Math.round(((x - offsetX) / scale) / stepLogical) * stepLogical;
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(formatLabel(worldX, stepLogical), (int) x - 10, (int) offsetY + 16);
            g2.setColor(new Color(200, 200, 200));
        }

        // orizzontali + etichette Y
        for (double y = startY; y < height; y += stepPixels) {
            g2.drawLine(0, (int) y, width, (int) y);
            double worldY = Math.round(((offsetY - y) / scale) / stepLogical) * stepLogical;
            if (worldY == 0) continue;
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(formatLabel(worldY, stepLogical), (int) offsetX + 2, (int) y - 2);
            g2.setColor(new Color(200, 200, 200));
        }
    }

    /* assi */
    private void drawAxes(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));
        int originX = (int) offsetX;
        int originY = (int) offsetY;
        g2.drawLine(0, originY, getWidth(), originY);
        g2.drawLine(originX, 0, originX, getHeight());
    }

    /* helpers */
    private double calculateLogicalStep() {
        double[] sequence = {1, 2, 5};
        double step = 1;

        // Trova il valore logaritmico del passo attuale (in unità mondo)
        double target = 50.0 / scale; // vogliamo almeno 50 pixel tra le linee

        // Trova l'esponente più vicino (base 10)
        double exponent = Math.floor(Math.log10(target));
        double base = Math.pow(10, exponent);

        // Trova il miglior step nella sequenza
        for (double s : sequence) {
            double candidate = s * base;
            if (candidate >= target) {
                return candidate;
            }
        }

        // Se nessuno soddisfa, passa al successivo ordine di grandezza
        return 10 * base;
    }



    private String formatLabel(double v, double step) {
        // 0 resta 0
        if (v == 0) return "0";

        double abs = Math.abs(v);

        // Notazione scientifica
        if (abs < 1e-3) {
            int exp  = (int) Math.floor(Math.log10(abs));
            double pow = Math.pow(10, exp);
            long coeff = Math.round(v / pow);
            if (Math.abs(coeff) == 10) { coeff /= 10; exp += 1; }
            return String.format(Locale.US, "%dE%d", coeff, exp);
        }

        // Notazione scientifica per valori molto grandi
        if (abs >= 1e4) {
            return labelFormat.format(v);
        }

        // Formato decimale dinamico basato su step
        int decimals = Math.max(0, (int) Math.ceil(-Math.log10(step)));
        decimals = Math.min(decimals, 6); // evita formati lunghissimi
        String fmt = "%." + decimals + "f";
        String s = String.format(Locale.US, fmt, v);
        // rimuovi zeri finali e punto se non necessario
        if (s.indexOf('.') >= 0) {
            s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        return s;
    }

    /* mouse */

    /* mouse */
    @Override public void mousePressed(MouseEvent e) { dragStart = e.getPoint(); }
    @Override public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - dragStart.x;
        int dy = e.getY() - dragStart.y;
        offsetX += dx; offsetY += dy;
        dragStart = e.getPoint(); repaint();
    }
    @Override public void mouseWheelMoved(MouseWheelEvent e) {
        double oldScale = scale;
        scale *= (e.getWheelRotation() < 0) ? 1.1 : 1 / 1.1;
        Point p = e.getPoint();
        double relX = (p.x - offsetX) / oldScale;
        double relY = (p.y - offsetY) / oldScale;
        offsetX = p.x - relX * scale;
        offsetY = p.y - relY * scale;
        repaint();
    }

    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}