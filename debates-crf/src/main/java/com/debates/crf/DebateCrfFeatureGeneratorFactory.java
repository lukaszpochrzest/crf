package com.debates.crf;

import com.debates.crf.feature.generator.Luke1CrfFeatureGenerator;
import org.crf.crf.run.CrfFeatureGenerator;
import org.crf.crf.run.CrfFeatureGeneratorFactory;
import org.crf.utilities.TaggedToken;

import java.util.List;
import java.util.Set;

/**
 * Created by lukasz on 20.04.16.
 */
public class DebateCrfFeatureGeneratorFactory implements CrfFeatureGeneratorFactory<String, String> {

    @Override
    public CrfFeatureGenerator<String, String> create(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus, Set<String> tags) {
        return new Luke1CrfFeatureGenerator(corpus, tags);
    }
}
