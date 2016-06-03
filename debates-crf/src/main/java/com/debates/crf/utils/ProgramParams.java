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
        return  "\tProgram takes single arg path to config file\n\n" +
                "\t" + PARAM_HELP[0] + "\thelp\n" +
                "\t" + PARAM_CONFIG_FILE + "\tconfig file\n";
    }

    private static final String[] PARAM_HELP = {
            "-help",
            "help",
            "-h",
            "h"
    };

    private static final String PARAM_CONFIG_FILE = "-conf";

    private Map<String, String> params = new HashMap<>();

    public ProgramParams(String args[]) {
        parseParamHelp(args);
        parseParamTrainingDataDir(args);
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
            if(PARAM_CONFIG_FILE.equals(args[i]) && i < args.length - 1) {
                params.put(PARAM_CONFIG_FILE, args[i+1]);
                return;
            }
        }
    }

    public boolean isHelp() {
        return params.get(PARAM_HELP[0]) != null;
    }

    public String getParamConfigFile() {
        return params.get(PARAM_CONFIG_FILE);
    }

}
