package com.company.xvspellconverter.view.word;

import com.company.xvspellconverter.entity.Word;

import com.company.xvspellconverter.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "words/:id", layout = MainView.class)
@ViewController("Word.detail")
@ViewDescriptor("word-detail-view.xml")
@EditedEntityContainer("wordDc")
public class WordDetailView extends StandardDetailView<Word> {
}