package com.debates.crf.implementation.luke.utils;

/**
 * Created by lukasz on 31.05.16.
 */

import com.debates.crf.Tag;
import com.debates.crf.implementation.luke.feature.PreviousPosAndTagFeature;

import java.util.HashMap;

/**
 * Created by lukasz on 23.05.16.
 */
public class PreviousPosAndTagFeatureBuilder2 {

    private final static Tag[] sortedTags = {
            Tag.PROPOSITION_START,
            Tag.REASON_START,
//            Tag.OTHER
    };

    private final static double[] weight_COMP = {
            0.02d,0.01d,
//            1.0d

//            82.0d/348,
//            94.0d/258,
//            5104.0d/105564
    };


    private final static HashMap<String, Double> previousPosAndTagFeatures = new HashMap<>();

    static {
        for (int i = 0; i < sortedTags.length; ++i) {
            String tag = sortedTags[i].name();
            previousPosAndTagFeatures.put(tag, weight_COMP[i]);
        }
    }

    public static PreviousPosAndTagFeature buildFeature(String forPos, String forTag) {
        if(!"comp".equals(forPos)) {
            throw new IllegalArgumentException();
        }
        Double weight = previousPosAndTagFeatures.get(forTag);
        if(weight != null) {
            return new PreviousPosAndTagFeature(forPos, forTag, weight);
        }
        throw new IllegalArgumentException();
    }

}
