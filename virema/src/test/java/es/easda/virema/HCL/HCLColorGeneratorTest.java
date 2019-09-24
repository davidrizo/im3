package es.easda.virema.HCL;

import javafx.scene.paint.Color;
import org.junit.Test;

import java.util.NavigableMap;

import static org.junit.Assert.*;

public class HCLColorGeneratorTest {

    @Test
    public void generateColors() throws Exception {
        HCLColorGenerator hclColorGenerator = new HCLColorGenerator();
        NavigableMap<Integer, Color> colors = hclColorGenerator.generateColors();
        System.out.println(colors);
    }
}
