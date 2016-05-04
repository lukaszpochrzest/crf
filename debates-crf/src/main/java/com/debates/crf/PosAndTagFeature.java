package com.debates.crf;

import com.debates.crf.gui.PosUtility;
import org.crf.crf.CrfFeature;

import static org.crf.utilities.PosTaggerUtilities.equalObjects;

/**
 * Created by lukasz on 20.04.16.
 */
public class PosAndTagFeature extends CrfFeature<String, String> {

    private final String forPos;
    private final String forTag;

    public PosAndTagFeature(String forPos, String forTag) {
        this.forPos = forPos;
        this.forTag = forTag;
    }

    @Override
    public double value(String[] sequence, int indexInSequence, String currentTag, String previousTag) {
        double ret = 0.0;
        if (equalObjects(currentTag,forTag) && equalObjects(PosUtility.getPoS(sequence[indexInSequence]), forPos)) {
            ret = 1.0;
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
        PosAndTagFeature other = (PosAndTagFeature) obj;
        if (forTag == null)
        {
            if (other.forTag != null)
                return false;
        } else if (!forTag.equals(other.forTag))
            return false;
        if (forPos == null)
        {
            if (other.forPos != null)
                return false;
        } else if (!forPos.equals(other.forPos))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((forTag == null) ? 0 : forTag.hashCode());
        result = prime * result
                + ((forPos == null) ? 0 : forPos.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "PosAndTagFeature [forPos=" + forPos + ", forTag="
                + forTag + "]";
    }
}
