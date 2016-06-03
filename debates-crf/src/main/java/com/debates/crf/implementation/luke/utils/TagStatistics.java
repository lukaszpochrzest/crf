package com.debates.crf.implementation.luke.utils;

import com.debates.crf.Tag;

import java.util.HashMap;

/**
 * Created by lukasz on 03.06.16.
 */
public class TagStatistics {

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
     */

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

    public static int lowestCount = count[count.length-1];

    public final static HashMap<String, Integer> tagCounts = new HashMap<>();

    static {
        for (int i = 0; i < sortedTags.length; ++i) {
            String tag = sortedTags[i].name();
            tagCounts.put(tag, count[i]);
        }
    }
}
