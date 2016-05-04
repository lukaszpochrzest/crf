package com.debates.crf.gui;

import com.jjlteam.domain.Statement;

/**
 * Created by lukasz on 14.04.16.
 */
public class SentimentStatement extends Statement {
    private Double sentiment;

    public SentimentStatement(Long startIndex, Long endIndex, Double sentiment) {
        super(startIndex, endIndex);
        this.sentiment = sentiment;
    }

    public Double getSentiment() {
        return sentiment;
    }

    public void setSentiment(Double sentiment) {
        this.sentiment = sentiment;
    }
}
