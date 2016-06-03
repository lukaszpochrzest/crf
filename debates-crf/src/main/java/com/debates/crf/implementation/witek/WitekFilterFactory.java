package com.debates.crf.implementation.witek;

import com.debates.crf.Tag;
import com.debates.crf.implementation.luke.filter.PosAndTagFilter;
import com.debates.crf.implementation.martyna.filter.DebateFeatureFilter;
import com.debates.crf.utils.PosUtility;
import org.crf.crf.filters.Filter;
import org.crf.crf.filters.FilterFactory;
import org.crf.crf.filters.TagFilter;
import org.crf.crf.filters.TwoTagsFilter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by lukasz on 25.05.16.
 */
public class WitekFilterFactory implements FilterFactory<String, String> {

    @Override
    public Set<Filter<String, String>> createFilters(String[] sequence,
                                                     int tokenIndex,
                                                     String currentTag,
                                                     String previousTag) {
        //  TODO edit this if new features added

        String token = sequence[tokenIndex];
        Set<Filter<String, String>> ret = new LinkedHashSet<>();
        ret.add(new PosAndTagFilter(PosUtility.getPoS(token), currentTag)); //  for PosAndTagFeatures

        //for WordAndTagFeatures
        if (Tag.PROPOSITION_START.name().equals(currentTag) || Tag.REASON_START.name().equals(currentTag)) {
            ret.add(new DebateFeatureFilter(token, currentTag));
        }

        ret.add(new TagFilter<>(currentTag));   // for TagFeatures
        ret.add(new TwoTagsFilter<>(currentTag, previousTag));  //  for TagTransitionFeatures

        return ret;
    }
}
