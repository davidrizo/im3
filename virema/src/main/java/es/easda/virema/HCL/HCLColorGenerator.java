package es.easda.virema.HCL;

import es.easda.virema.IColorGenerator;
import javafx.scene.paint.Color;

import java.util.NavigableMap;
import java.util.TreeMap;

public class HCLColorGenerator implements IColorGenerator {
    public static final int L_INCREMENT = 10;
    public static final int C_INCREMENT = 5;
    int cCount;
    int lCount;
    int hCount;

    /**
     * [H][L][C]
     */
    private Color[][][] colors;
    /**
     * [L][H]
     */
    private Color[][] musicPalette;

    public HCLColorGenerator() throws Exception {
        createCompleteHCLPalette();
        createMusicPalette();
    }

    private void createCompleteHCLPalette() {
        hCount = 12;
        lCount = HCLColor.MAX_L /  L_INCREMENT + 1;
        cCount = HCLColor.MAX_C /  C_INCREMENT + 1;

        int degrees = 360/12;
        colors = new Color[hCount][lCount][cCount];
        int hIndex = 0;
        for (int h=0; h<360; h+=degrees) {
            createPalette(hIndex, h);
            hIndex++;
        }
    }

    private void createPalette(int hIndex, int h) {
        int lIndex = 0;
        for (int L=0; L<=HCLColor.MAX_L; L+=L_INCREMENT) {
            int cIndex = 0;
            for (int C=0; C<=HCLColor.MAX_C; C+=C_INCREMENT) {
                HCLColor hclColor = new HCLColor(h, C, L);
                Color color = hclColor.toColor();

                colors[hIndex][lIndex][cIndex] = color;

                cIndex ++;
            }
            lIndex++;
        }
    }

    private void createMusicPalette() throws Exception {
        musicPalette = new Color[lCount][hCount];
        for (int lIndex = 0; lIndex < lCount; lIndex++) {
            int lastValidCIndex = -1;
            for (int cIndex = 0; cIndex < cCount; cIndex++) {
                boolean allValid = true;
                for (int hIndex = 0; allValid && hIndex < hCount; hIndex++) {
                    Color color = colors[hIndex][lIndex][cIndex];
                    allValid = !color.equals(Color.WHITE);
                }
                if (allValid) {
                    lastValidCIndex = cIndex;
                }
            }
            if (lastValidCIndex == -1) {
                throw new Exception("Cannot find a valid C index");
            }
            for (int hIndex = 0; hIndex < hCount; hIndex++) {
                musicPalette[lIndex][hIndex] = colors[hIndex][lIndex][lastValidCIndex];
            }
        }
    }

    public Color[][][] getColors() {
        return colors;
    }

    public Color[][] getMusicPalette() {
        return musicPalette;
    }

    public int getcCount() {
        return cCount;
    }

    public int getlCount() {
        return lCount;
    }

    public int gethCount() {
        return hCount;
    }

    @Override
    public NavigableMap<Integer, Color> generateColors() {
        NavigableMap<Integer, Color> result = new TreeMap<>();
        int fromOctave = 1;
        int toOctave = 8;

        int Lindex=1;
        for (int ioctave = fromOctave; ioctave <= toOctave; ioctave++) {
            int Hindex = 0;
            for (int pitchClass=0; pitchClass < 12; pitchClass++) {
                int pitch = ioctave*12 + pitchClass;
                Color color = this.musicPalette[Lindex][Hindex];
                result.put(pitch, color);
                Hindex = (Hindex + 7)%12; // circle of fifths
            }
            Lindex++;
        }

        return result;
    }
}
