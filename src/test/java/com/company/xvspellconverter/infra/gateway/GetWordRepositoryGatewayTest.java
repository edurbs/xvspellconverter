package com.company.xvspellconverter.infra.gateway;

import com.company.xvspellconverter.app.gateway.GetWordGateway;
import com.company.xvspellconverter.app.usecase.ConvertUseCase;
import com.company.xvspellconverter.test_support.AuthenticatedAsAdmin;
import io.jmix.core.DataManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(AuthenticatedAsAdmin.class)
class GetWordRepositoryGatewayTest {


    GetWordGateway getWordRepositoryGateway;
    @Autowired
    private DataManager dataManager;


    @BeforeEach
    void setUp() {
        this.getWordRepositoryGateway = new GetWordRepositoryGateway(dataManager);
    }

    @Test
    void getOneWordToByFrom() {
        String word = this.getWordRepositoryGateway.getOneWordToByFrom("Dzuwã");
        assertEquals("João", word);
    }

}