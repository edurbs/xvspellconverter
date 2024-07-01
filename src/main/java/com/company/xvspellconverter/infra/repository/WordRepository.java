package com.company.xvspellconverter.infra.repository;

import com.company.xvspellconverter.infra.entity.Word;
import io.jmix.core.repository.JmixDataRepository;

import java.util.List;

public interface WordRepository extends JmixDataRepository<Word, Long> {

    List<Word> findByFrom(String from);
    List<Word> findByTo(String to);

}