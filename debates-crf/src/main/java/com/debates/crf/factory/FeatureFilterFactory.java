package com.debates.crf.factory;

import com.debates.crf.filter.PreviousPosAndTagFilter;
import com.debates.crf.filter.PreviousWordAndTagFilter;
import com.debates.crf.filter.WordAndTagFilter;
import com.debates.crf.utils.PosUtility;
import org.crf.crf.filters.Filter;
import org.crf.crf.filters.FilterFactory;
import org.crf.crf.filters.TwoTagsFilter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by lukasz on 20.04.16.
 */
public class FeatureFilterFactory implements org.crf.crf.filters.FilterFactory<String, String> {

    @Override
    public Set<Filter<String, String>> createFilters(String[] sequence,
                                                     int tokenIndex,
                                                     String currentTag,
                                                     String previousTag) {
        String token = sequence[tokenIndex];
        Set<Filter<String, String>> ret = new LinkedHashSet<>();
        //add TwoTagsFilter(for TagTransitionFeature)
        ret.add(new TwoTagsFilter<>(currentTag, previousTag));
        if(tokenIndex > 0) {
            String previousPos = PosUtility.getPoS(sequence[tokenIndex - 1]);
            //  PreviousPosAndTagFilter
            ret.add(new PreviousPosAndTagFilter(previousPos, currentTag));

            //  PreviousWordAndTagFilter
            ret.add(new PreviousWordAndTagFilter(sequence[tokenIndex - 1], currentTag));
        }

        //  WordAndTagFilter
        ret.add(new WordAndTagFilter(token, currentTag));

        return ret;
    }
}
