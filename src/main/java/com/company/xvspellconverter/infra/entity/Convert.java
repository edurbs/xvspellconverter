package com.company.xvspellconverter.infra.entity;

import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JmixEntity(name = "Convert_")
public class Convert {
    @NotBlank
    @NotEmpty
    @JmixProperty(mandatory = true)
    @NotNull
    private String from;

    @NotEmpty
    @NotBlank
    @JmixProperty(mandatory = true)
    @NotNull
    private String to;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}