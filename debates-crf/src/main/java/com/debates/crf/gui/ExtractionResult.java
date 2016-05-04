package com.debates.crf.gui;

import java.util.List;

/**
 * Created by lukasz on 14.04.16.
 */
public class ExtractionResult {
    List<SentimentStatement> sentimentStatements;
    List<ClassifiedStatement> classifiedStatements;

    public ExtractionResult(List<SentimentStatement> sentimentStatements,
                            List<ClassifiedStatement> classifiedStatements) {
        this.sentimentStatements = sentimentStatements;
        this.classifiedStatements = classifiedStatements;
    }

    public List<SentimentStatement> getSentimentStatements() {
        return sentimentStatements;
    }

    public void setSentimentStatements(List<SentimentStatement> sentimentStatements) {
        this.sentimentStatements = sentimentStatements;
    }

    public List<ClassifiedStatement> getClassifiedStatements() {
        return classifiedStatements;
    }

    public void setClassifiedStatements(List<ClassifiedStatement> classifiedStatements) {
        this.classifiedStatements = classifiedStatements;
    }
}