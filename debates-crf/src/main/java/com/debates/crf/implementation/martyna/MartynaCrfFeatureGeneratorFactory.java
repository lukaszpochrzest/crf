package com.debates.crf.implementation.martyna;

import org.crf.crf.run.CrfFeatureGenerator;
import org.crf.crf.run.CrfFeatureGeneratorFactory;
import org.crf.utilities.TaggedToken;

import java.util.List;
import java.util.Set;

/**
 * Created by lukasz on 25.05.16.
 */
public class MartynaCrfFeatureGeneratorFactory implements CrfFeatureGeneratorFactory<String, String> {

    @Override
    public CrfFeatureGenerator<String, String> create(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus, Set<String> tags) {
        return new MartynaCrfFeatureGenerator(corpus, tags);
    }
}
