package com.debates.crf.factory;

import com.debates.crf.Tag;
import com.debates.crf.feature.MyTagTransitionFeature;
import com.debates.crf.filter.PreviousPosAndTagFilter;
import com.debates.crf.filter.PreviousWordAndTagFilter;
import com.debates.crf.filter.WordAndTagFilter;
import com.debates.crf.tools.PreviousPosAndTagFeatureBuilder;
import com.debates.crf.tools.PreviousWordAndTagFeatureBuilder;
import com.debates.crf.tools.WordAndTagFeatureBuilder;
import com.debates.crf.utils.PosUtility;
import org.crf.crf.filters.CrfFilteredFeature;
import org.crf.crf.filters.TwoTagsFilter;
import org.crf.crf.run.CrfFeatureGenerator;
import org.crf.utilities.TaggedToken;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lukasz on 22.05.16.
 */
public class FeatureGenerator extends CrfFeatureGenerator<String, String> {

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

    public FeatureGenerator(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus,
                            Set<String> tags) {
        super(corpus, tags);
    }

    @Override
    public void generateFeatures() {
        setFilteredFeatures = new LinkedHashSet<>();
        addTagTransitionFeatures();
        addPreviousPosAndTagFeatures();
        addPreviousWordAndTagFeatures();
        addWordAndTagFeatures();
    }

    @Override
    public Set<CrfFilteredFeature<String, String>> getFeatures() {
        return setFilteredFeatures;
    }

    private void addTagTransitionFeatures() {
        for(String tag : TAGS_THAT_MAY_BE_FIRST_IN_TEXT_ARRAY) {
            setFilteredFeatures.add(
                    new CrfFilteredFeature<>(
                            new MyTagTransitionFeature(null, tag),
                            new TwoTagsFilter<>(tag, null),
                            false)
            );
        }

        for(int i = 1; i < POSSIBLE_TAGS_PAIRS.length; i = i + 2) {
            String prevTag = POSSIBLE_TAGS_PAIRS[i-1];
            String currTag = POSSIBLE_TAGS_PAIRS[i];
            setFilteredFeatures.add(
                    new CrfFilteredFeature<>(
                            new MyTagTransitionFeature(prevTag, currTag),
                            new TwoTagsFilter<>(currTag, prevTag),
                            false)
            );
        }
    }

    private void addPreviousPosAndTagFeatures() {
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

        for( String keyWord : WORDS_BEFORE_PROPOSITION_START) {
            for(Tag possibleTag : Tag.values()) {
                    setFilteredFeatures.add(new CrfFilteredFeature<>(
                            PreviousWordAndTagFeatureBuilder.buildFeature(keyWord, possibleTag.name()),
                            new PreviousWordAndTagFilter(keyWord, possibleTag.name()),
                            false
                    ));
            }
        }

        for( String keyWord : WORDS_BEFORE_REASON_START) {
            for(Tag possibleTag : Tag.values()) {
                    setFilteredFeatures.add(new CrfFilteredFeature<>(
                            PreviousWordAndTagFeatureBuilder.buildFeature(keyWord, possibleTag.name()),
                            new PreviousWordAndTagFilter(keyWord, possibleTag.name()),
                            false
                    ));
            }
        }

    }

    private static final String[] WORDS_AS_PROPOSITION_START = new String[]{
            "powinien"
    };

    private static final String[] WORDS_AS_REASON_START = WORDS_BEFORE_REASON_START;

    private void addWordAndTagFeatures()
    {
        for( String keyWord : WORDS_AS_PROPOSITION_START) {
            for(Tag possibleTag : Tag.values()) {
                setFilteredFeatures.add(new CrfFilteredFeature<>(
                        WordAndTagFeatureBuilder.buildFeature(keyWord, possibleTag.name()),
                        new WordAndTagFilter(keyWord, possibleTag.name()),
                        false
                ));
            }
        }

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
