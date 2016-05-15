package com.debates.analyzer.tofactor;

import com.jjlteam.domain.Document;
import com.jjlteam.domain.Proposition;
import com.jjlteam.domain.Reason;
import com.jjlteam.parser.BratParser;
import morfologik.stemming.polish.PolishStemmer;
import org.apache.commons.io.IOUtils;
import org.crf.utilities.TaggedToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 15.05.16.
 */
public class DataUtility {

    public static List<List<? extends TaggedToken<String, String>>> loadData(String dataDir)
            throws IOException, CorpusCreationException {

        final File dirWithData = new File(dataDir);
        if(!dirWithData.isDirectory()) {
            throw new IOException("Path should be a directory");
        }

        List<List<? extends TaggedToken<String, String>>> corpus = new LinkedList<>();

        for(File trainFile: dirWithData.listFiles()) {
            if(trainFile.getPath().endsWith(".txt")) {
                // find a file with annotations
                String annotationsPath = trainFile.getPath()
                        .substring(0, trainFile.getPath().length() - 4) + ".ann";
                File annotationsFile = new File(annotationsPath);
                if(!annotationsFile.exists()) {
                    throw new IOException("File with annotations: " + annotationsPath + " does not exist");
                }
                corpus.addAll(createCorpus(trainFile, annotationsFile));
            }
        }
        return corpus;
    }


    /**
     * Transforms TextWithAnnotation into corpus, which is a tokenized String from TextWithAnnotation.textFile
     * tagged with com.debates.crf.Tag tags
     * @return
     * @throws IOException
     * @throws CorpusCreationException
     */
    private static List<List<? extends TaggedToken<String, String>>> createCorpus (File textFile, File annFile)
            throws IOException, CorpusCreationException {

        final String wordLetters = "[a-zA-Z0-9\\-\'zżźćńółęąśŻŹĆĄŚĘŁÓŃ]";

        /** read propositions   **/

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


            /** JLL block of code(ive been given) - want to work on the same data, so processing needs to be quite the same  */

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
