package com.debates.crf.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lukasz on 04.05.16.
 */
public class ProgramParams {

    public static String description() {
        return  "\tProgram takes single arg: dir to folder with .txt and .ann files and prints crf result in format:\n\t\tWORD(CRF_LABEL,ORYG_LABEL) ...\n\n" +
                "\t" + PARAM_HELP[0] + "\thelp\n" +
                "\t" + PARAM_TRAINING_DATA_DIR + "\ttraining data directory (with txt and ann files)\n";
    }

    private static final String[] PARAM_HELP = {
            "-help",
            "help",
            "-h",
            "h"
    };

    private static final String PARAM_TRAINING_DATA_DIR = "-trainDir";

    private static final String PARAM_TEST_DATA_FILE = "-testFile";

    private Map<String, String> params = new HashMap<>();

    public ProgramParams(String args[]) {
        parseParamHelp(args);
        parseParamTrainingDataDir(args);
        parseParamTestDataFile(args);
    }

    private void parseParamHelp(String args[]) {
        List<String> helpList = Arrays.asList(PARAM_HELP);
        for(String arg : args) {
            if(helpList.contains(arg)) {
                params.put(PARAM_HELP[0], "");
                return;
            }
        }
    }

    private void parseParamTrainingDataDir(String args[]) {
        for(int i = 0; i < args.length; ++i) {
            if(PARAM_TRAINING_DATA_DIR.equals(args[i]) && i < args.length - 1) {
                params.put(PARAM_TRAINING_DATA_DIR, args[i+1]);
                return;
            }
        }
    }

    private void parseParamTestDataFile(String args[]) {
        for(int i = 0; i < args.length; ++i) {
            if(PARAM_TEST_DATA_FILE.equals(args[i]) && i < args.length - 1) {
                params.put(PARAM_TEST_DATA_FILE, args[i+1]);
                return;
            }
        }
    }

    public boolean isHelp() {
        return params.get(PARAM_HELP[0]) != null;
    }

    public String getTrainingDataDir() {
        return params.get(PARAM_TRAINING_DATA_DIR);
    }

    public String getTestDataFile() {
        return params.get(PARAM_TEST_DATA_FILE);
    }

}
