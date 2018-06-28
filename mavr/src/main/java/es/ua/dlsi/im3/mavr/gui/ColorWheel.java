package es.ua.dlsi.im3.mavr.gui;

import javafx.scene.paint.Color;

/**
 * It contains a 12 division color wheel
 * @autor drizo
 */
public class ColorWheel {
    Color[] colors;

    public ColorWheel(int divisions, double saturation, double brightness) {
        int i=0;
        colors = new Color[divisions];
        for (int degree=0; degree<360; degree+=(360/divisions)) {
            colors[i++] = Color.hsb(degree, saturation, brightness);
        }
    }

    public Color[] getColors() {
        return colors;
    }
}
