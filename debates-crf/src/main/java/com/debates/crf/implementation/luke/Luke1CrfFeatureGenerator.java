package com.debates.crf.implementation.luke;

import com.debates.crf.Tag;
import com.debates.crf.implementation.luke.feature.PosAndTagFeature;
import com.debates.crf.implementation.common.feature.TagFeature;
import com.debates.crf.implementation.luke.feature.PreviousWordAndTagFeature;
import com.debates.crf.implementation.luke.filter.PosAndTagFilter;
import com.debates.crf.implementation.luke.filter.PreviousPosAndTagFilter;
import com.debates.crf.implementation.luke.filter.PreviousWordAndTagFilter;
import com.debates.crf.implementation.luke.utils.PreviousPosAndTagFeatureBuilder;
import com.debates.crf.implementation.luke.utils.PreviousWordAndTagFeatureBuilder;
import com.debates.crf.implementation.luke.utils.WordAndTagFeatureBuilder;
import com.debates.crf.implementation.witek.feature.WordAndTagFeature;
import com.debates.crf.implementation.witek.filter.WordAndTagFilter;
import com.debates.crf.utils.PosUtility;
import org.crf.crf.filters.CrfFilteredFeature;
import org.crf.crf.filters.TagFilter;
import org.crf.crf.filters.TwoTagsFilter;
import org.crf.crf.run.CrfFeatureGenerator;
import org.crf.postagging.postaggers.crf.features.TagTransitionFeature;
import org.crf.utilities.TaggedToken;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lukasz on 22.05.16.
 */
public class Luke1CrfFeatureGenerator extends CrfFeatureGenerator<String, String> {

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

    public Luke1CrfFeatureGenerator(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus,
                                    Set<String> tags) {
        super(corpus, tags);
    }

    /**
     * Place where fi "predicates" are generated (f - feature)
     */
    @Override
    public void generateFeatures() {
        setFilteredFeatures = new LinkedHashSet<>();
//        addTagFeatures();
//        addTagTransitionFeatures();
//        addPosAndTagFeatures();

        addPreviousPosAndTagFeatures();
        addPreviousWordAndTagFeatures();
        //witek
        addPropositionsFeatures();
        addReasonFeatures();
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
                        new PosAndTagFeature(pos, taggedToken.getTag()),// TODO thats pretty bad one
                        new PosAndTagFilter(pos, taggedToken.getTag()),// TODO thats pretty bad one
                        true
                ));
            }
        }
    }



    /**
     *
     * generates features like:
     * is_current_label_PROPOSITION_and_is_previous_token_ADJECTIVE
     */
    private void addPreviousPosAndTagFeatures() {
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
        for(String possiblePos : PosUtility.possiblePoses()) {
            for(Tag possibleTag : Tag.values()) {
                    setFilteredFeatures.add(new CrfFilteredFeature<>(
                            PreviousPosAndTagFeatureBuilder.buildFeature(possiblePos, possibleTag.name()),
                            new PreviousPosAndTagFilter(possiblePos, possibleTag.name()),
                            false
                    ));
            }
        }

    }

    private static final String[] WORDS_BEFORE_PROPOSITION_START = new String[]{
            "że"
    };

    private static final String[] WORDS_BEFORE_REASON_START = new String[]{
            "ponieważ", "bo", "że"
    };

    private void addPreviousWordAndTagFeatures() {
//        for( String keyWord : WORDS_BEFORE_PROPOSITION_START) {
//
//            setFilteredFeatures.add(new CrfFilteredFeature<>(
//                    new PreviousWordAndTagFeature( keyWord, Tag.PROPOSITION_START.name() ),
//                    new PreviousWordAndTagFilter( keyWord, Tag.PROPOSITION_START.name() ),
//                    true
//            ));
//        }
//
//        for( String keyWord : WORDS_BEFORE_REASON_START) {
//
//            setFilteredFeatures.add(new CrfFilteredFeature<>(
//                    new PreviousWordAndTagFeature( keyWord, Tag.REASON_START.name() ),
//                    new PreviousWordAndTagFilter( keyWord, Tag.REASON_START.name() ),
//                    true
//            ));
//        }

        for( String keyWord : WORDS_BEFORE_PROPOSITION_START) {
            for(Tag possibleTag : Tag.values()) {
//                if(!Tag.OTHER.name().equals(possibleTag)) {
                    setFilteredFeatures.add(new CrfFilteredFeature<>(
                            PreviousWordAndTagFeatureBuilder.buildFeature(keyWord, possibleTag.name()),
                            new PreviousWordAndTagFilter(keyWord, possibleTag.name()),
                            false
                    ));
//                }
            }
        }

        for( String keyWord : WORDS_BEFORE_REASON_START) {
            for(Tag possibleTag : Tag.values()) {
//                if(!Tag.OTHER.name().equals(possibleTag)) {
                    setFilteredFeatures.add(new CrfFilteredFeature<>(
                            PreviousWordAndTagFeatureBuilder.buildFeature(keyWord, possibleTag.name()),
                            new PreviousWordAndTagFilter(keyWord, possibleTag.name()),
                            false
                    ));
//                }
            }
        }

    }

    /** WITEK**/
    private static final String[] WORDS_AS_PROPOSITION_START = new String[]{
            "powinien", /*"można", "mogło", "mogli", "mogliśmy", "mógłby","mogłaby", "mogłoby",
            "powinniśmy", "powinny", "powinno", "powinna",
            "myśleć",
            "gdyby"*/
    };

    private static final String[] WORDS_AS_REASON_START = WORDS_BEFORE_REASON_START;

    private void addPropositionsFeatures()
    {
//        for( String keyWord : WORDS_AS_PROPOSITION_START) {
//
//            setFilteredFeatures.add(new CrfFilteredFeature<>(
//                    new WordAndTagFeature( keyWord, Tag.PROPOSITION_START.name() ),
//                    new WordAndTagFilter( keyWord, Tag.PROPOSITION_START.name() ),
//                    true
//            ));
//        }

        for( String keyWord : WORDS_AS_PROPOSITION_START) {
            for(Tag possibleTag : Tag.values()) {
                setFilteredFeatures.add(new CrfFilteredFeature<>(
                        WordAndTagFeatureBuilder.buildFeature(keyWord, possibleTag.name()),
                        new WordAndTagFilter(keyWord, possibleTag.name()),
                        false
                ));
            }
        }

    }

    private void addReasonFeatures()
    {
//        for( String keyWord : WORDS_AS_REASON_START) {
//            setFilteredFeatures.add(new CrfFilteredFeature<>(
//                    new WordAndTagFeature( keyWord, Tag.REASON_START.name() ),
//                    new WordAndTagFilter( keyWord, Tag.REASON_START.name() ),
//                    true
//            ));
//        }

        for( String keyWord : WORDS_AS_REASON_START) {
            for(Tag possibleTag : Tag.values()) {
                setFilteredFeatures.add(new CrfFilteredFeature<>(
                        WordAndTagFeatureBuilder.buildFeature(keyWord, possibleTag.name()),
                        new WordAndTagFilter(keyWord, possibleTag.name()),
                        false
                ));
            }
        }
    }

}
