package com.company.xvspellconverter.app;

import com.company.xvspellconverter.entity.Word;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ConvertXVText {

    private List<String> namesFrom;
    private List<String> namesTarget;
    private final StringBuilder finalText = new StringBuilder();

    @Autowired
    private DataManager dataManager;

    public String execute(String textToConvert){
        /*if (dataManager == null) {
            throw new BusinessException("dataManager is null");
        }
        this.namesFrom = dataManager.load(Word.class).all().list().stream().map(Word::getFrom).toList();
        this.namesTarget = dataManager.load(Word.class).all().list().stream().map(Word::getTo).toList();*/

        textToConvert = cleanText(textToConvert);
        List<String> words = Arrays.asList(textToConvert.split("(?=[.,:;! “”‘’()—\\n\\r])|(?<=[.,:;! “”‘’()—\\n\\r])"));
        words.forEach(this::convertWord);
        return finalText.toString();
    }

    private String cleanText(String textToConvert) {
        char phantomSpace = '\u00a0';
        char space = ' ';
        char apostrofe = '\'';
        char glotal = '\ua78c';
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

    private String convertName(String nameToConvert){
        if(nameToConvert.length()>1){
            var index = namesFrom.indexOf(nameToConvert);
            if(index>0){
                return getNameConverted(index);
            }
        }
        return nameToConvert;
    }

    private String getNameConverted(int index) {
        try {
            return namesTarget.get(index);
        } catch (IndexOutOfBoundsException  e) {
            throw new BusinessException("Name found on the list, but not found the respective pair.", e);
        }

    }

    private String convertNameWithSuffix(String nameToConvert){
        var nameLength = nameToConvert.length();
        if (!nameToConvert.isBlank()) {
            var lastChar = nameToConvert.charAt(nameLength - 1);
            var suffix = "h" + lastChar;
            if (nameToConvert.endsWith(suffix)) {
                var nameWithoutSuffix = nameToConvert.substring(0, nameLength-2);
                var index = namesFrom.indexOf(nameWithoutSuffix);
                if (index > 0) {
                    return getNameConverted(index);
                }
            }
        }
        return nameToConvert;
    }


}