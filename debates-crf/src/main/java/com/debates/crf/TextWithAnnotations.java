package com.debates.crf;

import java.io.File;

/**
 * Created by lukasz on 12.04.16.
 */
public class TextWithAnnotations {

    private final File textFile;
    private final File bratAnnotationsFile;

    public TextWithAnnotations(File textFile, File bratAnnotationsFile) {
        this.textFile = textFile;
        this.bratAnnotationsFile = bratAnnotationsFile;
    }

    public File getTextFile() {
        return textFile;
    }

    public File getAnnotationsFile() {
        return bratAnnotationsFile;
    }

}
