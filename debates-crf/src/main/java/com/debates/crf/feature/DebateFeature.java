package com.debates.crf.feature;

import org.crf.crf.CrfFeature;

import static org.crf.utilities.PosTaggerUtilities.equalObjects;

/**
 * Created by Martyna on 2016-05-16.
 */
public class DebateFeature extends CrfFeature<String, String> {

    private String forTag;
    private String featureName;

    public DebateFeature(String forTag, String featureName) {
        this.forTag = forTag;
        this.featureName = featureName;
    }

    @Override
    public double value(String[] sequence, int indexInSequence,
                        String currentTag, String previousTag) {
        double ret = 0.0;
        if (equalObjects(currentTag,forTag) && sequence[indexInSequence].equals( featureName ) ) {
            ret = 1.0;
        }
        return ret;
    }


    @Override
    public String toString() {
        return "DebateFeature [forTag=" + forTag + ", featureName= "+ featureName + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((forTag == null) ? 0 : forTag.hashCode());
        result = prime * result + featureName.hashCode();
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
        DebateFeature other = (DebateFeature) obj;
        if (forTag == null)
        {
            if (other.forTag != null)
                return false;
        } else if (!forTag.equals(other.forTag))
            return false;
        if ( featureName.equals( other.featureName ) )
            return true;
        return true;
    }

}
