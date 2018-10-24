package es.ua.dlsi.grfia.im3ws.muret.entity;

import org.springframework.aop.ThrowsAdvice;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * It is very similar to the format used in the Calco application
 */
public class CalcoStrokes extends Strokes {
    public static final String PREFIX = "C:";
    private static final String STROKESEP = "&";
    private static final String COMMA = ",";
    private static final String SEMICOLON = ";";
    List<CalcoStroke> strokes;

    public CalcoStrokes(List<CalcoStroke> strokes) {
        this.strokes = strokes;
    }

    public CalcoStrokes() {
        this.strokes = new LinkedList<>();
    }

    public void addStroke(CalcoStroke stroke) {
        this.strokes.add(stroke);
    }

    @Override
    public List<? extends Stroke> getStrokes() {
        return strokes;
    }

    public void setStrokes(List<CalcoStroke> strokes) {
        this.strokes = strokes;
    }

    public static CalcoStrokes parse(String s) throws IOException {
        try {
            String[] lines = s.substring(PREFIX.length()).split(STROKESEP);

            CalcoStrokes calcoStrokes = new CalcoStrokes();
            for (String line : lines) {
                String[] coordList = line.split(SEMICOLON);
                CalcoStroke stroke = new CalcoStroke();
                for (String sl : coordList) {
                    String[] coords = sl.split(COMMA);
                    if (coords.length != 3) {
                        throw new IOException("Invalid coordinate, must have 3 components (time, x, y) and it has " + coords.length + ": '" + sl + "'");
                    }
                    long time = Long.parseLong(coords[0]);
                    Point point = new Point(time, Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
                    stroke.addPoint(point);
                }
                calcoStrokes.addStroke(stroke);
            }
            return calcoStrokes;
        } catch (Throwable t) {
            String message = "Cannot parse string: '" + s + "'";
            throw new IOException(message);
        }
    }

    @Override
    public String toDatabaseString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(PREFIX); // To denote type
        boolean first = true;
        for (CalcoStroke stroke: strokes) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(STROKESEP);
            }
            boolean firstPoint = true;
            for (Point point: stroke.getPoints()) {
                if (firstPoint) {
                    firstPoint = false;
                } else {
                    stringBuilder.append(SEMICOLON);
                }
                stringBuilder.append(point.getTime());
                stringBuilder.append(COMMA);
                stringBuilder.append(point.getX());
                stringBuilder.append(COMMA);
                stringBuilder.append(point.getX());
            }
        }

        return stringBuilder.toString();
    }


}
