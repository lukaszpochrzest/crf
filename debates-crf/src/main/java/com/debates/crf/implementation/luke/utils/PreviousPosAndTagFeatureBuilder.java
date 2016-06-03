package com.debates.crf.implementation.luke.utils;

import com.debates.crf.implementation.luke.feature.PreviousPosAndTagFeature;

import static com.debates.crf.implementation.luke.utils.TagStatistics.lowestCount;
import static com.debates.crf.implementation.luke.utils.TagStatistics.tagCounts;

/**
 * Created by lukasz on 23.05.16.
 */
public class PreviousPosAndTagFeatureBuilder {

    public static PreviousPosAndTagFeature buildFeature(String forPos, String forTag) {
        Integer count = tagCounts.get(forTag);
        if(count != null) {
                return new PreviousPosAndTagFeature(forPos, forTag, (double)lowestCount/count);
        }
        throw new IllegalArgumentException();
    }

}
