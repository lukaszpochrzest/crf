package com.debates.crf;

import com.debates.crf.exception.CorpusCreationException;
import com.debates.crf.utils.Config;
import com.debates.crf.utils.ConfigRepository;
import com.debates.crf.utils.ProgramParams;
import com.debates.crf.utils.TextWithAnnotations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.crf.crf.CrfModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukasz on 12.04.16.
 */
public class Main {

    public static void main(String[] args) throws CorpusCreationException {
        try {

            //  program params
            ProgramParams pp = new ProgramParams(args);
            if(pp.isHelp()) {
                System.out.println(ProgramParams.description());
                System.exit(1);
            }
            String configFile = pp.getParamConfigFile();
            if(configFile == null) {
                System.out.println(ProgramParams.description());
                System.exit(1);
            }

            String configFileString = IOUtils.toString(new FileInputStream(configFile), "UTF8");
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            Config config = gson.fromJson(configFileString, Config.class);
            ConfigRepository.setConfig(config);

            if(config.getMinimizerConvergence() == null) {
                System.out.println("Minimizer convergence must be defined");
                System.exit(1);
            }

            if(config.getMinimizerConvergence() <= 0.0d) {
                System.out.println("Minimizer convergence must be > 0");
                System.exit(1);
            }

            if(config.getTestFileNames() == null) {
                System.out.println("Test file list must be defined in config file");
                System.exit(1);
            }

            System.out.println("Loading test files:");

            final List<TextWithAnnotations> testTwas = loadTextWithAnnotationPairs(config.getTestFileNames());

            System.out.println();

            if(config.getTrainFileNames() != null && config.getModelFileName() != null) {
                System.out.println("Config file needs to have either model or training files defined, but not both of them");
                System.exit(1);
            }

            if (config.getTrainFileNames() != null) {
                System.out.println("Loading training files:");

                final List<TextWithAnnotations> trainingTwas = loadTextWithAnnotationPairs(config.getTrainFileNames());

                System.out.println();

                CrfPerformer.perform(trainingTwas, testTwas);
            } else if(config.getModelFileName() != null) {
                CrfModel crfModel = load(new File(config.getModelFileName()));

                System.out.println("Model loaded");

                CrfPerformer.perform(crfModel, testTwas);
            } else {
                System.out.println("Either model or training file list must be defined in config file");
                System.exit(1);
            }


        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
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
                System.out.println("Loaded " + annotationsFile.getName().replace(".ann", ""));
                result.add(new TextWithAnnotations(textFile, annotationsFile));
            }
        }
        return result;
    }

//    private static List<TextWithAnnotations> loadTextWithAnnotationPairs(String trainingDataDir) throws IOException {
//
//
//        final File dirWithTrainingData = new File(trainingDataDir);
//        if(!dirWithTrainingData.isDirectory()) {
//            throw new IOException("Path should be a directory");
//        }
//
//        List<TextWithAnnotations> trainData = new ArrayList<>();
//
//        for(File trainFile: dirWithTrainingData.listFiles()) {
//            if(trainFile.getPath().endsWith(".txt")) {
//                // find a file with annotations
//                String annotationsPath = trainFile.getPath()
//                        .substring(0, trainFile.getPath().length() - 4) + ".ann";
//                File annotationsFile = new File(annotationsPath);
//                if(!annotationsFile.exists()) {
//                    throw new IOException("File with annotations: " + annotationsPath + " does not exist");
//                }
//                LOGGER.info("Loaded" + annotationsFile.getName());
//                trainData.add(new TextWithAnnotations(trainFile, annotationsFile));
//            }
//        }
//        LOGGER.info("Loaded: " + trainData.size() + " training items");
//        return trainData;
//    }


    public static CrfModel load(File file) throws IOException, ClassNotFoundException {
        System.out.println("Loading model...");
        try(ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file)))
        {
            return (CrfModel)stream.readObject();
        }
    }

}
