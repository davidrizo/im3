package es.ua.dlsi.im3.mavr.model.harmony;

import edu.stanford.vis.color.LAB;
import javafx.scene.paint.Color;

/**
 * @autor drizo
 */
public class IntegerHSB {
    int hue;
    int saturation;
    int bright;

    /**
     *
     * @param hue [0,355]
     * @param saturation [0,99]
     * @param bright [0,99]
     */
    public IntegerHSB(int hue, int saturation, int bright) {
        this.hue = hue;
        this.saturation = saturation;
        this.bright = bright;
    }

    /**
     *
     * @return [0,355]
     */
    public int getHue() {
        return hue;
    }

    /**
     *
     * @return [0,100]
     */
    public int getSaturation() {
        return saturation;
    }

    /**
     *
     * @return  [0,100]
     */
    public int getBright() {
        return bright;
    }

    //TODO Test unitario
    public LAB toLAB() {
        Color color = Color.hsb(hue, (double)saturation / 100.0, (double) bright / 100.0);
        return LAB.fromRGB((int)(color.getRed()*255.0), (int)(color.getGreen()*255.0), (int)(color.getBlue()*255.0), 0);
    }
}
