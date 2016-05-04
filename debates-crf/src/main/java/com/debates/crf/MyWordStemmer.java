package com.debates.crf;

import com.jjlteam.common.Pair;
import morfologik.stemming.IStemmer;
import morfologik.stemming.WordData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 12.04.16.
 */
public class MyWordStemmer {

    private final IStemmer stemmer;

    public MyWordStemmer(final IStemmer stemmer) {
        this.stemmer = stemmer;
    }

//    public String getPoS(final String word) {
//        List<WordData> wordDataList = stemmer.lookup(word);
//        if(wordDataList.isEmpty()) {
//            return "undef";
//        }
//        return asString(wordDataList.get(0).getTag())
//                .split(":", 2)[0]
//                .split("\\+", 2)[0];
//    }
//

    public Pair<String[], String[]> getStemAndPoS(final List<String> words) {
        String[] stems = new String[words.size()];
        String[] poss = new String[words.size()];
        int index = 0;
        for(String word : words) {
            List<WordData> wordDataList = stemmer.lookup(word);
            if(wordDataList.isEmpty()) {
                stems[index] = word;
                poss[index] = "undef";
            } else {
                stems[index] = asString(wordDataList.get(0).getStem());
                poss[index] = asString(wordDataList.get(0).getTag())
                        .split(":", 2)[0]
                        .split("\\+", 2)[0];
            }
            index++;
        }
        return new Pair<>(stems, poss);
    }

    /**
     * Returns word's stem and its PoS. If there is no stem
     * for a given word, word is returned as its own stem and
     * PoS is "undef".
     *
     * @param word
     * @return
     */
    public Pair<String, String> getStemAndPoS(final String word) {
        List<WordData> wordDataList = stemmer.lookup(word);
        if(wordDataList.isEmpty()) {
            return new Pair<>(word, "undef");
        }
        return new Pair<>(
                asString(wordDataList.get(0).getStem()),
                asString(wordDataList.get(0).getTag())
                        .split(":", 2)[0]
                        .split("\\+", 2)[0]
        );
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
    public String[] getStems(final String word) {
        ArrayList<String> result = stemmer.lookup(word).stream()
                .map(wd -> asString(wd.getStem()))
                .collect(Collectors.toCollection(ArrayList::new));
        return result.toArray(new String[result.size()]);
    }

    public String getPoS(final String word) {
        List<WordData> wordDataList = stemmer.lookup(word);
        if (wordDataList.isEmpty()) {
            return "undef";
        }
        return asString(wordDataList.get(0).getTag())
                .split(":", 2)[0]
                .split("\\+", 2)[0];
    }


    private String asString(final CharSequence s) {
        if (s == null)
            return null;
        return s.toString();
    }

}