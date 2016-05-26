package com.debates.analyzer.tofactor;

import morfologik.stemming.IStemmer;
import morfologik.stemming.WordData;
import morfologik.stemming.polish.PolishStemmer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by lukasz on 20.04.16.
 */
public class PosUtility {

    /**
     *
     */
    private static HashSet<String> morfeuszPoses = new HashSet<>(Arrays.asList(
            "subst",
            "depr",
            "adj",
            "adja",
            "adjp",
            "adv",
            "num",
            "ppron12",
            "ppron3",
            "siebie",
            "fin",
            "bedzie",
            "aglt",
            "praet",
            "impt",
            "imps",
            "inf",
            "pcon",
            "pant",
            "ger",
            "pact",
            "ppas",
            "winien",
            "pred",
            "prep",
            "conj",
            "qub",
            "xxs",
            "xxx",
            "verb",  //  mine
            "interj",  //  mine
            "comp",  //  mine
            "burk"  //  mine
    ));

    private static IStemmer stemmer = new PolishStemmer();

    public static String getPoS(final String word) {
        List<WordData> wordDataList = stemmer.lookup(word);
        if (wordDataList.isEmpty()) {
            return "undef";
        }
        String pos = asString(wordDataList.get(0).getTag())
                .split(":", 2)[0]
                .split("\\+", 2)[0];

        if(!morfeuszPoses.contains(pos)) {
            throw new RuntimeException("Morfeusz doesnt include " + pos + " (of word " + word + ")." +
                    "HACK - add it to com.debates.crf.utils.PosUtility");
        }

        return pos;
    }

    private static String asString(final CharSequence s) {
        if (s == null)
            return null;
        return s.toString();
    }

}
