package com.debates.crf;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.crf.crf.CrfLogLikelihoodFunction;
import org.crf.crf.CrfModel;
import org.crf.crf.CrfTags;
import org.crf.crf.filters.CrfFeaturesAndFilters;
import org.crf.crf.run.CrfInferencePerformer;
import org.crf.function.DerivableFunction;
import org.crf.function.optimization.LbfgsMinimizer;
import org.crf.function.optimization.NegatedFunction;
import org.crf.utilities.CrfException;
import org.crf.utilities.TaggedToken;



/**
 *
 * @author Asher Stern
 * Date: Nov 23, 2014
 *
 * @param <K>
 * @param <G>
 */
public class DebateCrfTrainer<K,G>
{
    public static final double DEFAULT_SIGMA_SQUARED_INVERSE_REGULARIZATION_FACTOR = 10.0;
    public static final boolean DEFAULT_USE_REGULARIZATION = true;


    public DebateCrfTrainer(CrfFeaturesAndFilters<K, G> features, CrfTags<G> crfTags)
    {
        this(features,crfTags,DEFAULT_USE_REGULARIZATION,DEFAULT_SIGMA_SQUARED_INVERSE_REGULARIZATION_FACTOR);
    }

    public DebateCrfTrainer(CrfFeaturesAndFilters<K, G> features, CrfTags<G> crfTags,
                      boolean useRegularization, double sigmaSquare_inverseRegularizationFactor)
    {
        super();
        this.features = features;
        this.crfTags = crfTags;
        this.useRegularization = useRegularization;
        this.sigmaSquare_inverseRegularizationFactor = sigmaSquare_inverseRegularizationFactor;
    }


    public void train(List<? extends List<? extends TaggedToken<K, G> >> corpus)
    {
        logger.info("CRF training: Number of tags = "+crfTags.getTags().size()+". Number of features = "+features.getFilteredFeatures().length +".");
        logger.info("Creating log likelihood function.");
        DerivableFunction convexNegatedCrfFunction = NegatedFunction.fromDerivableFunction(createLogLikelihoodFunctionConcave(corpus));
        logger.info("Optimizing log likelihood function.");
        LbfgsMinimizer lbfgsOptimizer = new LbfgsMinimizer(convexNegatedCrfFunction, 20, 500);
        lbfgsOptimizer.find();
        double[] parameters = lbfgsOptimizer.getPoint();
        if (parameters.length!=features.getFilteredFeatures().length) {throw new CrfException("Number of parameters, returned by LBFGS optimizer, differs from number of features.");}

        ArrayList<Double> parametersAsList = new ArrayList<Double>(parameters.length);
        for (double parameter : parameters)
        {
            parametersAsList.add(parameter);
        }

        learnedModel = new CrfModel<K, G>(crfTags,features,parametersAsList);
        logger.info("Training of CRF - done.");
    }






    public CrfModel<K, G> getLearnedModel()
    {
        return learnedModel;
    }

    public CrfInferencePerformer<K, G> getInferencePerformer()
    {
        if (null==learnedModel) throw new CrfException("Not yet trained");
        return new CrfInferencePerformer<K,G>(learnedModel);

    }



    private DerivableFunction createLogLikelihoodFunctionConcave(List<? extends List<? extends TaggedToken<K, G> >> corpus)
    {
        return new CrfLogLikelihoodFunction<K, G>(corpus,crfTags,features,useRegularization,sigmaSquare_inverseRegularizationFactor);
    }


    private final CrfFeaturesAndFilters<K, G> features;
    private final CrfTags<G> crfTags;
    private final boolean useRegularization;
    private final double sigmaSquare_inverseRegularizationFactor;

    private CrfModel<K, G> learnedModel = null;

    private static final Logger logger = Logger.getLogger(DebateCrfTrainer.class);

}
