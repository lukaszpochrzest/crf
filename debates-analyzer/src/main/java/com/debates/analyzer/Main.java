package com.debates.analyzer;

import com.debates.analyzer.tofactor.CorpusCreationException;
import com.debates.analyzer.tofactor.DataUtility;
import com.debates.analyzer.tofactor.PosUtility;
import com.debates.analyzer.tofactor.Tag;
import org.crf.utilities.TaggedToken;

import java.io.IOException;
import java.util.List;

/**
 * Created by lukasz on 15.05.16.
 */
public class Main {

    public static void main(String[] args) {

        //  get data dir from program args
        if(args.length < 1) {
            System.out.println("Provide data dir");
            System.exit(1);
        }

        String dataDir = args[0];

        try {
            List<List<? extends TaggedToken<String, String>>> corpus = DataUtility.loadData(dataDir);
            CorpusStatistics corpusStatistics = computeStatistics(corpus);
//            for(List<? extends TaggedToken<String, String>> sentence : corpus) {
//                print(sentence);
//            }
            System.out.println(corpusStatistics.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CorpusCreationException e) {
            e.printStackTrace();
        }

    }

    private static CorpusStatistics computeStatistics(List<List<? extends TaggedToken<String, String>>> corpus) {

        CorpusStatistics corpusStatistics = new CorpusStatistics();

        TaggedToken<String, String> previousTaggedToken = new TaggedToken<>("", "");
        for(List<? extends TaggedToken<String, String>> sentence : corpus) {
            for(TaggedToken<String, String> taggedToken : sentence) {

                String tokenTag = taggedToken.getTag();

                //  word statistics
                corpusStatistics.addToken(taggedToken.getToken());

                //  pos statistics
                corpusStatistics.addPos(PosUtility.getPoS(taggedToken.getToken()));

                //  tag statistics
                corpusStatistics.addTag(taggedToken.getTag());

                //  PROPOSITION_START and REASON_START statistics

                if(Tag.PROPOSITION_START.name().equals(tokenTag) ||
                        Tag.REASON_START.name().equals(tokenTag) ||
                        Tag.OTHER.name().equals(tokenTag)) {
                    corpusStatistics.addPrecedingToken(tokenTag, previousTaggedToken.getToken());
                    corpusStatistics.addPrecedingPos(tokenTag, PosUtility.getPoS(previousTaggedToken.getToken()));
                }

                if(tokenTag.startsWith(Tag.PROPOSITION.name())) {
                    corpusStatistics.addTagAndToken(Tag.PROPOSITION.name() + "*", taggedToken.getToken());
                } else if (tokenTag.startsWith(Tag.REASON.name())) {
                    corpusStatistics.addTagAndToken(Tag.REASON.name() + "*", taggedToken.getToken());
                }

                previousTaggedToken = taggedToken;
            }
        }

        return corpusStatistics;

    }


    private static void print(List<? extends TaggedToken<String, String>> taggedSentence) {

        // Print the result:
        for (TaggedToken<String, String> taggedToken : taggedSentence) {
            String token  = taggedToken.getToken();
            System.out.print(token + "_" + PosUtility.getPoS(token) +
                    "(" +
                    taggedToken.getTag().substring(0,2) +
                    ") ");
        }
        System.out.println();

    }

}
