package es.easda.virema.munsell;

import es.ua.dlsi.im3.core.score.PitchClass;
import es.ua.dlsi.im3.core.score.PitchClasses;
import es.ua.dlsi.im3.core.score.ScientificPitch;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MunsellColorGenerator {
    /**
     * [octave][pitch class]
     */
    Color [][] colors;

    NavigableMap<ScientificPitch, Color> scientificPitchColorHashMap;

    public MunsellColorGenerator(int octaves) throws Exception {
        MunsellTreeModel munsellTreeModel = new MunsellTreeModel();
        colors = new Color[octaves][PitchClasses.values().length];
        scientificPitchColorHashMap = new TreeMap<>();

        PitchClass [] pitchClassesV = new PitchClass[PitchClasses.values().length];
        int [] base40Values = new int[PitchClasses.values().length];
        String [] hueNames = new String[base40Values.length];

        int i=0;
        for (PitchClasses pitchClasses: PitchClasses.values()) {
            pitchClassesV[i] = pitchClasses.getPitchClass();
            int base40 = pitchClasses.getPitchClass().getBase40Chroma();
            base40Values[i] = base40;
            String hueName = munsellTreeModel.getHues()[base40];
            hueNames[i] = hueName;
            i++;
        }

        for (int octave = 0; octave < octaves; octave++) {
            final int V = octave;
            // find the highest C
            int maxC = 0;
            for (int C=0; C<20; C++) {
                boolean allOK = true;
                for (String hueName: hueNames) {
                    List<MunsellColor> colorsHue = munsellTreeModel.getColors(hueName);
                    final int finalC = C;
                    Supplier<Stream<MunsellColor>> streamSupplier = () -> colorsHue.stream().filter(munsellColor -> munsellColor.getV() == V && munsellColor.getC() == finalC);
                    if (streamSupplier.get().count() == 0 || streamSupplier.get().anyMatch(munsellColor -> munsellColor.toColor() == Color.WHITE)) {
                        allOK = false;
                        break;
                    }
                }
                if (allOK) {
                    maxC = C;
                }
            }

            System.out.println("For L = " + octave + ", the maximum possible C is " + maxC);
            for (i=0; i<hueNames.length; i++) {
                List<MunsellColor> colorsHue = munsellTreeModel.getColors(hueNames[i]);
                final int finalC = maxC;
                Stream<MunsellColor> colorStream = colorsHue.stream().filter(munsellColor -> munsellColor.getV() == V && munsellColor.getC() == finalC);
                List<MunsellColor> colorList = colorStream.collect(Collectors.toList());
                Color color;
                if (colorList.isEmpty()) {
                    color = Color.WHITE;
                } else if (colorList.size() > 1) {
                    throw new Exception("Color stream should be 1, and it is " + colorList.size());
                } else {
                    color = colorList.get(0).toColor();
                }

                colors[octave][i] = color;

                scientificPitchColorHashMap.put(new ScientificPitch(pitchClassesV[i], octave), color);
            }

        }
    }

    public Color getColor(int octave, PitchClass pitchClass) {
        return getColor(new ScientificPitch(pitchClass, octave));
    }

    public Color getColor(ScientificPitch scientificPitch) {
        return scientificPitchColorHashMap.get(scientificPitch);
    }
}
