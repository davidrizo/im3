package es.ua.dlsi.im3.mavr.gui;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Key;
import es.ua.dlsi.im3.core.score.Keys;
import es.ua.dlsi.im3.core.score.Mode;
import javafx.scene.paint.Color;

/**
 * Create based on the circle of fifths
 * @autor drizo
 */
public class KeyColorMapping {
    private Color [] colorsMajor;
    private Color [] colorsMinor;

    public KeyColorMapping() {
        colorsMajor = new Color[12];
        colorsMinor = new Color[12];
        //double degree = 240; // CMajor should yield blue for being more stable
        double degree = 300;
        int keyIndex = 0;
        for (int i=0; i<12; i++) {
            colorsMajor[keyIndex] = Color.hsb(degree, 0.8, 0.8);
            colorsMinor[keyIndex] = Color.hsb(degree, 0.5, 0.5); //TODO Munsell
            degree += (360/12)%360;
            keyIndex = (keyIndex + 7)%12; // fifth
        }
    }

    public Color getColor(Key key) throws IM3Exception {
        if (key.getMode() == Mode.MAJOR) {
            int i=key.getPitchClass().getSemitonesFromC();
            return colorsMajor[i];
        } else if (key.getMode() == Mode.MINOR){
            Key keyMajor = key.computeRelativeMajor();
            int i=keyMajor.getPitchClass().getSemitonesFromC();
            return colorsMinor[i];
        } else {
            throw new IM3Exception("Invalid key mode " + key.getMode());
        }
    }
}
