package com.debates.crf.implementation.luke;

import com.debates.crf.Tag;
import com.debates.crf.implementation.luke.filter.PreviousPosAndTagFilter;
import com.debates.crf.implementation.luke.filter.PreviousWordAndTagFilter;
import com.debates.crf.implementation.martyna.filter.DebateFeatureFilter;
import com.debates.crf.implementation.witek.filter.WordAndTagFilter;
import com.debates.crf.utils.PosUtility;
import org.crf.crf.filters.Filter;
import org.crf.crf.filters.FilterFactory;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by lukasz on 20.04.16.
 */
public class Luke1FilterFactory implements FilterFactory<String, String> {

    @Override
    public Set<Filter<String, String>> createFilters(String[] sequence,
                                                     int tokenIndex,
                                                     String currentTag,
                                                     String previousTag) {
        //  TODO edit this if new features added

        String token = sequence[tokenIndex];
        Set<Filter<String, String>> ret = new LinkedHashSet<>();
//        ret.add(new TagFilter<>(currentTag));
//        ret.add(new TwoTagsFilter<>(currentTag, previousTag));
//        ret.add(new PosAndTagFilter(PosUtility.getPoS(token), currentTag));
        if(tokenIndex > 0) {
            String previousPos = PosUtility.getPoS(sequence[tokenIndex - 1]);
//
//            //  PreviousPosAndTagFilter
            ret.add(new PreviousPosAndTagFilter(previousPos, currentTag));

            //  PreviousWordAndTagFilter
//            if ( Tag.PROPOSITION_START.name().equals(currentTag) || Tag.REASON_START.name().equals(currentTag)) {
//            if(!Tag.OTHER.name().equals(currentTag)) {
                ret.add(new PreviousWordAndTagFilter(sequence[tokenIndex - 1], currentTag));
//            }
//            }
        }

        //  WordAndTagFilter
//        if ( Tag.PROPOSITION_START.name().equals(currentTag) || Tag.REASON_START.name().equals(currentTag)) {
            ret.add(new WordAndTagFilter(token, currentTag));
//        }

        return ret;
    }
}
