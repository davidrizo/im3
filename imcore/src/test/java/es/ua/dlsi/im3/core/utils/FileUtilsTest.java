package es.ua.dlsi.im3.core.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class FileUtilsTest {

    @Test
    public void leaveValidCaracters() {
        String input = "España 2018, arroç i tartana";
        String expectedOutput = "Espa_a_2018__arro__i_tartana";
        assertEquals("Valid caracters", expectedOutput, FileUtils.leaveValidCaracters(input, '_'));
    }
}