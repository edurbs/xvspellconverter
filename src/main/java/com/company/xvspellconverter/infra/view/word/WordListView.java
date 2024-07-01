package com.company.xvspellconverter.infra.view.word;

import com.company.xvspellconverter.infra.entity.Word;

import com.company.xvspellconverter.infra.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "words", layout = MainView.class)
@ViewController("Word.list")
@ViewDescriptor("word-list-view.xml")
@LookupComponent("wordsDataGrid")
@DialogMode(width = "64em")
public class WordListView extends StandardListView<Word> {
}