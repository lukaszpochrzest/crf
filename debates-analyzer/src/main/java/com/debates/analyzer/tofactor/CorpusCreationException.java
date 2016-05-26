package com.debates.analyzer.tofactor;

/**
 * Created by lukasz on 14.04.16.
 */
public class CorpusCreationException extends Exception {

    public CorpusCreationException() {
    }

    public CorpusCreationException(String message) {
        super(message);
    }

    public CorpusCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CorpusCreationException(Throwable cause) {
        super(cause);
    }

    public CorpusCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
