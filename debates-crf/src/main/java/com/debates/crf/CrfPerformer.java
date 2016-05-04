package com.debates.crf;

import com.jjlteam.domain.Document;
import com.jjlteam.domain.Proposition;
import com.jjlteam.domain.Reason;
import com.jjlteam.parser.BratParser;
import morfologik.stemming.polish.PolishStemmer;
import org.apache.commons.io.IOUtils;
import org.crf.crf.CrfModel;
import org.crf.crf.run.CrfInferencePerformer;
import org.crf.utilities.TaggedToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 14.04.16.
 */
public class CrfPerformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @SuppressWarnings("unchecked")
    public static void perform(List<TextWithAnnotations> trainingTwas,
//                               List<String> stopList,
                               TextWithAnnotations testTwa) throws IOException, CorpusCreationException {

        if(trainingTwas.isEmpty()) {
            LOGGER.info("No training data");
            return;
        }

        List<List<? extends TaggedToken<String, String>>> corpus = new ArrayList<>();

        for(TextWithAnnotations twa : trainingTwas) {
           corpus.addAll(createCorpus(twa/*, stopList*/));
        }

//        /** */
//        System.out.println(testTwa.getTextFile().getName());
//        for(List<? extends TaggedToken<String, String>> sequence : createCorpus(testTwa, stopList)) {
//            if(sequence.isEmpty()) {
//                continue;
//            }
//            for(TaggedToken<String, String> tt : sequence) {
//                System.out.print(tt.getToken() + "(" + tt.getTag() + ") ");
//            }
//            System.out.println();
//        }
//
//        if(true) {
//            return;
//        }
//        /** */



        // Create trainer factory
        DebateCrfTrainerFactory<String, String> trainerFactory = new DebateCrfTrainerFactory<>();

        // Create trainer
        DebateCrfTrainer<String,String> trainer = trainerFactory.createTrainer(
                corpus,
                new DebateCrfFeatureGeneratorFactory(),
                new DebateFilterFactory());

        // Run training with the loaded corpus.
        trainer.train(corpus);

        // Get the model
        CrfModel<String, String> crfModel = trainer.getLearnedModel();

//        // Save the model into the disk.
//        File file = new File("example.ser");
//        save(crfModel,file);
//
//        ////////
//
//        // Later... Load the model from the disk
//        crfModel = (CrfModel<String, String>) load(file);

        // Create a CrfInferencePerformer, to find tags for test data
        CrfInferencePerformer<String, String> inferencePerformer = new CrfInferencePerformer<>(crfModel);

        // Test:
//        List<String> test = Arrays.asList("This is a sequence for test data".split("\\s+"));
//        List<TaggedToken<String, String>> result = inferencePerformer.tagSequence(test);
//        // Print the result:
//        for (TaggedToken<String, String> taggedToken : result)
//        {
//            System.out.println("Tag for: "+taggedToken.getToken()+" is "+taggedToken.getTag());
//        }
        List<List<? extends TaggedToken<String, String>>> testCorpus = createCorpus(testTwa/*, stopList*/);
        for(List<? extends TaggedToken<String, String>> testSentence : testCorpus) {
            List<String> sentence = testSentence.stream().map(TaggedToken::getToken).collect(Collectors.toList());

            List<TaggedToken<String, String>> result = inferencePerformer.tagSequence(sentence);

            if(result.size() != testSentence.size()) {
                throw new RuntimeException();
            }

            // Print the result:
            int i = 0;
            for (TaggedToken<String, String> taggedToken : result) {
                System.out.print(taggedToken.getToken() +
                        "(" +
                            taggedToken.getTag().substring(0,2) +
                                "/" +
                            testSentence.get(i).getTag().substring(0,2) +
                        ") ");
                ++i;
            }
            System.out.println();
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

    public static Object load(File file)
    {
        try(ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file)))
        {
            return stream.readObject();
        }
        catch (ClassNotFoundException | IOException e)
        {
            throw new RuntimeException("Failed to load",e);
        }
    }


    private static List<List<? extends TaggedToken<String, String>>> createCorpus (
            TextWithAnnotations twa
//            , List<String> stopList
    )
            throws IOException, CorpusCreationException {

        final String wordLetters = "[a-zA-Z0-9\\-\'zżźćńółęąśŻŹĆĄŚĘŁÓŃ]";

        /** read propositions   **/

        File annFile = twa.getAnnotationsFile();
        Document parsedAnnDocument = BratParser.parse(annFile);

        List<Proposition> propositions = new ArrayList<>(parsedAnnDocument.getPropositions().values());
        List<Reason> reasons = new ArrayList<>(parsedAnnDocument.getReasons().values());

        //  sort propositions and reasosn asc according to startIndex
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

        File textFile = twa.getTextFile();
        String text = IOUtils.toString(new FileInputStream(textFile));

        List<TaggedToken<String, String>> currSequence = new ArrayList<>();

//        Tag previousTag = Tag.OTHER;

        for(int leftIdx = 0; leftIdx < text.length(); ++leftIdx) {

            char currChar = text.charAt(leftIdx);

            if(currChar == '\n' || currChar == '\r') {
                result.add(currSequence);
                currSequence = new ArrayList<>();
                continue;
            }


            /** JLL block of code - want to work on the same data, so processing needs to be quite the same  */

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
                        break;
                    }
                }

                Tag tag = Tag.OTHER;

                //  is it proposition ?
                if(currProposition != null) {
                    if(leftIdx > currProposition.getEndIndex()) {
                        if(propositionsSortedIterator.hasNext()) {
                            currProposition = propositionsSortedIterator.next();
                        } else {
                            currProposition = null;
                        }
                    }
                    if(currProposition != null) {
                        if(leftIdx >= currProposition.getStartIndex() &&
                                rightIdx == currProposition.getEndIndex()) {
                            tag = Tag.PROPOSITION_END;
                        } else if(leftIdx == currProposition.getStartIndex() &&
                                rightIdx <= currProposition.getEndIndex()) {
                            tag = Tag.PROPOSITION_START;
                        } else if(leftIdx >= currProposition.getStartIndex() &&
                                rightIdx <= currProposition.getEndIndex()) {
                            tag = Tag.PROPOSITION;

//                            if(previousTag != Tag.PROPOSITION && previousTag != Tag.PROPOSITION_START) {
//                                tag = Tag.PROPOSITION_START;
//                            }

                        }
                    }
                }


                //  is it reason?
                if(currReason != null) {
                    if(leftIdx > currReason.getEndIndex()) {
                        if(reasonSortedIterator.hasNext()) {
                            currReason = reasonSortedIterator.next();
                        } else {
                            currReason = null;
                        }
                    }
                    if(currReason != null) {
                        if(leftIdx >= currReason.getStartIndex() &&
                                rightIdx == currReason.getEndIndex()) {
                            tag = Tag.REASON_END;
                        } else if(leftIdx == currReason.getStartIndex() &&
                                rightIdx <= currReason.getEndIndex()) {
                            tag = Tag.REASON_START;
                        } else if(leftIdx >= currReason.getStartIndex() &&
                                rightIdx <= currReason.getEndIndex()) {
                            tag = Tag.REASON;

//                            if(previousTag != Tag.REASON && previousTag != Tag.REASON_START) {
//                                tag = Tag.REASON_START;
//                            }

                        }
                    }
                }


                final String selectedWord = text.substring(leftIdx, rightIdx).toLowerCase();

                final String stem = myWordStemmer.getStemNotNull(selectedWord);

                currSequence.add(new TaggedToken<>(stem, tag.name()));

//                previousTag = tag;
            }
        }
        if(currProposition != null || currReason != null) {
            throw new CorpusCreationException("Not all the propositions or reasons were properly parsed");
        }

        List<List<? extends TaggedToken<String, String>>> filteredResult =
                result.stream().filter(taggedTokens -> !taggedTokens.isEmpty()).collect(Collectors.toList());

        return filteredResult;
    }

}
