package com.debates.crf;

import com.debates.crf.exception.CorpusCreationException;
import com.debates.crf.utils.Config;
import com.debates.crf.utils.ProgramParams;
import com.debates.crf.utils.TextWithAnnotations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lukasz on 12.04.16.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws CorpusCreationException {
        try {

            //  program params
            ProgramParams pp = new ProgramParams(args);
            if(pp.isHelp()) {
                System.out.println(ProgramParams.description());
                System.exit(1);
            }
//            String trainingDataDir = pp.getTrainingDataDir();
//            if(trainingDataDir == null) {
//                System.out.println(ProgramParams.description());
//                System.exit(1);
//            }
            String configFile = pp.getParamConfigFile();
            if(configFile == null) {
                System.out.println(ProgramParams.description());
                System.exit(1);
            }

            String configFileString = IOUtils.toString(new FileInputStream(configFile), "UTF8");
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            Config config = gson.fromJson(configFileString, Config.class);

            //  lets do some crf
            final List<TextWithAnnotations> trainingTwas = loadTextWithAnnotationPairs(config.getTrainFileNames());
            final List<TextWithAnnotations> testTwas = loadTextWithAnnotationPairs(config.getTestFileNames());

            CrfPerformer.perform(trainingTwas, testTwas);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            System.exit(1);
        }
    }

    private static List<TextWithAnnotations> loadTextWithAnnotationPairs(List<String> textFileNames) throws IOException {
        List<TextWithAnnotations> result = new ArrayList<>();

        for(String textFileName: textFileNames) {
            final File textFile = new File(textFileName);
            if(!textFile.exists()) {
                throw new IOException("File " + textFileName + " doesnt exist");
            }

            if(textFile.getPath().endsWith(".txt")) {
                // find a file with annotations
                String annotationsPath = textFile.getPath()
                        .substring(0, textFile.getPath().length() - 4) + ".ann";
                File annotationsFile = new File(annotationsPath);
                if(!annotationsFile.exists()) {
                    throw new IOException("File with annotations: " + annotationsPath + " does not exist");
                }
                LOGGER.info("Loaded" + annotationsFile.getName());
                result.add(new TextWithAnnotations(textFile, annotationsFile));
            }
        }
        return result;
    }

    private static List<TextWithAnnotations> loadTextWithAnnotationPairs(String trainingDataDir) throws IOException {


        final File dirWithTrainingData = new File(trainingDataDir);
        if(!dirWithTrainingData.isDirectory()) {
            throw new IOException("Path should be a directory");
        }

        List<TextWithAnnotations> trainData = new ArrayList<>();

        for(File trainFile: dirWithTrainingData.listFiles()) {
            if(trainFile.getPath().endsWith(".txt")) {
                // find a file with annotations
                String annotationsPath = trainFile.getPath()
                        .substring(0, trainFile.getPath().length() - 4) + ".ann";
                File annotationsFile = new File(annotationsPath);
                if(!annotationsFile.exists()) {
                    throw new IOException("File with annotations: " + annotationsPath + " does not exist");
                }
                LOGGER.info("Loaded" + annotationsFile.getName());
                trainData.add(new TextWithAnnotations(trainFile, annotationsFile));
            }
        }
        LOGGER.info("Loaded: " + trainData.size() + " training items");
        return trainData;
    }
}
