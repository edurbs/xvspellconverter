package com.company.xvspellconverter.infra.view.word;

import com.company.xvspellconverter.infra.entity.Word;

import com.company.xvspellconverter.infra.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "words/:id", layout = MainView.class)
@ViewController("Word.detail")
@ViewDescriptor("word-detail-view.xml")
@EditedEntityContainer("wordDc")
public class WordDetailView extends StandardDetailView<Word> {
}