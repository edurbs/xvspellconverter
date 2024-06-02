package com.company.xvspellconverter.app;

import com.company.xvspellconverter.entity.Word;
import io.jmix.core.DataManager;
import io.jmix.core.querycondition.PropertyCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ConvertXVText {

    private final StringBuilder finalText = new StringBuilder();

    @Autowired
    private DataManager dataManager;

    public String execute(String textToConvert){
        finalText.setLength(0);


        textToConvert = cleanText(textToConvert);
        List<String> words = Arrays.asList(textToConvert.split("(?=[.,:;! “”‘’()—\\n\\r])|(?<=[.,:;! “”‘’()—\\n\\r])"));
        words.forEach(this::convert);
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

    private void convert(String word){
        if(!word.isBlank()){
            word = convertLetters(word);
            word = convertWordWithSuffix(word);
            word = convertWord(word);
        }
        finalText.append(word);
    }

    private String convertLetters(String word){
        Pattern iFirstPersonLowerCase = Pattern.compile("(^| )ĩ", Pattern.CANON_EQ);
        word = iFirstPersonLowerCase.matcher(word).replaceAll("ĩ̱");
        Pattern iFirstPersonUpperCase = Pattern.compile("(^| )Ĩ", Pattern.CANON_EQ);
        word = iFirstPersonUpperCase.matcher(word).replaceAll("Ĩ̱");
        Pattern iThirdPersonLowerCase = Pattern.compile("(^| )i", Pattern.CANON_EQ);
        word = iThirdPersonLowerCase.matcher(word).replaceAll("ĩ");
        Pattern iThirdPersonUpperCase = Pattern.compile("(^| )I", Pattern.CANON_EQ);
        word = iThirdPersonUpperCase.matcher(word).replaceAll("Ĩ");
        word = replaceLetter(word, "Z[A-ZÀ-ÖØ-ÝĀ-Ž]", "DZ");
        word = replaceLetter(word, "z[a-zà-öø-ýā-ž]", "dz");
        word = replaceLetter(word, "Z[a-zà-öø-ýā-ž]", "Dz");
        word = replaceLetter(word, "S[A-ZÀ-ÖØ-ÝĀ-Ž]", "TS");
        word = replaceLetter(word, "s[a-zà-öø-ýā-ž]", "ts");
        word = replaceLetter(word, "S[a-zà-öø-ýā-ž]", "Ts");
        word = word.replace("â", "ö");
        word = word.replace("Â", "Ö");
        return word;
    }

    private static String replaceLetter(String word, String regex, String replaceWith) {
        Pattern lowerCaseS = Pattern.compile(regex);
        Matcher matcher = lowerCaseS.matcher(word);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String matchedLetter = matcher.group(0);
            matcher.appendReplacement(sb, replaceWith + matchedLetter.substring(1));
        }
        matcher.appendTail(sb);
        word = sb.toString();
        return word;
    }

    private String convertWord(String wordToConvert){
        if(wordToConvert.length()>1){
            Word word = dataManager.load(Word.class)
                    .condition(PropertyCondition.equal("from", wordToConvert))
                    .optional()
                    .orElse(null);
            if(word!=null){
                wordToConvert = word.getTo();
            }
        }
        return wordToConvert;
    }


    private String convertWordWithSuffix(String wordToConvert){
        var wordLength = wordToConvert.length();
        if (!wordToConvert.isBlank()) {
            var lastChar = wordToConvert.charAt(wordLength - 1);
            var suffix = "h" + lastChar;
            if (wordToConvert.endsWith(suffix)) {
                var wordWithoutSuffix = wordToConvert.substring(0, wordLength-2);
                Word word = dataManager.load(Word.class)
                        .condition(PropertyCondition.equal("from", wordWithoutSuffix))
                        .optional()
                        .orElse(null);
                if(word!=null){
                    wordToConvert = word.getTo();
                }
            }
        }
        return wordToConvert;
    }


}