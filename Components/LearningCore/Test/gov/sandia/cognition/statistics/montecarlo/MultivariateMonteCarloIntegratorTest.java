/*
 * File:                MultivariateMonteCarloIntegratorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 12, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.montecarlo;

import gov.sandia.cognition.learning.function.vector.LinearVectorFunction;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.distribution.ChiSquareDistribution;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.DefaultWeightedValue;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for MultivariateMonteCarloIntegratorTest.
 *
 * @author krdixon
 */
public class MultivariateMonteCarloIntegratorTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Samples
     */
    public int NUM_SAMPLES = 1000;

    /**
     * Dimension
     */
    public int DIM = 3;

    /**
     * range
     */
    public double RANGE = 3.0;

    /**
     * Tests for class MultivariateMonteCarloIntegratorTest.
     * @param testName Name of the test.
     */
    public MultivariateMonteCarloIntegratorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class MultivariateMonteCarloIntegratorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        assertNotNull( MultivariateMonteCarloIntegrator.INSTANCE );
        assertNotNull( new MultivariateMonteCarloIntegrator() );
    }

    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "clone" );
        MultivariateMonteCarloIntegrator instance =
            new MultivariateMonteCarloIntegrator();
        MultivariateMonteCarloIntegrator clone =
            (MultivariateMonteCarloIntegrator) instance.clone();
        assertNotSame( instance, clone );
        assertNotNull( clone );
    }

    /**
     * Test of integrate method, of class MultivariateMonteCarloIntegrator.
     */
    public void testIntegrate_Collection_Evaluator()
    {
        System.out.println("integrate");
        Vector mean = VectorFactory.getDefault().createUniformRandom(
            DIM, -RANGE, RANGE, RANDOM );
        Matrix variance = MatrixFactory.getDefault().createIdentity(DIM, DIM).scale( RANDOM.nextDouble() * RANGE + 2.0 );
        MultivariateGaussian.PDF targetDistribution =
            new MultivariateGaussian.PDF( mean, variance );
        MultivariateMonteCarloIntegrator instance =
            new MultivariateMonteCarloIntegrator();

        int num = 100;
        ArrayList<Vector> means = new ArrayList<Vector>( num );
        ArrayList<Vector> variances = new ArrayList<Vector>( num );
        LinearVectorFunction linear = new LinearVectorFunction(1.0);
        for( int n = 0; n < num; n++ )
        {
            ArrayList<Vector> samples =
                targetDistribution.sample(RANDOM, NUM_SAMPLES);
            MultivariateGaussian.PDF g =
                instance.integrate(samples, linear);
            means.add( g.getMean() );
            variances.add( g.getCovariance().convertToVector() );
        }

        MultivariateGaussian sampleMeanDistribution =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn(means, 0.0);
        MultivariateGaussian sampleVarianceDistribution =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn(variances, 0.0);

        Vector meanTarget = targetDistribution.getMean();
        double zsquared = sampleMeanDistribution.computeZSquared( meanTarget );
        double p = 1.0-ChiSquareDistribution.CDF.evaluate( zsquared, DIM );

        System.out.println( "====== Mean ========" );
        System.out.println( "Target   = " + targetDistribution.getMean() );
        System.out.println( "Sample   = " + sampleMeanDistribution.getMean() );
        System.out.println( "Z-squared= " + zsquared );
        System.out.println( "P-value  = " + p );
        assertEquals( 1.0, p, 0.95 );

        Vector varianceTarget = targetDistribution.getCovariance().convertToVector();
        zsquared = sampleVarianceDistribution.computeZSquared(
            varianceTarget );
        p = 1.0-ChiSquareDistribution.CDF.evaluate( zsquared, DIM*DIM );
        System.out.println( "====== Variance =======" );
        System.out.println( "Target   = " + varianceTarget );
        System.out.println( "Sample   = " + sampleVarianceDistribution.getMean() );
        System.out.println( "Z-squared= " + zsquared );
        System.out.println( "P-value  = " + p );
        assertEquals( 1.0, p, 0.95 );

    }

    /**
     * Test of integrate method, of class MultivariateMonteCarloIntegrator.
     */
    public void testIntegrate_List_Evaluator()
    {
        System.out.println("integrate");
        Vector mean = VectorFactory.getDefault().createUniformRandom(
            DIM, -RANGE, RANGE, RANDOM );
        Matrix variance = MatrixFactory.getDefault().createIdentity(DIM, DIM).scale( RANDOM.nextDouble() * RANGE + 2.0 );
        MultivariateGaussian.PDF targetDistribution =
            new MultivariateGaussian.PDF( mean, variance );
        MultivariateMonteCarloIntegrator instance =
            new MultivariateMonteCarloIntegrator();

        MultivariateGaussian.PDF importanceDistribution =
            new MultivariateGaussian.PDF( mean.scale(2.0), variance.scale(2.0) );
        ImportanceSampler<Vector> sampler =
            new ImportanceSampler<Vector>( importanceDistribution );

        System.out.println( "Target     = " + targetDistribution.getMean() );
        System.out.println( "Importance = " + importanceDistribution.getMean() );

        int num = 100;
        ArrayList<Vector> means = new ArrayList<Vector>( num );
        ArrayList<Vector> variances = new ArrayList<Vector>( num );
        LinearVectorFunction linear = new LinearVectorFunction(1.0);
        for( int n = 0; n < num; n++ )
        {
            MultivariateGaussian.PDF g =
                instance.integrate( sampler.sample(targetDistribution, RANDOM, NUM_SAMPLES ), linear );
            means.add( g.getMean() );
            variances.add( g.getCovariance().convertToVector() );
        }

        MultivariateGaussian sampleMeanDistribution =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn(means, 0.0);
        MultivariateGaussian sampleVarianceDistribution =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn(variances, 0.0);

        Vector meanTarget = targetDistribution.getMean();
        double zsquared = sampleMeanDistribution.computeZSquared( meanTarget );
        double p = 1.0-ChiSquareDistribution.CDF.evaluate( zsquared, DIM );

        System.out.println( "====== Mean ========" );
        System.out.println( "Target   = " + targetDistribution.getMean() );
        System.out.println( "Sample   = " + sampleMeanDistribution.getMean() );
        System.out.println( "Z-squared= " + zsquared );
        System.out.println( "P-value  = " + p );
        assertEquals( 1.0, p, 0.95 );

    }

    /**
     * Test of getMean method, of class MultivariateMonteCarloIntegrator.
     */
    public void testGetMean_Collection()
    {
        System.out.println("getMean");
        Vector mean = VectorFactory.getDefault().createUniformRandom(
            DIM, -RANGE, RANGE, RANDOM );
        Matrix variance = MatrixFactory.getDefault().createIdentity(DIM, DIM).scale( RANDOM.nextDouble() * RANGE + 2.0 );
        MultivariateGaussian.PDF targetDistribution =
            new MultivariateGaussian.PDF( mean, variance );
        MultivariateMonteCarloIntegrator instance =
            new MultivariateMonteCarloIntegrator();

        int num = 100;
        ArrayList<Vector> means = new ArrayList<Vector>( num );
        ArrayList<Vector> variances = new ArrayList<Vector>( num );
        for( int n = 0; n < num; n++ )
        {
            MultivariateGaussian.PDF g =
                instance.getMean( targetDistribution.sample(RANDOM, NUM_SAMPLES) );
            means.add( g.getMean() );
            variances.add( g.getCovariance().convertToVector() );
        }

        MultivariateGaussian sampleMeanDistribution =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn(means, 0.0);
        MultivariateGaussian sampleVarianceDistribution =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn(variances, 0.0);

        Vector meanTarget = targetDistribution.getMean();
        double zsquared = sampleMeanDistribution.computeZSquared( meanTarget );
        double p = 1.0-ChiSquareDistribution.CDF.evaluate( zsquared, DIM );
        
        System.out.println( "====== Mean ========" );
        System.out.println( "Target   = " + targetDistribution.getMean() );
        System.out.println( "Sample   = " + sampleMeanDistribution.getMean() );
        System.out.println( "Z-squared= " + zsquared );
        System.out.println( "P-value  = " + p );
        assertEquals( 1.0, p, 0.95 );

        Vector varianceTarget = targetDistribution.getCovariance().scale(1.0/NUM_SAMPLES).convertToVector();
        zsquared = sampleVarianceDistribution.computeZSquared(
            varianceTarget );
        p = 1.0-ChiSquareDistribution.CDF.evaluate( zsquared, DIM*DIM );
        System.out.println( "====== Variance =======" );
        System.out.println( "Target   = " + varianceTarget );
        System.out.println( "Sample   = " + sampleVarianceDistribution.getMean() );
        System.out.println( "Z-squared= " + zsquared );
        System.out.println( "P-value  = " + p );
        assertEquals( 1.0, p, 0.95 );

    }

    /**
     * Test of getMean method, of class MultivariateMonteCarloIntegrator.
     */
    public void testGetMean_List()
    {
        System.out.println("getMean");
        Vector mean = VectorFactory.getDefault().createUniformRandom(
            DIM, -RANGE, RANGE, RANDOM );
        Matrix variance = MatrixFactory.getDefault().createIdentity(DIM, DIM).scale( RANDOM.nextDouble() * RANGE + 2.0 );
        MultivariateGaussian.PDF targetDistribution =
            new MultivariateGaussian.PDF( mean, variance );
        MultivariateMonteCarloIntegrator instance =
            new MultivariateMonteCarloIntegrator();

        MultivariateGaussian.PDF importanceDistribution =
            new MultivariateGaussian.PDF( mean.scale(2.0), variance.scale(2.0) );
        ImportanceSampler<Vector> sampler =
            new ImportanceSampler<Vector>( importanceDistribution );

        System.out.println( "Target     = " + targetDistribution.getMean() );
        System.out.println( "Importance = " + importanceDistribution.getMean() );

        int num = 100;
        ArrayList<Vector> means = new ArrayList<Vector>( num );
        ArrayList<Vector> variances = new ArrayList<Vector>( num );
        for( int n = 0; n < num; n++ )
        {
            MultivariateGaussian.PDF g =
                instance.getMean( sampler.sample(targetDistribution, RANDOM, NUM_SAMPLES ) );
            means.add( g.getMean() );
            variances.add( g.getCovariance().convertToVector() );
        }

        MultivariateGaussian sampleMeanDistribution =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn(means, 0.0);
        MultivariateGaussian sampleVarianceDistribution =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn(variances, 0.0);

        Vector meanTarget = targetDistribution.getMean();
        double zsquared = sampleMeanDistribution.computeZSquared( meanTarget );
        double p = 1.0-ChiSquareDistribution.CDF.evaluate( zsquared, DIM );

        System.out.println( "====== Mean ========" );
        System.out.println( "Target   = " + targetDistribution.getMean() );
        System.out.println( "Sample   = " + sampleMeanDistribution.getMean() );
        System.out.println( "Z-squared= " + zsquared );
        System.out.println( "P-value  = " + p );
        assertEquals( 1.0, p, 0.95 );

    }

    /**
     * Equivalence of Weighted/Unweighted methods
     */
    public void testGetMean_Equivalence()
    {
        System.out.println( "Equivalence of Weighted/Unweighted methods" );

        final double weight = RANDOM.nextDouble() * 10.0;
        Vector mean = VectorFactory.getDefault().createUniformRandom(
            DIM, -RANGE, RANGE, RANDOM );
        Matrix variance = MatrixFactory.getDefault().createIdentity(DIM, DIM).scale( RANDOM.nextDouble() * RANGE + 2.0 );
        MultivariateGaussian.PDF targetDistribution =
            new MultivariateGaussian.PDF( mean, variance );
        MultivariateMonteCarloIntegrator instance =
            new MultivariateMonteCarloIntegrator();

        ArrayList<Vector> samples = targetDistribution.sample(RANDOM, NUM_SAMPLES);
        ArrayList<DefaultWeightedValue<Vector>> weightedSamples =
            new ArrayList<DefaultWeightedValue<Vector>>( samples.size() );
        for( Vector sample : samples )
        {
            weightedSamples.add( new DefaultWeightedValue<Vector>( sample, weight ) );
        }

        MultivariateGaussian ur = instance.getMean(samples);
        MultivariateGaussian wr = instance.getMean(weightedSamples);
        System.out.println( "Unweighted = " + ur.getMean() );
        System.out.println( "Weighted   = " + wr.getMean() );
//        System.out.println( "U/W Ratio  = " + ur.get() / wr.getVariance() );
        if( !ur.getMean().equals( wr.getMean(), TOLERANCE ) )
        {
            assertEquals( ur.getMean(), wr.getMean() );
        }

        if( !ur.getCovariance().equals( wr.getCovariance(), TOLERANCE ) )
        {
            assertEquals( ur.getCovariance(), wr.getCovariance() );
        }


        // Now add a whole bunch of samples to the weighted method with zero
        // weights.  This should return the sample variance.
        weightedSamples.ensureCapacity( weightedSamples.size() + NUM_SAMPLES );
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            weightedSamples.add( new DefaultWeightedValue<Vector>( samples.get(n), 0.0 ) );
        }
        MultivariateGaussian wr2 = instance.getMean(weightedSamples);
        System.out.println( "Weighted2  = " + wr2.getMean() );
        assertEquals( wr.getMean(), wr2.getMean() );
        assertEquals( wr.getCovariance(), wr2.getCovariance() );


    }


}
