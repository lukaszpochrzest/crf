package com.debates.crf.tools;


import com.debates.crf.feature.PreviousWordAndTagFeature;

import static com.debates.crf.tools.TagStatistics.lowestCount;
import static com.debates.crf.tools.TagStatistics.tagCounts;

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
