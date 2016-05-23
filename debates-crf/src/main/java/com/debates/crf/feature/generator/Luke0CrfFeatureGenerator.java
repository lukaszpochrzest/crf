package com.debates.crf.feature.generator;

import com.debates.crf.Tag;
import com.debates.crf.feature.PosAndTagFeature;
import com.debates.crf.feature.PreviousPosAndTagFeature;
import com.debates.crf.feature.TagFeature;
import com.debates.crf.feature.filter.PosAndTagFilter;
import com.debates.crf.feature.filter.PreviousPosAndTagFilter;
import com.debates.crf.utils.PosUtility;
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
public class Luke0CrfFeatureGenerator extends CrfFeatureGenerator<String, String> {

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

    public Luke0CrfFeatureGenerator(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus,
                                    Set<String> tags) {
        super(corpus, tags);
    }

    /**
     * Place where fi "predicates" are generated (f - feature)
     */
    @Override
    public void generateFeatures() {
        setFilteredFeatures = new LinkedHashSet<>();
        addTagFeatures();
//        addTagTransitionFeatures();
//        addPosAndTagFeatures();
//        addPreviousPosAndTagFeatures();
    }

    @Override
    public Set<CrfFilteredFeature<String, String>> getFeatures() {
        return setFilteredFeatures;
    }

    /**
     * generates features like:
     * is_current_label_PROPOSITION_START
     * is_current_label_PROPOSITION
     *
     * (check com.debates.crf.Tag)
     */
    private void addTagFeatures() {
        tags.forEach(
                tag -> setFilteredFeatures.add(new CrfFilteredFeature<>(
                        new TagFeature(tag),
                        new TagFilter<>(tag),
                        true
                )));
    }

    /**
     * copied from org.crf.postagging.postaggers.crf.features.StandardFeatureGenerator
     *
     * generates features like:
     * is_current_label_PROPOSITION_and_previous_label_PREPOSITION_START
     * is_current_label_REASON_and_previous_label_REASON_START
     */
    private void addTagTransitionFeatures() {
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

    /**
     * generates features like:
     * is_current_label_PROPOSITION_and_is_current_token_ADJECTIVE
     */
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

//    /**
//     * generates features like:
//     * is_current_label_PROPOSITION_and_is_previous_token_ADJECTIVE
//     */
//    private void addPreviousPosAndTagFeatures() {
//        for (List<? extends TaggedToken<String, String> > sentence : corpus) {
//            String previousPos = null;
//            for (TaggedToken<String, String> taggedToken : sentence) {
//                String pos = PosUtility.getPoS(taggedToken.getToken());
//                if(previousPos != null) {   //TODO allow null maybe?
//                    setFilteredFeatures.add(new CrfFilteredFeature<>(
//                            new PreviousPosAndTagFeature(previousPos, taggedToken.getTag()),
//                            new PreviousPosAndTagFilter(previousPos, taggedToken.getTag()),
//                            true
//                    ));
//                }
//                previousPos = pos;
//            }
//        }
//    }

}
