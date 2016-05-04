package com.debates.crf;

import org.crf.crf.filters.Filter;

/**
 * Created by lukasz on 20.04.16.
 */
public class PreviousPosAndTagFilter extends Filter<String, String> {

    private final String pos;
    private final String currentTag;

    private transient int hashCodeValue = 0;
    private transient boolean hashCodeCalculated = false;

    public PreviousPosAndTagFilter(String pos, String currentTag) {
        this.pos = pos;
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
            result = prime * result + ((pos == null) ? 0 : pos.hashCode());
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
        PreviousPosAndTagFilter other = (PreviousPosAndTagFilter) obj;
        if (currentTag == null)
        {
            if (other.currentTag != null)
                return false;
        } else if (!currentTag.equals(other.currentTag))
            return false;
        if (pos == null)
        {
            if (other.pos != null)
                return false;
        } else if (!pos.equals(other.pos))
            return false;
        return true;
    }
}