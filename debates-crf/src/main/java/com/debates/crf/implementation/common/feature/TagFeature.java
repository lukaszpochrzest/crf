package com.debates.crf.implementation.common.feature;

import org.crf.crf.CrfFeature;

import static org.crf.utilities.PosTaggerUtilities.equalObjects;

/**
 * Created by lukasz on 20.04.16.
 */
public class TagFeature extends CrfFeature<String, String> {

    private String forTag;

    public TagFeature(String forTag) {
        this.forTag = forTag;
    }

    @Override
    public double value(String[] sequence, int indexInSequence,
                        String currentTag, String previousTag) {
        double ret = 0.0;
        if (equalObjects(currentTag,forTag)) {
            ret = 1.0;
        }
        return ret;
    }


    @Override
    public String toString() {
        return "TagFeature [forTag=" + forTag + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((forTag == null) ? 0 : forTag.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TagFeature other = (TagFeature) obj;
        if (forTag == null)
        {
            if (other.forTag != null)
                return false;
        } else if (!forTag.equals(other.forTag))
            return false;
        return true;
    }

}
