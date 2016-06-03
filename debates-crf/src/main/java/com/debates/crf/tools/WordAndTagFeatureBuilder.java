package com.debates.crf.tools;


import com.debates.crf.feature.WordAndTagFeature;

import static com.debates.crf.tools.TagStatistics.lowestCount;
import static com.debates.crf.tools.TagStatistics.tagCounts;

/**
 * Created by lukasz on 03.06.16.
 */
public class WordAndTagFeatureBuilder {

    public static WordAndTagFeature buildFeature(String forToken, String forTag) {
        Integer count = tagCounts.get(forTag);
        if(count != null) {
            return new WordAndTagFeature(forToken, forTag, (double)lowestCount/count);
        }
        throw new IllegalArgumentException();
    }

}
