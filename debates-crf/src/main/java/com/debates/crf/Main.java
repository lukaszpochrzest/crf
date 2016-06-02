package com.debates.crf;

import com.debates.crf.exception.CorpusCreationException;
import com.debates.crf.utils.ProgramParams;
import com.debates.crf.utils.TextWithAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
            String trainingDataDir = pp.getTrainingDataDir();
            if(trainingDataDir == null) {
                System.out.println(ProgramParams.description());
                System.exit(1);
            }

            //  lets do some crf
            final List<TextWithAnnotations> trainingData = loadTrainingData(trainingDataDir);

            trainingData.remove(2);
            trainingData.remove(5);

            List<TextWithAnnotations> trainingTwas = trainingData.subList(1, trainingData.size());
            TextWithAnnotations testTwa = trainingData.get(0);

            CrfPerformer.perform(trainingTwas, testTwa);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            System.exit(1);
        }
    }


    private static List<TextWithAnnotations> loadTrainingData(String trainingDataDir) throws IOException {


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
