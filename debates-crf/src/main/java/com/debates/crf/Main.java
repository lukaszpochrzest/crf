package com.debates.crf;

import com.debates.crf.gui.ClassifiedStatement;
import com.debates.crf.gui.ExtractionResult;
import com.debates.crf.gui.GUI;
import com.debates.crf.gui.MarkClasses;
import com.jjlteam.domain.Document;
import com.jjlteam.domain.Proposition;
import com.jjlteam.domain.Reason;
import com.jjlteam.parser.BratParser;
import com.jjlteam.reader.DocumentReader;
import com.jjlteam.reader.StemmedWordN;
import morfologik.stemming.polish.PolishStemmer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by lukasz on 12.04.16.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String DEFAULT_TRAINING_DATA_DIR = "../res/debates_with_ann";//"../../../res/debates_with_ann"

    public static void main(String[] args) throws CorpusCreationException {

        try {

//            final Properties properties = loadProperties();
            final List<TextWithAnnotations> trainingData = loadTrainingData(args);
//            final List<String> stopList = loadStopList(properties.getProperty("stopListDir"));

            CrfPerformer.perform(trainingData.subList(1, 7),
//                    stopList,
                    trainingData.get(0));

//            showGUI(twa0.getTextFile(), twa0.getAnnotationsFile());
//            printPOS(trainingData, stopList);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            System.exit(1);
        }

    }

    private static void showGUI(File textFile, File annotationsFile) throws IOException {
        GUI gui = new GUI();
        gui.create();
        gui.setText("Please wait...Extracting in process...", GUI.Panel.PROPOSITION);
        gui.setText("Please wait...Extracting in process...", GUI.Panel.REASON);
        gui.setText("Please wait...Extracting in process...", GUI.Panel.SENTIMENT);


        final Document annDocument = BratParser.parse(annotationsFile);

        List<ClassifiedStatement> classifiedStatements = new LinkedList<>();

        for (Proposition proposition : annDocument.getPropositions().values()) {
            ClassifiedStatement classifiedStatement = new ClassifiedStatement(proposition.getStartIndex(),
                    proposition.getEndIndex());
            classifiedStatement.setPropositionClass(MarkClasses.MIDDLE);
            classifiedStatements.add(classifiedStatement);
        }
        for (Reason reason : annDocument.getReasons().values()) {
            ClassifiedStatement classifiedStatement = new ClassifiedStatement(reason.getStartIndex(),
                    reason.getEndIndex());
            classifiedStatement.setReasonClass(MarkClasses.MIDDLE);
            classifiedStatements.add(classifiedStatement);
        }

        gui.highlightText(textFile, new ExtractionResult(new LinkedList<>(),
                classifiedStatements));
    }

//    private static List<List<? extends TaggedToken<String, String>>> createCorpus(TextWithAnnotations twa,
//                                                                                  List<String> stopList)
//            throws IOException {
//
//        MyDocumentReader myDocumentReader = new MyDocumentReader(stopList);
//
//        List<List<String>> sequences = myDocumentReader.readSequences(twa.getTextFile());
//
//        MyWordStemmer myWordStemmer = new MyWordStemmer(new PolishStemmer());
//
//        List<List<? extends TaggedToken<String, String>>> result = new LinkedList<>();
//
//        for(List<String> sequence : sequences) {
//            Pair<String[], String[]> stemAndPos = myWordStemmer.getStemAndPoS(sequence);
//
//            String[] stems = stemAndPos.getFirst();
//            String[] pos = stemAndPos.getSecond();
//
//            if(stems.length != pos.length) {
//                throw new RuntimeException();
//            }
//
//            List<TaggedToken<String, String>> sequenceStemmedAndPos =
//                    new ArrayList<>(stems.length);
//
//            for(int i = 0; i < stems.length; ++i) {
//                sequenceStemmedAndPos.add(new TaggedToken<>(stems[i], pos[i]));
//            }
//
//            result.add(sequenceStemmedAndPos);
//        }
//
//        return result;
//    }

    private static void printPOS(Collection<TextWithAnnotations> trainingData, List<String> stopList) {
        try {
            TextWithAnnotations twa0 = trainingData.iterator().next();
//            final Document annDocument = BratParser.parse(twa0.getAnnotationsFile());
            final DocumentReader docReader = new DocumentReader(stopList);

            LOGGER.info(twa0.getTextFile().getName());
            StemmedWordN stemmedWordN = docReader.getStemmedWordNeighbours(twa0.getTextFile(), 800, 12,
                    new com.jjlteam.stemming.WordStemmer(new PolishStemmer()));
            LOGGER.info(toString(stemmedWordN));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties loadProperties() throws IOException {
        PropertyLoader propertyLoader = new PropertyLoader();
        return propertyLoader.loadProperties();
    }


    private static List<TextWithAnnotations> loadTrainingData(String[] args) throws IOException {
        String trainingDataDir = null;
        if(args.length != 1) {
            LOGGER.info("Using default training data dir " + DEFAULT_TRAINING_DATA_DIR);
            trainingDataDir = DEFAULT_TRAINING_DATA_DIR;
        } else {
            trainingDataDir = args[0];
        }

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
                trainData.add(new TextWithAnnotations(trainFile, annotationsFile));
            }
        }
        LOGGER.info("Loaded: " + trainData.size() + " training items");
        return trainData;
    }

    private static List<String> loadStopList(String stopListDir) throws IOException {
        final List<String> stopList = new LinkedList<>();
        try {
            Files.lines(Paths.get(stopListDir)).forEach(line -> {
                stopList.add(line.trim());
            });
            return stopList;
        } catch (IOException e) {
            throw new IOException("Loading stoplist file failed");
        }
    }

    private static String toString(StemmedWordN stemmedWordN) {
        String[] wordsBefore = stemmedWordN.getsWordsBefore();
        String word = stemmedWordN.getsWord();
        String[] wordsAfter = stemmedWordN.getsWordsAfter();

        String[] POSBefore = stemmedWordN.getWordsBeforePoS();
        String POS = stemmedWordN.getWordPoS();
        String[] POSAfter = stemmedWordN.getWordsAfterPoS();

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < wordsBefore.length; ++i) {
            sb.append(wordsBefore[i]).append("(").append(POSBefore[i]).append(") ");
        }
        sb.append("\n");

        sb.append(word).append("(").append(POS).append(")\n");

        for(int i = 0; i < wordsAfter.length; ++i) {
            sb.append(wordsAfter[i]).append("(").append(POSAfter[i]).append(") ");
        }
        return sb.toString();
    }


    private static class PropertyLoader {

        public Properties loadProperties() throws IOException {
            try {
//                File file = new File("app.properties");
//                FileInputStream fileInput = new FileInputStream(file);
                Properties properties = new Properties();
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("app.properties");    //TODO Main.lass.getClassLoader()...
                properties.load(inputStream);
                inputStream.close();
//                fileInput.close();
                return properties;
            } catch(IOException e) {
                throw new IOException("Error while reading app properties");
            }
        }
    }

}
