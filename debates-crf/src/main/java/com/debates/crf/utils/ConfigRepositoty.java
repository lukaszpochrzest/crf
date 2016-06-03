package com.debates.crf.utils;

/**
 * Created by lukasz on 03.06.16.
 */
public class ConfigRepositoty {

    private static Config config;

    public static Config getConfig() {
        return config;
    }

    public static void setConfig(Config config) {
        ConfigRepositoty.config = config;
    }
}
