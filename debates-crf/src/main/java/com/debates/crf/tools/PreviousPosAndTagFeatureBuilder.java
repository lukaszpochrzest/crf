package com.debates.crf.tools;


import com.debates.crf.feature.PreviousPosAndTagFeature;

import static com.debates.crf.tools.TagStatistics.lowestCount;
import static com.debates.crf.tools.TagStatistics.tagCounts;

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
