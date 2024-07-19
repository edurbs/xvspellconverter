package com.company.xvspellconverter.infra.controller;

import com.company.xvspellconverter.app.usecase.ConvertUseCase;
import io.jmix.rest.annotation.RestMethod;
import io.jmix.rest.annotation.RestService;
import org.springframework.beans.factory.annotation.Autowired;

@RestService("convert")
public class ConvertController {

    @Autowired
    private ConvertUseCase convertUseCase;

    @RestMethod
    public String execute(String text) {
        return convertUseCase.execute(text);
    }
}