package com.debates.crf.feature;

import com.debates.crf.utils.PosUtility;
import org.crf.crf.CrfFeature;

import static org.crf.utilities.PosTaggerUtilities.equalObjects;

/**
 * Created by lukasz on 20.04.16.
 */


public class PreviousPosAndTagFeature extends CrfFeature<String, String> {

    private final String forPreviousPos;
    private final String forTag;
    private final double weight;

    public PreviousPosAndTagFeature(String forPreviousPos, String forTag, double weight) {
        this.forPreviousPos = forPreviousPos;
        this.forTag = forTag;
        this.weight = weight;
    }

    @Override
    public double value(String[] sequence, int indexInSequence, String currentTag, String previousTag) {
        if(indexInSequence == 0) {  //there is no previous pos
            return 0.0d;
        }
        double ret = 0.0;
        if (equalObjects(currentTag,forTag) && equalObjects(PosUtility.getPoS(sequence[indexInSequence-1]), forPreviousPos)) {
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
        PreviousPosAndTagFeature other = (PreviousPosAndTagFeature) obj;
        if (forTag == null)
        {
            if (other.forTag != null)
                return false;
        } else if (!forTag.equals(other.forTag))
            return false;
        if (forPreviousPos == null)
        {
            if (other.forPreviousPos != null)
                return false;
        } else if (!forPreviousPos.equals(other.forPreviousPos))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((forTag == null) ? 0 : forTag.hashCode());
        result = prime * result
                + ((forPreviousPos == null) ? 0 : forPreviousPos.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "PreviousPosAndTagFeature [forPreviousPos=" + forPreviousPos + ", forTag="
                + forTag + "]";
    }
}