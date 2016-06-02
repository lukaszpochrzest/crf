package com.debates.crf.implementation.witek;

import com.debates.crf.implementation.martyna.MartynaCrfFeatureGenerator;
import org.crf.crf.run.CrfFeatureGenerator;
import org.crf.crf.run.CrfFeatureGeneratorFactory;
import org.crf.utilities.TaggedToken;

import java.util.List;
import java.util.Set;

/**
 * Created by lukasz on 25.05.16.
 */
public class WitekCrfFeatureGeneratorFactory implements CrfFeatureGeneratorFactory<String, String> {

    @Override
    public CrfFeatureGenerator<String, String> create(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus, Set<String> tags) {
        return new WitekCrfFeatureGenerator(corpus, tags);
    }
}