/*
 * File:                BayesianRegressionTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 1, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.regression.LinearRegression;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.scalar.VectorFunctionLinearDiscriminant;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.ScalarDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.GaussianConfidence;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for BayesianRegressionTestHarness.
 * @param <PosteriorType> Posterior type
 * @author krdixon
 */
public abstract class BayesianRegressionTestHarness<PosteriorType extends Distribution<Vector>>
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double TOLERANCE = 1e-5;

    /**
     * Default number of samples, {@value}.
     */
    public static int NUM_SAMPLES = 10;

    /**
     * Default confidence, {@value}.
     */
    public double CONFIDENCE = 0.95;

    /**
     * Tests for class BayesianRegressionTestHarness.
     * @param testName Name of the test.
     */
    public BayesianRegressionTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates an instance
     * @return
     * Instance
     */
    abstract public BayesianRegression<Double,Double,PosteriorType> createInstance();

    /**
     * Model
     */
    public static class Model
        extends AbstractCloneableSerializable
        implements Evaluator<Double,UnivariateGaussian>
    {

        /**
         * Variance
         */
        double variance;

        /**
         * Model
         * @param variance
         * Variance
         */
        public Model(
            double variance)
        {
            this.variance = variance;
        }

        public UnivariateGaussian evaluate(
            Double input)
        {
            double mean = Math.sin( 2.0*Math.PI * input );
            return new UnivariateGaussian( mean, variance );
        }

    }

    /**
     * Creates the model of the data.
     * @return
     * Model
     */
    public Model createModel()
    {
        return new Model( RANDOM.nextDouble() );
    }

    /**
     * Creates inputs for the model.
     * @param random Random
     * @return
     * Inputs for the model
     */
    public static ArrayList<Double> createInputs(
        Random random )
    {
        ArrayList<Double> samples = new ArrayList<Double>( NUM_SAMPLES );
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            samples.add( random.nextDouble() );
        }
        return samples;
    }

    /**
     * RBF
     */
    public static class RadialBasisVectorFunction
        extends AbstractCloneableSerializable
        implements Evaluator<Number,Vector>
    {

        /**
         * Number of RBFs
         */
        int num;

        /**
         * RBF
         * @param num
         * Number of RBFs
         */
        public RadialBasisVectorFunction(
            int num)
        {
            this.num = num;
        }

        public Vector evaluate(
            Number input)
        {
            Vector x = VectorFactory.getDefault().createVector(num+1);
            x.setElement(num-1, 1.0);
            for( int i = 0; i < num; i++ )
            {
                double mean = i*(2.0/num)-1.0;
                double variance = 0.01;
                x.setElement(i, evaluate(input.doubleValue(), mean, variance));
            }
            return x;
        }

        /**
         * Evaluates
         * @param input Input
         * @param mean mean
         * @param variance Variance
         * @return
         * Output
         */
        public static double evaluate(
            double input,
            double mean,
            double variance )
        {
            double delta = input - mean;
            return Math.exp( delta*delta / (-2.0*variance) );
        }


    }


    /**
     * Creates data
     * @param inputs Inputs
     * @param model Model
     * @param random Random
     * @return
     * Data
     */
    public static ArrayList<InputOutputPair<Double,Double>> createData(
        ArrayList<Double> inputs,
        Evaluator<? super Double,? extends ScalarDistribution<Double>> model,
        Random random )
    {

        ArrayList<InputOutputPair<Double,Double>> samples =
            new ArrayList<InputOutputPair<Double, Double>>( inputs.size() );
        for( int n = 0; n < inputs.size(); n++ )
        {
            Double input = inputs.get(n);
            ScalarDistribution<Double> outputDistribution = model.evaluate( input );
            samples.add( new DefaultInputOutputPair<Double, Double>(
                input, outputDistribution.sample(random) ) );
        }
        return samples;

    }


    /**
     * Tests the constructors of class BayesianRegressionTestHarness.
     */
    abstract public void testConstructors();

    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        BayesianRegression<Double,Double,? extends Distribution<Vector>> instance =
            this.createInstance();
        @SuppressWarnings("unchecked")
        BayesianRegression<Double,Double,? extends Distribution<Vector>> clone =
            (BayesianRegression<Double,Double,? extends Distribution<Vector>>) instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getFeatureMap(), clone.getFeatureMap() );
        assertNotNull( clone.getFeatureMap() );

    }

    /**
     * Test of getFeatureMap method, of class BayesianRegression.
     */
    public void testGetFeatureMap()
    {
        System.out.println("getFeatureMap");
        BayesianRegression<Double,Double,? extends Distribution<Vector>> instance =
            this.createInstance();
        assertNotNull( instance.getFeatureMap() );
    }

    /**
     * Test of setFeatureMap method, of class BayesianRegression.
     */
    public void testSetFeatureMap()
    {
        System.out.println("setFeatureMap");
        BayesianRegression<Double,Double,? extends Distribution<Vector>> instance =
            this.createInstance();
        Evaluator<? super Double, Vector> featureMap = instance.getFeatureMap();
        assertNotNull( featureMap );
        instance.setFeatureMap(null);
        assertNull( instance.getFeatureMap() );
        instance.setFeatureMap(featureMap);
        assertSame( featureMap, instance.getFeatureMap() );
    }

    /**
     * Learn
     */
    public void testLearn()
    {
        System.out.println( "learn" );

        System.out.println("createConditionalDistribution");
        BayesianRegression<Double,Double,? extends Distribution<Vector>> instance =
            this.createInstance();
        ArrayList<Double> inputs = createInputs(RANDOM);
        Evaluator<? super Double,? extends ScalarDistribution<Double>> target = this.createModel();
        ArrayList<InputOutputPair<Double,Double>> data = createData(inputs, target, RANDOM);
        Distribution<Vector> posterior = instance.learn(data);

        Vector mean = posterior.getMean();


        LinearRegression<Double> linearRegression = new LinearRegression<Double>(
            instance.getFeatureMap() );
        VectorFunctionLinearDiscriminant<Double> result = linearRegression.learn(data);
        System.out.println( "Mean: " + mean );
        System.out.println( "Result: " + result.getWeightVector() );

//        System.out.println( "=====================" );
//        System.out.println( "Estimates!" );
//        for( double x = 0.0; x <= 1.0; x += 0.1 )
//        {
//            UnivariateGaussian y = f.evaluate(x);
//            System.out.println( "x = " + x + ", y = " + y );
//        }

    }

    /**
     * Test of createConditionalDistribution method, of class BayesianRegression.
     */
    public void testCreateConditionalDistribution()
    {
        System.out.println("createConditionalDistribution");
        // This is similar to Bishop's example on p. 157

        ArrayList<Double> inputs = createInputs(RANDOM);
        Evaluator<? super Double,? extends ScalarDistribution<Double>> target = this.createModel();
        ArrayList<InputOutputPair<Double,Double>> samples =
            this.createData(inputs, target, RANDOM);

        System.out.println( "Targets:" );
        for( InputOutputPair<Double,Double> sample : samples )
        {
            System.out.println( "x = " + sample.getInput() + ", y = " + sample.getOutput() );
        }

        BayesianRegression<Double,Double,PosteriorType> instance = this.createInstance();
        PosteriorType posterior = instance.learn(samples);

        Vector weights = posterior.getMean();
        ScalarDistribution<Double> conditional = (ScalarDistribution<Double>)
            instance.createConditionalDistribution(samples.get(0).getFirst(), weights );
        System.out.println( "Result: " + conditional );
        System.out.println( "Target: " + samples.get(0).getSecond() );

        ConfidenceInterval interval = GaussianConfidence.computeConfidenceInterval(
            conditional, 1, 0.95);
        System.out.println( "Interval: " + interval );
        assertTrue( interval.withinInterval(samples.get(0).getSecond()) );

    }

    public static void compareMethods(
        Evaluator<? super Double, ? extends Distribution<Double>> predictive,
        VectorFunctionLinearDiscriminant<Double> mle,
        Model target )
    {

        System.out.println( "=====================" );
        double logMLE = 0.0;
        double logBayesian = 0.0;
        double logTarget = 0.0;
        for( double x = 0.0; x <= 1.0; x += 0.1 )
        {
            ProbabilityFunction<Double> y = target.evaluate(x).getProbabilityFunction();
            Distribution<Double> ybayes = predictive.evaluate(x);
            Double ymle = mle.evaluate(x);
            System.out.println( "x = " + x + ", target = " + y + ", Estimate: " + ybayes + ", MLE: " + ymle);
            logTarget = y.logEvaluate( y.getMean() );
            logBayesian += y.logEvaluate( ybayes.getMean() );
            logMLE += y.logEvaluate(ymle);
        }

        System.out.println( "Log-Likelihood Results: " );
        System.out.println( "Target: " + logTarget );
        System.out.println( "Bayes: " + logBayesian );
        System.out.println( "MLE: " + logMLE );
        assertTrue( logTarget > logBayesian );
        assertTrue( logBayesian > logMLE );
    }

    /**
     * Test of createPredictiveDistribution method, of class BayesianRegression.
     */
    public void testCreatePredictiveDistribution10()
    {
        System.out.println("createPredictiveDistribution(10)");

        NUM_SAMPLES = 10;
        ArrayList<Double> inputs = createInputs(RANDOM);
        Model target = new Model(0.25);
        ArrayList<InputOutputPair<Double,Double>> data = createData(inputs, target,RANDOM);
        BayesianRegression<Double,Double,PosteriorType> instance =
            this.createInstance();
        Evaluator<? super Double, ? extends Distribution<Double>> predictive =
            instance.createPredictiveDistribution( instance.learn(data) );
        LinearRegression<Double> regression = new LinearRegression<Double>(
            instance.getFeatureMap() );
        VectorFunctionLinearDiscriminant<Double> mle = regression.learn(data);

        compareMethods(predictive, mle, target);
    }


    /**
     * Test of createPredictiveDistribution method, of class BayesianRegression.
     */
    public void testCreatePredictiveDistribution100()
    {
        System.out.println("createPredictiveDistribution(100)");
        NUM_SAMPLES = 100;
        ArrayList<Double> inputs = createInputs(RANDOM);
        Model target = new Model(0.25);
        ArrayList<InputOutputPair<Double,Double>> data = createData(inputs, target,RANDOM);
        BayesianRegression<Double,Double,PosteriorType> instance =
            this.createInstance();
        Evaluator<? super Double, ? extends Distribution<Double>> predictive =
            instance.createPredictiveDistribution( instance.learn(data) );
        LinearRegression<Double> regression = new LinearRegression<Double>(
            instance.getFeatureMap() );
        VectorFunctionLinearDiscriminant<Double> mle = regression.learn(data);

        compareMethods(predictive, mle, target);
    }

    /**
     * Test of createPredictiveDistribution method, of class BayesianRegression.
     */
    public void testCreatePredictiveDistribution5()
    {
        System.out.println("createPredictiveDistribution(5)");
        NUM_SAMPLES = 5;
        ArrayList<Double> inputs = createInputs(RANDOM);
        Model target = new Model(0.25);
        ArrayList<InputOutputPair<Double,Double>> data = createData(inputs, target,RANDOM);
        BayesianRegression<Double,Double,PosteriorType> instance =
            this.createInstance();
        Evaluator<? super Double, ? extends Distribution<Double>> predictive =
            instance.createPredictiveDistribution( instance.learn(data) );
        LinearRegression<Double> regression = new LinearRegression<Double>(
            instance.getFeatureMap() );
        VectorFunctionLinearDiscriminant<Double> mle = regression.learn(data);

        compareMethods(predictive, mle, target);
    }

    /**
     * Test of createPredictiveDistribution method, of class BayesianRegression.
     */
    public void testCreatePredictiveDistribution1000()
    {
        System.out.println("createPredictiveDistribution(100)");
        NUM_SAMPLES = 100;
        ArrayList<Double> inputs = createInputs(RANDOM);
        Model target = new Model(1.0);
        ArrayList<InputOutputPair<Double,Double>> data = createData(inputs, target,RANDOM);
        BayesianRegression<Double,Double,PosteriorType> instance =
            this.createInstance();
        Evaluator<? super Double, ? extends Distribution<Double>> predictive =
            instance.createPredictiveDistribution( instance.learn(data) );
        LinearRegression<Double> regression = new LinearRegression<Double>(
            instance.getFeatureMap() );
        VectorFunctionLinearDiscriminant<Double> mle = regression.learn(data);

        compareMethods(predictive, mle, target);
    }


}
