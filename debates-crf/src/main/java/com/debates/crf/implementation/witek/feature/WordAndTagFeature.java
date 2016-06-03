package com.debates.crf.implementation.witek.feature;

import org.crf.crf.CrfFeature;

import static org.crf.utilities.PosTaggerUtilities.equalObjects;

/**
 * Created by Witek on 2016-05-14.
 */
public class WordAndTagFeature extends CrfFeature<String, String> {

    private final String word;
    private final String forTag;
    private final double weight;

    public WordAndTagFeature(String word, String forTag, double weight) {
        this.word = word;
        this.forTag = forTag;
        this.weight = weight;
    }

    @Override
    public double value(String[] sequence, int indexInSequence, String currentTag, String previousTag) {
        double ret = 0.0;
        if (equalObjects(currentTag,forTag) && sequence[indexInSequence].equals( word ) ) {
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
        WordAndTagFeature other = (WordAndTagFeature) obj;
        if (forTag == null)
        {
            if (other.forTag != null)
                return false;
        } else if (!forTag.equals(other.forTag))
            return false;
        if ( word.equals( other.word ) )
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((forTag == null) ? 0 : forTag.hashCode());
        result = prime * result
                + word.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "WordAndTagFeature [word=" + word + ", forTag="
                + forTag + "]";
    }
}