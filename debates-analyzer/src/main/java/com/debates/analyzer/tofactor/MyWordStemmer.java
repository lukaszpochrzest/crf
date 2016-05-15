package com.debates.analyzer.tofactor;

import morfologik.stemming.IStemmer;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 12.04.16.
 */
public class MyWordStemmer {

    private final IStemmer stemmer;

    public MyWordStemmer(final IStemmer stemmer) {
        this.stemmer = stemmer;
    }


    /**
     * Returns the most probable stem of the given word.
     * If there is no stem for a given word, that word
     * is returned as its own stem.
     *
     * @param word word
     * @return stem of the word
     */
    public String getStemNotNull(final String word) {
        String[] stems = getStems(word);
        if (stems.length == 0)
            return word;
        return stems[0];
    }

    /**
     * Returns all possible stems of the given word. If there is
     * no stem for a word, returned array is empty.
     *
     * @param word word to stem
     * @return array of stems
     */
    private String[] getStems(final String word) {
        ArrayList<String> result = stemmer.lookup(word).stream()
                .map(wd -> asString(wd.getStem()))
                .collect(Collectors.toCollection(ArrayList::new));
        return result.toArray(new String[result.size()]);
    }


    private String asString(final CharSequence s) {
        if (s == null)
            return null;
        return s.toString();
    }

}