package com.company.xvspellconverter.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConvertXVTextTest {

    static private ConvertXVText convertXVText;

    @BeforeAll
    static void setup(){
        convertXVText = new ConvertXVText();
    }

    @Test
    void shouldConvertZtoDZ() {
        assertEquals("aiwadzé", convertXVText.execute("aiwazé"));
        assertEquals("dzara", convertXVText.execute("zara"));
        assertEquals("Dzara", convertXVText.execute("Zara"));
        assertEquals("DZARA", convertXVText.execute("ZARA"));
    }

    @Test
    void shouldConvertTtoTS(){
        assertEquals("petse", convertXVText.execute("pese"));
        assertEquals("tsaré", convertXVText.execute("saré"));
        assertEquals("Tsaré", convertXVText.execute("Saré"));
        assertEquals("TSARÉ", convertXVText.execute("SARÉ"));
    }

    @Test
    void shouldConvertAtoO(){
        assertEquals("ö", convertXVText.execute("â"));
        assertEquals("Ö", convertXVText.execute("Â"));
        assertEquals("öiba", convertXVText.execute("âiba"));
        assertEquals("Öiba", convertXVText.execute("Âiba"));
        assertEquals("ÖIBA", convertXVText.execute("ÂIBA"));
        assertEquals("aibö", convertXVText.execute("aibâ"));
        assertEquals("Aibö", convertXVText.execute("Aibâ"));
        assertEquals("AIBÖ", convertXVText.execute("AIBÂ"));

    }

    @Test
    void shouldConvertI(){
        assertEquals("ĩ̱na", convertXVText.execute("ĩna"));
        assertEquals("Ĩ̱na", convertXVText.execute("Ĩna"));
        assertEquals("ĩna", convertXVText.execute("ina"));
        assertEquals("Ĩna", convertXVText.execute("Ina"));
        assertEquals("ĨNA", convertXVText.execute("INA"));
        assertEquals("norĩ", convertXVText.execute("norĩ"));
        assertEquals("NORĨ", convertXVText.execute("NORĨ"));
        assertEquals("ĩ̱morĩ", convertXVText.execute("ĩmorĩ"));
        assertEquals("aihĩni", convertXVText.execute("aihĩni"));
    }

    @Test
    void shouldRemovePhantonSpace(){
        assertEquals("text text", convertXVText.execute("text text"));
    }

    @Test
    void shouldConvertGlobal(){
        assertEquals("danhiptiꞌa", convertXVText.execute("danhipti'a"));
    }

    @Test
    void shouldConvertAllWordFromALine(){
        assertEquals("ĩꞌadzadaꞌöbödzém na dzama. 48 Ĩmama norĩ hã", convertXVText.execute("i'azada'âbâzém na zama. 48 Imama norĩ hã"));
    }
}