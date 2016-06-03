package com.debates.crf.feature;

import org.crf.crf.CrfFeature;

import static org.crf.utilities.PosTaggerUtilities.equalObjects;

/**
 * Created by lukasz on 03.06.16.
 */
public class PreviousWordAndTagFeature extends CrfFeature<String, String> {

    private final String forPreviousWord;
    private final String forTag;
    private final double weight;

    public PreviousWordAndTagFeature(String forPreviousWord, String forTag, double weight) {
        this.forPreviousWord = forPreviousWord;
        this.forTag = forTag;
        this.weight = weight;
    }

    @Override
    public double value(String[] sequence, int indexInSequence, String currentTag, String previousTag) {
        if(indexInSequence == 0) {  //there is no previous word
            return 0.0d;
        }
        double ret = 0.0;
        if (equalObjects(currentTag,forTag) && equalObjects(sequence[indexInSequence - 1], forPreviousWord)) {
            ret = weight;
        }
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PreviousWordAndTagFeature other = (PreviousWordAndTagFeature) obj;
        if (forTag == null)
        {
            if (other.forTag != null)
                return false;
        } else if (!forTag.equals(other.forTag))
            return false;
        if (forPreviousWord == null)
        {
            if (other.forPreviousWord != null)
                return false;
        } else if (!forPreviousWord.equals(other.forPreviousWord))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((forTag == null) ? 0 : forTag.hashCode());
        result = prime * result
                + ((forPreviousWord == null) ? 0 : forPreviousWord.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "PreviousWordAndTagFeature [forPreviousWord=" + forPreviousWord + ", forTag="
                + forTag + "]";
    }
}