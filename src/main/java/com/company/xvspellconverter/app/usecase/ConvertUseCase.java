package com.company.xvspellconverter.app.usecase;

import com.company.xvspellconverter.app.gateway.GetWordGateway;
import com.company.xvspellconverter.infra.entity.Word;
import io.jmix.core.querycondition.PropertyCondition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ConvertUseCase {

    private final StringBuilder finalText = new StringBuilder();

    private final GetWordGateway getWordGateway;

    public ConvertUseCase(GetWordGateway getWordGateway) {
        this.getWordGateway = getWordGateway;
    }

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

    private String convertWord(final String originalWord){
        if(originalWord.length()>1){
            String newWord = getWordGateway.getOneWordToByFrom(originalWord);
            if(newWord!=null && !newWord.isBlank()){
                return newWord;
            }
        }
        return originalWord;
    }


    private String convertWordWithSuffix(final String originalWord){
        if(originalWord==null || originalWord.isBlank()){
            return originalWord;
        }
        int wordLength = originalWord.length();
        char lastChar = originalWord.charAt(wordLength - 1);
        String suffix = "h" + lastChar;
        if(!originalWord.endsWith(suffix)){
            return originalWord;
        }
        String wordWithoutSuffix = originalWord.substring(0, wordLength-2);
        if(wordWithoutSuffix.length()<2) {
            return originalWord;
        }
        String wordConverted = getWordGateway.getOneWordToByFrom(wordWithoutSuffix);
        if(wordConverted!=null && !wordConverted.isBlank()){
            return wordConverted;
        }
        return originalWord;
    }


}