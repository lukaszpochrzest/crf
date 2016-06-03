package com.debates.crf.implementation.luke.utils;

import com.debates.crf.implementation.luke.feature.PreviousPosAndTagFeature;
import com.debates.crf.implementation.luke.feature.PreviousWordAndTagFeature;

import static com.debates.crf.implementation.luke.utils.TagStatistics.lowestCount;
import static com.debates.crf.implementation.luke.utils.TagStatistics.tagCounts;

/**
 * Created by lukasz on 03.06.16.
 */
public class PreviousWordAndTagFeatureBuilder {

    public static PreviousWordAndTagFeature buildFeature(String forPos, String forTag) {
        Integer count = tagCounts.get(forTag);
        if(count != null) {
            return new PreviousWordAndTagFeature(forPos, forTag, (double)lowestCount/count);
        }
        throw new IllegalArgumentException();
    }
}
