package com.debates.crf;

import com.debates.crf.exception.CorpusCreationException;
import com.debates.crf.factory.FeatureGeneratorFactory;
import com.debates.crf.factory.FeatureFilterFactory;
import com.debates.crf.stemming.MyWordStemmer;
import com.debates.crf.utils.TextWithAnnotations;
import com.debates.crf.utils.Tuple;
import com.jjlteam.domain.Document;
import com.jjlteam.domain.Proposition;
import com.jjlteam.domain.Reason;
import com.jjlteam.parser.BratParser;
import morfologik.stemming.polish.PolishStemmer;
import org.apache.commons.io.IOUtils;
import org.crf.crf.CrfModel;
import org.crf.crf.filters.CrfFeaturesAndFilters;
import org.crf.crf.filters.CrfFilteredFeature;
import org.crf.crf.run.CrfInferencePerformer;
import org.crf.utilities.TaggedToken;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 14.04.16.
 */
public class CrfPerformer {

    /**
     * performs CRF on test data (testTwa) using training data(trainingTwas)
     *
     * @param trainingTwas
     * @param testTwas
     * @throws IOException
     * @throws CorpusCreationException
     */
    @SuppressWarnings("unchecked")
    public static void perform(List<TextWithAnnotations> trainingTwas,
                               List<TextWithAnnotations> testTwas) throws IOException, CorpusCreationException {

        if (trainingTwas.isEmpty()) {
            System.err.println("No training data to perform CRF on");
            return;
        }

        // prepare training corpus
        List<List<? extends TaggedToken<String, String>>> corpus = new ArrayList<>();
        for (TextWithAnnotations twa : trainingTwas) {
            Tuple<List<List<? extends TaggedToken<String, String>>>, List<List<String>>> t = createCorpus(twa);
            corpus.addAll(t.getFirst());
        }

        int i = 0;


        // Create trainer factory
        DebateCrfTrainerFactory<String, String> trainerFactory = new DebateCrfTrainerFactory<>();

        // Create trainer
        DebateCrfTrainer<String, String> trainer = trainerFactory.createTrainer(
                corpus,
                /** change these two to switch between implementations  */
                new FeatureGeneratorFactory(),
                new FeatureFilterFactory());

        // Run training with the loaded corpus.
        trainer.train(corpus);

        // Get the model
        CrfModel<String, String> crfModel = trainer.getLearnedModel();

        // Save the model into the disk.
        System.out.println("Writing a model to a file 'model'");
        File file = new File("model");
        save(crfModel, file);

        infer(crfModel, testTwas);
    }

    /**
     * performs CRF on test data (testTwa) using model
     * @param crfModel
     * @param testTwas
     * @throws IOException
     * @throws CorpusCreationException
     */
    @SuppressWarnings("unchecked")
    public static void perform(CrfModel crfModel,
                               List<TextWithAnnotations> testTwas) throws IOException, CorpusCreationException {
        infer(crfModel, testTwas);
    }


    private static void infer(CrfModel crfModel, List<TextWithAnnotations> testTwas) throws IOException, CorpusCreationException {
        printFeaturesWeights(crfModel);

        // Create a CrfInferencePerformer, to find tags for test data
        CrfInferencePerformer<String, String> inferencePerformer = new CrfInferencePerformer<>(crfModel);

//          //create test corpus
//        List<List<? extends TaggedToken<String, String>>> testCorpus = new ArrayList<>();
        OutputCreator outputCreator = new OutputCreator();
        List<String> testTwaResults = new ArrayList<>(testTwas.size());
        for (TextWithAnnotations twa : testTwas) {

            System.out.println("Processing " + twa.getTextFile().getName().replace(",txt", ""));

            StringBuilder stringBuilder = new StringBuilder();
            Tuple<List<List<? extends TaggedToken<String, String>>>, List<List<String>>> t = createCorpus(twa);
            List<List<? extends TaggedToken<String, String>>> testCorpus = t.getFirst();

            // infer tags
            int i=0;
            for(List<? extends TaggedToken<String, String>> testSentence : testCorpus) {

                //  extract sentence from testSentence
                List<String> sentence = testSentence.stream().map(TaggedToken::getToken).collect(Collectors.toList());

                //  infer tags for the sentence
                List<TaggedToken<String, String>> result = inferencePerformer.tagSequence(sentence);

                outputCreator.soak(stringBuilder, result, testSentence, t.getSecond().get(i++));
            }
            testTwaResults.add(stringBuilder.toString());
        }

        // save every test twa result to a single file

        System.out.println();
        System.out.println("Computing test data finished.");
        System.out.println();

        int i = 0;
        for(String testTwaResult : testTwaResults) {
            String name = testTwas.get(i++).getTextFile().getName() + ".processed";
            try(  PrintWriter out = new PrintWriter( name )  ){
                out.println( testTwaResult );
                System.out.println("Result saved to " + name);
            }
        }



        System.out.println();
        System.out.println();
        System.out.println(
                "Number of tokens predicted as REASON: " + outputCreator.tagsRPredictedCount + System.lineSeparator() +
                "Number of tokens predicted as REASON_START: " + outputCreator.tagsRSPredictedCount + System.lineSeparator() +
                "Number of tokens predicted as REASON_END: " + outputCreator.tagsREPredictedCount + System.lineSeparator() +
                "Number of tokens predicted as PROPOSITION: " + outputCreator.tagsPPredictedCount + System.lineSeparator() +
                "Number of tokens predicted as PROPOSITION_START: " + outputCreator.tagsPSPredictedCount + System.lineSeparator() +
                "Number of tokens predicted as PROPOSITION_END: " + outputCreator.tagsPEPredictedCount + System.lineSeparator() +

                        System.lineSeparator() +

                "% of well classified tokens:" + System.lineSeparator() + "\t" + outputCreator.tagsOKCount + "/" + outputCreator.tagsOverall + " = " + (double)outputCreator.tagsOKCount/outputCreator.tagsOverall + System.lineSeparator() + System.lineSeparator() +
                        "% of well classified REASON tokens:" + System.lineSeparator() + "\t" + outputCreator.tagsROKCount + "/" + outputCreator.tagsRCount + (outputCreator.tagsRCount > 0 ? " = " + (double) outputCreator.tagsROKCount /outputCreator.tagsRCount : "") + System.lineSeparator() + System.lineSeparator() +
                        "% of well classified REASON_START tokens:" + System.lineSeparator() + "\t" + outputCreator.tagsRSOKCount + "/" + outputCreator.tagsRSCount + (outputCreator.tagsRSCount > 0 ? " = " + (double) outputCreator.tagsRSOKCount /outputCreator.tagsRSCount : "") + System.lineSeparator() + System.lineSeparator() +
                        "% of well classified REASON_END tokens:" + System.lineSeparator() + "\t" + outputCreator.tagsREOKCount + "/" + outputCreator.tagsRECount + (outputCreator.tagsRECount > 0 ? " = " + (double) outputCreator.tagsREOKCount /outputCreator.tagsRECount : "") + System.lineSeparator() + System.lineSeparator() +
                        "% of well classified PROPOSITION tokens:" + System.lineSeparator() + "\t" + outputCreator.tagsPOKCount + "/" + outputCreator.tagsPCount + (outputCreator.tagsPCount > 0 ? " = " + (double) outputCreator.tagsPOKCount /outputCreator.tagsPCount : "") + System.lineSeparator() + System.lineSeparator() +
                        "% of well classified PROPOSITION_START tokens:" + System.lineSeparator() + "\t" + outputCreator.tagsPSOKCount + "/" + outputCreator.tagsPSCount + (outputCreator.tagsPSCount  >0 ? " = " + (double) outputCreator.tagsPSOKCount /outputCreator.tagsPSCount : "") + System.lineSeparator() + System.lineSeparator() +
                        "% of well classified PROPOSITION_END tokens:" + System.lineSeparator() + "\t" + outputCreator.tagsPEOKCount + "/" + outputCreator.tagsPECount + (outputCreator.tagsPECount > 0 ? " = " + (double) outputCreator.tagsPEOKCount /outputCreator.tagsPECount : "") + System.lineSeparator() + System.lineSeparator() +

                        System.lineSeparator() +

                "% of well classified PROPOSITION* (with loose constraint .i.e. PROPOSITION == PROPOSITION_START == PROPOSITION_END): " + System.lineSeparator() +
                        "\t" + outputCreator.tagsOKPSTARPredictedCount + "/" + (outputCreator.tagsPCount + outputCreator.tagsPSCount + outputCreator.tagsPECount) + System.lineSeparator() +
                        "% of well classified REASON* (with loose constraint .i.e. REASON == REASON_START == REASON_END): " + System.lineSeparator() +
                        "\t" + outputCreator.tagsOKRSTARPredictedCount + "/" + (outputCreator.tagsRCount + outputCreator.tagsRSCount + outputCreator.tagsRECount) + System.lineSeparator()
        );
    }

    public static class OutputCreator {
        public int tagsOverall = 0;
        public int tagsOKCount = 0;

        public int tagsRCount = 0;
        public int tagsRSCount = 0;
        public int tagsRECount = 0;
        public int tagsPCount = 0;
        public int tagsPSCount = 0;
        public int tagsPECount = 0;

        public int tagsROKCount = 0;
        public int tagsRSOKCount = 0;
        public int tagsREOKCount = 0;
        public int tagsPOKCount = 0;
        public int tagsPSOKCount = 0;
        public int tagsPEOKCount = 0;

        public int tagsRPredictedCount = 0;
        public int tagsRSPredictedCount = 0;
        public int tagsREPredictedCount = 0;
        public int tagsPPredictedCount = 0;
        public int tagsPSPredictedCount = 0;
        public int tagsPEPredictedCount = 0;

        public int tagsOKRSTARPredictedCount = 0;
        public int tagsOKPSTARPredictedCount = 0;


        /**
         * prints testSentence in format:
         *  WORD0_pos0(PREDICTED_TAG0, REAL_TAG0) WORD1_pos1(PREDICTED_TAG1, REAL_TAG1) ...
         * @param taggedSentence
         * @param testSentence
         */
        public void soak(StringBuilder stringBuilder,
                                 List<TaggedToken<String, String>> taggedSentence,
                                 List<? extends TaggedToken<String, String>> testSentence,
                         List<String> notStemmedTestSentence) {


            // Print the result:
            int i = 0;
            for (TaggedToken<String, String> taggedToken : taggedSentence) {
                String originalToken = notStemmedTestSentence.get(i);
                String originalTag = testSentence.get(i).getTag();
                String predictedTag = taggedToken.getTag();

                if(predictedTag.equals(originalTag)) {
                    ++tagsOKCount;
                    if (Tag.REASON.name().equals(predictedTag)) {
                        ++tagsROKCount;
                    } else if(Tag.REASON_START.name().equals(predictedTag)) {
                        ++tagsRSOKCount;
                    } else if(Tag.REASON_END.name().equals(predictedTag)) {
                        ++tagsREOKCount;
                    } else if (Tag.PROPOSITION.name().equals(predictedTag)) {
                        ++tagsPOKCount;
                    } else if(Tag.PROPOSITION_START.name().equals(predictedTag)) {
                        ++tagsPSOKCount;
                    } else if(Tag.PROPOSITION_END.name().equals(predictedTag)) {
                        ++tagsPEOKCount;
                    }
                }

                if (Tag.REASON.name().equals(originalTag)) {
                    ++tagsRCount;
                } else if(Tag.REASON_START.name().equals(originalTag)) {
                    ++tagsRSCount;
                } else if(Tag.REASON_END.name().equals(originalTag)) {
                    ++tagsRECount;
                } else if (Tag.PROPOSITION.name().equals(originalTag)) {
                    ++tagsPCount;
                } else if(Tag.PROPOSITION_START.name().equals(originalTag)) {
                    ++tagsPSCount;
                } else if(Tag.PROPOSITION_END.name().equals(originalTag)) {
                    ++tagsPECount;
                }

                if (Tag.REASON.name().equals(predictedTag)) {
                    ++tagsRPredictedCount;
                } else if(Tag.REASON_START.name().equals(predictedTag)) {
                    ++tagsRSPredictedCount;
                } else if(Tag.REASON_END.name().equals(predictedTag)) {
                    ++tagsREPredictedCount;
                } else if (Tag.PROPOSITION.name().equals(predictedTag)) {
                    ++tagsPPredictedCount;
                } else if(Tag.PROPOSITION_START.name().equals(predictedTag)) {
                    ++tagsPSPredictedCount;
                } else if(Tag.PROPOSITION_END.name().equals(predictedTag)) {
                    ++tagsPEPredictedCount;
                }

                String firstTwoLetters = predictedTag.substring(0,2);
                if(firstTwoLetters.equals(originalTag.substring(0,2))) {
                    if(firstTwoLetters.equals(Tag.REASON.name().substring(0,2))) {
                        ++tagsOKRSTARPredictedCount;
                    } else if(firstTwoLetters.equals(Tag.PROPOSITION.name().substring(0,2))) {
                        ++tagsOKPSTARPredictedCount;
                    }
                }


                stringBuilder.append(originalToken +
                        "(" +
                        predictedTag +
                        "/" +
                        originalTag +
                        ") ");
                ++i;
            }
            tagsOverall += taggedSentence.size();
            stringBuilder.append("\r\n");
        }
    }



    private static void printFeaturesWeights( CrfModel<String, String> crfModel )
    {
        System.out.println("Printing model...(features and their weights):");

        CrfFeaturesAndFilters< String, String > features = crfModel.getFeatures();
        ArrayList< Double > weights = crfModel.getParameters();

        int idx = 0;
        for( CrfFilteredFeature filteredFeature : features.getFilteredFeatures() )
        {
            System.out.println(filteredFeature.getFeature().toString() + "          " + weights.get( idx ).toString() );

            ++idx;
        }
    }

    public static void save(Object object, File file)
    {
        try(ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file)))
        {
            stream.writeObject(object);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to save",e);
        }
    }
//
//    public static Object load(File file)
//    {
//        try(ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file)))
//        {
//            return stream.readObject();
//        }
//        catch (ClassNotFoundException | IOException e)
//        {
//            throw new RuntimeException("Failed to load",e);
//        }
//    }


    /**
     * Transforms TextWithAnnotation into corpus, which is a tokenized String from TextWithAnnotation.textFile
     * tagged with com.debates.crf.Tag tags
     * @param twa
     * @return
     * @throws IOException
     * @throws CorpusCreationException
     */
    private static Tuple<List<List<? extends TaggedToken<String, String>>>, List<List<String>>> createCorpus (TextWithAnnotations twa)
            throws IOException, CorpusCreationException {

        final String wordLetters = "[a-zA-Z0-9\u00F3\u0105" +
                "\u0119" + "\u0142" + "u\u017C" + "\u017A" + "\u0144" + "\u0107" + "\u015B" + "\u0104" +
                "\u0118" + "\u00D3" + "\u0141" + "\u0179" + "\u017B" + "\u0143" + "\u015A" + "\u0106" +
                "\\-" + "\\%" + "\u22ee]"; //...
        final String punctuationsEndSentence = "[./?!]";


        /** read propositions   **/

        File annFile = twa.getAnnotationsFile();
        Document parsedAnnDocument = BratParser.parse(annFile);

        List<Proposition> propositions = new ArrayList<>(parsedAnnDocument.getPropositions().values());
        List<Reason> reasons = new ArrayList<>(parsedAnnDocument.getReasons().values());

        //  sort propositions and reasons asc according to startIndex
        Collections.sort(propositions, (Proposition o1, Proposition o2) ->
                o1.getStartIndex().compareTo(o2.getStartIndex()));
        Collections.sort(reasons, (Reason o1, Reason o2) ->
                o1.getStartIndex().compareTo(o2.getStartIndex()));

        //  set currProposition as first one (with smallest start index)
        Iterator<Proposition> propositionsSortedIterator = propositions.iterator();
        Proposition currProposition = propositionsSortedIterator.hasNext() ? propositionsSortedIterator.next() : null;

        //  set currReason as first one (with smallest start index)
        Iterator<Reason> reasonSortedIterator = reasons.iterator();
        Reason currReason = reasonSortedIterator.hasNext() ? reasonSortedIterator.next() : null;

        /** read text file  **/

        MyWordStemmer myWordStemmer = new MyWordStemmer(new PolishStemmer());
        List<List<? extends TaggedToken<String, String>>> result = new LinkedList<>();
        List<List<String>> resultNotStemmed = new LinkedList<>();

        File textFile = twa.getTextFile();
        String text = IOUtils.toString(new FileInputStream(textFile), "UTF8");

        List<TaggedToken<String, String>> currSequence = new ArrayList<>();
        List<String> currSequenceNotStemmed = new ArrayList<>();

        // tag for previous stem is null
        Tag previousTag = null;
        // real index so we do not count \n and \r twice
        int realIdx =-1;
        for(int leftIdx = 0; leftIdx < text.length(); ++leftIdx) {

            // if there was punctuation mark at the end of the sentence
            boolean wasTherePunctuationMark = false;

            char currChar = text.charAt(leftIdx);


            if(currChar == '\n' || currChar == '\r') {
                if (currChar == '\n')
                    realIdx++;
                result.add(currSequence);
                currSequence = new ArrayList<>();
                resultNotStemmed.add(currSequenceNotStemmed);
                currSequenceNotStemmed = new ArrayList<>();
                continue;
            }

            if (currChar == ' ') {
                realIdx++;
                continue;
            }

            realIdx++;


            // JLL block of code(ive been given) - want to work on the same data, so processing needs to be quite the same

            // if the first letter is a word character or before a word character is a white character
            if(
                    leftIdx == 0 && Character.toString(text.charAt(leftIdx)).matches(wordLetters) ||
                            (
                                    leftIdx > 0 && Character.toString(text.charAt(leftIdx)).matches(wordLetters) &&
                                            !Character.toString(text.charAt(leftIdx - 1)).matches(wordLetters)
                            )
                    ) {
                int rightIdx = leftIdx + 1;
                for (; rightIdx < text.length(); ++rightIdx) {
                    if (!Character.toString(text.charAt(rightIdx)).matches(wordLetters)) {
                        // white character
                        if (Character.toString(text.charAt(rightIdx)).matches(punctuationsEndSentence) )
                            wasTherePunctuationMark = true;
                        break;
                    }
                }

                Tag tag = Tag.OTHER;

                //  is it proposition ?
                if(currProposition != null) {
                    if(realIdx > currProposition.getEndIndex()) {
                        if(propositionsSortedIterator.hasNext()) {
                            currProposition = propositionsSortedIterator.next();
                        } else {
                            currProposition = null;
                        }
                    }
                    if(currProposition != null) {

                        int compareRight = rightIdx - (leftIdx - realIdx);
                        if(realIdx >= currProposition.getStartIndex() &&
                                compareRight == currProposition.getEndIndex()) {
                            tag = Tag.PROPOSITION_END;
                        } else if(realIdx == currProposition.getStartIndex() &&
                                compareRight <= currProposition.getEndIndex()) {
                            tag = Tag.PROPOSITION_START;
                        } else if (realIdx >= currProposition.getStartIndex() &&
                                compareRight + 1 == currProposition.getEndIndex()
                                && wasTherePunctuationMark == true &&
                                ((previousTag == Tag.PROPOSITION || previousTag == Tag.PROPOSITION_START)) ){
                            tag = Tag.PROPOSITION_END;
                        } else if(realIdx >= currProposition.getStartIndex() &&
                                compareRight < currProposition.getEndIndex()) {
                            tag = Tag.PROPOSITION;
                        }
                    }
                }

                //  is it reason?
                if(currReason != null) {
                    if(realIdx > currReason.getEndIndex()) {
                        if(reasonSortedIterator.hasNext()) {
                            currReason = reasonSortedIterator.next();
                        } else {
                            currReason = null;
                        }
                    }
                    if(currReason != null) {
                        int compareRight = rightIdx - (leftIdx - realIdx);
                        if(realIdx >= currReason.getStartIndex() &&
                                compareRight == currReason.getEndIndex()) {
                            tag = Tag.REASON_END;
                        } else if(realIdx == currReason.getStartIndex() &&
                                compareRight <= currReason.getEndIndex()) {
                            tag = Tag.REASON_START;
                        } else if (realIdx >= currReason.getStartIndex()
                                && compareRight + 1 == currReason.getEndIndex()
                                && wasTherePunctuationMark == true && ((previousTag == Tag.REASON || previousTag == Tag.REASON_START))) {
                            tag = Tag.REASON_END;
                        }
                        else if(realIdx >= currReason.getStartIndex() &&
                                compareRight < currReason.getEndIndex()) {
                            tag = Tag.REASON;
                        }
                    }
                }

                previousTag = tag;
                final String selectedWord = text.substring(leftIdx, rightIdx).toLowerCase();
                final String stem = myWordStemmer.getStemNotNull(selectedWord);
                currSequence.add(new TaggedToken<>(stem, tag.name()));
                currSequenceNotStemmed.add(selectedWord);
            }
        }

        if(currProposition != null || currReason != null) {
            throw new CorpusCreationException("Not all the propositions or reasons were properly parsed");
        }

        List<List<? extends TaggedToken<String, String>>> filteredResult =
                result.stream().filter(taggedTokens -> !taggedTokens.isEmpty()).collect(Collectors.toList());

        List<List<String>> filteredResultNotStemmed =
                resultNotStemmed.stream().filter(sequence -> !sequence.isEmpty()).collect(Collectors.toList());

        return new Tuple<>(filteredResult, filteredResultNotStemmed);
    }

}
