package com.debates.crf.feature.filter;

import org.crf.crf.filters.Filter;

/**
 * Created by Witek on 2016-05-14.
 */
public class WordAndTagFeatureFilter extends Filter<String, String> {

    private final String word;
    private final String currentTag;

    private transient int hashCodeValue = 0;
    private transient boolean hashCodeCalculated = false;

    public WordAndTagFeatureFilter(String word, String currentTag) {
        this.word = word;
        this.currentTag = currentTag;
    }

    @Override
    public int hashCode() {
        if (hashCodeCalculated)
        {
            return hashCodeValue;
        }
        else
        {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((currentTag == null) ? 0 : currentTag.hashCode());
            result = prime * result + word.hashCode();
            hashCodeValue = result;
            hashCodeCalculated = true;
            return result;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WordAndTagFeatureFilter other = (WordAndTagFeatureFilter) obj;
        if (currentTag == null)
        {
            if (other.currentTag != null)
                return false;
        } else if (!currentTag.equals(other.currentTag))
            return false;
        if ( word.equals( other.word ) )
            return true;
        return false;
    }
}
