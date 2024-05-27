package com.company.xvspellconverter.app;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ConvertXVText {

    private final StringBuilder finalText = new StringBuilder();

    public String getFinalText() {
        return finalText.toString();
    }

    public String convert(String textToConvert){
        //this.namesFrom = names.getNamesFrom();
        //this.namesTarget = names.getNamesTarget();
        textToConvert = cleanText(textToConvert);
        List<String> words = Arrays.asList(textToConvert.split("(?=[.,:;! “”‘’()—\\n\\r])|(?<=[.,:;! “”‘’()—\\n\\r])"));
        words.forEach(this::convertWord);
        return finalText.toString().trim();
    }

    private String cleanText(String textToConvert) {
        char phantomSpace = '\u00a0';
        char space = ' ';
        char apostrofe = '\'';
        char glotal = 'ꞌ';
        textToConvert = textToConvert.replace(phantomSpace, space);
        textToConvert = textToConvert.replace(apostrofe, glotal);
        return textToConvert;
    }

    private void convertWord(String word){
        if(!word.isBlank()){
            word = convertLetters(word);
            //word = convertNameWithSuffix(word);
            //word = convertName(word);
        }
        finalText.append(word);
    }

    private String convertLetters(String word){
        word = word.replace("s", "ts");
        word = word.replace("S", "Ts");
        word = word.replace("z", "dz");
        word = word.replace("Z", "Dz");
        word = word.replace("â", "ö");
        word = word.replace("Â", "Ö");
        Pattern iFirstPersonLowerCase = Pattern.compile("(^|\s)ĩ", Pattern.CANON_EQ);
        word = iFirstPersonLowerCase.matcher(word).replaceAll("ĩ̱");
        Pattern iFirstPersonUpperCase = Pattern.compile("(^|\s)Ĩ", Pattern.CANON_EQ);
        word = iFirstPersonUpperCase.matcher(word).replaceAll("Ĩ̱");
        Pattern iThirdPersonLowerCase = Pattern.compile("(^|\s)i", Pattern.CANON_EQ);
        word = iThirdPersonLowerCase.matcher(word).replaceAll("ĩ");
        Pattern iThirdPersonUpperCase = Pattern.compile("(^|\s)I", Pattern.CANON_EQ);
        word = iThirdPersonUpperCase.matcher(word).replaceAll("Ĩ");
        return word;
    }


}