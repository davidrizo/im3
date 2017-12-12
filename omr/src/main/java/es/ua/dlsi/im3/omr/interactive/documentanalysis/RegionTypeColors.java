package es.ua.dlsi.im3.omr.interactive.documentanalysis;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;
import javafx.scene.paint.Color;

import java.util.HashMap;

public class RegionTypeColors {
    private HashMap<RegionType, Color> colors;

    private RegionTypeColors() {
        this.colors = new HashMap<>();
        this.colors.put(RegionType.author, Color.PINK);
        this.colors.put(RegionType.title, Color.VIOLET);
        this.colors.put(RegionType.lyrics, Color.YELLOW);
        this.colors.put(RegionType.staff, Color.BLUE);
        this.colors.put(RegionType.unknwon, Color.RED);
    }

    public static RegionTypeColors instance = null;

    public static final RegionTypeColors getInstance() {
        synchronized (RegionTypeColors.class) {
            if (instance == null) {
                instance = new RegionTypeColors();
            }
        }
        return instance;
    }
    public Color getColor(RegionType regionType, double opacity) {
        Color color = colors.get(regionType);
        if (color == null) {
            throw new IM3RuntimeException("Cannot find a color for region type: " + regionType);
        }
        Color result = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
        return result;
    }
}
