package com.company.xvspellconverter.app;

import com.company.xvspellconverter.app.gateway.GetWordGateway;
import com.company.xvspellconverter.app.usecase.ConvertUseCase;
import com.company.xvspellconverter.infra.entity.Word;
import com.company.xvspellconverter.infra.gateway.GetWordRepositoryGateway;
import com.company.xvspellconverter.infra.repository.WordRepository;
import com.company.xvspellconverter.test_support.AuthenticatedAsAdmin;
import io.jmix.core.DataManager;
import io.jmix.core.FluentLoader;
import io.jmix.core.querycondition.PropertyCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(AuthenticatedAsAdmin.class)
class ConvertUseCaseTest {


    ConvertUseCase convertXVText;
    @Autowired
    private DataManager dataManager;


    @BeforeEach
    void setUp() {

        GetWordGateway  getWordRepositoryGateway = new GetWordRepositoryGateway(dataManager);
        this.convertXVText = new ConvertUseCase(getWordRepositoryGateway);
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
    void shouldConvertGlotal(){
        assertEquals("danhiptiꞌa", convertXVText.execute("danhipti'a"));
    }

    @Test
    void shouldConvertNameWithSuffix(){
        assertEquals("Moisés", convertXVText.execute("Mozésihi"));
    }

    @Test
    void shouldConvertAllWordFromALine(){

        String textToConvert = "i'azada'âbâzém na ãhã zama tahã. 48 Ãhã Zuwã Mozésihi Imama norĩ hã";
        String expected      = "ĩꞌadzadaꞌöbödzém na ãhã dzama tahã. 48 Ãhã João Moisés Ĩmama norĩ hã";
        assertEquals(expected, convertXVText.execute(textToConvert));
    }
}
