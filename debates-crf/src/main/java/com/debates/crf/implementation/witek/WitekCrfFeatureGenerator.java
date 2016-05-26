package com.debates.crf.implementation.witek;

import com.debates.crf.Tag;
import com.debates.crf.implementation.common.feature.TagFeature;
import com.debates.crf.implementation.witek.feature.WordAndTagFeature;
import com.debates.crf.implementation.witek.filter.WordAndTagFilter;
import org.crf.crf.filters.CrfFilteredFeature;
import org.crf.crf.filters.TagFilter;
import org.crf.crf.filters.TwoTagsFilter;
import org.crf.crf.run.CrfFeatureGenerator;
import org.crf.postagging.postaggers.crf.features.TagTransitionFeature;
import org.crf.utilities.TaggedToken;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WitekCrfFeatureGenerator extends CrfFeatureGenerator<String, String> {

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

    public WitekCrfFeatureGenerator(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus,
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
        addTagTransitionFeatures();
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

    private static final String[] PROPOSITION_BEGIN = new String[]{
            "można", "mogło", "mogli", "mogliśmy", "mógłby","mogłaby", "mogłoby",
            "powinniśmy", "powinny", "powinno", "powinna", "powinien",
            "myśleć",
            "gdyby"
    };

    private static final String[] REASON_BEGIN = new String[]{
            "ponieważ", "bo", "gdyż"
    };

    private void addPropositionsFeatures()
    {
        for( String keyWord : PROPOSITION_BEGIN ) {
            setFilteredFeatures.add(new CrfFilteredFeature<>(
                    new WordAndTagFeature( keyWord, Tag.PROPOSITION_START.toString() ),// TODO thats pretty bad one
                    new WordAndTagFilter( keyWord, Tag.PROPOSITION_START.toString() ),// TODO thats pretty bad one
                    true
            ));
        }
    }

    private void addReasonFeatures()
    {
        for( String keyWord : REASON_BEGIN ) {
            setFilteredFeatures.add(new CrfFilteredFeature<>(
                    new WordAndTagFeature( keyWord, Tag.REASON_START.toString() ),
                    new WordAndTagFilter( keyWord, Tag.REASON_START.toString() ),
                    true
            ));
        }
    }



}