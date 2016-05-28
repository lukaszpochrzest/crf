package com.debates.crf.implementation.witek.feature;

import org.crf.crf.CrfFeature;
import org.crf.postagging.postaggers.crf.features.TagTransitionFeature;
import org.crf.utilities.PosTaggerUtilities;

/**
 * Created by Witek on 2016-05-28.
 */
public class WeightedTransitionFeature extends CrfFeature<String, String>
{
    double returnWeight;
    private final String forPreviousTag;
    private final String forCurrentTag;

    public WeightedTransitionFeature(String forPreviousTag, String forCurrentTag, double weight)
    {
        this.forPreviousTag = forPreviousTag;
        this.forCurrentTag = forCurrentTag;
        returnWeight = weight;
    }

    public double value(String[] sequence, int indexInSequence, String currentTag, String previousTag) {
        double ret = 0.0D;
        if(PosTaggerUtilities.equalObjects(previousTag, this.forPreviousTag) && PosTaggerUtilities.equalObjects(currentTag, this.forCurrentTag)) {
            ret = returnWeight;
        }

        return ret;
    }

    public String toString() {
        return "WeightedTransitionFeature [forPreviousTag=" + this.forPreviousTag + ", forCurrentTag=" + this.forCurrentTag + ", weight=" + this.returnWeight + "]";
    }
    public int hashCode() {
        boolean prime = true;
        byte result = 1;
        int result1 = 31 * result + (this.forCurrentTag == null?0:this.forCurrentTag.hashCode());
        result1 = 31 * result1 + (this.forPreviousTag == null?0:this.forPreviousTag.hashCode());
        //result1 = 31 * result1 + returnWeight.hashCode();
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
            WeightedTransitionFeature other = (WeightedTransitionFeature)obj;
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
