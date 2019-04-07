package es.ua.dlsi.im3.core.io;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.utils.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.LinkedHashMap;

import static org.junit.Assert.*;

public class JSONGlyphNamesReaderTest {

    @Test
    public void readCodepointToOrderedGlyphMap() throws ImportException {
        File file = TestFileUtils.getFile("/testdata/core/io/font_mapping.json");
        JSONGlyphNamesReader jsonGlyphNamesReader = new JSONGlyphNamesReader(file);
        LinkedHashMap<String, String> unicodes = jsonGlyphNamesReader.readCodepointToOrderedGlyphMap();
        assertEquals("Size", 7, unicodes.size());
        assertEquals("First element", "U+E901", unicodes.entrySet().iterator().next().getKey());

        LinkedHashMap<String, Object> mappings = jsonGlyphNamesReader.getMappings();
        assertEquals("Size", 7, mappings.size());
        assertEquals("First clef.G", "clef.G", mappings.entrySet().iterator().next().getKey());

    }
}
