package com.debates.crf.implementation.luke.feature;

import org.crf.crf.CrfFeature;
import org.crf.utilities.PosTaggerUtilities;

/**
 * Created by lukasz on 03.06.16.
 */
public class MyTagTransitionFeature extends CrfFeature<String, String> {

    private static final long serialVersionUID = -61200838311988363L;
    private final String forPreviousTag;
    private final String forCurrentTag;

    public MyTagTransitionFeature(String forPreviousTag, String forCurrentTag) {
        this.forPreviousTag = forPreviousTag;
        this.forCurrentTag = forCurrentTag;
    }
    @Override
    public double value(String[] sequence, int indexInSequence, String currentTag, String previousTag) {
        double ret = 0.0D;
        if(PosTaggerUtilities.equalObjects(previousTag, this.forPreviousTag) && PosTaggerUtilities.equalObjects(currentTag, this.forCurrentTag)) {
            ret = 0.0001D;
        }

        return ret;
    }

    public String toString() {
        return "TagTransitionFeature [forPreviousTag=" + this.forPreviousTag + ", forCurrentTag=" + this.forCurrentTag + "]";
    }

    public int hashCode() {
        boolean prime = true;
        byte result = 1;
        int result1 = 31 * result + (this.forCurrentTag == null?0:this.forCurrentTag.hashCode());
        result1 = 31 * result1 + (this.forPreviousTag == null?0:this.forPreviousTag.hashCode());
        return result1;
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(obj == null) {
            return false;
        } else if(this.getClass() != obj.getClass()) {
            return false;
        } else {
            MyTagTransitionFeature other = (MyTagTransitionFeature)obj;
            if(this.forCurrentTag == null) {
                if(other.forCurrentTag != null) {
                    return false;
                }
            } else if(!this.forCurrentTag.equals(other.forCurrentTag)) {
                return false;
            }

            if(this.forPreviousTag == null) {
                if(other.forPreviousTag != null) {
                    return false;
                }
            } else if(!this.forPreviousTag.equals(other.forPreviousTag)) {
                return false;
            }

            return true;
        }
    }
}
