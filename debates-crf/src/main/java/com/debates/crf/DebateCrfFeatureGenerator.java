package com.debates.crf;

import com.debates.crf.gui.PosUtility;
import org.crf.crf.filters.CrfFilteredFeature;
import org.crf.crf.filters.TagFilter;
import org.crf.crf.filters.TwoTagsFilter;
import org.crf.crf.run.CrfFeatureGenerator;
import org.crf.postagging.postaggers.crf.features.TagTransitionFeature;
import org.crf.utilities.TaggedToken;

import java.util.*;

/**
 * Created by lukasz on 20.04.16.
 */
public class DebateCrfFeatureGenerator extends CrfFeatureGenerator<String, String> {

    private static final String[] TAGS_THAT_MAY_BE_FIRST_IN_TEXT_ARRAY = new String[] {
            Tag.OTHER.name(),
            Tag.PROPOSITION_START.name(),
            Tag.REASON_START.name()
    };

    private static final String[] POSSIBLE_TAGS_PAIRS = new String[] {
            Tag.OTHER.name(), Tag.OTHER.name(),
            Tag.OTHER.name(), Tag.PROPOSITION_START.name(),
            Tag.OTHER.name(), Tag.REASON_START.name(),

            Tag.PROPOSITION_START.name(), Tag.PROPOSITION.name(),
            Tag.PROPOSITION_START.name(), Tag.PROPOSITION_END.name(),

            Tag.PROPOSITION.name(), Tag.PROPOSITION.name(),
            Tag.PROPOSITION.name(), Tag.PROPOSITION_END.name(),

            Tag.PROPOSITION_END.name(), Tag.OTHER.name(),
            Tag.PROPOSITION_END.name(), Tag.REASON_START.name(),
            Tag.PROPOSITION_END.name(), Tag.PROPOSITION_START.name(),

            Tag.REASON.name(), Tag.REASON.name(),
            Tag.REASON.name(), Tag.REASON_END.name(),

            Tag.REASON_START.name(), Tag.REASON.name(),
            Tag.REASON_START.name(), Tag.REASON_END.name(),

            Tag.REASON_END.name(), Tag.OTHER.name(),
            Tag.REASON_END.name(), Tag.PROPOSITION_START.name(),
            Tag.REASON_END.name(), Tag.REASON_START.name()
    };

    protected Set<CrfFilteredFeature<String, String>> setFilteredFeatures = null;

    public DebateCrfFeatureGenerator(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus,
                                     Set<String> tags) {
        super(corpus, tags);
    }

    @Override
    public void generateFeatures() {
        setFilteredFeatures = new LinkedHashSet<>();
        addTagFeatures();
        addTagTransitionFeatures();
        addPosAndTagFeatures();
        addPreviousPosAndTagFeatures();
    }

    @Override
    public Set<CrfFilteredFeature<String, String>> getFeatures() {
        return setFilteredFeatures;
    }

    private void addTagFeatures() {
//        for (List<? extends TaggedToken<String, String> > sentence : corpus)
//        {
//            for (TaggedToken<String, String> taggedToken : sentence)
//            {
//                setFilteredFeatures.add(
//                        new CrfFilteredFeature<String, String>(
//                                new CaseInsensitiveTokenAndTagFeature(taggedToken.getToken(), taggedToken.getTag()),
//                                new CaseInsensitiveTokenAndTagFilter(taggedToken.getToken(), taggedToken.getTag()),
//                                true
//                        )
//                );
//            }
//        }

        tags.forEach(
                tag -> setFilteredFeatures.add(new CrfFilteredFeature<>(
                        new TagFeature(tag),
                        new TagFilter<>(tag),
                        true
                )));
    }

    /**
     * copied form org.crf.postagging.postaggers.crf.features.StandardFeatureGenerator
     */
    private void addTagTransitionFeatures() {
//        for (String tag : tags) {
//            setFilteredFeatures.add(
//                    new CrfFilteredFeature<>(
//                            new TagTransitionFeature(null, tag),
//                            new TwoTagsFilter<>(tag, null),
//                            true)
//            );
//
//            for (String previousTag : tags) {
//                setFilteredFeatures.add(
//                        new CrfFilteredFeature<>(
//                                new TagTransitionFeature(previousTag, tag),
//                                new TwoTagsFilter<>(tag, previousTag),
//                                true)
//                );
//            }
//        }
        for(String tag : TAGS_THAT_MAY_BE_FIRST_IN_TEXT_ARRAY) {
            setFilteredFeatures.add(
                    new CrfFilteredFeature<>(
                            new TagTransitionFeature(null, tag),
                            new TwoTagsFilter<>(tag, null),
                            true)
            );
        }

        for(int i = 1; i < POSSIBLE_TAGS_PAIRS.length; i = i + 2) {
            String prevTag = POSSIBLE_TAGS_PAIRS[i-1];
            String currTag = POSSIBLE_TAGS_PAIRS[i];
            setFilteredFeatures.add(
                    new CrfFilteredFeature<>(
                            new TagTransitionFeature(prevTag, currTag),
                            new TwoTagsFilter<>(currTag, prevTag),
                            true)
            );
        }
    }

    private void addPosAndTagFeatures() {
        for (List<? extends TaggedToken<String, String> > sentence : corpus) {
            for (TaggedToken<String, String> taggedToken : sentence) {
                String pos = PosUtility.getPoS(taggedToken.getToken());
                setFilteredFeatures.add(new CrfFilteredFeature<>(
                        new PosAndTagFeature(pos, taggedToken.getTag()),
                        new PosAndTagFilter(pos, taggedToken.getTag()),
                        true
                ));
            }
        }
    }

    private void addPreviousPosAndTagFeatures() {
        for (List<? extends TaggedToken<String, String> > sentence : corpus) {
            String previousPos = null;
            for (TaggedToken<String, String> taggedToken : sentence) {
                String pos = PosUtility.getPoS(taggedToken.getToken());
                if(previousPos != null) {   //TODO allow null maybe?
                    setFilteredFeatures.add(new CrfFilteredFeature<>(
                            new PreviousPosAndTagFeature(previousPos, taggedToken.getTag()),
                            new PreviousPosAndTagFilter(previousPos, taggedToken.getTag()),
                            true
                    ));
                }
                previousPos = pos;
            }
        }
    }

}
