package com.debates.crf.feature.generator;

import com.debates.crf.Tag;
import com.debates.crf.feature.PreviousPosAndTagFeature;

import java.util.HashMap;

/**
 * Created by lukasz on 23.05.16.
 */
public class PreviousPosAndTagFeatureBuilder {

    private final static Tag[] sortedTags = {
            Tag.OTHER,
            Tag.PROPOSITION,
            Tag.REASON,
            Tag.PROPOSITION_START,
            Tag.PROPOSITION_END,
            Tag.REASON_START,
            Tag.REASON_END };

    private final static int[] count = {
            98521,
            3196,
            2647,
            348,
            342,
            258,
            252 };

    private static int lowestCount = count[count.length-1];

    private final static HashMap<String, Integer> previousPosAndTagFeatures = new HashMap<>();

    static {
        for (int i = 0; i < sortedTags.length; ++i) {
            String tag = sortedTags[i].name();
            previousPosAndTagFeatures.put(tag, count[i]);
        }
    }

    /**
     * Corpus stats:
     *
     *  Tags
     *  98521	OTHER
     *  3196	PROPOSITION
     *  2647	REASON
     *  348     PROPOSITION_START
     *  342     PROPOSITION_END
     *  258     REASON_START
     *  252     REASON_END
     *
     * @param forPos
     * @param forTag
     * @return
     */
    public static PreviousPosAndTagFeature buildFeature(String forPos, String forTag) {
        Integer count = previousPosAndTagFeatures.get(forTag);
        if(count != null) {
                return new PreviousPosAndTagFeature(forPos, forTag, (double)lowestCount/count);
        }
        throw new IllegalArgumentException();
    }

}
