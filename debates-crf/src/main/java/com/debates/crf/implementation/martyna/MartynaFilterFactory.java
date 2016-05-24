package com.debates.crf.implementation.martyna;

import com.debates.crf.Tag;
import com.debates.crf.implementation.martyna.filter.DebateFeatureFilter;
import org.crf.crf.filters.Filter;
import org.crf.crf.filters.FilterFactory;
import org.crf.crf.filters.TagFilter;
import org.crf.crf.filters.TwoTagsFilter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by lukasz on 23.05.16.
 */
public class MartynaFilterFactory implements FilterFactory<String, String> {

    @Override
    public Set<Filter<String, String>> createFilters(String[] sequence,
                                                     int tokenIndex,
                                                     String currentTag,
                                                     String previousTag) {
        //  TODO edit this if new features added

        String token = sequence[tokenIndex];
        Set<Filter<String, String>> ret = new LinkedHashSet<>();

        //for DebateFeatures
        if (Tag.PROPOSITION_START.name().equals(currentTag) || Tag.REASON_START.equals(currentTag)) {
            ret.add(new DebateFeatureFilter(token, currentTag));
        }

        ret.add(new TagFilter<>(currentTag));   // for TagFeatures
        ret.add(new TwoTagsFilter<>(currentTag, previousTag));  //  for TagTransitionFeatures

        return ret;
    }
}
