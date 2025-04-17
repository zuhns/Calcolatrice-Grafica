package idk;

import java.awt.Graphics2D;
import java.util.List;

public class AdaptiveFunctionDrawer {

    public static void draw(Graphics2D g2, List<Funzione> funzioni, double offsetX, double offsetY, double scale, int width, int height) {
        double worldPerPixel = 1.0 / scale;
        int SAMPLES;
        
        double baseSamples = 64;
        double maxSamples = 256;
        double minSamples = 16;

        double peakScale = 3.0;
        double scaleDiff = scale - peakScale;
        double factor = 1.0 / (1.0 + scaleDiff * scaleDiff);  // forma a campana centrata su 3

        SAMPLES = (int) Math.max(minSamples,
            Math.min(maxSamples, baseSamples + baseSamples * factor));


        for (Funzione funzione : funzioni) {
            g2.setColor(funzione.getColore());

            for (int sx = 0; sx < width; sx++) {
                double xLeft  = (sx - offsetX) / scale;
                double xRight = xLeft + worldPerPixel;

                double yMin = Double.POSITIVE_INFINITY;
                double yMax = Double.NEGATIVE_INFINITY;
                boolean hasInvalid = false;

                for (int i = 0; i < SAMPLES; i++) {
                    double t = (double) i / (SAMPLES - 1);
                    double xSample = xLeft + t * (xRight - xLeft);
                    try {
                        double y = funzione.calcola(xSample);
                        if (!Double.isFinite(y)) {
                            hasInvalid = true;
                            continue;
                        }
                        if (y < yMin) yMin = y;
                        if (y > yMax) yMax = y;
                    } catch (Exception ex) {
                        hasInvalid = true;
                    }
                }

                if (yMin <= yMax) {
					if (yMin == Double.POSITIVE_INFINITY) yMin = 0;
					if (yMax == Double.NEGATIVE_INFINITY) yMax = 0;							
                    if ((yMax - yMin > 1e3) && (yMin * yMax < 0)) continue;

                    int syMin = (int) (offsetY - yMax * scale);
                    int syMax = (int) (offsetY - yMin * scale);

                    if (hasInvalid) {
                        if (xLeft < 0 && xRight > 0) {
                            syMax = height;
                        } else {
                            if (yMin > 0) syMin = 0;
                            else syMax = height;
                        }
                    }

                    if (syMin < 0) syMin = 0;
                    if (syMax > height) syMax = height;
                    if (syMin <= syMax) g2.drawLine(sx, syMin, sx, syMax);
                    continue;
                }

                if (xLeft < 0 && xRight > 0) {
                    int originY = (int) offsetY;
                    if (originY < 0) originY = 0;
                    g2.drawLine(sx, originY, sx, height);
                }
            }
        }
    }
}
