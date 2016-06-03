package com.debates.crf.utils;

import java.util.List;

/**
 * Created by lukasz on 02.06.16.
 */
public class Config {

    private List<String> trainFileNames;
    private List<String> testFileNames;
    private String modelFileName;
    private Double minimizerConvergence;

    public List<String> getTrainFileNames() {
        return trainFileNames;
    }

    public void setTrainFileNames(List<String> trainFileNames) {
        this.trainFileNames = trainFileNames;
    }

    public List<String> getTestFileNames() {
        return testFileNames;
    }

    public void setTestFileNames(List<String> testFileNames) {
        this.testFileNames = testFileNames;
    }

    public String getModelFileName() {
        return modelFileName;
    }

    public void setModelFileName(String modelFileName) {
        this.modelFileName = modelFileName;
    }

    public Double getMinimizerConvergence() {
        return minimizerConvergence;
    }

    public void setMinimizerConvergence(Double minimizerConvergence) {
        this.minimizerConvergence = minimizerConvergence;
    }
}
