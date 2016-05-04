package com.debates.crf.gui;

import com.jjlteam.domain.Statement;

/**
 * Created by lukasz on 14.04.16.
 */
public class ClassifiedStatement extends Statement {
    private MarkClasses propositionClass;

    private MarkClasses reasonClass;

    public ClassifiedStatement(Long startIndex, Long endIndex) {
        super(startIndex, endIndex);
    }

    public MarkClasses getPropositionClass() {
        return propositionClass;
    }

    public void setPropositionClass(MarkClasses propositionClass) {
        this.propositionClass = propositionClass;
    }

    public MarkClasses getReasonClass() {
        return reasonClass;
    }

    public void setReasonClass(MarkClasses reasonClass) {
        this.reasonClass = reasonClass;
    }

    public Boolean hasCommonPart(Statement statement) {
        Long maxBegin = Math.max(this.getStartIndex(), statement.getStartIndex());
        Long minEnd = Math.min(this.getEndIndex(), statement.getEndIndex());

        return maxBegin <= minEnd;
    }
}

